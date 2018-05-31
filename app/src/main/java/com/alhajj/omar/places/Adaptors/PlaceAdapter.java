package com.alhajj.omar.places.Adaptors;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.alhajj.omar.places.Models.Place;
import com.alhajj.omar.places.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Omar on 20-05-2018.
 */

public class PlaceAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Place> placeArrayList;

    public PlaceAdapter(Context context, ArrayList<Place> placeArrayList) {
        this.placeArrayList = placeArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (placeArrayList != null) {
            return placeArrayList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (placeArrayList != null) {
            return placeArrayList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    public void removeItem(int position) {
        placeArrayList.remove(position);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.place_list_item, null);
        }

        Place place = placeArrayList.get(position);

        TextView placeNameTextView = convertView.findViewById(R.id.placeNameTextView);
        TextView coordinatesTextView = convertView.findViewById(R.id.coordinatesTextView);
        TextView distanceTextView = convertView.findViewById(R.id.distanceTextView);

        placeNameTextView.setText(place.getName());

        //String coordinatesString = this.context.getString(R.string.coordinates, place.getLatitude(), place.getLongitude());
        String coordinatesString = place.getLatitude() + ", " + place.getLongitude();
        coordinatesTextView.setText(coordinatesString);

        String distance = calculateDistance(56.160477, 10.136271, place.getLatitude(), place.getLongitude());
        distanceTextView.setText(distance);

        return convertView;
    }

    private String calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        // Reference: https://developer.android.com/reference/android/location/Location.html#distanceBetween(double,%20double,%20double,%20double,%20float[])
        float[] result = new float[10];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, result);

        return String.format("%.1f", (result[0]/1000));
    }
}
