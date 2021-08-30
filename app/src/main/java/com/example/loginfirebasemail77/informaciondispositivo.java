package com.example.loginfirebasemail77;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.loginfirebasemail77.modelos.sensores;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class informaciondispositivo extends AppCompatActivity {

    List<sensores> list=new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String mac="2C:F4:32:19:78:F6";
    TextView DistanciaDerecha;
    TextView DistanciaFrente;
    TextView DistanciaIzquierda;
    TextView cayo;
    Switch estado;
    Button bntGraficas;
    ImageView imgView;
    String idUsuario;
    TextView  nombres, genero, mac2, nombredispo,nombreTutor, fecha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion_dispositivo);
        inicializarFirebase();
        getInformacionSensores();
        informacionUbicacion();
        idUsuario=getIntent().getExtras().getString("idUsuario");
        nombres=findViewById(R.id.txtPerfilNombre);
        genero=findViewById(R.id.TxtPerfilGenero);
        mac2=findViewById(R.id.txtPerfilMacdispositivo);
        nombredispo=findViewById(R.id.txtPerfilDeciveName);
        imgView=findViewById(R.id.imgFotoReconocimiento);
        fecha=findViewById(R.id.TxtPerfilNacimiento);
        estado=findViewById(R.id.switch1);
        bntGraficas=findViewById(R.id.bntGraficas);

        fecha.setText(getIntent().getExtras().getString("fecha"));
        nombres.setText(getIntent().getExtras().getString("nombres"));
        genero.setText(getIntent().getExtras().getString("genero"));
        mac2.setText(getIntent().getExtras().getString("mac"));
        nombredispo.setText(getIntent().getExtras().getString("nombredispositivo"));
        Picasso.get().load(getIntent().getExtras().getString("img")).resize(100,100).centerCrop().into(imgView);


        bntGraficas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(informaciondispositivo.this,graficas.class);
                i.putExtra("idUsuario",idUsuario);
                startActivity(i);
            }
        });

    }

    private void informacionUbicacion() {
        databaseReference.child("ubicaciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                    TextView coox= findViewById(R.id.latitude);
                    TextView cooY= findViewById(R.id.longitud);
                    coox.setText(objShaptshot.child("coorX").getValue().toString().substring(0,10));
                    cooY.setText(objShaptshot.child("coorY").getValue().toString().substring(0,10));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("elementos: "+error.getMessage());
            }
        });
    }
    public  void goReconocimientoTimeReal(View view)
    {
        Intent i = new Intent(informaciondispositivo.this,reconocimientoTimeReal.class);
        startActivity(i);
    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        DistanciaDerecha=findViewById(R.id.txtSensorDerecho);
        DistanciaFrente=findViewById(R.id.txtSensorFrontal);
        DistanciaIzquierda=findViewById(R.id.txtSensorIzquierdo);
        cayo=findViewById(R.id.txtCaida);
    }
    private void getInformacionSensores() {
        databaseReference.child("Sensores").orderByKey().equalTo("2C:F4:32:19:78:F6").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                    System.out.println("objShaptshot: "+objShaptshot);
                    DistanciaDerecha.setText(objShaptshot.child("DistanciaDerecha").getValue().toString()+"\n cm");
                    DistanciaFrente.setText(objShaptshot.child("DistanciaFrente").getValue().toString()+"\n cm");
                    DistanciaIzquierda.setText(objShaptshot.child("DistanciaIzquierda").getValue().toString()+"\n cm");
                    String estado=(objShaptshot.child("cayo").getValue().toString());
                    validaciones(objShaptshot.child("DistanciaDerecha").getValue().toString(),objShaptshot.child("DistanciaFrente").getValue().toString(),objShaptshot.child("DistanciaIzquierda").getValue().toString());
                    String aux= cayo.getText().toString();
                    if(aux.equals("Patient in good condition") &&  estado.equals("sigue de pie"))
                    {

                    }else
                    if(aux.equals("Alert is in danger") && !estado.equals("sigue de pie"))
                    {

                    }else
                    if(estado.equals("sigue de pie"))
                    {
                        cayo.setText("Patient in good condition");
                        ImageView imageView=findViewById(R.id.imageView14);
                        imageView.setImageResource(R.drawable.bbbbbb);
                        notificaciones( cayo.getText().toString(),"Drop",3);
                    }else
                    {
                        cayo.setText("Alert is in danger");
                        ImageView imageView=findViewById(R.id.imageView14);
                        imageView.setImageResource(R.drawable.aaaaaaa);
                        notificaciones( cayo.getText().toString(),"Drop",3);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("elementos: "+error.getMessage());
            }
        });
    }
    private void validaciones(String derecha,String frontal, String izquierda  ) {

     int Derecho=Integer.parseInt(derecha);
     int Frontal=Integer.parseInt(frontal);
     int Izquierdo=Integer.parseInt(izquierda);
     int suma=Derecho+Frontal+Izquierdo;
        if(suma<1)
        {
            notificaciones("There is the possibility that the patient"+nombres.getText()+" is in a narrow place or has suffered from a fall","All sensor",1);

        }else if (Derecho<100 || Izquierdo< 100 || Frontal<100 )
        {
            notificaciones("Alert the patient has "+nombres.getText()+"an obstacle very close",
                            "Right: "+Derecho+"      Left: "+ Izquierdo +"     Frontal:"+Frontal+"",2);
        }

    }
    public String nombreAleatodio()
    {
        int p=(int) (Math.random()*25+1); int s=(int) (Math.random()*25+1);
        int t=(int) (Math.random()*25+1);int c=(int) (Math.random()*25+1);
        int numero1=(int) (Math.random()*1012+2111);
        int numero2=(int) (Math.random()*1012+2111);

        String[] elementos={"a","b","c","d","e","f","g","h","i","j","k","l",
                "m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
        final String aleatorio=numero1+numero2+numero1+numero2+"";
        return  aleatorio;
    }
 private void  notificaciones(String mensaje,String titulo,int id)
 {
     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         NotificationChannel channel =
                 new NotificationChannel(nombreAleatodio()+"", nombreAleatodio()+"N", NotificationManager.IMPORTANCE_DEFAULT);
         NotificationManager manager = getSystemService(NotificationManager.class);
         manager.createNotificationChannel(channel);
     }
     NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
            .setContentTitle(titulo)
             .setSmallIcon(R.drawable.ic_launcher_background)
             .setAutoCancel(true)
             .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                     R.mipmap.ic_launcher))
             .setContentText(mensaje);

     NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
     managerCompat.notify(id, builder.build());
 }
}