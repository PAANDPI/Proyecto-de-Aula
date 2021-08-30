package com.example.loginfirebasemail77;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.loginfirebasemail77.modelos.paciente;
import com.example.loginfirebasemail77.modelos.ubicaciones;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class mapa extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {
     GoogleMap mapG;

    List<ubicaciones> list = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<ubicaciones> listUbicaciones = new ArrayList<ubicaciones>();
    ArrayAdapter<ubicaciones> arrayAdapterUbicaciones;
    ListView listaView;
    ubicaciones ubicacionesSelect;
    String log, la;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listaView = findViewById(R.id.listaUbicacion);

        inicializarFirebase();
        obtenerUbicaciones();


        listaView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ubicacionesSelect = (ubicaciones) parent.getItemAtPosition(position);
                String longitud = ubicacionesSelect.getCoorX();
                String lalitud = ubicacionesSelect.getCoorY();
                String FechaUbicacion = ubicacionesSelect.getFechaUbicación();
                String MacDispositivo = ubicacionesSelect.getMacDispositivo();
                System.out.println("Datos: " + longitud + " " + lalitud + " " + FechaUbicacion + " " + MacDispositivo);
                dibujarMarcadorSeleccionado(longitud, lalitud, FechaUbicacion, MacDispositivo);
                return false;

            }
        });
    }

    public void dibujarMarcadorSeleccionado(String longitud, String lalitud, String FechaUbicacion, String MacDispositivo) {

        final LatLng melbourneLocation = new LatLng(Double.parseDouble(longitud), Double.parseDouble(lalitud));
        Marker melbourne = mapG.addMarker(
                new MarkerOptions()
                        .position(melbourneLocation)
                        .title(datosPaciente())
                        .snippet("Fecha: " + FechaUbicacion + " \n" + "Mac: " + MacDispositivo));
    }

    public String datosPaciente() {
        return "Pablo de los monteros";
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
                list.clear();
                for (DataSnapshot objShaptshot : snapshot.getChildren()) {
                    ubicaciones p = objShaptshot.getValue(ubicaciones.class);
                    list.add(p);
                    arrayAdapterUbicaciones = new ArrayAdapter<ubicaciones>(mapa.this, android.R.layout.simple_list_item_1, list);
                    listaView.setAdapter(arrayAdapterUbicaciones);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void ejecutarMarcador() {
        System.out.println("tamanio: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            System.out.println("recorrido: " + list.get(i).getCoorX());
            final LatLng melbourneLocation = new LatLng(Double.parseDouble(list.get(i).getCoorX()), Double.parseDouble(list.get(i).getCoorY()));
            Marker melbourne = mapG.addMarker(
                    new MarkerOptions()
                            .position(melbourneLocation)
                            .title("Melbourne")
                            .snippet("Population: 4,137,400"));
        }

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
        mapG.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                System.out.println("aaaaa"+location.getLatitude());
                log=""+location.getLongitude();
                la=""+location.getLatitude();
                final LatLng melbourneLocation = new LatLng(Double.parseDouble(la), Double.parseDouble(log));
                Marker melbourne = mapG.addMarker(
                        new MarkerOptions()
                                .position(melbourneLocation)
                                .title("Melbourne")
                                .snippet("Population: 4,137,400"));
            }
        });
    }
    JSONObject jso;
    public void funcionVolley(View view)
    {
        String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+la+","+log+"&destination=-0.20119975454204073, -79.08866386233856&key=AIzaSyC0vPHN0b2QcoPq2yE9eYUAibG3TWprZqA&mode=drive";
        System.out.println("url:"+url);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jso = new JSONObject(response);
                    //trazarRuta(jso);
                    Log.i("jsonRuta: ",""+response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }
    private void trazarRuta(JSONObject jso) {

        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i=0; i<jRoutes.length();i++){

                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray("legs");

                for (int j=0; j<jLegs.length();j++){

                    jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");

                    for (int k = 0; k<jSteps.length();k++){


                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        Log.i("end",""+polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        mapG.addPolyline(new PolylineOptions().addAll(list).color(Color.GRAY).width(5));



                    }



                }



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
      /*private void agregarPrueba()
    {
        List<ubicaciones> ubicacionesX;
        ubicacionesX=new ArrayList<>();
        ubicacionesX.add(new ubicaciones(
                UUID.randomUUID().toString(),
                "15/05/2020",
                " -0.2386985239372658",
                "-79.16854318904792",
                "wxyz"));
        ubicacionesX.add(new ubicaciones(
                UUID.randomUUID().toString(),
                "16/05/2020",
                "-0.23955682333007444",
                "-79.1697662762788",
                "wxyz"));
        ubicacionesX.add(new ubicaciones(
                UUID.randomUUID().toString(),
                "17/05/2020",
                "-0.23797969815455983",
                "-79.16518506358065",
                "wxyz"));
        ubicacionesX.add(new ubicaciones(
                UUID.randomUUID().toString(),
                "18/05/2020",
                "-0.23803334187098443",
                "-79.16565713233643",
                "wxyz"));
        ubicacionesX.add(new ubicaciones(
                UUID.randomUUID().toString(),
                "20/05/2020",
                "-0.23790459695122698",
                "-79.1676419668778",
                "wxyz"));

        for(int i=0; i<ubicacionesX.size(); i++)
        {
            ubicaciones m=new ubicaciones();
            m.setIdUbicacion(ubicacionesX.get(i).getIdUbicacion());
            m.setCoorX(ubicacionesX.get(i).getCoorX());
            m.setCoorY(ubicacionesX.get(i).getCoorY());
            m.setFechaUbicación(ubicacionesX.get(i).getFechaUbicación());
            m.setMacDispositivo(ubicacionesX.get(i).getMacDispositivo());
            databaseReference.child("ubicaciones").child(m.getIdUbicacion()).setValue(m);
        }

        Toast.makeText(this, " ubicaciones", Toast.LENGTH_SHORT).show();

    }*/

}