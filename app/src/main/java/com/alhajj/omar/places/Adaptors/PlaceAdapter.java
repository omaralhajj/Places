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
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        TextView coordinatesTextView = convertView.findViewById(R.id.coordinatesTextView);

        placeNameTextView.setText(place.getName());
        descriptionTextView.setText(place.getDescription());

        String coordinatesString = this.context.getString(R.string.coordinates, place.getLatitude(), place.getLongitude());
        coordinatesTextView.setText(coordinatesString);

        return convertView;
    }


}
