package com.example.a22100178_geochat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import POJOs.Usuario;

public class MainActivity extends AppCompatActivity {

    private LocationManagerHelper locationHelper;
    private GeofenceManager geofenceManager;
    private GeofenceHelper geofenceHelper;

    private static final int PERMISSIONS_FINE_LOCATION = 99;

    TextView tv_lat, tv_lon, tv_alt, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, tv_wp;
    Switch sw_updates, sw_gps;
    Button btn_nw, btn_sw, btn_sm;

    //current location
    Location currentLocation;
    //list  of saved locations
    List<Location> savedLocations;

    LocationCallback locationCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                updateUIValues(locationResult.getLastLocation());

            }
        };

        //Inicializando helper de geofences
        geofenceHelper = new GeofenceHelper(this);
        geofenceManager = new GeofenceManager(this, LocationServices.getGeofencingClient(this), geofenceHelper);

        //Inicializando helper de ubicación
        locationHelper = new LocationManagerHelper(this, locationCallBack);

        tv_lat = findViewById(R.id.tv_latitud);
        tv_lon = findViewById(R.id.tv_longitud);
        tv_alt = findViewById(R.id.tv_altitud);
        tv_accuracy = findViewById(R.id.tv_exactitud);
        tv_speed = findViewById(R.id.tv_velocidad);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_direccion);
        tv_wp = findViewById(R.id.tv_waypoints);
        sw_updates = findViewById(R.id.sw_updates);
        sw_gps = findViewById(R.id.sw_gps);
        btn_nw = findViewById(R.id.btn_newWayPoint);
        btn_sw = findViewById(R.id.btn_showWayPoint);
        btn_sm = findViewById(R.id.btn_showMap);



        btn_nw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the gps location
                //add the new location to the global list;

                MyApplication myApplication = (MyApplication)getApplicationContext();
                savedLocations = myApplication.getMyLocations();
                savedLocations.add(currentLocation);
            }
        });

        btn_sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ShowSavedLocastionsList.class);
                startActivity(i);
            }
        });

        btn_sm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_gps.isChecked()) {
                    locationHelper.setHighAccuracy(true); //usa gps
                    tv_sensor.setText("Using GPS sensors");
                }
                else {
                    locationHelper.setHighAccuracy(false); //usa wifi y torres
                    tv_sensor.setText("Using Towers + Wifi");
                }
            }
        });

        sw_updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_updates.isChecked()) {
                    locationHelper.startLocationUpdates();
                }
                else {
                    locationHelper.stopLocationUpdates();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1001);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //updateGPS();
    } //final onCreate method


    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            sw_updates.setChecked(false);
            locationHelper.stopLocationUpdates();
            updateGPS(); // Llamada segura
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "This app requiere permissions!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateGPS() {
        locationHelper.getLastKnownLocation(location -> {
            currentLocation = location;
            updateUIValues(location);
        });
    }

    private void updateUIValues(Location location) {

        if (location == null) {
            tv_lat.setText("No disponible");
            tv_lon.setText("No disponible");
            tv_accuracy.setText("No disponible");
            tv_alt.setText("No disponible");
            tv_speed.setText("No disponible");
            tv_address.setText("No disponible");
            return;
        }
        else {
            addGeofenceAtUserLocation(location);
            tv_lat.setText(String.valueOf(location.getLatitude()));
            tv_lon.setText(String.valueOf(location.getLongitude()));
            tv_accuracy.setText(String.valueOf(location.getAccuracy()));

            if (location.hasAltitude()) {
                tv_alt.setText(String.valueOf(location.getAltitude()));
            } else {
                tv_alt.setText("No disponible!");
            }

            if (location.hasSpeed()) {
                tv_speed.setText(String.valueOf(location.getSpeed()));
            } else {
                tv_speed.setText("No disponible!");
            }

            Geocoder geocoder = new Geocoder(MainActivity.this);

            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                tv_address.setText(addresses.get(0).getAddressLine(0));
            } catch (Exception e) {
                tv_address.setText("Unable to get street address");
            }

            MyApplication myApplication = (MyApplication) getApplicationContext();
            savedLocations = myApplication.getMyLocations();

        }
        //show the number of waypoints saved.
        tv_wp.setText(Integer.toString(savedLocations.size()));
    }

    private void addGeofenceAtUserLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        geofenceManager.addGeofenceAtLocation(latLng);
    }
}