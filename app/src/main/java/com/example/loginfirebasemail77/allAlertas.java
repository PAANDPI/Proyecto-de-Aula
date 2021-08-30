package com.example.loginfirebasemail77;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.loginfirebasemail77.modelos.ubicaciones;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class allAlertas extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ArrayList alertas ;
    private ArrayAdapter adaptador1;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_alertas);

        alertas=new ArrayList();
        adaptador1=new ArrayAdapter(this,android.R.layout.simple_list_item_1,alertas);

        listView=findViewById(R.id.listaAlertas);
        inicializarFirebase();
        obtenerUbicaciones();

    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
    private void obtenerUbicaciones() {

        databaseReference.child("ubicaciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alertas.clear();
                for (DataSnapshot objShaptshot : snapshot.getChildren()) {
                    alertas.add("Ubicaciones\n" +
                                "Longitud:"+objShaptshot.child("coorX").getValue().toString()+"\n"+
                                "Latitud:"+objShaptshot.child("coorY").getValue().toString()+"\n"+
                                "Fecha:"+objShaptshot.child("fechaUbicación").getValue().toString());
                }
                informacionSensores(alertas);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void informacionSensores(ArrayList alertas) {

        databaseReference.child("Sensores").orderByKey().equalTo("2C:F4:32:19:78:F6").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                    alertas.add("Sensores\n" +
                            "Derecho:"+objShaptshot.child("DistanciaDerecha").getValue().toString()+"\n"+
                            "Frente:"+objShaptshot.child("DistanciaFrente").getValue().toString()+"\n"+
                            "Izquierdo:"+objShaptshot.child("DistanciaIzquierda").getValue().toString());
                }
                obtenerReconocimiento(alertas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("elementos: "+error.getMessage());
            }
        });
    }


    private void obtenerReconocimiento(ArrayList alertas) {
        databaseReference.child("reconocimiento").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot objShaptshot : snapshot.getChildren()) {
                    alertas.add("Reconocimiento\n"+
                            "Nombre:"+objShaptshot.child("reconocimiento").getValue().toString().split(";")[0]+"\n"+
                            "Confianza:"+objShaptshot.child("confianza").getValue().toString().split(";")[0]+"\n");
                }
                imprimirAlertas(alertas);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void imprimirAlertas(ArrayList alertas)
    {
        for(int i=0;i<alertas.size(); i++)
        {
                System.out.println("Alerta:"+alertas.get(i).toString());
        }

        adaptador1=new ArrayAdapter(this,android.R.layout.simple_list_item_1,alertas);
        listView=findViewById(R.id.listaAlertas);
        listView.setAdapter(adaptador1);

    }

}