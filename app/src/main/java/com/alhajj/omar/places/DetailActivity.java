package com.alhajj.omar.places;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alhajj.omar.places.Models.Place;
import com.alhajj.omar.places.Services.LocationService;

public class DetailActivity extends AppCompatActivity {

    private Place place;
    private double latitude = 56.171482; // Default in case permission is not granted
    private double longitude = 10.191075; // Same

    private ServiceConnection locationServiceConnection;
    private LocationService locationService;

    TextView distanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        place = (Place) intent.getSerializableExtra("Place");

        //Latitudes are longitudes and longitudes are latitudes for some reason?
        longitude = intent.getDoubleExtra("Latitude", 0);
        latitude = intent.getDoubleExtra("Longitude", 0);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("location-data-event"));

        setupConnection();
        bindToService();

        TextView placeNameTextView = findViewById(R.id.detailTextViewName);
        placeNameTextView.setText(place.getName());

        TextView coordinatesTextView = findViewById(R.id.detailTextViewCoordinates);
        coordinatesTextView.setText(getString(R.string.coordinates, place.getLatitude(), place.getLongitude()));

        distanceTextView = findViewById(R.id.detailTextViewDistance);
        String distance = calculateDistance(latitude, longitude, place.getLatitude(), place.getLongitude());
        distanceTextView.setText(distance);

        TextView ratingTextView = findViewById(R.id.textViewRating);
        String formattedRating = String.format("%.1f", place.getRating());
        ratingTextView.setText(getString(R.string.rating, formattedRating));

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setRating(place.getRating());
        ratingBar.setEnabled(false);

        TextView numberOfRatings = findViewById(R.id.textViewRatings);
        numberOfRatings.setText(getString(R.string.no_of_ratings, place.getNumberOfRatings()));

        TextView urlTextView = findViewById(R.id.textViewUrl);
        urlTextView.setText(place.getUrl());

        TextView addressTextView = findViewById(R.id.textViewAddress);
        addressTextView.setText(place.getAddress());

        Button viewInMaps = findViewById(R.id.viewInMapButton);
        viewInMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("Latitude", place.getLatitude());
                intent.putExtra("Longitude", place.getLongitude());
                startActivity(intent);
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Idk where i messed up, but this is right somehow
            longitude = intent.getDoubleExtra("Latitude", 0);
            latitude = intent.getDoubleExtra("Longitude", 0);

            String updatedDistance = calculateDistance(latitude, longitude, place.getLatitude(), place.getLongitude());
            distanceTextView.clearComposingText();
            distanceTextView.setText(updatedDistance);
        }
    };

    private void setupConnection(){
        locationServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                locationService = ((LocationService.LocationBinder)service).getService();
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

    private String calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        // Reference: https://developer.android.com/reference/android/location/Location.html#distanceBetween(double,%20double,%20double,%20double,%20float[])
        float[] result = new float[10];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, result);
        Log.d("Khara detail", result[0]/1000 + "");
        Log.d("Khara detail", result.length + "");
        String distance = String.format("%.1f", (result[0]/1000)); // Round to 1 decimal and get distance in km
        return this.getString(R.string.distance, distance);
    }

}
