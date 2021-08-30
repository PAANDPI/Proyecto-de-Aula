package com.example.loginfirebasemail77;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class reconocerIdioma extends AppCompatActivity {
    EditText etLangString;
    Button btnCheckNow;
    TextView tvResult;
    EditText txtTraducido;
    Button btnTraducir;
    String originalTex="";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Translator inglesSpañol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconocer_idioma);
        inicializarFirebase();
    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference=firebaseDatabase.getReference();
        etLangString=findViewById(R.id.etLangString);
        btnCheckNow=findViewById(R.id.btnCheckNow);
        tvResult=findViewById(R.id.tvResult);
        txtTraducido=findViewById(R.id.traducido);
        //btnTraducir=findViewById(R.id.btnTraducir);

        /*btnTraducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        btnCheckNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lanText=etLangString.getText().toString();
                if(lanText.equals(""))
                {
                    Toast.makeText(reconocerIdioma.this,"Ingrese un texto por favor", Toast.LENGTH_SHORT).show();
                    
                }else
                {
                    identificar();
                    detectarTexto(lanText);
                }
            }


        });
    }
    public void identificar()
    {
        originalTex=etLangString.getText().toString();
        prepareModel();
    }
    private void prepareModel() {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.fromLanguageTag("es"))
                        .setTargetLanguage(TranslateLanguage.fromLanguageTag("en"))
                        .build();
        inglesSpañol = Translation.getClient(options);

        inglesSpañol.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                traslaterlenguaje();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void traslaterlenguaje() {
        inglesSpañol.translate(originalTex).addOnSuccessListener(new OnSuccessListener<String>(){
           @Override
           public  void onSuccess(String s)
           {
               txtTraducido.setText(s);
           }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                txtTraducido.setText("Error al traducir el texto");
            }
        });
    }

    private void detectarTexto(String lanTextDelMetodo) {
        LanguageIdentifier languageIdentifier= LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(lanTextDelMetodo).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String languageCode) {
                if(languageCode.equals("und"))
                {
                    tvResult.setText("No se pudo identificar el idioma");

                }else
                {
                    tvResult.setText("El lenguaje es:"+languageCode);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                tvResult.setText("Exception: "+e);
            }
        });
    }
}