package com.example.a22100178_geochat;

import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.a22100178_geochat.databinding.ActivityMapsBinding;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    List<Location>savedLocations;

    ImageButton btn_updateLoc;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btn_updateLoc = findViewById(R.id.btn_updateLoc);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MyApplication myApplication = (MyApplication)getApplicationContext();
        savedLocations = myApplication.getMyLocations();
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

        LatLng defLoc = new LatLng(-34, 151);

        LatLng lastLocationPlaced = defLoc;

        if (savedLocations != null) {
            for (Location location : savedLocations) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Lat: " + location.getLatitude() + "Lon: " + location.getLongitude());
                mMap.addMarker(markerOptions);
                lastLocationPlaced = latLng;
            }

            CircleOptions circleOptions = new CircleOptions().center(new LatLng(savedLocations.get(savedLocations.size() - 1).getLatitude(), savedLocations.get(savedLocations.size() - 1).getLongitude())).radius(20).fillColor(0x55000000).strokeWidth(5);
            mMap.addCircle(circleOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocationPlaced, 18.0f));

        }
        else {
            Toast.makeText(this, "No se han registrado ubicaciones", Toast.LENGTH_SHORT).show();
        }
        // Add a marker in Sydney and move the camera

//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}