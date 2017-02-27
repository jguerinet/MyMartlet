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

package ca.appvelopers.mcgillmobile.util;

/**
 * Constants used around the app
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Constants {

    /* INTENT KEYS */

    /**
     * Passes an Id via an intent
     */
    public static final String ID = "id";
    /**
     * Key used to pass a semester via an intent
     */
    public static final String SEMESTER = "semester";
    /**
     * Key used to pass a term via an intent
     */
    public static final String TERM = "term";
    /**
     * Key used to pass a list of courses via an intent
     */
    public static final String COURSES = "courses";
    /**
     * Key that is true if this is the first time the user is signing in, false otherwise
     */
    public static final String FIRST_OPEN = "first_open";
    /**
     * Used to pass the exception to the login screen when there's an error
     */
    public static final String EXCEPTION = "exception";

    /* INTERNAL STORAGE */

    /**
     * The file name where the user's ebill is stored
     */
    public static final String EBILL_FILE = "ebill";
    /**
     * The file name where the user's default term is stored
     */
    public static final String DEFAULT_TERM_FILE = "default_term";
    /**
     * The file name where the user's wishlist is stored
     */
    public static final String WISHLIST_FILE = "wishlist";
    /**
     * The file name where the list of terms a user can register for are stored
     */
    public static final String REGISTER_TERMS_FILE = "register_terms";

    /* BROADCASTS */

    /**
     * Broadcasted action when a MinervaException occurs
     */
    public static final String BROADCAST_MINERVA = "broadcast_minerva";
}
