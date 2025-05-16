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

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private final float GEOFENCE_RADIUS = 20;
    private final String GEOFENCE_ID = "User_Geofence";
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    TextView tv_lat, tv_lon, tv_alt, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, tv_wp;
    Switch sw_updates, sw_gps;
    Button btn_nw, btn_sw, btn_sm;

    //variables para recordar si estamos rastreando al usuario o no.
    boolean updateOn = false;

    //current location
    Location currentLocation;

    //list  of saved locations
    List<Location> savedLocations;

    //Archivo de configuración para todas las configuraciones relacionadas con la clase FusedLocationProviderClient
    LocationRequest locationRequest;

    LocationCallback locationCallBack;

    //Clase para la mayoria de las funciones de la app, proporcionada por la API de google
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

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

        locationRequest = new LocationRequest();

        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                updateUIValues(locationResult.getLastLocation());

            }
        };

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
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                }
                else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + Wifi");
                }
            }
        });

        sw_updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_updates.isChecked()) {
                    startLocationUpdates();
                }
                else {
                    stopLocationUpdates();
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

        updateGPS();
    } //final onCreate method

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            sw_updates.setChecked(false);
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {

        tv_updates.setText("Location is NOT beign tracked");
        tv_lat.setText("Not tracking location");
        tv_lon.setText("Not tracking location");
        tv_speed.setText("Not tracking location");
        tv_address.setText("Not tracking location");
        tv_accuracy.setText("Not tracking location");
        tv_alt.setText("Not tracking location");
        tv_sensor.setText("Not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {
        tv_updates.setText("Location is being tracked");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
            updateGPS();
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                    currentLocation = location;
                }
            });
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }


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
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, GEOFENCE_RADIUS, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("GEOFENCE", "No hay permisos de ubicación");
            return;
        }

        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(unused -> Log.i("GEOFENCE", "Geovalla añadida correctamente")).addOnFailureListener(e -> Log.e("GEOFENCE", "Fallo al añadir geovalla: " + e.getMessage()));
    }
}