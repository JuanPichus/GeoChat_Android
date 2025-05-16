package com.example.a22100178_geochat;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceHelper extends ContextWrapper {
    public GeofenceHelper(Context base) {
        super(base);
    }

    public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitionTypes) {
        return new Geofence.Builder().setCircularRegion(latLng.latitude, latLng.longitude, radius).setRequestId(ID).setTransitionTypes(transitionTypes).setLoiteringDelay(10000).setExpirationDuration(Geofence.NEVER_EXPIRE).build();
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder().setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).addGeofence(geofence).build();
    }

    public PendingIntent getPendingIntent() {
        Intent i = new Intent(this, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(
                this,
                2607,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );
    }
}
