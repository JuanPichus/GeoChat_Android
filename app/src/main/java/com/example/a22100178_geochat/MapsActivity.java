package com.example.a22100178_geochat;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.a22100178_geochat.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private LocationManagerHelper locationManagerHelper;
    private GeofenceManager geofenceManager;
    private GeofenceHelper geofenceHelper;

    List<Circle> circles = new ArrayList<>();
    List<Location>savedLocations;
    Location currentLocation;
    Address address;

    LocationCallback locationCallback;

    ImageButton btn_updateLoc;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btn_updateLoc = findViewById(R.id.btn_updateLoc);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };

        //Inizialiando helper de geofences
        geofenceHelper = new GeofenceHelper(this);
        geofenceManager = new GeofenceManager(this, LocationServices.getGeofencingClient(this), geofenceHelper);

        //Inizializando helper de ubicación
        locationManagerHelper = new LocationManagerHelper(this, locationCallback);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1001);
        }

        updateGps();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_updateLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateLocation();
            }
        });

//        MyApplication myApplication = (MyApplication)getApplicationContext();
//        savedLocations = myApplication.getMyLocations();
    } //final onCreate

    private void onClickUpdateLocation() {
        updateGps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManagerHelper.setHighAccuracy(false);
            locationManagerHelper.stopLocationUpdates();
            updateGps();
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
                    updateGps();
                }
                else {
                    Toast.makeText(this, "This app requiere permissions!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defLoc = new LatLng(20, -105);

        LatLng lastLocationPlaced = defLoc;

        try {
//            for (Location location : savedLocations) {
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//                markerOptions.title("Lat: " + location.getLatitude() + "Lon: " + location.getLongitude());
//                mMap.addMarker(markerOptions);
//                lastLocationPlaced = latLng;
//            }
        } catch (Exception e) {
            Log.e("MAPA", "Error al cargar direcciones: " + e.getMessage());
            Toast.makeText(geofenceHelper, "No hay ubicación", Toast.LENGTH_SHORT).show();
        }
        // Add a marker in Sydney and move the camera

//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void updateGps() {
        locationManagerHelper.getCurrentLocation(location -> {
            if (location != null) {
                currentLocation = location;
                Log.d("LOCATION", "Ubicación actual: " + location.getLatitude() + ", " + location.getLongitude());
                geofenceManager.addGeofenceAtLocation(currentLocation);
                Log.d("GEOVALLA", "Geovalla creada correctamente");
                Toast.makeText(geofenceHelper, "Ubicación obtenida correctamente", Toast.LENGTH_SHORT).show();

                if (!circles.isEmpty()) {
                    clearAllCircles();
                }

                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                drawGeofence(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Lat: " + currentLocation.getLatitude() + "Lon: " + currentLocation.getLongitude());
                mMap.addMarker(markerOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
            } else {
                Log.w("LOCATION", "No se pudo obtener la ubicacion actual");
            }
        });
    }

    private void clearAllCircles() {
        for (Circle circle : circles) {
            circle.remove();
        }
        circles.clear();
    }

    private void drawGeofence(LatLng latLng) {
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(20)
                .strokeColor(Color.BLACK)
                .fillColor(0x1A00FF00));
        circles.add(circle);
    }


}