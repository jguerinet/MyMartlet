/*
 * Copyright 2014-2019 Julien Guerinet
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

package com.guerinet.mymartlet.util

/**
 * Constants used throughout the app
 * @author Julien Guerinet
 * @since 1.0.0
 */
object Constants {

    /* INTENT KEYS */

    const val COURSES = "courses"

    const val EXCEPTION = "exception"

    const val FIRST_OPEN = "first_open"

    const val ID = "id"

    const val TERM = "currentTerm"

    /* BROADCASTS */

    const val BROADCAST_MINERVA = "broadcast_minerva"

    /**
     * Db names within Firebase
     */
    object Firebase {

        const val CATEGORIES = "categories"
    }
}

/**
 * SharedPreferences keys
 */
object Prefs {

    const val EULA = "user_agreement"
    const val GRADE_CHECKER = "grade_checker"
    const val IMS_CONFIG = "ims_config"
    const val IMS_PLACES = "ims_places"
    const val IMS_REGISTRATION = "ims_registration"
    const val IS_FIRST_OPEN = "first_open"
    const val MIN_VERSION = "min_version"
    const val REMEMBER_USERNAME = "remember_username"
    const val PASSWORD = "password"
    const val SCHEDULE_24HR = "24hr Schedule"
    const val SEAT_CHECKER = "seat_checker"
    const val STATS = "statistics"
}
