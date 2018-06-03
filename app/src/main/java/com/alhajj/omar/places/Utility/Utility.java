package com.alhajj.omar.places.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class Utility {
    private Context context;

    public Utility(Context context) {
        this.context = context;
    }

    public void saveStringToSharedPrefs(String stringToSave, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("STRING_STORE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, stringToSave);
        editor.apply();
    }

    public String getStringFromSharedPrefs(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("STRING_STORE", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    //https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
    public BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public String calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        // Reference: https://developer.android.com/reference/android/location/Location.html#distanceBetween(double,%20double,%20double,%20double,%20float[])
        float[] result = new float[10];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, result);
        String distance = String.format("%.1f", (result[0] / 1000)); // Round to 1 decimal and get distance in km
        return distance;
    }
}
