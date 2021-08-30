package com.example.loginfirebasemail77;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginfirebasemail77.modelos.adaptadorLista;
import com.example.loginfirebasemail77.modelos.paciente;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class listapacientes extends AppCompatActivity {

    List<paciente>  list=new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String idUsuario;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listapacientes);
        idUsuario=getIntent().getExtras().getString("idUsuario");
        inicializarFirebase();
        recyclerView=findViewById(R.id.listaRevista);
        listapaciente();
    }
    public void goaddPaciente(View view)
    {
        Intent i = new Intent(this,registrarpaciente.class);
        i.putExtra("idUsuario",idUsuario);
        startActivity(i);
    }
    public void goMapa(View view)
    {
        Intent i = new Intent(this,mapa.class);
        i.putExtra("idUsuario",idUsuario);
        startActivity(i);
    }
    public void goHome(View view)
    {
        Intent i = new Intent(this,HomeActivity.class);
        i.putExtra("idUsuario",idUsuario);
        startActivity(i);
    }

    private void listapaciente() {
        databaseReference.child("Paciente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                    paciente p= objShaptshot.getValue(paciente.class);
                    list.add(p);
                    System.out.println(list);
                }
                for (int i=0; i<list.size();i++)
                {
                   System.out.println("valor:" +list.get(i).getNameTutor());
                }
                ejecutar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void ejecutar()
    {
        adaptadorLista lista= new adaptadorLista(list, this);
        RecyclerView recyclerView=findViewById(R.id.listaRevista);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(listapacientes.this,informaciondispositivo.class);
                i.putExtra("idUsuario",list.get(recyclerView.getChildAdapterPosition(v)).getIdUsuario());
                i.putExtra("nombres",list.get(recyclerView.getChildAdapterPosition(v)).getFirstname());

                i.putExtra("tutor",list.get(recyclerView.getChildAdapterPosition(v)).getNameTutor());
                i.putExtra("fecha",list.get(recyclerView.getChildAdapterPosition(v)).getBirthname());

                i.putExtra("genero",list.get(recyclerView.getChildAdapterPosition(v)).getGender());
                i.putExtra("img",list.get(recyclerView.getChildAdapterPosition(v)).getImagBase64());


                i.putExtra("mac",list.get(recyclerView.getChildAdapterPosition(v)).getMacadress());
                i.putExtra("nombredispositivo",list.get(recyclerView.getChildAdapterPosition(v)).getDecivename());
                startActivity(i);

            }
        });

        recyclerView.setAdapter(lista);

    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }
}