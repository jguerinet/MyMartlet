package ca.appvelopers.mcgillmobile.object;

/**
 * Author: Julien Guerinet
 * Date: 2014-07-11 11:24 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 * Represents a place on the map
 */

public class Place {
    private String mName;
    private PlaceCategory mCategory;
    private double mLongitude;
    private double mLatitude;

    public Place(String name, PlaceCategory category, double longitude, double latitude){
        this.mName = name;
        this.mCategory = category;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
    }

    /* GETTERS */
    public String getName(){
        return mName;
    }

    public PlaceCategory getCategory(){
        return mCategory;
    }

    public double getLongitude(){
        return mLongitude;
    }

    public double getLatitude(){
        return mLatitude;
    }
}
