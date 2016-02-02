/*
 * Copyright 2014-2016 Appvelopers
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

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

/**
 * A place on the campus map
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Place implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The place name
     */
    private String name;
    /**
     * The place types
     */
    private List<PlaceType> types;
    /**
     * The address of this place
     */
    private String address;
    /**
     * The latitude coordinate of this place
     */
    private double latitude;
    /**
     * The longitude coordinate of this place
     */
    private double longitude;

    /**
     * Default Constructor
     *
     * @param name      The name of this place
     * @param types     The place types
     * @param address   The address of this place
     * @param latitude  The latitude coordinate of this place
     * @param longitude The longitude coordinate of this place
     */
    public Place(String name, String[] types, String address, double latitude, double longitude) {
        this.name = name;
        this.types = PlaceType.getTypes(types);
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /* GETTERS */

    /**
     * @return The name of this place
     */
    public String getName() {
        return name;
    }

    /**
     * @return The address of this place
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return The place coordinates
     */
    public LatLng getCoordinates() {
        return new LatLng(latitude, longitude);
    }

    /**
     * Checks if this place is of the given type
     *
     * @param type The type
     * @return True if it is part of the type, false otherwise
     */
    public boolean isOfType(PlaceType type) {
        return types.contains(type);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Place)) {
            return false;
        }

        Place place = (Place) object;
        return name.equalsIgnoreCase(place.getName()) &&
                address.equalsIgnoreCase(place.getAddress()) &&
                getCoordinates().equals(place.getCoordinates());
    }
}
