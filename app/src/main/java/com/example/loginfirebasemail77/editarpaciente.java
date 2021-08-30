package com.example.loginfirebasemail77;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loginfirebasemail77.modelos.paciente;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class editarpaciente extends AppCompatActivity {
    EditText nameTutor,firstname,lastname,birthname,imagBase64,decivename,macadress;
    TextView gender;
    String idpatient;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarpaciente);
        /*nameTutor=findViewById(R.id.EditNameTutoor);
        firstname=findViewById(R.id.Editarrfirtname);
        lastname=findViewById(R.id.Editarrlastname);
        birthname=findViewById(R.id.EditarrDate);
        gender=findViewById(R.id.EditarrtextView5);
        imagBase64=findViewById(R.id.Editarrimg);
        decivename=findViewById(R.id.EditarrDecivename);
        macadress=findViewById(R.id.EditarrMac);

        idpatient=getIntent().getExtras().getString("idpatient");
        nameTutor.setText(getIntent().getExtras().getString("nameTutor"));
        firstname.setText(getIntent().getExtras().getString("firstname"));
        lastname.setText(getIntent().getExtras().getString("lastname"));
        birthname.setText(getIntent().getExtras().getString("birthname"));
        gender.setText(getIntent().getExtras().getString("gender"));
        imagBase64.setText(getIntent().getExtras().getString("imagBase64"));
        decivename.setText(getIntent().getExtras().getString("decivename"));
        macadress.setText(getIntent().getExtras().getString("macadress"));*/
        inicializarFirebase();


    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }
    public void modificarPaciente(View view)
    {
        paciente p= new paciente();
        p.setIdpatient(idpatient);
        p.setNameTutor(nameTutor.getText().toString().trim());
        p.setFirstname(firstname.getText().toString().trim());
        p.setLastname(lastname.getText().toString().trim());
        p.setBirthname(birthname.getText().toString().trim());
        p.setGender(gender.getText().toString().trim());
        p.setImagBase64(imagBase64.getText().toString().trim());
        p.setDecivename(decivename.getText().toString().trim());
        p.setMacadress(macadress.getText().toString().trim());


        databaseReference.child("Paciente").child(p.getIdpatient()).setValue(p);


    }
}