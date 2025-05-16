package com.example.a22100178_geochat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.e("GEOFENCE", "Error en Geofence: " + geofencingEvent.getErrorCode());
            return;
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.i("GEOFENCE", "ENTRASTE a la geovalla");
            //Iniciar chat
        } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i("GEOFENCE", "SALISTE de la geovalla");
        }
    }
}
