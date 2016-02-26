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

package ca.appvelopers.mcgillmobile.util.storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.util.Constants;
import timber.log.Timber;

/**
 * Loads objects from the internal storage
 * @author Julien Guerinet
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public class Load {

    /**
     * Loads the object at the given file name
     *
     * @param tag      The tag to use in case of failure
     * @param fileName The file name
     * @return The object loaded, null if none
     */
    private static Object loadObject(String tag, String fileName){
        try{
            FileInputStream fis = App.getContext().openFileInput(fileName);
            ObjectInputStream in = new ObjectInputStream(fis);
            return in.readObject();
        } catch(FileNotFoundException e){
            Timber.e("File not found: %s", tag);
        } catch(Exception e){
            Timber.e(e, "Failure: %s", tag);
        }

        return null;
    }

    /**
     * @return The list of places, an empty list if none
     */
    public static List<Place> places(){
        List<Place> places = (List<Place>)loadObject("Places", Constants.PLACES_FILE);
        return places == null ? new ArrayList<Place>() : places;
    }

    /**
     * @return The list of place types, an empty list if none
     */
    public static List<PlaceType> placeTypes(){
        List<PlaceType> types = (List<PlaceType>)loadObject("Place Types",
                Constants.PLACE_TYPES_FILE);
        return types == null ? new ArrayList<PlaceType>() : types;
    }

    /**
     * @return The list of terms that the user can currently register in, an empty list if none
     */
    public static List<Term> registerTerms(){
        List<Term> terms = (List<Term>)loadObject("Register Terms", Constants.REGISTER_TERMS_FILE);
        return terms == null ? new ArrayList<Term>() : terms;
    }

    /**
     * @return The user's transcript, null if none
     */
    public static Transcript transcript(){
        return (Transcript)loadObject("Transcript", Constants.TRANSCRIPT_FILE);
    }

    /**
     * @return The user's classes, an empty list if none
     */
    public static List<Course> classes(){
        List<Course> courses = (List<Course>)loadObject("Classes", Constants.COURSES_FILE);
        return courses == null ? new ArrayList<Course>() : courses;
    }

    /**
     * @return The user's Ebill statements, and empty list if none
     */
    public static List<Statement> ebill(){
        List<Statement> statements = (List<Statement>)loadObject("Ebill", Constants.EBILL_FILE);
        return statements == null ? new ArrayList<Statement>() : statements;
    }

    /**
     * @return The user's chosen default term, null if none
     */
    public static Term defaultTerm(){
        return (Term)loadObject("Default Term", Constants.DEFAULT_TERM_FILE);
    }

    /**
     * @return The user's class wishlist, an empty list if none
     */
    public static List<Course> wishlist(){
        List<Course> wishlist = (List<Course>)loadObject("Wishlist", Constants.WISHLIST_FILE);
        return wishlist == null ? new ArrayList<Course>() : wishlist;
    }

    /**
     * @return The user's favorite places, an empty list if none
     */
    public static List<Place> favoritePlaces(){
        List<Place> places = (List<Place>)loadObject("Favorite Places",
                Constants.FAVORITE_PLACES_FILE);
        return places == null ? new ArrayList<Place>() : places;
    }
}
