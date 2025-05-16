package com.example.a22100178_geochat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceManager {
    private final Activity activity;
    private final GeofencingClient geofencingClient;
    private final GeofenceHelper geofenceHelper;

    private final float GEOFENCE_RADIUS = 20f;
    private final String GEOFENCE_ID = "User_Geofence";

    public GeofenceManager(Activity activity, GeofencingClient client, GeofenceHelper helper) {
        this.activity = activity;
        this.geofencingClient = client;
        this.geofenceHelper = helper;
    }

    public void addGeofenceAtLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, GEOFENCE_RADIUS,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

        GeofencingRequest request = geofenceHelper.getGeofencingRequest(geofence);

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("GEOFENCE", "Sin permisos de ubicación");
            return;
        }

        geofencingClient.addGeofences(request, geofenceHelper.getPendingIntent())
                .addOnSuccessListener(unused -> Toast.makeText(activity, "Geovalla creada correctamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(activity, "Error: La Geovalla no se pudo crear", Toast.LENGTH_SHORT).show());
    }
}
