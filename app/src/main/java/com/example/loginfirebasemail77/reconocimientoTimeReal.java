package com.example.loginfirebasemail77;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.loginfirebasemail77.modelos.adaptadorLista;
import com.example.loginfirebasemail77.modelos.adaptadorReconocimiento;
import com.example.loginfirebasemail77.modelos.paciente;
import com.example.loginfirebasemail77.modelos.reconocimientoFire;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class reconocimientoTimeReal extends AppCompatActivity {

    List<reconocimientoFire> list=new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reconocimiento_time_real);
        recyclerView=findViewById(R.id.listaCaecus);
        inicializarFirebase();
        esp32cam();
    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }
    private void esp32cam() {
        databaseReference.child("reconocimiento").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                    reconocimientoFire r= objShaptshot.getValue(reconocimientoFire.class);
                    list.add(r);
                }
                ejecutar();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("elementos: "+error.getMessage());
            }
        });
    }
    public void  ejecutar()
    {
        adaptadorReconocimiento lista= new adaptadorReconocimiento(list, this);
        RecyclerView recyclerView=findViewById(R.id.listaCaecus);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(lista);
    }

}