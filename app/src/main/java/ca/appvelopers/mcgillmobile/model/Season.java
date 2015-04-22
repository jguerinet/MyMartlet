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

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;

/**
 * The different seasons a term can be in
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public enum Season {
    /**
     * September - December
     */
    FALL,
    /**
     * January - April
     */
    WINTER,
    /**
     * May, June, July
     */
    SUMMER;

    /**
     * Finds a season based on a String
     *
     * @param season The String
     * @return The corresponding season
     */
    public static Season findSeason(String season){
        if(season.equalsIgnoreCase(FALL.getId())){
            return FALL;
        }
        else if(season.equalsIgnoreCase(WINTER.getId())){
            return WINTER;
        }
        else if(season.equalsIgnoreCase(SUMMER.getId())){
            return SUMMER;
        }
        return null;
    }

    /**
     * @return The language independent Id of this season
     */
    public String getId(){
        switch (this){
            case FALL:
                return "Fall";
            case WINTER:
                return "Winter";
            case SUMMER:
                return "Summer";
            default:
                return "Error";
        }
    }

    /**
     * @return The McGill season number for the given season
     */
    public String getSeasonNumber(){
        switch(this){
            case FALL:
                return "09";
            case WINTER:
                return "01";
            case SUMMER:
                return "05";
            default:
                return "-1";
        }
    }

    @Override
    public String toString(){
        switch(this){
            case FALL:
                return App.getContext().getString(R.string.fall);
            case WINTER:
                return App.getContext().getString(R.string.winter);
            case SUMMER:
                return App.getContext().getString(R.string.summer);
            default:
                return App.getContext().getString(R.string.error);
        }
    }
}
