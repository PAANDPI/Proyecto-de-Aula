package com.example.loginfirebasemail77;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.loginfirebasemail77.modelos.reconocimientoFire;
import com.example.loginfirebasemail77.modelos.ubicaciones;
import com.example.loginfirebasemail77.reproducir.TTSManager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class registroReconocimiento extends AppCompatActivity {

    ImageView ivPicture;
    TextView txtResult;
    Button bntChoosePicture,btnOpenCamara;
    private static final String TAG = "MiTag";
    private static  final int STORAGE_PERMISSION_CODE=113;
    private static  final int CAMERA_PERMISSION_CODE=223;
    private static  final int READ_STORAGE_PERMISSION_CODE=144;
    private static  final int WRITE_STORAGE_PERMISSION_CODE=144;
    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;


    List<reconocimientoFire> listReconocimiento;
    ListView listaView;
    ArrayAdapter<reconocimientoFire> arrayAdapterreconocimientoFire;
    InputImage inputImage;
    ImageLabeler labeler;
    reconocimientoFire selectReconocimiento;


    private Button btnSpeak;
    private EditText editText;
    TTSManager ttsManager=null;
    String text="No hay";
    String mac="";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Translator translator;
    reconocimientoFire r= new reconocimientoFire();
    Switch aSwitch;
    Switch voz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_reconocimiento);
        ivPicture=findViewById(R.id.ivPicture2);
        bntChoosePicture=findViewById(R.id.idGaleria);
        listaView = findViewById(R.id.listaPaciente);
        btnOpenCamara=findViewById(R.id.btnOpenCamara3);
        voz=findViewById(R.id.voceswitch);
        mac=getIntent().getExtras().getString("mac");

        aSwitch=findViewById(R.id.switch3);
        labeler= ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        ttsManager = new TTSManager();
        ttsManager.init(this);

        inicializarFirebase();
        esp32cam();
        getInformacionSensores();
        bntChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(registroReconocimiento.this, "Buscar img",Toast.LENGTH_SHORT).show();
                Intent storIntent= new Intent();
                storIntent.setType("image/*");
                storIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryLauncher.launch(storIntent);
            }
        });

        galleryLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        Intent data=result.getData();
                        try{
                            inputImage=InputImage.fromFilePath(registroReconocimiento.this, data.getData());
                            ivPicture.setImageURI(data.getData());
                            processImage();
                        }catch (Exception e)
                        {
                            Log.d(TAG, "onActivityResult: "+e.getMessage());
                        }
                    }
                }
        );

        listaView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectReconocimiento= (reconocimientoFire) parent.getItemAtPosition(position);
                text=selectReconocimiento.getReconocimiento().toString();
                if(aSwitch.isChecked())
                {
                 prepareModel(text);
                }else
                {
                 ttsManager.initQueue(text);
                }

                return false;

            }
        });


        btnOpenCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camaraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(camaraIntent);
                Toast.makeText(registroReconocimiento.this, "No hay",Toast.LENGTH_SHORT).show();
            }
        });
        cameraLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent data =result.getData();
                        try
                        {
                            Bitmap photo=(Bitmap) data.getExtras().get("data");
                            ivPicture.setImageBitmap(photo);
                            inputImage=InputImage.fromBitmap(photo,0);
                            processImage();

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] dataq = baos.toByteArray();

                            StorageReference reference=storageReference.child(nombreAleatodio());
                            UploadTask uploadTask = reference.putBytes(dataq);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });

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
                                    guardarreconocimiento(dUri.toString());
                                    System.out.println("Uri "+dUri.toString());
                                }
                            });
                        }catch (Exception e)
                        {
                            Log.d(TAG, "onActivityResult: "+e.getMessage());
                        }
                    }
                }
        );

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
    private void esp32cam() {
        databaseReference.child("esp32cam").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                    String baseRe="data:image/jpeg;base64,%2F9j%2F4AAQSkZJRgABAQEAAAAAAAD%2F2wBDAAoHCAkIBgoJCAkLCwoMDxkQDw4ODx8WFxIZJCAmJiQgIyIoLToxKCs2KyIjMkQzNjs9QEFAJzBHTEY%2FSzo%2FQD7%2F2wBDAQsLCw8NDx0QEB0%2BKSMpPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj7%2FxAAfAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv%2FxAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEFEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5%2Bjp6vHy8%2FT19vf4%2Bfr%2FxAAfAQADAQEBAQEBAQEBAAAAAAAAAQIDBAUGBwgJCgv%2FxAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIygQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2dri4%2BTl5ufo6ery8%2FT19vf4%2Bfr%2FwAARCAEoAZADASEAAhEBAxEB%2F9oADAMBAAIRAxEAPwCgIUA%2B4KUQx90Bq5vUx9mkHkx5%2FwBWuKXyIOvkj8zS3KsO8mI8%2BWtHkxDpGKHsL2a3HmKLHES03yY%2F%2BeYoWiHyAI4%2F%2BeYpfKQ%2FwCjpoHIAhh7xg0eTF%2FzzFMOSwGGLP%2BrWk8iD%2Fniv60IXIhRHF%2FzzFN8tN3CCla2o3TGmJf7opUiiUj92D%2BJpaDUEZutIsOg3irgHdHjB7bq5condf1oaKjBDCoHSoiAaaY7IXFHGeoqZbXAXavfihlyjY6bevvVoFE9CvEhfRbby4027Izx39axmhXdzHxTi%2BgezSI3t49vCZ%2F4FWcY4%2FtcqsnI7ZNF9B2I5kVVY4xWfIuRjp70tblpCEZPTH0oPSob7iSsJjvQAKLhuJtHpWlo8CyXLF1yFSgXKbBt4z0WrEdsmPmQVasjPlQ2%2Bji%2Bwy4QA7eDk%2BtZRjQ9qdk0HKCxjPSq9zaLN0%2BX3oHyD%2FsqdV4qIqR1ANQxkJVmHQAUnlAUWRPKBSmkDbjilIq9tjvaUVciWG05pwWpTGGKWncSFopMoAKCDTQIQ06kJjSKKEJiUvSqepYhpNjP%2FAKsZPpnFSJK5heIVuPs8cvk%2BWsg8uXI6Y5rnmBxnNUpRErkZ6UyqY7BS4bb14z6VL2Bi4zSDhHPoKIMqJ6EAB4ft0Y%2FP9kU4%2FGs4%2B9JIbImU%2FwB4AYzWW%2FGoOD%2FEu4mqVrE2IJ%2BaoGl6Ft2GUUuUGwpKLCTFHWtXS9yrwOSaloEbsOCKmqiNirqZHlxp%2FeOazDTBXHoDSsBUsWoxshaqnrTGMNMNCQWGN0pu2pCyO%2FIpat6gxaUUkAuKKlgAFFMGLRSASkxQAUh4poA7U2gL2EqWE%2FPikBleKR%2FxLce9ci1CGNcVF2qmUHfNFT5kDqcke%2F5Om7iriM7%2BNnk01w391l%2FJRVAjJFPYYx%2BOay7vm83HqRSS6k3Ks33T71nuM5BqEafExppvNHqTLQBR91M%2BlAkhRmt7Ro82AkPUyMM%2BmKCzRGfSp4%2FQ1aIKWpn%2FAEuNf7kX8zVI1JKJV4WkxTQ2RvzVXrQwSI24pDSKGkUzpSSuI9BNFBIUmaoY7NAqQDNJmhIBc0lMAzSZoASkzQITdTd%2FNDAaas2ML3U%2Flw4LLy2ewpdCkU%2FFllPDo907x%2FIgB3dq4sgde9WthETH1x%2BFRZp6Ie6HU2lvoIWrlhCZrqILz84zilJ8qHE7LSm%2B0afFL%2Fz0mm%2Fwqu3SojK5bI%2B1Yt5KDqIjH%2FLP%2BtaRM92RTdc1nmoZdhlJ%2BNWkUFL2qW0IM4rqdNXZpsI7HLfjS0BaloDilBOeKaM%2BpQu38y8kc%2F7v5VBxnpTYD%2B1FHwlWK0p5FRGkSRGmmmC3EpjUraj3O9zzS0EhuHQ0gosIWkz6mkrlIM0uadrg9Q7U3NIAzxSZpoBpNFPYLiZpKWgB3rX0O9SMm0l2IJGyr4xk0rD6E%2Fi2LPhDWQy8pbZH%2FfS15W%2F3qpakw1ZGVqKptcqwhpaYha1NJ%2BV89CzAClL4S4nS%2BGNsmnMqNkRT4%2Bm7mm49TSj5lSIm%2BXla5%2B6x%2FbMgx6D9Kohbjro%2FewPwrNPWlFGnMNPJpppk3CjrSBD1610lsxCIP4RS6ivoaG3I4p6x45Pai4jF%2B9znrzQoy39KpoWqHU48JTsPQpykFqhP3qYDGpppWGxlM70MlnecZoJpmaEDCnZpWLG5pc0pAGaM0MBKKYhaTFGwxaMUgEopByhS44o2At6pqIk8E6xDM3737KVHv8y154w%2Bb3p36iS1ZEx5NRU%2Bg2JxiikN6i1ZsiFu4X9Go0HE6zwecPqcX8I2S%2FrirDp8x%2F3jSCW5Aw%2FWuYkbzNSuXz83mkY%2FShjgTzDcrnjpWY%2FWmkW1cbmkoZkIaO9BpHUfGRkZ9a3JdatQ4FtA8rE4VMYo2JlsTC88QSx%2F6JpBjX3h3U2b%2FhJ%2FsjyzT28aBfmjGA1EXB7kv3TN8zUEfjyiMd1zSrqEkWTdWkrf7aLtFJorfUtRXlvKeJMH0fipZri2Th7iJfq1PUixnvd22f8AXofpVd72Ld8uTS1GRG9T%2B4350xr3%2B7H%2BZqmIia6c%2FwB0CozctSKsekUU%2FQyE7U4dKCwo70mIXGaUUrDFxQBVWAXFKBQwHYpNtSgF2%2B1GygEG2jZzVBci1aBf%2BEY1ViORbkj8xXDP785o1EiFto2jnLdKYcex%2BtDRpYbTuMUEWQ3PNSD0BpyKR2XgmRG1W8UlfntF4J67Xq5MfnYe9QvMctys9ZOsn92i9P3nP%2FfJosuoluVZF%2Fdn0CVlt1p3uaXuNptLYi4lHtTY%2FQM4q7baxc2aItvFbfJ0ZlyaI2erILB8T6qeT9kz%2FwBcKin8Q6nPF5bSwKvfbbrTdmSoFE3t2etw%2FwBaja4lf780hz23UOxZGTnrSbsDAo6CEJppNTcY3NBNADc0inNV0sB6hil2mnexAu00oWpYIfso2UDkO2U7ZSANvtTgntQmAuyjZTANtLikAuKTFAIMUqIXIUDJoQxNbXZ4Y1QN977K358V50PuL9KvoSt2MJPK%2BtQt1oHqhOfaiofYoKcv1AouxdTe8LSmPX4O%2FmKYeuOtdDN99vqavmvoDRDWNrn3LXHeY%2F8AoNTYsilP7hwNvKgVkN1pJBJWGUlA0JSUCY16iNNEsTPFIT70NDWwKfWlPrSehI3vSUMAzSUxDTSckUyri03pQhanrITHajbSeoh2yn7OKVhBspQtMdh2KNtJbgKBS4qtgExRSYIWkpD6BTc0xXDNJupAV9TuVGhX8TISptm6VwfRFB9K0WwkMaoW61mNiU3vVhYWigaNHSrpbPUrW5k3bIpATtHOK2G8QW0jfu7e56%2FxEUrLcvckvL37MsZERYv69qy5pPPO%2BfAx93Hakg2HfegmwM%2FLuz9Kym55FDKkNpP4qlGdxho71S2KsRyVHRcTG0lUFxRRUW0IE9qKuK6juNoqRPUCKSmPqFNqeoXPXsc0uKBCiloELkUZoHcN1G6mAm6jdSAaWo3UCDdSbqY0GabSAM0ygClqn%2FIMu8f88WrjiMcdaBDDURo0KWoxuKSmhsKXFK4BmpVfHPpzQVY29XRPsVnKvUrk5%2BlQ265gb17VPLoXoXxEsllfyY4EDGucxhBxjiqWxIykpEidBSYpjInPzVETzSsiAopjjsIKXimwG%2FxUuaHdgNpKVheglLQ1oFxpPNFCkGzuevbqN1AhN1G7mmIN1JupDFzSUIAJozQAZpCaADNGaEFxKKAA0lLzAqXyf6FNu7Rt%2FKuMH3BTY0RnrUbZp6FDDzSYoIFpBUu4C077gY%2BgzT2NUdNq0Tf2JayH%2BNfy4rPg5g%2B9j5aV%2BxKN618trHWIjyTbNjiuODZhQ9yBU6j6jaOMVewluM%2FlRQxkDfeptCJ5RKXFG4hKShuxWglNNaRJQUprMdxKM%2BtDZI2jtRYaPV99Jn0p7CQZp1FgDNFKwC5opvYbClpCDBpdpoAXYads4pAGyl20wDbSYoAgvU3WVwo6%2BU%2F8q4KM%2Fuk%2BlAIRuKhPWjpcdxlHeheYg4xTe1IoM07AaNlHGRVL4Soo63UG%2B0eF7VgGAWHr%2BFY9tzHmjoKGrJWmZXdAeo5rK47UrGjGmkoMhO9JTKID1oxSQhKSmxPUQ%2BtN7U7k9QNGKWgCYzS4p7AN20u2jcaE20YqbDsep0maZItLzTYMdinAGpuMdspfLoELspcUAO20u2gdhcUbaBC7c0EYHzYH1OKAIWurZf8Al4jz6ZqA6lbfwiaT6JxQBVvNSbyJfKtCPkYZdq40DbGi%2BgxRbUBrc1A2RVbghppKkbCkqED3HUikk1UUUjctZYpNIdHkIbe3y7etV4z%2FAKBcL8%2Fng%2Fu8Yx7091qGpJI0ZvHMe%2FyieN%2FXpWYOn4UegxDSD3x%2BFFx3QnekNArkdFAhrUhFDATafal20X7DsJRT2JExS4zRsISijzANtIFoGeo0bKRI7y6ftoBjgKdigBdtOC%2BlAxcY68fXio2nt0%2B9PHSJuNa9tR%2FFIfolRm%2FH8FvIfxqumo9Rhu7k8LBEvvkmm%2Bfef89Av0WktR7kZglkHz3M59s0gsk9GPuxzRfoBKtqgHC04QfWpEhr225HHqp%2FlXCL9wU4ANb6j6VE%2FWrsMi6UtSwQnAo6mny9wQUo%2B9SHuatipNvknCkkVGhzu%2F3jUSY%2FURjVRutNFMSkNUgYHpTCKWxLQyigAopiDrSUtLDDFIRTQDadTEhKSkNiHmkponyPVwmTjvTvLOeRj60xEZuLZfv3MI%2F4Hmm%2FbrXtIW%2F3VzSd7XAZ%2FaUJ%2FwBVFM%2F1G2k%2B2S%2FwQx%2F8DJodx2ENxdt%2FGi%2F7qU3%2FAEiQYa4lP%2FA8UgtoILYfxbn%2FAN5s1KIBijrqIesFSeRzQA%2F7PjqMUhSNfvMo%2FGpAaZoF%2Fj%2FSmfbYc%2FcY%2B%2FFVYGRm%2B9IR%2BJqJr2c9Nij%2FAHaEMrz3E2wkyNXHhcCnEBD71G1LcZF05oPIpghOg5PNFD94QlKPvU%2BUEbenPb%2F2akcsqK%2FmO2G9KrIyBmye9Ye8aN3GFsudvSoHqtQQ32pMVVrDCmN92nYm5GelFFiBe1FBQgpRk9KGITK9mFP8iY%2FdhkI%2F3aAvceLSdusRH1qQWEv95KSdw9RPsOPvSA%2FRaryrEGAik3nofajcnqWorVNuX%2BY%2FlT%2FKjX7q4p6sfNY60i4kQhrqbB7bu1ItqhP8f4NR8IXTJRAF4CY%2FCpkgH8KgUPUexL5PrUi2%2Ffb%2BNESSTygPvFV%2F3jim%2BZaj%2Fluh9hzQA03UA6Zb9Kab5f8AlnB%2F30aAIzezH7qxp%2BGaYbm4brM34cUMLDPmPUn86TyvmzRsPYf5RpfI9qLi3HeR6UfZj%2BFAALXLgYBHvXCr9wc5poBCKiYDvQ7gRnn6UUvUBKTvTQwoFVsImfmMZ7U6PpUG2%2BhMv4Goj1pIHEY3tS0paMgbTHqim9CPvTsUbEMTpSZoBsA2asaeN2oQA8jfyCKHsSbwUbei%2FgtPKbk5p7LQZCVxTGKKpZjhR3pMp2Mu7nEj7Ldsj1H8VOijS2GZJI1Ppuo8hEi3EB%2F5aLT%2BKrYlnaf6MnDXEYPpmk8%2BzH3pDn%2FZWpGJ9vjXpBIfxFIb5iOEQfTmnYViP7XcMfvbP90Uw%2BYx%2BaRyf940vQPIQxBuoz9aeF96bLWg7yTmpBB7UiSTyOOKeIBQCHiCniAelAhwiHpSlFHXApAM3wf89lPsKaZoR%2Ff%2FACp2AjN2EdcRnnNcAi4T8TTiKw0io2pORRF3ozVBYTHvSduKGITr1pehpblIfninR8UhJllelQml1NBKSla4rh2qJ6dhPUZ3py9asgVhxUQqWMBwat6d8t9GxKgckknHai1xmlNqNvE2Bmb%2FAK5kU1dahHW2m%2F77FWo9wd%2BhWudWZn%2F0aPyx%2Ft%2FMaoSTyy%2F62V2HpnigS8xolK9DUTGk9QsJuq3b3RA2ljiiw7XO0w56Nx9KcIz3B%2BtS1qK9h3l9qlSBvSgRMtufSpFtvwoAlWDmpBD%2FALP40eoB%2B6TrJGP%2BBCk82D%2B%2Fn6UCA3MY6Kxpv2sdoj%2BJoGIbt88BQPpTfPkP8RoaAYzSN1dj%2BNN5obCIu0mm7TQgDy90sf8AvCuGHT%2FgR%2FnRF20ENaojTsUMPsKSj1F1E70HiqGxKSl8IXFB5p4%2B9Rew0Wl%2B7UJ61N9SxDRii4xD0qu7fNQQM3UokoUdSLCmRqbvp2AZkmnAmn0sNB9KKB7iGm5NAhM0manlGxM0U9gR6kIo05eRUWlVrToLiNv92iRIfaLdcbQx%2FClF4h%2B7Aw%2F3mpIevUPtbdlUfrSfapmHVf8AvmmxDWmkbrI2PrSZz607lMWlA5qRD9lKI%2BaAHiKn%2BTSAXyaf5FN6iHeT6UbF7kUgGAwiaPe2BuFedk8P%2FwBdGP6mmlcXUaahamUMxTaW4NB3o60hje9Lim0IaacKARbQ%2FJUdR6mj2GUuaqQBiqjYJp3sQM%2FixTttO4BQBRcLi4FG5BxSuAhamZ5pAN%2FGjpVi6BQaTASjGaF5geibfnyKXFPcExxFSRr%2BNSxseIqlERo3JHrCTUn2em2MkEAFP8nildg2GxR3FO%2Fd%2BtJ3EwyvYGlyey0AL8%2FpRtf%2B9S2Cw7yC3enCzJ6IxpgTW2nSPcwrjZlx1rywgbpRnDeZJj%2Fvo04i1uMYVGR60DiiM0nU1Rb2ENJ2pWuUrCcdqM0uUSWocU3I3Yp2FbUtx%2Fcpp4pLUvoJ3pO9DdiNxG%2B7VZ6AaG000MgSlxTHYWkpW0AKQ0LQYUYoYhKWhjGkUvAFU9gR6eLb0qUWntU%2BpA7yYk%2B%2FJGn1YU5fs3aZfzoLASw4yodvwp3ng8R20gP%2B1QKw7e5%2FgUU797x933xQId5bnu1PEBNAEi2h9KmSzNICUWf%2B0BUi2iimBL9li9D%2BdPWJF6KKGBJSVKAdB%2Fx9Q%2F74%2FnXjEn%2BumOBnzpO3%2B0atC6kbHiojRrcvYjNMzR6khSUWZYmaKNguIaSnuSieNsCnZqTRiUCpZIyQ8VWPWqQhp9aKeoWCloELSUDA5opX0ATFFMQUUmMSkqiWz1ALcyD%2FAFmPocU%2F7Kx%2B9I30NSFiSOwA%2FgFTpZ46CgomS09xUwtqCSQWyD1qVYUFAEgQelPFIBwp1AC0tIBaWmAUlAyS1%2F4%2Fbf8A66L%2FADrxh%2Fvzf9dpP%2FQzT6CW5A3WopKeoIjNNoBiZpM0rMbYlLxQxNhTe9Fhi78Uvm0PXQpsb5vtS%2BZSaJ3Gls0zvVAJjikxQhjscUYpO4gH3qXFILBRimUJikoJDFGKADFNoGexCNQegqUKKqRKH08VID6dQA4U6gBwpaLAOpaAFpaQC0tMBKKQD7Y%2F6bB%2F10X%2BdeMuP3s3%2FXaT%2FwBDNNbC%2B0RGozQOxE1Mql2BCcUnHalrcApKEhiUvFKQ9hKbQwDFFIQuKbtqkxpC4NLtpAKRSCgLABzk9aDQxCYpQtAARSbaewuUMUoAz1oGJJgHrSKu73p7iR7BTwakBwp9MBymnClYB9OpDFpwpiHUooAWlBpDFpaYhKSkMfb%2FAPH5B%2F10X%2BdeOS%2F66f8A67Sf%2BhmqRP2iBj81Rmg06ERpnGOaakS0JTaBBTqChKTFRYS0FxxRtp26juLtpcCgQdaXFQNABRVIQhHFR96aGOUUmU%2Fvr%2BdDAdGvmPtQqT6Zq7JpV8mNtvv%2FANxxkVPMogxI9Lv2b%2Fj0kX3fAFWho8flAvI%2Fm98fdp899iXcT%2BxlUjdcM3%2FAKnFskQ%2BQUwGsOeefrzTMc0WHflPQgadT6iuOWn5o6gPFPFIdh1OoAdS0CHCnUAGaWkAUuaBhmigQ%2B2bF7b%2F9dF%2FnXjc3%2Btn%2FAOu0n%2FoZqkT1K%2BOtMbrV9Sxp%2B9TDUMTuJjNJto2H0FxijFTcNhKWmmD1EopgFHemMWlqQDvRQJolijMs0ca9Xbb0zWqvhyT%2BK9h%2FCNqjmaGWINDgRv3skkvH0FXjBFt2%2BXHgcfcFVuK48AIuI0RF9FUCo9tSgGlaa1VvsIjpj0xlV%2BtRE0kx2O%2BzTwa0aJHg08GoActSA0N3HcdTs0XEOzTqAFpc0AGaWgBc0ZoQBmkzSAfb%2FwDH5B%2F10X%2BdeOy%2F8fd0c%2F8ALxJ%2F6EapMnqQtTDSuVqNNNxTiN67CUh5osA6kNCsISjPNIoKSqAXtQKm4C0daRNw706go2fD1uWna7Zfli4TnHz1vZ9aQWG5qNjQybjS9NZqFYZGXphagUtyMk1GxNA0yvIKhqtB3O%2BzT80CHg09TTEPBp4NTYBc04GmA8GlpAOzRTAdmlzQAUZpAGaSgARsTxn%2FAGhXlWpJ5er36dMXDcUwtqUyKiNUkO4nXmm0hCUlK1h3FpKB%2BYlLVML3CkNTsGgdadQ9BMQ0o96QC1es9PluSpZSsGeWpXGzpYlEUCxxj5VpSaBERNRGgBpPakpPUBDTDTENNN70DI3FVqNQud1nmn5q2IUGpAaQDs08NQA7dTwaLAOzS5oAXNOzQAoNLmkAmaXNABmkJoAYWrzrxTB5Xii%2B5OJdko49RTjuC0Zj0009LjYnamUC1Eo70hoKSmUxaKRNhB1pe1D1ENxTqAYCnCmUKMjpzXT2S%2BXbRR91UZqLDsW92KiL0rEDDTOe9MBKShdwG0hpgNphoKGmq0i80J2JO0zT91Md7j804GgVx%2BadmmF9Rd1ODUhj91O3UhDgaXNMA3Uu6gA3Ub6LAJvpvmUANaUVy3jKCN4be9UL5kZ8uVs%2FwY4pCucofu0xulOC7jGdRSUnuNMSkpsewtJ%2BXNIBKO9O4mGKXrSkxW1E6U6m7DYuaSlbQEWLRfMuIx%2FtCujjx1plji1J2pEjST7YqMKqk4HXmj0JA0Ubg9RpNNoHYa3WmmgLjTUTcmgTOr3c05Wqhjweak30CY4NTs0gFDU8GhjYu%2Fml8ykIXzKPNpgJ5vvS%2BYc9DTsIa0xHVT%2BPFV31K3XhriIH0zSfkCKUniGxT%2FluSfQCqcnieLH7qCVv9onFNRHYpS%2BJbkj9zDGp%2FwBvms%2B71m%2Fu4pIZfK8uQcgLS0uKyM803NVcpjO9FTJgNooTGxOaO9MSYUHmpYXClqgEpaWgXFoFDKL2nr%2B%2B3f3RWzHwtIm46kzT0YhM0lKwxuaKaGIabmgQ2koBjDTTSQjoVbP%2B93p%2B75qb8gsO34p%2B%2BhMY7ee1L5uKBAJ%2BaX7Rg45%2BlOwMY15En35UX%2FgVQPq9oo%2F4%2BoTS1Dcqv4itx9xXm%2Bh4qu3iNv4LYr%2FvsDQ1ZAVW12%2FP3WRPoKrPqV3Lw0zfnVLYEyuXlkXJllYH1c0zb7n86FqDDpSHpg0AHbFMNCTENemDsTScWh9BDy3AxTTSHEOlNB4pJDYtJRYQUU0gSCih6gFKKOgwp1AGlYD5C2OpxWgKRI6kJpWAQUpoYCCkai4xDSU7sBtNNNCG0w1Nx7m0HHQ4p241rYNtxxkIXgVVuZZQFMYY%2BuDSasLmKxuHUhm5%2FGoJNQuF5yvXgUJhuQNf3jZHnED%2FAGRiofNfPLuf%2BBU3IBnToKWpQ2woqtyeouKWpasAdOKaTTSBMTdSF6bG2NzmkzU%2BQDMU007iQlJ3qdxjTmgUDuGaSjYTFoJpbjuGKBTbEhaM0WGFPFDQjUtBtjUfjVrdUdQYuTSUxocKM0WBhTSaYtwNNzQMQ03vRcLjCeaSnYTZpbvWn7%2BOopu4C%2BZx1qle3Pl4UEZPUe1LlbEZ7Sn86jPNVyjuFH4UKIMXIpN1IBCwpS3oKoGN3GgnNVuF9ApO1RLQQUmRU%2BoDc0jNTcRhnNJQ0IjPtRS1Q0B60ykCCiquDF6UhpW7BsLRRsNsSnUgCnL8xx61QjYj4p%2BfSpQwzS5osAtOpoQGkrPUBM02qAaTQVb0IpjGkH0NMPHXii9hWNEn0NNzWjC9xC2PpVaeAy3Bk3qBtHFT6C2K00flkc9ahb2pjuNzRigAz0opJWJBWpd1FhiBqSmDG80E1QIO1Jmla4%2BohI20bqW247CFjnim87qEiNgIptHUNQprUi0JS0hMKKPQYUtDAT%2BdFAXFqe3Hzih6iNNPu0pNSAZp3I7GjQAzTftEAba1xGG%2Fu0wFlmjjXLuBVKTUv7kR%2FwCBNQkBXfUZz0IT2AqP7XOf%2BWzfhxRYCMzSnrK5%2FGo9x%2FvN%2FwB9GtExhvpu6kLyOt701uhx1pSeoLQYcMmGHWnZyKTl0BshlhEoAPY5qrcRiPbj15qk%2Bg1sVif7tNpJAL6UnQ1XkTcN1HegYE0m%2Bly3BBmm9TVWsA7FJikIDScVOo7idKTNVcWgEnNN71DWo0ApppuWoxtOpSYCUGjcAp1D1FcSjvQNC1PFIqHJoBkyX6jja1Ib%2Fn%2FVj86LCRC1%2FL%2FDtx6EZqHzSSTnk0bDLQ1DCYKbjjGQcVTZ8nnqaBDDiihjuNpM0CYtFAIb3oFOzA61zg9PxzTSaSSDcaaQGhxsIXdUciLIvz%2FWjURWuUAjXAHX0qm1NhcGyMUVSQwoxQ3Yd7Bmk%2BXPSn5hzCUZxQK4tNNQPqJSVYnoLSVIwopctgG96CKXmA00VXQph3pe9S%2FIlbCd6WgANFFxiZppagBuaTPNNiA0tJgrhmihgFFDASkoCwtFMEhKP50XKVjqz16UzNUlYgCM02o1egCA80ueKbH0GOoYYZQ31qnJCfM%2BUYWiLRJE6EHFR4oTY1qGaOlFxie%2FakaqW2hAUvNSi2gOccUg5pKVxWE6jNGOcVQ2JS1LYdBCOaKpSuA09aKPMBD15oANT0GFJ3pxEg70tIYZ4oosDCourUCCkoVx2FopiCgdaLXGh9NxUpiYUYprULi7GI4GamjtHbrtFDl0AsC0jH3st%2BlSiNF6IBQCP%2F%2FZ";
                    if(objShaptshot.child("photo").getValue().toString().equals(""))
                    {
                        baseRe="data:image/jpeg;base64,%2F9j%2F4AAQSkZJRgABAQEAAAAAAAD%2F2wBDAAoHCAkIBgoJCAkLCwoMDxkQDw4ODx8WFxIZJCAmJiQgIyIoLToxKCs2KyIjMkQzNjs9QEFAJzBHTEY%2FSzo%2FQD7%2F2wBDAQsLCw8NDx0QEB0%2BKSMpPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj4%2BPj7%2FxAAfAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv%2FxAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEFEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5%2Bjp6vHy8%2FT19vf4%2Bfr%2FxAAfAQADAQEBAQEBAQEBAAAAAAAAAQIDBAUGBwgJCgv%2FxAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIygQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2dri4%2BTl5ufo6ery8%2FT19vf4%2Bfr%2FwAARCAEoAZADASEAAhEBAxEB%2F9oADAMBAAIRAxEAPwCgIUA%2B4KUQx90Bq5vUx9mkHkx5%2FwBWuKXyIOvkj8zS3KsO8mI8%2BWtHkxDpGKHsL2a3HmKLHES03yY%2F%2BeYoWiHyAI4%2F%2BeYpfKQ%2FwCjpoHIAhh7xg0eTF%2FzzFMOSwGGLP%2BrWk8iD%2Fniv60IXIhRHF%2FzzFN8tN3CCla2o3TGmJf7opUiiUj92D%2BJpaDUEZutIsOg3irgHdHjB7bq5condf1oaKjBDCoHSoiAaaY7IXFHGeoqZbXAXavfihlyjY6bevvVoFE9CvEhfRbby4027Izx39axmhXdzHxTi%2BgezSI3t49vCZ%2F4FWcY4%2FtcqsnI7ZNF9B2I5kVVY4xWfIuRjp70tblpCEZPTH0oPSob7iSsJjvQAKLhuJtHpWlo8CyXLF1yFSgXKbBt4z0WrEdsmPmQVasjPlQ2%2Bji%2Bwy4QA7eDk%2BtZRjQ9qdk0HKCxjPSq9zaLN0%2BX3oHyD%2FsqdV4qIqR1ANQxkJVmHQAUnlAUWRPKBSmkDbjilIq9tjvaUVciWG05pwWpTGGKWncSFopMoAKCDTQIQ06kJjSKKEJiUvSqepYhpNjP%2FAKsZPpnFSJK5heIVuPs8cvk%2BWsg8uXI6Y5rnmBxnNUpRErkZ6UyqY7BS4bb14z6VL2Bi4zSDhHPoKIMqJ6EAB4ft0Y%2FP9kU4%2FGs4%2B9JIbImU%2FwB4AYzWW%2FGoOD%2FEu4mqVrE2IJ%2BaoGl6Ft2GUUuUGwpKLCTFHWtXS9yrwOSaloEbsOCKmqiNirqZHlxp%2FeOazDTBXHoDSsBUsWoxshaqnrTGMNMNCQWGN0pu2pCyO%2FIpat6gxaUUkAuKKlgAFFMGLRSASkxQAUh4poA7U2gL2EqWE%2FPikBleKR%2FxLce9ci1CGNcVF2qmUHfNFT5kDqcke%2F5Om7iriM7%2BNnk01w391l%2FJRVAjJFPYYx%2BOay7vm83HqRSS6k3Ks33T71nuM5BqEafExppvNHqTLQBR91M%2BlAkhRmt7Ro82AkPUyMM%2BmKCzRGfSp4%2FQ1aIKWpn%2FAEuNf7kX8zVI1JKJV4WkxTQ2RvzVXrQwSI24pDSKGkUzpSSuI9BNFBIUmaoY7NAqQDNJmhIBc0lMAzSZoASkzQITdTd%2FNDAaas2ML3U%2Flw4LLy2ewpdCkU%2FFllPDo907x%2FIgB3dq4sgde9WthETH1x%2BFRZp6Ie6HU2lvoIWrlhCZrqILz84zilJ8qHE7LSm%2B0afFL%2Fz0mm%2Fwqu3SojK5bI%2B1Yt5KDqIjH%2FLP%2BtaRM92RTdc1nmoZdhlJ%2BNWkUFL2qW0IM4rqdNXZpsI7HLfjS0BaloDilBOeKaM%2BpQu38y8kc%2F7v5VBxnpTYD%2B1FHwlWK0p5FRGkSRGmmmC3EpjUraj3O9zzS0EhuHQ0gosIWkz6mkrlIM0uadrg9Q7U3NIAzxSZpoBpNFPYLiZpKWgB3rX0O9SMm0l2IJGyr4xk0rD6E%2Fi2LPhDWQy8pbZH%2FfS15W%2F3qpakw1ZGVqKptcqwhpaYha1NJ%2BV89CzAClL4S4nS%2BGNsmnMqNkRT4%2Bm7mm49TSj5lSIm%2BXla5%2B6x%2FbMgx6D9Kohbjro%2FewPwrNPWlFGnMNPJpppk3CjrSBD1610lsxCIP4RS6ivoaG3I4p6x45Pai4jF%2B9znrzQoy39KpoWqHU48JTsPQpykFqhP3qYDGpppWGxlM70MlnecZoJpmaEDCnZpWLG5pc0pAGaM0MBKKYhaTFGwxaMUgEopByhS44o2At6pqIk8E6xDM3737KVHv8y154w%2Bb3p36iS1ZEx5NRU%2Bg2JxiikN6i1ZsiFu4X9Go0HE6zwecPqcX8I2S%2FrirDp8x%2F3jSCW5Aw%2FWuYkbzNSuXz83mkY%2FShjgTzDcrnjpWY%2FWmkW1cbmkoZkIaO9BpHUfGRkZ9a3JdatQ4FtA8rE4VMYo2JlsTC88QSx%2F6JpBjX3h3U2b%2FhJ%2FsjyzT28aBfmjGA1EXB7kv3TN8zUEfjyiMd1zSrqEkWTdWkrf7aLtFJorfUtRXlvKeJMH0fipZri2Th7iJfq1PUixnvd22f8AXofpVd72Ld8uTS1GRG9T%2B4350xr3%2B7H%2BZqmIia6c%2FwB0CozctSKsekUU%2FQyE7U4dKCwo70mIXGaUUrDFxQBVWAXFKBQwHYpNtSgF2%2B1GygEG2jZzVBci1aBf%2BEY1ViORbkj8xXDP785o1EiFto2jnLdKYcex%2BtDRpYbTuMUEWQ3PNSD0BpyKR2XgmRG1W8UlfntF4J67Xq5MfnYe9QvMctys9ZOsn92i9P3nP%2FfJosuoluVZF%2Fdn0CVlt1p3uaXuNptLYi4lHtTY%2FQM4q7baxc2aItvFbfJ0ZlyaI2erILB8T6qeT9kz%2FwBcKin8Q6nPF5bSwKvfbbrTdmSoFE3t2etw%2FwBaja4lf780hz23UOxZGTnrSbsDAo6CEJppNTcY3NBNADc0inNV0sB6hil2mnexAu00oWpYIfso2UDkO2U7ZSANvtTgntQmAuyjZTANtLikAuKTFAIMUqIXIUDJoQxNbXZ4Y1QN977K358V50PuL9KvoSt2MJPK%2BtQt1oHqhOfaiofYoKcv1AouxdTe8LSmPX4O%2FmKYeuOtdDN99vqavmvoDRDWNrn3LXHeY%2F8AoNTYsilP7hwNvKgVkN1pJBJWGUlA0JSUCY16iNNEsTPFIT70NDWwKfWlPrSehI3vSUMAzSUxDTSckUyri03pQhanrITHajbSeoh2yn7OKVhBspQtMdh2KNtJbgKBS4qtgExRSYIWkpD6BTc0xXDNJupAV9TuVGhX8TISptm6VwfRFB9K0WwkMaoW61mNiU3vVhYWigaNHSrpbPUrW5k3bIpATtHOK2G8QW0jfu7e56%2FxEUrLcvckvL37MsZERYv69qy5pPPO%2BfAx93Hakg2HfegmwM%2FLuz9Kym55FDKkNpP4qlGdxho71S2KsRyVHRcTG0lUFxRRUW0IE9qKuK6juNoqRPUCKSmPqFNqeoXPXsc0uKBCiloELkUZoHcN1G6mAm6jdSAaWo3UCDdSbqY0GabSAM0ygClqn%2FIMu8f88WrjiMcdaBDDURo0KWoxuKSmhsKXFK4BmpVfHPpzQVY29XRPsVnKvUrk5%2BlQ265gb17VPLoXoXxEsllfyY4EDGucxhBxjiqWxIykpEidBSYpjInPzVETzSsiAopjjsIKXimwG%2FxUuaHdgNpKVheglLQ1oFxpPNFCkGzuevbqN1AhN1G7mmIN1JupDFzSUIAJozQAZpCaADNGaEFxKKAA0lLzAqXyf6FNu7Rt%2FKuMH3BTY0RnrUbZp6FDDzSYoIFpBUu4C077gY%2BgzT2NUdNq0Tf2JayH%2BNfy4rPg5g%2B9j5aV%2BxKN618trHWIjyTbNjiuODZhQ9yBU6j6jaOMVewluM%2FlRQxkDfeptCJ5RKXFG4hKShuxWglNNaRJQUprMdxKM%2BtDZI2jtRYaPV99Jn0p7CQZp1FgDNFKwC5opvYbClpCDBpdpoAXYads4pAGyl20wDbSYoAgvU3WVwo6%2BU%2F8q4KM%2Fuk%2BlAIRuKhPWjpcdxlHeheYg4xTe1IoM07AaNlHGRVL4Soo63UG%2B0eF7VgGAWHr%2BFY9tzHmjoKGrJWmZXdAeo5rK47UrGjGmkoMhO9JTKID1oxSQhKSmxPUQ%2BtN7U7k9QNGKWgCYzS4p7AN20u2jcaE20YqbDsep0maZItLzTYMdinAGpuMdspfLoELspcUAO20u2gdhcUbaBC7c0EYHzYH1OKAIWurZf8Al4jz6ZqA6lbfwiaT6JxQBVvNSbyJfKtCPkYZdq40DbGi%2BgxRbUBrc1A2RVbghppKkbCkqED3HUikk1UUUjctZYpNIdHkIbe3y7etV4z%2FAKBcL8%2Fng%2Fu8Yx7091qGpJI0ZvHMe%2FyieN%2FXpWYOn4UegxDSD3x%2BFFx3QnekNArkdFAhrUhFDATafal20X7DsJRT2JExS4zRsISijzANtIFoGeo0bKRI7y6ftoBjgKdigBdtOC%2BlAxcY68fXio2nt0%2B9PHSJuNa9tR%2FFIfolRm%2FH8FvIfxqumo9Rhu7k8LBEvvkmm%2Bfef89Av0WktR7kZglkHz3M59s0gsk9GPuxzRfoBKtqgHC04QfWpEhr225HHqp%2FlXCL9wU4ANb6j6VE%2FWrsMi6UtSwQnAo6mny9wQUo%2B9SHuatipNvknCkkVGhzu%2F3jUSY%2FURjVRutNFMSkNUgYHpTCKWxLQyigAopiDrSUtLDDFIRTQDadTEhKSkNiHmkponyPVwmTjvTvLOeRj60xEZuLZfv3MI%2F4Hmm%2FbrXtIW%2F3VzSd7XAZ%2FaUJ%2FwBVFM%2F1G2k%2B2S%2FwQx%2F8DJodx2ENxdt%2FGi%2F7qU3%2FAEiQYa4lP%2FA8UgtoILYfxbn%2FAN5s1KIBijrqIesFSeRzQA%2F7PjqMUhSNfvMo%2FGpAaZoF%2Fj%2FSmfbYc%2FcY%2B%2FFVYGRm%2B9IR%2BJqJr2c9Nij%2FAHaEMrz3E2wkyNXHhcCnEBD71G1LcZF05oPIpghOg5PNFD94QlKPvU%2BUEbenPb%2F2akcsqK%2FmO2G9KrIyBmye9Ye8aN3GFsudvSoHqtQQ32pMVVrDCmN92nYm5GelFFiBe1FBQgpRk9KGITK9mFP8iY%2FdhkI%2F3aAvceLSdusRH1qQWEv95KSdw9RPsOPvSA%2FRaryrEGAik3nofajcnqWorVNuX%2BY%2FlT%2FKjX7q4p6sfNY60i4kQhrqbB7bu1ItqhP8f4NR8IXTJRAF4CY%2FCpkgH8KgUPUexL5PrUi2%2Ffb%2BNESSTygPvFV%2F3jim%2BZaj%2Fluh9hzQA03UA6Zb9Kab5f8AlnB%2F30aAIzezH7qxp%2BGaYbm4brM34cUMLDPmPUn86TyvmzRsPYf5RpfI9qLi3HeR6UfZj%2BFAALXLgYBHvXCr9wc5poBCKiYDvQ7gRnn6UUvUBKTvTQwoFVsImfmMZ7U6PpUG2%2BhMv4Goj1pIHEY3tS0paMgbTHqim9CPvTsUbEMTpSZoBsA2asaeN2oQA8jfyCKHsSbwUbei%2FgtPKbk5p7LQZCVxTGKKpZjhR3pMp2Mu7nEj7Ldsj1H8VOijS2GZJI1Ppuo8hEi3EB%2F5aLT%2BKrYlnaf6MnDXEYPpmk8%2BzH3pDn%2FZWpGJ9vjXpBIfxFIb5iOEQfTmnYViP7XcMfvbP90Uw%2BYx%2BaRyf940vQPIQxBuoz9aeF96bLWg7yTmpBB7UiSTyOOKeIBQCHiCniAelAhwiHpSlFHXApAM3wf89lPsKaZoR%2Ff%2FACp2AjN2EdcRnnNcAi4T8TTiKw0io2pORRF3ozVBYTHvSduKGITr1pehpblIfninR8UhJllelQml1NBKSla4rh2qJ6dhPUZ3py9asgVhxUQqWMBwat6d8t9GxKgckknHai1xmlNqNvE2Bmb%2FAK5kU1dahHW2m%2F77FWo9wd%2BhWudWZn%2F0aPyx%2Ft%2FMaoSTyy%2F62V2HpnigS8xolK9DUTGk9QsJuq3b3RA2ljiiw7XO0w56Nx9KcIz3B%2BtS1qK9h3l9qlSBvSgRMtufSpFtvwoAlWDmpBD%2FALP40eoB%2B6TrJGP%2BBCk82D%2B%2Fn6UCA3MY6Kxpv2sdoj%2BJoGIbt88BQPpTfPkP8RoaAYzSN1dj%2BNN5obCIu0mm7TQgDy90sf8AvCuGHT%2FgR%2FnRF20ENaojTsUMPsKSj1F1E70HiqGxKSl8IXFB5p4%2B9Rew0Wl%2B7UJ61N9SxDRii4xD0qu7fNQQM3UokoUdSLCmRqbvp2AZkmnAmn0sNB9KKB7iGm5NAhM0manlGxM0U9gR6kIo05eRUWlVrToLiNv92iRIfaLdcbQx%2FClF4h%2B7Aw%2F3mpIevUPtbdlUfrSfapmHVf8AvmmxDWmkbrI2PrSZz607lMWlA5qRD9lKI%2BaAHiKn%2BTSAXyaf5FN6iHeT6UbF7kUgGAwiaPe2BuFedk8P%2FwBdGP6mmlcXUaahamUMxTaW4NB3o60hje9Lim0IaacKARbQ%2FJUdR6mj2GUuaqQBiqjYJp3sQM%2FixTttO4BQBRcLi4FG5BxSuAhamZ5pAN%2FGjpVi6BQaTASjGaF5geibfnyKXFPcExxFSRr%2BNSxseIqlERo3JHrCTUn2em2MkEAFP8nildg2GxR3FO%2Fd%2BtJ3EwyvYGlyey0AL8%2FpRtf%2B9S2Cw7yC3enCzJ6IxpgTW2nSPcwrjZlx1rywgbpRnDeZJj%2Fvo04i1uMYVGR60DiiM0nU1Rb2ENJ2pWuUrCcdqM0uUSWocU3I3Yp2FbUtx%2Fcpp4pLUvoJ3pO9DdiNxG%2B7VZ6AaG000MgSlxTHYWkpW0AKQ0LQYUYoYhKWhjGkUvAFU9gR6eLb0qUWntU%2BpA7yYk%2B%2FJGn1YU5fs3aZfzoLASw4yodvwp3ng8R20gP%2B1QKw7e5%2FgUU797x933xQId5bnu1PEBNAEi2h9KmSzNICUWf%2B0BUi2iimBL9li9D%2BdPWJF6KKGBJSVKAdB%2Fx9Q%2F74%2FnXjEn%2BumOBnzpO3%2B0atC6kbHiojRrcvYjNMzR6khSUWZYmaKNguIaSnuSieNsCnZqTRiUCpZIyQ8VWPWqQhp9aKeoWCloELSUDA5opX0ATFFMQUUmMSkqiWz1ALcyD%2FAFmPocU%2F7Kx%2B9I30NSFiSOwA%2FgFTpZ46CgomS09xUwtqCSQWyD1qVYUFAEgQelPFIBwp1AC0tIBaWmAUlAyS1%2F4%2Fbf8A66L%2FADrxh%2Fvzf9dpP%2FQzT6CW5A3WopKeoIjNNoBiZpM0rMbYlLxQxNhTe9Fhi78Uvm0PXQpsb5vtS%2BZSaJ3Gls0zvVAJjikxQhjscUYpO4gH3qXFILBRimUJikoJDFGKADFNoGexCNQegqUKKqRKH08VID6dQA4U6gBwpaLAOpaAFpaQC0tMBKKQD7Y%2F6bB%2F10X%2BdeMuP3s3%2FXaT%2FwBDNNbC%2B0RGozQOxE1Mql2BCcUnHalrcApKEhiUvFKQ9hKbQwDFFIQuKbtqkxpC4NLtpAKRSCgLABzk9aDQxCYpQtAARSbaewuUMUoAz1oGJJgHrSKu73p7iR7BTwakBwp9MBymnClYB9OpDFpwpiHUooAWlBpDFpaYhKSkMfb%2FAPH5B%2F10X%2BdeOS%2F66f8A67Sf%2BhmqRP2iBj81Rmg06ERpnGOaakS0JTaBBTqChKTFRYS0FxxRtp26juLtpcCgQdaXFQNABRVIQhHFR96aGOUUmU%2Fvr%2BdDAdGvmPtQqT6Zq7JpV8mNtvv%2FANxxkVPMogxI9Lv2b%2Fj0kX3fAFWho8flAvI%2Fm98fdp899iXcT%2BxlUjdcM3%2FAKnFskQ%2BQUwGsOeefrzTMc0WHflPQgadT6iuOWn5o6gPFPFIdh1OoAdS0CHCnUAGaWkAUuaBhmigQ%2B2bF7b%2F9dF%2FnXjc3%2Btn%2FAOu0n%2FoZqkT1K%2BOtMbrV9Sxp%2B9TDUMTuJjNJto2H0FxijFTcNhKWmmD1EopgFHemMWlqQDvRQJolijMs0ca9Xbb0zWqvhyT%2BK9h%2FCNqjmaGWINDgRv3skkvH0FXjBFt2%2BXHgcfcFVuK48AIuI0RF9FUCo9tSgGlaa1VvsIjpj0xlV%2BtRE0kx2O%2BzTwa0aJHg08GoActSA0N3HcdTs0XEOzTqAFpc0AGaWgBc0ZoQBmkzSAfb%2FwDH5B%2F10X%2BdeOy%2F8fd0c%2F8ALxJ%2F6EapMnqQtTDSuVqNNNxTiN67CUh5osA6kNCsISjPNIoKSqAXtQKm4C0daRNw706go2fD1uWna7Zfli4TnHz1vZ9aQWG5qNjQybjS9NZqFYZGXphagUtyMk1GxNA0yvIKhqtB3O%2BzT80CHg09TTEPBp4NTYBc04GmA8GlpAOzRTAdmlzQAUZpAGaSgARsTxn%2FAGhXlWpJ5er36dMXDcUwtqUyKiNUkO4nXmm0hCUlK1h3FpKB%2BYlLVML3CkNTsGgdadQ9BMQ0o96QC1es9PluSpZSsGeWpXGzpYlEUCxxj5VpSaBERNRGgBpPakpPUBDTDTENNN70DI3FVqNQud1nmn5q2IUGpAaQDs08NQA7dTwaLAOzS5oAXNOzQAoNLmkAmaXNABmkJoAYWrzrxTB5Xii%2B5OJdko49RTjuC0Zj0009LjYnamUC1Eo70hoKSmUxaKRNhB1pe1D1ENxTqAYCnCmUKMjpzXT2S%2BXbRR91UZqLDsW92KiL0rEDDTOe9MBKShdwG0hpgNphoKGmq0i80J2JO0zT91Md7j804GgVx%2BadmmF9Rd1ODUhj91O3UhDgaXNMA3Uu6gA3Ub6LAJvpvmUANaUVy3jKCN4be9UL5kZ8uVs%2FwY4pCucofu0xulOC7jGdRSUnuNMSkpsewtJ%2BXNIBKO9O4mGKXrSkxW1E6U6m7DYuaSlbQEWLRfMuIx%2FtCujjx1plji1J2pEjST7YqMKqk4HXmj0JA0Ubg9RpNNoHYa3WmmgLjTUTcmgTOr3c05Wqhjweak30CY4NTs0gFDU8GhjYu%2Fml8ykIXzKPNpgJ5vvS%2BYc9DTsIa0xHVT%2BPFV31K3XhriIH0zSfkCKUniGxT%2FluSfQCqcnieLH7qCVv9onFNRHYpS%2BJbkj9zDGp%2FwBvms%2B71m%2Fu4pIZfK8uQcgLS0uKyM803NVcpjO9FTJgNooTGxOaO9MSYUHmpYXClqgEpaWgXFoFDKL2nr%2B%2B3f3RWzHwtIm46kzT0YhM0lKwxuaKaGIabmgQ2koBjDTTSQjoVbP%2B93p%2B75qb8gsO34p%2B%2BhMY7ee1L5uKBAJ%2BaX7Rg45%2BlOwMY15En35UX%2FgVQPq9oo%2F4%2BoTS1Dcqv4itx9xXm%2Bh4qu3iNv4LYr%2FvsDQ1ZAVW12%2FP3WRPoKrPqV3Lw0zfnVLYEyuXlkXJllYH1c0zb7n86FqDDpSHpg0AHbFMNCTENemDsTScWh9BDy3AxTTSHEOlNB4pJDYtJRYQUU0gSCih6gFKKOgwp1AGlYD5C2OpxWgKRI6kJpWAQUpoYCCkai4xDSU7sBtNNNCG0w1Nx7m0HHQ4p241rYNtxxkIXgVVuZZQFMYY%2BuDSasLmKxuHUhm5%2FGoJNQuF5yvXgUJhuQNf3jZHnED%2FAGRiofNfPLuf%2BBU3IBnToKWpQ2woqtyeouKWpasAdOKaTTSBMTdSF6bG2NzmkzU%2BQDMU007iQlJ3qdxjTmgUDuGaSjYTFoJpbjuGKBTbEhaM0WGFPFDQjUtBtjUfjVrdUdQYuTSUxocKM0WBhTSaYtwNNzQMQ03vRcLjCeaSnYTZpbvWn7%2BOopu4C%2BZx1qle3Pl4UEZPUe1LlbEZ7Sn86jPNVyjuFH4UKIMXIpN1IBCwpS3oKoGN3GgnNVuF9ApO1RLQQUmRU%2BoDc0jNTcRhnNJQ0IjPtRS1Q0B60ykCCiquDF6UhpW7BsLRRsNsSnUgCnL8xx61QjYj4p%2BfSpQwzS5osAtOpoQGkrPUBM02qAaTQVb0IpjGkH0NMPHXii9hWNEn0NNzWjC9xC2PpVaeAy3Bk3qBtHFT6C2K00flkc9ahb2pjuNzRigAz0opJWJBWpd1FhiBqSmDG80E1QIO1Jmla4%2BohI20bqW247CFjnim87qEiNgIptHUNQprUi0JS0hMKKPQYUtDAT%2BdFAXFqe3Hzih6iNNPu0pNSAZp3I7GjQAzTftEAba1xGG%2Fu0wFlmjjXLuBVKTUv7kR%2FwCBNQkBXfUZz0IT2AqP7XOf%2BWzfhxRYCMzSnrK5%2FGo9x%2FvN%2FwB9GtExhvpu6kLyOt701uhx1pSeoLQYcMmGHWnZyKTl0BshlhEoAPY5qrcRiPbj15qk%2Bg1sVif7tNpJAL6UnQ1XkTcN1HegYE0m%2Bly3BBmm9TVWsA7FJikIDScVOo7idKTNVcWgEnNN71DWo0ApppuWoxtOpSYCUGjcAp1D1FcSjvQNC1PFIqHJoBkyX6jja1Ib%2Fn%2FVj86LCRC1%2FL%2FDtx6EZqHzSSTnk0bDLQ1DCYKbjjGQcVTZ8nnqaBDDiihjuNpM0CYtFAIb3oFOzA61zg9PxzTSaSSDcaaQGhxsIXdUciLIvz%2FWjURWuUAjXAHX0qm1NhcGyMUVSQwoxQ3Yd7Bmk%2BXPSn5hzCUZxQK4tNNQPqJSVYnoLSVIwopctgG96CKXmA00VXQph3pe9S%2FIlbCd6WgANFFxiZppagBuaTPNNiA0tJgrhmihgFFDASkoCwtFMEhKP50XKVjqz16UzNUlYgCM02o1egCA80ueKbH0GOoYYZQ31qnJCfM%2BUYWiLRJE6EHFR4oTY1qGaOlFxie%2FakaqW2hAUvNSi2gOccUg5pKVxWE6jNGOcVQ2JS1LYdBCOaKpSuA09aKPMBD15oANT0GFJ3pxEg70tIYZ4oosDCourUCCkoVx2FopiCgdaLXGh9NxUpiYUYprULi7GI4GamjtHbrtFDl0AsC0jH3st%2BlSiNF6IBQCP%2F%2FZ";
                    }else
                    {
                        baseRe=objShaptshot.child("photo").getValue().toString().split(",")[1];
                    }
                    String base=baseRe.replace("%2F","/").replace("%2B","+");
                    System.out.println(baseRe.replace("%2F","/"));
                    byte[] decodedString = Base64.decode(base, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    inputImage=InputImage.fromBitmap(decodedByte,0);
                    ivPicture.setImageBitmap(decodedByte);
                    processImage();

                    StorageReference reference=storageReference.child(nombreAleatodio());
                    UploadTask uploadTask = reference.putBytes(decodedString);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });

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
                            guardarreconocimiento(dUri.toString());
                            System.out.println("Uri "+dUri.toString());
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("elementos: "+error.getMessage());
            }
        });
    }
    public void guardarreconocimiento(String url)
    {
        r.setIdReconocimiento(UUID.randomUUID().toString());
        r.setConfianza(StringConfianza());
        r.setReconocimiento(Stringreconocimiento());
        r.setMacDispositivo(mac);
        r.setUrl(url);

        databaseReference.child("reconocimiento").child(r.getIdReconocimiento()).setValue(r);
        ttsManager.initQueue("Se guardó el reconocimiento");


    }
    public String Stringreconocimiento()
    {

        String html="";
        for (int i=0; i<listReconocimiento.size(); i++) {

            html=listReconocimiento.get(i).getReconocimiento().toString()+";"+html;
        }
        return html;
    }
    public String StringConfianza()
    {
        String html="";
        for (int i=0; i<listReconocimiento.size(); i++) {

            html=listReconocimiento.get(i).getConfianza().toString()+";"+html;
        }
        return html;
    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        storageReference= FirebaseStorage.getInstance().getReference().child("Esp32");
    }

    private void traslaterlenguaje(String text) {

        translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>(){
            @Override
            public  void onSuccess(String s)
            {
                ttsManager.initQueue(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                ttsManager.initQueue("Fallo al traducir");
            }
        });

    }

    private void prepareModel(String text) {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.fromLanguageTag("en"))
                        .setTargetLanguage(TranslateLanguage.fromLanguageTag("es"))
                        .build();
        translator = Translation.getClient(options);
        translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                traslaterlenguaje(text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsManager.shutDown();
    }
    private void processImage() {
        labeler.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(@NonNull @NotNull List<ImageLabel> imageLabels) {
                        String result="";
                        listReconocimiento=new ArrayList<>();
                        listReconocimiento.clear();
                        for (ImageLabel label: imageLabels)
                        {
                            listReconocimiento.add(new reconocimientoFire((label.getConfidence()+""),label.getText()));

                        }
                        arrayAdapterreconocimientoFire = new ArrayAdapter<reconocimientoFire>(registroReconocimiento.this, android.R.layout.simple_list_item_1, listReconocimiento);
                        listaView.setAdapter(arrayAdapterreconocimientoFire);

                        Reprodicir();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG,"onFailure"+e.getMessage());
            }
        });

    }
    public void Reprodicir()
    {
            for (int i=0; i<listReconocimiento.size(); i++) {
                try {
                    ttsManager.initQueue(listReconocimiento.get(i).getReconocimiento().toString());
                    Thread.sleep( 1000);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
    }
    String derecha, izquierda, frontal;
    private void getInformacionSensores() {
        databaseReference.child("Sensores").orderByKey().equalTo("2C:F4:32:19:78:F6").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                    try
                    {
                        validaciones(objShaptshot.child("DistanciaDerecha").getValue().toString(),objShaptshot.child("DistanciaFrente").getValue().toString(),objShaptshot.child("DistanciaIzquierda").getValue().toString());

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("elementos: "+error.getMessage());
            }
        });
    }
    public void validaciones(String derecha,String frontal, String izquierda) throws InterruptedException {

        if(voz.isChecked())
        {
            double Derecho=Integer.parseInt(derecha);
            double Frontal=Integer.parseInt(frontal);
            double Izquierdo=Integer.parseInt(izquierda);
            double suma=Derecho+Frontal+Izquierdo;
            if(suma<1)
            {
                ttsManager.initQueue("no detection");
            }
            if (Derecho<100)
            {
                //double valor=(Derecho*(1/100));
                ttsManager.initQueue("Right");
                Thread.sleep( 1000);
            }
            if(Izquierdo<100)
            {
                //double valor=(Izquierdo*(1/100));
                ttsManager.initQueue("Left");
                Thread.sleep( 1000);
            }
            if(Frontal<100)
            {
                //double valor=(Frontal*(1/100));
                ttsManager.initQueue("Frontal" );
                Thread.sleep( 1000);
            }

        }else
        {

        }


    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL("https://res.cloudinary.com/durxpegdm/image/upload/v1627940101/3d-flame-279_xt18fx.png");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            Log.d(TAG,"error"+e.getMessage());
            return null;
        }
    }
    @Override
    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults )
    {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if(requestCode== CAMERA_PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            { Toast.makeText(registroReconocimiento.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();
            }else
            { Toast.makeText(registroReconocimiento.this,"Denego los permisos", Toast.LENGTH_SHORT).show();}
        }
        else if(requestCode==STORAGE_PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(registroReconocimiento.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();}
            else
            {Toast.makeText(registroReconocimiento.this,"Permisos denegados", Toast.LENGTH_SHORT).show();}
        }
        else  if(requestCode==READ_STORAGE_PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(registroReconocimiento.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();}
            else
            {Toast.makeText(registroReconocimiento.this,"Permisos denegados", Toast.LENGTH_SHORT).show();}
        }
        else  if(requestCode==WRITE_STORAGE_PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(registroReconocimiento.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();}
            else
            {Toast.makeText(registroReconocimiento.this,"Permisos denegados", Toast.LENGTH_SHORT).show();}
        }
    }

}