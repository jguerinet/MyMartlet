/*
 * Copyright 2014-2015 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.model;

import java.io.Serializable;
import java.util.List;

/**
 * A place on the campus map
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Place implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * The place name
     */
    private String mName;
    /**
     * The place types
     */
    private List<PlaceType> mTypes;
    /**
     * The address of this place
     */
    private String mAddress;
    /**
     * The latitude coordinate of this place
     */
    private double mLatitude;
    /**
     * The longitude coordinate of this place
     */
    private double mLongitude;

    /**
     * Default Constructor
     *
     * @param name      The name of this place
     * @param types     The place types
     * @param address   The address of this place
     * @param latitude  The latitude coordinate of this place
     * @param longitude The longitude coordinate of this place
     */
    public Place(String name, String[] types, String address, double latitude,
                 double longitude){
        this.mName = name;
        this.mTypes = PlaceType.getTypes(types);
        this.mAddress = address;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    /* GETTERS */

    /**
     * @return The name of this place
     */
    public String getName(){
        return this.mName;
    }

    /**
     * @return The address of this place
     */
    public String getAddress(){
        return this.mAddress;
    }

    /**
     * @return The latitude coordinate of this place
     */
    public double getLatitude(){
        return this.mLatitude;
    }

    /**
     * @return The longitude coordinate of this place
     */
    public double getLongitude(){
        return this.mLongitude;
    }

    /**
     * Checks if this place is of the given type
     *
     * @param type The type
     * @return True if it is part of the type, false otherwise
     */
    public boolean isOfType(PlaceType type){
        return mTypes.contains(type);
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof Place)){
            return false;
        }

        Place place = (Place)object;
        return mName.equalsIgnoreCase(place.getName()) &&
                mAddress.equalsIgnoreCase(place.getAddress()) &&
                mLatitude == place.getLatitude() &&
                mLongitude == place.getLongitude();
    }
}
