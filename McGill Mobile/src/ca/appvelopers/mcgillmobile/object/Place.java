package ca.appvelopers.mcgillmobile.object;

import java.io.Serializable;
import java.util.List;

/**
 * Author: Julien Guerinet
 * Date: 2014-07-11 11:24 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 * Represents a place on the map
 */

public class Place implements Serializable{
    private static final long serialVersionUID = 1L;

    private String mName;
    private List<PlaceCategory> mCategories;
    private String mAddress;
    private double mLongitude;
    private double mLatitude;
    private String mDetails;

    public Place(String name, List<PlaceCategory> categories, String address, double longitude,
                 double latitude, String details){
        this.mName = name;
        this.mCategories = categories;
        this.mAddress = address;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mDetails = details;
    }

    /* GETTERS */
    public String getName(){
        return mName;
    }

    public List<PlaceCategory> getCategories(){
        return mCategories;
    }

    public String getAddress(){
        return mAddress;
    }

    public double getLongitude(){
        return mLongitude;
    }

    public double getLatitude(){
        return mLatitude;
    }

    public String getDetails(){
        return mDetails;
    }
}
