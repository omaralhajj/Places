package com.alhajj.omar.places;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alhajj.omar.places.Models.Place;
import com.alhajj.omar.places.Services.LocationService;
import com.alhajj.omar.places.Utility.Utility;

public class DetailActivity extends AppCompatActivity {

    private Place place;
    Utility utility;
    private double latitude;

    private ServiceConnection locationServiceConnection;
    private LocationService locationService;
    private double longitude;

    TextView distanceTextView;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Idk where i messed up, but this is right somehow
                longitude = intent.getDoubleExtra("Latitude", 0);
                latitude = intent.getDoubleExtra("Longitude", 0);
                String updatedDistance = utility.calculateDistance(latitude, longitude, place.getLatitude(), place.getLongitude());
                distanceTextView.setText(getString(R.string.distance, updatedDistance));

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        place = (Place) intent.getSerializableExtra("Place");


        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("location-data-event"));

        setupConnection();
        bindToService();

        TextView placeNameTextView = findViewById(R.id.detailTextViewName);
        placeNameTextView.setText(place.getName());

        TextView coordinatesTextView = findViewById(R.id.detailTextViewCoordinates);
        coordinatesTextView.setText(getString(R.string.coordinates, place.getLatitude(), place.getLongitude()));

        distanceTextView = findViewById(R.id.detailTextViewDistance);
        utility = new Utility(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Latitudes are longitudes and longitudes are latitudes for some reason (y)
            longitude = Double.valueOf(utility.getStringFromSharedPrefs("latitude"));
            latitude = Double.valueOf(utility.getStringFromSharedPrefs("longitude"));

            String distance = utility.calculateDistance(latitude, longitude, place.getLatitude(), place.getLongitude());
            distanceTextView.setText(getString(R.string.distance, distance));
        }

        TextView ratingTextView = findViewById(R.id.textViewRating);
        String formattedRating = String.format("%.1f", place.getRating());
        ratingTextView.setText(getString(R.string.rating, formattedRating));

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setRating(place.getRating());
        ratingBar.setEnabled(false);

        TextView numberOfRatings = findViewById(R.id.textViewRatings);
        numberOfRatings.setText(getString(R.string.no_of_ratings, place.getNumberOfRatings()));

        TextView descriptionTextView = findViewById(R.id.detailTextViewDesc);
        descriptionTextView.setText(place.getDescription());

        TextView urlTextView = findViewById(R.id.textViewUrl);
        urlTextView.setText(place.getUrl());

        TextView addressTextView = findViewById(R.id.textViewAddress);
        addressTextView.setText(place.getAddress());
    }

    private void setupConnection(){
        locationServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                locationService = ((LocationService.LocationBinder)service).getService();
                locationService.initializeLocationManager();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                locationService = null;
            }
        };
    }

    private void bindToService() {
        bindService(new Intent(this, LocationService.class), locationServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(locationServiceConnection);
    }
}
