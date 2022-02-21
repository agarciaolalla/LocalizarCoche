package com.example.localizarcoche;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {
        Marker myMarker;
        private double Longitud,Latitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtenemos el mapa de forma asíncrona (notificará cuando esté listo)
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        //Este if sirve para dar permisos a la app y a la geolocalización
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //LocationManager es para sacar la Latitud y Longitud para poder luego utilizarla en el setMarcadorCoche y guardarlo en el sharedPreferences.
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        Latitud = myLocation.getLatitude();
        Longitud = myLocation.getLongitude();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mapa = googleMap;
        //Creamos un OnClickListener para escuchar al botón creado para centrarnos en el coche.
        Button bCoche = (Button) findViewById(R.id.coche);
        bCoche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(myMarker.getPosition(), 13.0f));
            }
        });
        //Este if sirve para dar permisos a la app y a la geolocalización
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Guardamos las preferencias del "SharedPreferences" mediante este IF, dentro de un método que he llamado setMarcadorCoche()
        if (getMarcadorCoche() != null) {
            myMarker = mapa.addMarker(new MarkerOptions()
                    .position(getMarcadorCoche())
                    .title("Mi coche")
                    .icon(BitmapDescriptorFactory.fromResource(R.raw.coche)));
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(getMarcadorCoche(), 13.0f));
        } else {
            LatLng setLocation = new LatLng(Latitud, Longitud);
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(setLocation, 13.0f));
        }

        //Introducimos estas tres líneas para poner el botón del GPS / Botón del ZOOM
        UiSettings uiSettings = mapa.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        mapa.setMyLocationEnabled(true);

        //Con este método creamos el marcador manteniendo pulsada la pantalla.
        mapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // primero comprueba si hay marcador anteriormente
                if (myMarker == null) {
                    // Si el marcador no esta inicializado, lo inicializa
                    myMarker = googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Marcador de mi coche")
                    );
                    setMarcadorCoche();
                } else {
                    // Si el marcador existe, actualiza la posición
                    myMarker.setPosition(latLng);
                    setMarcadorCoche();
                }
            }
        });
    }
    //Métodos para guardar y obtener la localización del coche después de que se haya cerrado la aplicación.
    public LatLng getMarcadorCoche() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String lat = sharedPref.getString(("Latitud"), "0");
        String lng = sharedPref.getString(("Longitud"), "0");
        return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

    }
    private void setMarcadorCoche(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Latitud", ""+myMarker.getPosition().latitude);
        editor.putString("Longitud", ""+myMarker.getPosition().longitude);
        editor.apply();
    }



}