package com.alhajj.omar.places;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.alhajj.omar.places.Adaptors.PlaceAdapter;
import com.alhajj.omar.places.Interfaces.OnGetPlaceDataListener;
import com.alhajj.omar.places.Interfaces.PermissionListener;
import com.alhajj.omar.places.Models.Place;
import com.alhajj.omar.places.Services.LocationService;
import com.alhajj.omar.places.Utility.PermissionUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";

    private Double latitude;
    private Double longitude;

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    private ServiceConnection locationServiceConnection;
    private LocationService locationService;

    PlaceAdapter placeAdapter;
    ArrayList<Place> placeList = new ArrayList<>();

    // Checking permissions: https://developer.android.com/training/permissions/requesting#java
    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionAsk(String permission) {
            String[] permissions = new String[]{permission};
            ActivityCompat.requestPermissions(MainActivity.this, permissions, Keys.REQUEST_FINE_LOCATION);
        }

        @Override
        public void onPermissionPreviouslyDenied(final String permission) {
            showAsyncAlertDialog(permission);
        }

        @Override
        public void onPermissionDisabled() {
            Log.d(TAG, "Asking for permission disabled");
        }

        @Override
        public void onPermissionGranted() {
            Log.d(TAG, "Permission granted");
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            latitude = intent.getDoubleExtra("Latitude", 0);
            longitude = intent.getDoubleExtra("Longitude", 0);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Keys.REQUEST_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationService.initializeLocationManager();
            }
        }
    }


    private void setupConnection(){
        locationServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBind) {
                locationService = ((LocationService.LocationBinder) iBind).getService();
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

    /**
     * Listeners:
     */

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
        startService(new Intent(this, LocationService.class));

        PermissionUtility permissionUtility = new PermissionUtility(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionUtility.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, permissionListener);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("location-data-event"));

        setupConnection();
        bindToService();


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
            }
        });

        placeAdapter = new PlaceAdapter(this, placeList);
        ListView placeListView = findViewById(R.id.placeListView);
        placeListView.setAdapter(placeAdapter);
        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Place place = (Place) placeAdapter.getItem(i);
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("Place", place);
                Log.d("Khara", "Lat: " + latitude + " Lon: " + longitude);
                startActivity(intent);
            }
        });

        Button viewInMaps = findViewById(R.id.viewInMapButton);
        viewInMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    //Reference: previous assignments and exercises
    public void showAsyncAlertDialog(final String permission) {
        class showDialogAsync extends AsyncTask<Void, Void, String> {

            private AlertDialog alertDialog;

            @Override
            protected String doInBackground(Void... voids) {
                return null;
            }

            protected void onPreExecute() {
                super.onPreExecute();
                alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                alertDialog.setTitle(getString(R.string.permission_needed));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setMessage(getString(R.string.permission_rationale));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String[] permissions = new String[]{permission};
                                ActivityCompat.requestPermissions(MainActivity.this, permissions, Keys.REQUEST_FINE_LOCATION);
                            }
                        });

                alertDialog.show();

            }

        }
        new showDialogAsync().execute();

    }

}

