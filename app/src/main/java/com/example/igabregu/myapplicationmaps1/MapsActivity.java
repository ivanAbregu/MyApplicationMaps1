package com.example.igabregu.myapplicationmaps1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng l1 = new LatLng(-34.587482, -58.43772);
        mMap.addMarker(new MarkerOptions().position(l1).title("titulo1"));
        LatLng l2 = new LatLng(-34.58211, -58.433987);
        mMap.addMarker(new MarkerOptions().position(l2).title("titulo2"));
         mMap.addCircle(new CircleOptions()
                         .center(l1)
                         .radius(100)
                         .fillColor(ContextCompat.getColor(this, R.color.ORANGE))
         );
        mMap.addCircle(new CircleOptions()
                        .center(l2)
                        .radius(100)
                        .fillColor(ContextCompat.getColor(this, R.color.ORANGE))
        );

          LatLngBounds AUSTRALIA = new LatLngBounds(l1, l2);
        //// TODO: 25/04/2016 buscar metodo que posicione la camara entre varios puntos.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AUSTRALIA.getCenter(), 15));

    }
}
