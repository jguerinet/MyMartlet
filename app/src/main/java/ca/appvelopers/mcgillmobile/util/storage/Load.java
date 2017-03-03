/*
 * Copyright 2014-2017 Julien Guerinet
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
import ca.appvelopers.mcgillmobile.model.CourseResult;
import ca.appvelopers.mcgillmobile.model.Term;
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
     * @return The user's chosen default term, null if none
     */
    public static Term defaultTerm(){
        return (Term)loadObject("Default Term", Constants.DEFAULT_TERM_FILE);
    }

    /**
     * @return The user's class wishlist, an empty list if none
     */
    public static List<CourseResult> wishlist(){
        List<CourseResult> wishlist = (List<CourseResult>)loadObject("Wishlist",
                Constants.WISHLIST_FILE);
        return wishlist == null ? new ArrayList<CourseResult>() : wishlist;
    }
}
