package com.alhajj.omar.places;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.alhajj.omar.places.Interfaces.OnGetPlaceDataListener;
import com.alhajj.omar.places.Models.Place;
import com.alhajj.omar.places.Utility.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Utility utility;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    ArrayList<Place> placeList = new ArrayList<>();
    // Reference: //https://stackoverflow.com/questions/46675057/trying-to-use-data-outside-the-ondatachange-in-firebase
    // Callback interface to get place object(s) from async call to firebase
    OnGetPlaceDataListener listener = new OnGetPlaceDataListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(Place place) {
            placeList.add(place);
        }

        @Override
        public void onFailed(Place place) {
        }
    };
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        utility = new Utility(this);

        // Reference: https://firebase.google.com/docs/firestore/query-data/get-data
        database.collection("Places").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Place place = document.toObject(Place.class);
                        listener.onSuccess(place);
                    }
                }
                if (task.isComplete()) {
                    onMapReady(mMap);
                }
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        float ZOOM_CONSTANT = 12;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            longitude = Double.valueOf(utility.getStringFromSharedPrefs("latitude"));
            latitude = Double.valueOf(utility.getStringFromSharedPrefs("longitude"));

            LatLng userPosition = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions()
                    .icon(utility.bitmapDescriptorFromVector(R.drawable.ic_person_pin_circle_48dp))
                    .position(userPosition)
                    .title(getString(R.string.you_are_here)))
                    .setTag("user"); //To differentiate between places and user marker
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition, ZOOM_CONSTANT));
        } else {
            Double defaultLatitude = 56.153837;
            Double defaultLongitude = 10.199703;
            LatLng defaultPosition = new LatLng(defaultLatitude, defaultLongitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, ZOOM_CONSTANT));
        }


        for (int i = 0; i < placeList.size(); i++) {
            Place place = placeList.get(i);
            LatLng location = new LatLng(place.getLatitude(), place.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .icon(utility.bitmapDescriptorFromVector(R.drawable.ic_place_48dp))
                    .title(place.getName())
                    .snippet(getString(R.string.click_for_more))
                    .position(location))
                    .setTag(i); //its index in placeList
        }

        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag() == "user"){
                    return false;
                } else {
                    Place place = placeList.get((int)marker.getTag());
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); //To not open duplicate activities
                    intent.putExtra("Place", place);
                    startActivity(intent);
                    return false;
                }
            }
        });*/

        //Click on marker to go to details view instead
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getTag() != "user") {
                    Place place = placeList.get((int) marker.getTag());
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra("Place", place);
                    startActivity(intent);
                }
            }
        });
    }

}
