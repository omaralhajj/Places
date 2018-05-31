package com.alhajj.omar.places;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.alhajj.omar.places.Adaptors.PlaceAdapter;
import com.alhajj.omar.places.Interfaces.OnGetPlaceDataListener;
import com.alhajj.omar.places.Models.Place;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    PlaceAdapter placeAdapter;
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
            placeAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed(Place place) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        printHashCodeToConsole();

        // Reference: https://firebase.google.com/docs/firestore/query-data/get-data
        database.collection("Places").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult() ) {
                        Place place = document.toObject(Place.class);
                        listener.onSuccess(place);
                    }
                }
            }
        });

        placeAdapter = new PlaceAdapter(this, placeList);
        ListView placeListView = findViewById(R.id.placeListView);
        placeListView.setAdapter(placeAdapter);
    }

    void printHashCodeToConsole()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.alhajj.omar.places",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
        }
    }
}
