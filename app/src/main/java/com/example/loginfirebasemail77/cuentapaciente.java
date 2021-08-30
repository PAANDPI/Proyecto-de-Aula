package com.example.loginfirebasemail77;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.loginfirebasemail77.modelos.paciente;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class cuentapaciente extends AppCompatActivity {

    EditText txtNameDecive;
    EditText txtMac;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button btnIngresarPaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuentapaciente);
        txtMac=findViewById(R.id.txtMacDispo);
        txtNameDecive=findViewById(R.id.txtNamePaciente);
        inicializarFirebase();
        btnIngresarPaciente=findViewById(R.id.btnIngresarPaciente);
        btnIngresarPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarUsuario();
            }
        });
    }
    public void buscarUsuario()
    {
            databaseReference.child("Paciente").orderByChild("decivename").equalTo(txtNameDecive.getText().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    for (DataSnapshot objShaptshot : snapshot.getChildren())
                    {
                        String name= objShaptshot.child("decivename").getValue().toString();
                        System.out.println("Usuario: "+name);
                        validarMac();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
    public void validarMac()
    {
      databaseReference.child("Paciente").orderByChild("macadress").equalTo(txtMac.getText().toString()).addValueEventListener(new ValueEventListener() {
          @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot objShaptshot : snapshot.getChildren())
                    {
                        String Mac= objShaptshot.child("macadress").getValue().toString();
                        System.out.println("Mac: "+Mac);
                        notificacion();
                        goIngresar();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
    }
    public void goIngresar()
    {
     Intent i = new Intent(cuentapaciente.this,registroReconocimiento.class);
     i.putExtra("mac",txtMac.getText().toString());
     startActivity(i);
    }
    private void inicializarFirebase()
    {
     FirebaseApp.initializeApp(this);
     firebaseDatabase=FirebaseDatabase.getInstance();
     databaseReference=firebaseDatabase.getReference();
    }
    private void notificacion() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                .setContentText("Code Sphere")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setContentText("Su cuenta esta en iniciada");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());
    }

}