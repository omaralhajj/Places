package com.alhajj.omar.places.Services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.alhajj.omar.places.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocationService extends Service {

    // LocationManager ref: https://developer.android.com/reference/android/location/LocationManager#requestLocationUpdates(java.lang.String,%20long,%20float,%20android.location.LocationListener)
    private static final long minTime = 60 * 1000; // 1 min
    private static final float minDistance = 100; // 100 m
    private LocationManager locationManager  = null;

    private class LocationListener implements android.location.LocationListener {
        Location lastLocation;

        /*public LocationListener(String provider) {
            lastLocation = new Location(provider);
        }*/

        @Override
        public void onLocationChanged(Location location) {
            lastLocation.set(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    private final IBinder binder = new LocationBinder();

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationListener locationListener = new LocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    public class LocationBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    /*private void updateLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please turn on location tracking", Toast.LENGTH_SHORT).show();
                return;
            } else {
                locationManager.requestSingleUpdate(new Criteria(), locationListener, null);
            }
        } else {
            locationManager.requestSingleUpdate(new Criteria(), locationListener, null);
        }
    }*/
}