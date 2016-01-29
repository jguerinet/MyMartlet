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

package ca.appvelopers.mcgillmobile.util;

/**
 * Constants used around the app
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Constants {
    /**
     * URL to the config
     */
    public static final String CONFIG_URL = "http://admin.mymartlet.ca/config";
    /**
     * Play Store link to our app (when we require the user to update their version)
     */
    public static final String PLAY_STORE_LINK = "market://details?id=ca.appvelopers.mcgillmobile";

    /* SYNCHRONIZATION LOCKS*/
    /**
     * Lock used for the transcript
     */
    public static final Object TRANSCRIPT_LOCK = new Object();
    /**
     * Key used to pass a ConnectionStatus via an intent
     */
    public static final String CONNECTION_STATUS = "connection_status";
    /**
     * Key used to pass a semester via an intent
     */
    public static final String SEMESTER = "semester";
    /**
     * Key used to pass a date via an intent
     */
    public static final String DATE = "date";
    /**
     * Key used to pass a term via an intent
     */
    public static final String TERM = "term";
    /**
     * Key used to pass an email via an intent
     */
    public static final String EMAIL = "email";
    /**
     * Key used to pass a list of courses via an intent
     */
    public static final String COURSES = "courses";
    /**
     * Key used to pass a parsing bug via an intent
     */
    public static final String BUG = "bug";
    /**
     * Key used to pass a transcript via an intent
     */
    public static final String TRANSCRIPT = "transcript";
    /**
     * Key used to pass whether or not the user needs to accept the EULA via an intent
     */
    public static final String EULA_REQUIRED = "eula_required";

    /* INTERNAL STORAGE */

    /**
     * The file name where the user's transcript is stored
     */
    public static final String TRANSCRIPT_FILE = "transcript";
    /**
     * The file name where the user's courses are stored
     */
    public static final String COURSES_FILE = "courses";
    /**
     * The file name where the user's ebill is stored
     */
    public static final String EBILL_FILE = "ebill";
    /**
     * The file name where the user's info is stored
     */
    public static final String USER_FILE = "user_info";
    /**
     * The file name where the user's default term is stored
     */
    public static final String DEFAULT_TERM_FILE = "default_term";
    /**
     * The file name where the user's wishlist is stored
     */
    public static final String WISHLIST_FILE = "wishlist";
    /**
     * The file name where the list of places are stored
     */
    public static final String PLACES_FILE = "places";
    /**
     * The file name where the user's favorite places are stored
     */
    public static final String FAVORITE_PLACES_FILE = "favorite_places";
    /**
     * The file name where the different place types are stored
     */
    public static final String PLACE_TYPES_FILE = "place_types";
    /**
     * The file name where the list of terms a user can register for are stored
     */
    public static final String REGISTER_TERMS_FILE = "register_terms";

    /* SHARED PREFS */

    /**
     * True if the user has currently turned on the seat checker, false otherwise
     */
    public static final String SEAT_CHECKER = "seat_checker";
    /**
     * True if the user has currently turned on the grade checker, false otherwise
     */
    public static final String GRADE_CHECKER = "grade_checker";
    /**
     * The user's chosen language
     */
    public static final String LANGUAGE = "language";
    /**
     * The user's chosen homepage
     */
    public static final String HOMEPAGE = "home_page";
    /**
     * Intent key that is true if this is the first time the user is signing in, false otherwise
     */
    public static final String FIRST_OPEN = "first_open";
}
