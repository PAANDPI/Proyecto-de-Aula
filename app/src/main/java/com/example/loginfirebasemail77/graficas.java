package com.example.loginfirebasemail77;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class graficas extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    BarChart barChart;
    GoogleMap mapG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graficas);
        barChart= findViewById(R.id.chart1);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        inicializarFirebase();
        informacionSensores();
        informacionUbicacion();
    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapG = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mapG.getUiSettings().setZoomControlsEnabled(true);
        mapG.setMyLocationEnabled(true);
    }
    public void  camaraMarcadores(Double latiG,Double logitiG)
    {
        LatLng coorInicial = new LatLng(latiG, logitiG);
        CameraPosition camaraP = new CameraPosition.Builder()
                .target(coorInicial)
                .zoom(18)
                .bearing(0)
                .tilt(0)
                .build();
        CameraUpdate cameraUpdate =
                CameraUpdateFactory.newCameraPosition(camaraP);

        mapG.animateCamera(cameraUpdate);

        LatLng uteqCampus = new LatLng(latiG, logitiG);
        Marker campusUteq = mapG.addMarker(
                new MarkerOptions()
                        .position(uteqCampus)
                        .title("Ubicación"));
        campusUteq.showInfoWindow();
    }


    BarDataSet barDataSet;
    public void crearreglo(int Derecho,int Frontal, int Izquierdo)
    {
        ArrayList<BarEntry> visitors= new ArrayList<>();
        visitors.add(new BarEntry(1,Derecho));
        visitors.add(new BarEntry(2,Frontal));
        visitors.add(new BarEntry(3,Izquierdo));

        barDataSet= new BarDataSet(visitors, "visitors");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData= new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setMaxHighlightDistance(2000);
        barChart.setData(barData);
        barChart.getDescription().setText("Bar Chart Example");
        barChart.animateY(2000);

    }
    private void informacionSensores() {
        databaseReference.child("Sensores").orderByKey().equalTo("2C:F4:32:19:78:F6").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { ;
                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                        int Derecho=Integer.parseInt(objShaptshot.child("DistanciaDerecha").getValue().toString());
                        int Frontal=Integer.parseInt(objShaptshot.child("DistanciaFrente").getValue().toString());
                        int Izquierdo=Integer.parseInt(objShaptshot.child("DistanciaIzquierda").getValue().toString());

                    crearreglo(Derecho,Frontal,Izquierdo);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("elementos: "+error.getMessage());
            }
        });
    }
    private void informacionUbicacion() {
        databaseReference.child("ubicaciones").orderByChild("fechaUbicación").limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                  double x=Double.parseDouble(objShaptshot.child("coorX").getValue().toString());
                  double y=Double.parseDouble(objShaptshot.child("coorY").getValue().toString());
                  camaraMarcadores(x,y);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("elementos: "+error.getMessage());
            }
        });
    }
    @Override
    public boolean onMarkerClick( Marker marker) {
        Integer clickCount = (Integer) marker.getTag();
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}