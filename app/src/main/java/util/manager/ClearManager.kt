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

package com.guerinet.mymartlet.util.manager

import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.prefs.DefaultTermPref
import com.guerinet.mymartlet.util.prefs.RegisterTermsPref
import com.guerinet.mymartlet.util.prefs.UsernamePref
import com.guerinet.mymartlet.util.room.UserDb
import com.guerinet.suitcase.prefs.BooleanPref
import com.orhanobut.hawk.Hawk

/**
 * Clears data from databases or the SharedPrefs
 * @author Julien Guerinet
 * @since 1.0.0
 */
class ClearManager(
    private val usernamePref: UsernamePref,
    private val homepageManager: HomepageManager,
    private val defaultTermPref: DefaultTermPref,
    private val registerTermsPref: RegisterTermsPref,
    private val rememberUsernamePref: BooleanPref,
    private val userDb: UserDb
) {

    /**
     * Clears all of the user's info
     */
    fun clearUserInfo() {
        // If the user had not chosen to remember their username, clear it
        if (!rememberUsernamePref.value) {
            usernamePref.clear()
        }

        // Password
        Hawk.delete(Prefs.PASSWORD)

        // User Db
        userDb.clearAllTables()

        // HomePageManager
        homepageManager.clear()

        // Default term
        defaultTermPref.clear()
    }

    /**
     * Clears all config info
     */
    fun clearConfig() {
        // Register terms
        registerTermsPref.clear()
    }
}
