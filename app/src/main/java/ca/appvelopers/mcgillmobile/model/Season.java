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

import android.content.Context;

import ca.appvelopers.mcgillmobile.R;

/**
 * Author: Julien
 * Date: 2014-03-22 17:39
 */
public enum Season {
    FALL,
    WINTER,
    SUMMER,
    ERROR;

    public static Season findSeason(String season){
        if(season.equalsIgnoreCase(Token.FALL.getString())){
            return FALL;
        }
        else if(season.equalsIgnoreCase(Token.WINTER.getString())){
            return WINTER;
        }
        else if(season.equalsIgnoreCase(Token.SUMMER.getString())){
            return SUMMER;
        }
        return ERROR;
    }

    public String toString(Context context){
        switch(this){
            case FALL:
                return context.getResources().getString(R.string.fall);
            case WINTER:
                return context.getResources().getString(R.string.winter);
            case SUMMER:
                return context.getResources().getString(R.string.summer);
            default:
                return context.getString(R.string.error);
        }
    }

    @Override
    public String toString(){
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
}
