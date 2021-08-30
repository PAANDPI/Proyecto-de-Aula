package com.example.loginfirebasemail77;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginfirebasemail77.modelos.paciente;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class registrarpaciente extends AppCompatActivity {

    EditText  nameTutor, firstname, lastname, birthname, decivename,macadress;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatePickerDialog.OnDateSetListener setListener;
    Button btnFecha;


    EditText etPlannedDate;
    String base64imagen="asdasdasdasdas"; /// en esta trabajas
    RadioButton rbtMasculino, rbtFemenino;
    String genero;

    Uri imageUri;
    //variables de imagenes
    ImageView foto;
    Button subir,seleccionar;
    StorageReference storageReference;
    ProgressDialog cargando;
    Bitmap thump_bitmap=null;
    Button botonCargar;
    ImageView imagen;
    String path;
    Image image;
    TextView tv;
    Switch estado;
    private static int RESULT_LOAD_IMAGE = 1;
    //fin de variables para imagenes
    private static final String TAG = "MiTag";
    private static  final int STORAGE_PERMISSION_CODE=113;

    private GestureDetector gesto;

    String idUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarpaciente);
        //----Iniciando--los--componentes--------//
        nameTutor=findViewById(R.id.txtNameTutor_R);
        firstname=findViewById(R.id.txtfirtname_R);
        lastname=findViewById(R.id.txtLastname_R);
        subir=findViewById(R.id.bntGuardar);
        decivename=findViewById(R.id.txtDecivename_R);
        macadress=findViewById(R.id.txtMac_R);
        etPlannedDate=findViewById(R.id.txtDate_R);
        birthname=findViewById(R.id.txtDate_R);
        btnFecha=findViewById(R.id.btnFechaNacimiento_R);
        rbtMasculino=findViewById(R.id.radioButton_R);
        rbtFemenino=findViewById(R.id.radioButton2_R);
        estado=findViewById(R.id.swStado);
        //----Fin--de-Iniciando--los--componentes--------//

        Calendar cal=Calendar.getInstance();
        int YEAR=cal.get(Calendar.YEAR);
        int MES=cal.get(Calendar.MONTH);
        int DIA=cal.get(Calendar.DAY_OF_MONTH);
        int style= AlertDialog.THEME_HOLO_LIGHT;

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(
                        registrarpaciente.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        ,setListener,YEAR,MES,DIA);
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.show();

            }
        });
        setListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
              month=month+1;
              String date=dayOfMonth+"/"+month+"/"+year;
                birthname.setText(date);
            }
        };
        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog= new DatePickerDialog(
                        registrarpaciente.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month=month+1;
                        String date=dayOfMonth+"/"+month+"/"+year;
                        birthname.setText(date);
                    }
                },YEAR,MES,DIA);
                datePickerDialog.show();
            }
        });


        idUsuario=getIntent().getExtras().getString("idUsuario");

        System.out.println("id del usuario:"+idUsuario);
        inicializarFirebase();

        imagen= (ImageView) findViewById(R.id.imagemId);
        Picasso.get().load("https://res.cloudinary.com/durxpegdm/image/upload/v1627940101/3d-flame-279_xt18fx.png").resize(200,200).centerCrop().into(imagen);
        botonCargar= (Button) findViewById(R.id.btnCargarImg);
        botonCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();

            }
        });

    }
    //---------------------------------------Fin del onCreate-------------------------------------//


    public void OpenGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                ImageView imageView = (ImageView) findViewById(R.id.imagemId);
                imageView.setImageURI(data.getData());
                //FIN DE PONER LAS IMAGEN EN EL VIEW
                imageUri = CropImage.getPickImageResultUri(this, data);

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
       final String aleatorio=elementos[p]+elementos[s]+numero1+elementos[t]+elementos[c]+numero2+"comprimido.jpg";
       return  aleatorio;
   }
    public void addFirebasePaciente(View view)
    {

        StorageReference reference=storageReference.child(nombreAleatodio());
        UploadTask uploadTask= reference.putFile(imageUri);

        Task<Uri> uriTask= uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then( Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw Objects.requireNonNull(task.getException());
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(Task<Uri> task) {
                Uri dUri=task.getResult();
                guardarTodo(dUri.toString());
                Toast.makeText(registrarpaciente.this,"Todo bien ",Toast.LENGTH_SHORT );
            }
        });

    }
    public  void  guardarTodo(String url)
    {
        paciente p=new paciente();
        p.setIdpatient(UUID.randomUUID().toString());
        p.setNameTutor(nameTutor.getText().toString());
        p.setFirstname(firstname.getText().toString());
        p.setLastname(lastname.getText().toString());
        p.setBirthname(birthname.getText().toString());
        p.setImagBase64(url);

        if(rbtFemenino.isChecked())
        {
            genero="Femenino";

        }else if(rbtMasculino.isChecked())
        {
            genero="Maculino";
        }
        p.setGender(genero);
        p.setDecivename(decivename.getText().toString());
        p.setIdUsuario(idUsuario);
        p.setMacadress(macadress.getText().toString());
        if(estado.isChecked())
        {
            p.setState("true");
        }else
        {
            p.setState("false");
        }


        databaseReference.child("Paciente").child(p.getIdpatient()).setValue(p);
        Toast.makeText(this, "Agregado", Toast.LENGTH_SHORT).show();
    }
    //--------------------------Iniciar Firebase-----------------------------------------------//
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        storageReference= FirebaseStorage.getInstance().getReference().child("Fotos");
    }
    //--------------------------Fin de Firebase-----------------------------------------------//
    //--------------------------------------------Parte de los permisos---------------------------------------------------//
    @Override
    protected void onResume()
    {
        super.onResume();
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
    }
    public void checkPermission(String permission, int requestCode)
    {
        if(ContextCompat.checkSelfPermission(registrarpaciente.this, permission)== PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(registrarpaciente.this,new String[]{permission},requestCode);
        }
    }
    @Override
    public  void onRequestPermissionsResult(int requestCode, @NonNull  String[] permission, @NonNull int[] grantResults )
    {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if(requestCode==STORAGE_PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {Toast.makeText(registrarpaciente.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();}
        }else
        {Toast.makeText(registrarpaciente.this,"Permisos denegados", Toast.LENGTH_SHORT).show();}
    }
    //--------------------------------------------Parte de los permisos---------------------------------------------------//

}