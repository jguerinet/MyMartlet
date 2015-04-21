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

public class Place implements Serializable{
    private static final long serialVersionUID = 1L;

    private String mName;
    private List<PlaceCategory> mCategories;
    private String mAddress;
    private double mLatitude;
    private double mLongitude;

    public Place(String name, String[] categories, String address, double latitude, double longitude){
        this.mName = name;
        this.mCategories = PlaceCategory.getCategories(categories);
        this.mAddress = address;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
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

    public double getLatitude(){
        return mLatitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof Place)){
            return false;
        }

        Place place = (Place)object;

        return mName.equalsIgnoreCase(place.getName()) && mAddress.equalsIgnoreCase(place.getAddress()) &&
                mLatitude == place.getLatitude() && mLongitude == place.getLongitude();
    }
}
