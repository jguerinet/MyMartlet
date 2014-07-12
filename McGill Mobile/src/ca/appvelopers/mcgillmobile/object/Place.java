package ca.appvelopers.mcgillmobile.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Author: Julien Guerinet
 * Date: 2014-07-11 11:24 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 * Represents a place on the map
 */

@JsonIgnoreProperties(ignoreUnknown =  true)
public class Place implements Serializable{
    private static final long serialVersionUID = 1L;

    private String mName;
    private List<PlaceCategory> mCategories;
    private String mAddress;
    private double mLongitude;
    private double mLatitude;
    private String mDetails;

    public Place(@JsonProperty("Name") String name,
                 @JsonProperty("Categories") String[] categories,
                 @JsonProperty("Address") String address,
                 @JsonProperty("Longitude") double longitude,
                 @JsonProperty("Latitude") double latitude,
                 @JsonProperty("Details") String details){
        this.mName = name;
        this.mCategories = PlaceCategory.getCategories(categories);
        this.mAddress = address;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mDetails = details;
    }

    /* GETTERS */
    public String getName(){
        return mName;
    }

    public boolean hasCategory(PlaceCategory category){
        return mCategories.contains(category);
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
