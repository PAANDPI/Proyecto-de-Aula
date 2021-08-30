package com.example.loginfirebasemail77;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class animation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_animation);


        //Agregar Animaciones
        Animation animation_1= AnimationUtils.loadAnimation(this, R.anim.deplazamiento_arriba);
        Animation animation_2= AnimationUtils.loadAnimation(this, R.anim.deplazamiento_abajo);


        TextView textView=findViewById(R.id.textView);
        final TextView textView1=findViewById(R.id.textView5);
        final ImageView imageView=findViewById(R.id.imageView);

        textView.setAnimation(animation_2);
        textView1.setAnimation(animation_2);
        imageView.setAnimation(animation_1);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(animation.this,MainActivity.class);

                Pair[] pairs= new Pair[2];
                pairs[0]=new Pair<View, String>(imageView, "logoImagen");
                pairs[1]=new Pair<View, String>(textView1, "textos");
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
                {
                    ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(animation.this, pairs);
                    startActivity(i,options.toBundle());
                }else
                {
                    startActivity(i);
                    finish();
                }



            }
        },4000);
    }
}