package com.alhajj.omar.places.Models;

import java.io.Serializable;

//Implements serializable so that the object can be passed w/ intents
public class Place implements Serializable {
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private String url;
    private String address;
    private float rating;
    private String numberOfRatings;

    public Place(){}

    public Place(String name, String description, Double latitude, Double longitude, String url, String address, float rating, String numberOfRatings) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.url = url;
        this.address = address;
        this.rating = rating;
        this.numberOfRatings = numberOfRatings;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(String numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }
}
