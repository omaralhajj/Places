package com.alhajj.omar.places;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.alhajj.omar.places.Models.Place;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        Place place = (Place) intent.getSerializableExtra("Place");

        TextView placeNameTextView = findViewById(R.id.detailTextViewName);
        placeNameTextView.setText(place.getName());

        TextView coordinatesTextView = findViewById(R.id.detailTextViewCoordinates);
        coordinatesTextView.setText(getString(R.string.coordinates, place.getLatitude(), place.getLongitude()));

        TextView distanceTextView = findViewById(R.id.detailTextViewDistance);
        String distance = calculateDistance(56.160477, 10.136271, place.getLatitude(), place.getLongitude());
        distanceTextView.setText(distance);
    }

    private String calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        // Reference: https://developer.android.com/reference/android/location/Location.html#distanceBetween(double,%20double,%20double,%20double,%20float[])
        float[] result = new float[10];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, result);

        String distance = String.format("%.1f", (result[0]/1000)); // Round to 1 decimal and get distance in km
        return this.getString(R.string.distance, distance);
    }

}
