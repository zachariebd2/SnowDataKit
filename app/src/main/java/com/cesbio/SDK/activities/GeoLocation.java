/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 *               Zacharie BARROU DUMONT, CESBIO 2020
 *
 *
 *
 *
 * Activity page for getting user location
 */

package com.cesbio.SDK.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.cesbio.SDK.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


public class GeoLocation extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int DEFAULT_UPDATE_INTERVAL = 5000;
    private static final int FASTEST_UPDATE_INTERVAL = 2500;
    private static final int PERMISSION_FINE_LOCATION = 99;
    private TextView textLatLong, textAccAlt;
    private GoogleMap map;
    private float x1;
    private double latitude, longitude;
    private int accuracy, altitude;
    private int saved_id_FSC;
    private String username;
    private int id_user;
    FusedLocationProviderClient fusedLocationProviderClient; //Google API for location services
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private Location highestAccuracyReading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocation);
        textLatLong = findViewById(R.id.lat_lon);
        textAccAlt = findViewById(R.id.accuracy);

        //setting location priorities and update interval
        locationRequest = new LocationRequest();
        locationRequest.setInterval(DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //event triggered whenever location interval is met
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValues(locationResult.getLastLocation());
            }
        };


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            int re_restoredAccuracy = extras.getInt("re_savedAccuracy");
            int re_restoredAltitude = extras.getInt("re_savedAltitude");
            double re_restoredLatitude = extras.getDouble("re_savedLatitude");
            double re_restoredLongitude = extras.getDouble("re_savedLongitude");
            saved_id_FSC = extras.getInt("id_input_fsc");
            id_user = extras.getInt("id_user");
            username = extras.getString("username");


            textAccAlt.setText(getString(R.string.accuracy) + re_restoredAccuracy + "m");


            textLatLong.setText(getFormattedLocationInDegree(re_restoredLatitude, re_restoredLongitude) + " | Altitude : " + re_restoredAltitude + "m");
            getCurrentLocation();
        }

        TextView tv_loggeEnTantQue = findViewById(R.id.tv_logged_as);
        tv_loggeEnTantQue.setText(getString(R.string.log_msg) + username);

        // Fonction : Check des permissions au clic du bouton
        findViewById(R.id.btn_get_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        // Récupération des permissions nécessaires (déclarées dans le fichier Manifest)
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            GeoLocation.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                } else {
                    highestAccuracyReading = null;
                    getCurrentLocation();
                }
            }
        });
    }

    // Fonction : Éxécuter le code de la fonction getCurrentLocation si les permissions ont été check, afficher un message Toast le cas contraire
/*    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) // Si le request code est le même que celui du check, et que le résultat du check est supérieur à 0
            getCurrentLocation();
        else
            Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show();
    }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show();
                    //finish();
                }
        }
    }

    private void getCurrentLocation() {

        // Check des permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GeoLocation.this);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(GeoLocation.this, new OnSuccessListener<Location>(){
                @Override
                public void onSuccess(Location location){

                    updateUIValues(location);


                }
            });
        }
        else{
            //permission not granted yet
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);

            }

        }

    }

    private void updateUIValues(Location location) {




        if (location != null && (highestAccuracyReading == null || !highestAccuracyReading.hasAccuracy()
                || (location.hasAccuracy() && highestAccuracyReading.hasAccuracy() && location.getAccuracy() < highestAccuracyReading.getAccuracy()))) {

            highestAccuracyReading = location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            accuracy = (int) location.getAccuracy();
            altitude = (int) location.getAltitude();

            // Stockage des données dans les TextView
            textLatLong.setText(getFormattedLocationInDegree(latitude, longitude) + " | Altitude : " + altitude + "m");
            textAccAlt.setText(getString(R.string.accuracy) + accuracy + "m");
            if (accuracy > 5) {
                textAccAlt.setTextColor(Color.RED);
            } else if (accuracy <= 5) {
                textAccAlt.setTextColor(Color.GREEN);
            }

            // Affichage de la position sur la map
            LatLng pos = new LatLng(latitude, longitude);
            map.clear();
            map.addMarker(new MarkerOptions().position(pos).title("Position"));
            map.moveCamera(CameraUpdateFactory.newLatLng(pos));
            map.setMinZoomPreference(15);
        }
    }

    // Conversion de la latitude et de la longitude en format degrés
    public static String getFormattedLocationInDegree(double latitude, double longitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;

            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String latDegree = latDegrees >= 0 ? "N" : "S";
            String lonDegrees = longDegrees >= 0 ? "E" : "W";

            return "Lat : " + Math.abs(latDegrees) + "°" + latMinutes + "'" + latSeconds
                    + "\"" + latDegree + " " + "| Lon : " + Math.abs(longDegrees) + "°" + longMinutes
                    + "'" + longSeconds + "\"" + lonDegrees;
        } catch (Exception e) {
            return "" + String.format("%8.5f", latitude) + "  "
                    + String.format("%8.5f", longitude);
        }
    }

    // Chargement de la map, et style
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle)); // Style contenu dans le dossier raw > mapstyle en format JSON

            if (!success) Log.e("Localisation", "Style JSON file parsing failed.");
        } catch (Resources.NotFoundException e) {
            Log.e("Localisation", "Style file couldn't be found, Error : ", e);
        }
    }

    // Méthode pour Swipe vers la suite du formulaire
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                // Pour le swipe
                float x2 = touchEvent.getX();

                // Swipe vers la gauche
                if (x1 > x2) {
                    // Envoi des données vers l'activité NeigePourcentage
                    Intent i = new Intent(this, Form.class);
                    i.putExtra("savedLatitude", latitude);
                    i.putExtra("savedLongitude", longitude);
                    i.putExtra("savedAccuracy", accuracy);
                    i.putExtra("savedAltitude", altitude);
                    i.putExtra("saved_id_fsc", saved_id_FSC);
                    i.putExtra("id_user", id_user);
                    i.putExtra("username", username);
                    startActivity(i);
                    overridePendingTransition(R.xml.activity_in, R.xml.activity_out);
                    finish();
                } else {
                    Intent i = new Intent(this, Home.class);
                    startActivity(i);
                    overridePendingTransition(R.xml.activity_back_in, R.xml.activity_back_out);
                    finish();
                }
                break;
        }
        return false;
    }
}