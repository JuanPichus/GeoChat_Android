package com.example.a22100178_geochat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationManagerHelper {

    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final LocationRequest locationRequest;
    private final Activity activity;

    private final LocationCallback locationCallback;

    public interface OnLocationReady {
        void onLocationResult(Location location);
    }

    public LocationManagerHelper(Activity activity, LocationCallback locationCallback) {
        this.activity = activity;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        this.locationCallback = locationCallback;

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000 * 30);
        locationRequest.setFastestInterval(1000 * 5);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public void getLastKnownLocation(OnLocationReady callback) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                callback.onLocationResult(location);
                            } else {
                                Log.w("LOCATION", "No se pude obtener la última ubicación");
                            }
                        }
                    });
        } else {
            requestFineLocationPermission();
        }
    }

    public void getCurrentLocation(OnLocationReady callback) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    null
            ).addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        callback.onLocationResult(location);
                    } else {
                        Log.w("LOCATION", "No se pudo obtener la ubicación actual");
                    }
                }
            });
        } else {
            requestFineLocationPermission();
        }
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            requestFineLocationPermission();
        }
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void setHighAccuracy(boolean highAccuracy) {
        locationRequest.setPriority(highAccuracy ?
                LocationRequest.PRIORITY_HIGH_ACCURACY :
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void requestFineLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        }
    }

}
