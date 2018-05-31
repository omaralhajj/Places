package com.alhajj.omar.places.Interfaces;

import com.alhajj.omar.places.Models.Place;

//https://stackoverflow.com/questions/46675057/trying-to-use-data-outside-the-ondatachange-in-firebase
public interface OnGetPlaceDataListener {
    public void onStart();
    public void onSuccess(Place place);
    public void onFailed(Place place);
}