package com.example.loginfirebasemail77;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class recuperarPassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_pass);
    }
    public void addpaciente(View view)
    {
        Intent i = new Intent(this,registrarpaciente.class);
        startActivity(i);
    }
}