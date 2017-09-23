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

package com.guerinet.mymartlet.util;

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
     * Key used to pass a currentTerm via an intent
     */
    public static final String TERM = "currentTerm";
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
     * The file name where the user's wishlist is stored
     */
    public static final String WISHLIST_FILE = "wishlist";

    /* BROADCASTS */

    /**
     * Broadcasted action when a MinervaException occurs
     */
    public static final String BROADCAST_MINERVA = "broadcast_minerva";
}
