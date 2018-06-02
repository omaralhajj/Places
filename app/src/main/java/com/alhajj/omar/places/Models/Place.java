package com.alhajj.omar.places.Models;

import java.io.Serializable;

//Implements serializable so that the object can be passed w/ intents
public class Place implements Serializable {
    private String name;
    private String description;
    private double latitude;
    private double longitude;

    public Place(){}

    public Place(String name, String description, Double latitude, Double longitude) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
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
}
