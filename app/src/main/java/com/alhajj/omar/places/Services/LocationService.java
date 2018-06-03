package com.alhajj.omar.places.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.alhajj.omar.places.Keys;
import com.alhajj.omar.places.R;
import com.alhajj.omar.places.Utility.Utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Service inspired by:
 * https://stackoverflow.com/questions/14478179/background-service-with-location-listener-in-android
 * https://github.com/codepath/android_guides/issues/220
 * and previous experience
 */
public class LocationService extends Service {

    private static final String TAG = "LocationService";

    // LocationManager ref: https://developer.android.com/reference/android/location/LocationManager#requestLocationUpdates(java.lang.String,%20long,%20float,%20android.location.LocationListener)
    private static final long minTime = 10 * 1000; // 10 sec
    private static final float minDistance = 10; // 10 m

    public class LocationBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    private final IBinder binder = new LocationBinder();

    LocationListener locationListener;
    LocationManager locationManager;
    Location lastKnownLocation;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void onCreate() {
        super.onCreate();
        locationListener = new LocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        saveLastKnowLocation();

    }

    private void saveLastKnowLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager != null) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                Utility utility = new Utility(getApplicationContext());
                utility.saveStringToSharedPrefs(Double.toString(lastKnownLocation.getLatitude()), "latitude");
                utility.saveStringToSharedPrefs(Double.toString(lastKnownLocation.getLongitude()), "longitude");
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initializeLocationManager();
        }
        return START_STICKY;
    }


    @SuppressLint("MissingPermission")
    public void initializeLocationManager() {
        if (locationManager != null) {
            Log.d(TAG, "Initialized with: " + locationManager.getProviders(true));
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
        } else {
            Log.d("LocationService", "LocationManager is null");
        }
    }

    // https://developer.android.com/training/notify-user/build-notification
    private void createNotification() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = simpleDateFormat.format(Calendar.getInstance().getTime());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(Keys.CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), Keys.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.location_updated, currentTime.toString()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        startForeground(Keys.NOTIFICATION_ID, notification);
    }

    private class LocationListener implements android.location.LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            Intent intent = new Intent("location-data-event");
            intent.putExtra("Latitude", location.getLatitude());
            intent.putExtra("Longitude", location.getLongitude());
            createNotification();
            Toast.makeText(getApplicationContext(), getString(R.string.location_updated_now), Toast.LENGTH_SHORT).show();
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d(TAG, "onStatusChanged: " + s);

        }

        @Override
        public void onProviderEnabled(String s) {
            Log.d(TAG, "onProviderEnabled");

        }

        @Override
        public void onProviderDisabled(String s) {
            Log.d(TAG, "onProviderDisabled");

        }
    }
}