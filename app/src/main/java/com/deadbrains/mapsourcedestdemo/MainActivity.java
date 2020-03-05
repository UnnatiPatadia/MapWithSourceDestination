package com.deadbrains.mapsourcedestdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    EditText edSource,edDestination;
    Button btnGo;
    private GoogleMap mMap;
    ArrayList markerPoints= new ArrayList();
    double originLat,originLang,destLat,destLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        edSource = findViewById( R.id.edSource );
        edDestination = findViewById( R.id.edDestination );
        btnGo = findViewById( R.id.btnGo );

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnGo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (originLat == 0.0 || originLang == 0.0 || destLat == 0.0 || destLang == 0.0){
                    Toast.makeText( MainActivity.this ,"Select Sorce and Destination" ,Toast.LENGTH_SHORT ).show();
                } else {
                    Intent intent = new Intent( MainActivity.this,ListActivity.class );
                    intent.putExtra( "originLat", originLat );
                    intent.putExtra( "originLang", originLang );
                    intent.putExtra( "destLat", destLat );
                    intent.putExtra( "destLang", destLang );
                    startActivity( intent );
                }
            }
        } );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        LatLng sydney = new LatLng(-33, 151);
//        LatLng sydney = new LatLng(22, 72);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(sydney, 16));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    edDestination.setText( "" );
                    destLang = 0.0;
                    destLat = 0.0;
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN) );
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = (LatLng) markerPoints.get(0);
                    LatLng dest = (LatLng) markerPoints.get(1);

                    originLat = origin.latitude;
                    originLang = origin.longitude;

                    destLat = dest.latitude;
                    destLang = dest.longitude;

                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                    try {
                        List <Address> sourceAddresses = geocoder.getFromLocation( originLat ,originLang ,1 );
                        Address sourceObj = sourceAddresses.get( 0 );
                        String sourceAdd = sourceObj.getAddressLine( 0 );

                        List <Address> destAddresses = geocoder.getFromLocation( destLat ,destLang ,1 );
                        Address destObj = destAddresses.get( 0 );
                        String destAdd = destObj.getAddressLine( 0 );

                        edSource.setText( sourceAdd );
                        edDestination.setText( destAdd );

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
