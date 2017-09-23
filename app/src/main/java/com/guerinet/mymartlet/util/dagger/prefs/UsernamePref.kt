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

package com.guerinet.mymartlet.util.dagger.prefs

import android.content.Context
import android.content.SharedPreferences
import com.guerinet.mymartlet.R
import com.guerinet.suitcase.prefs.StringPref
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores and loads the user's username (McGill email)
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Singleton
class UsernamePref @Inject constructor(context: Context, prefs: SharedPreferences) :
        StringPref(prefs, "username", null) {

    /**
     * McGill email suffix
     */
    private val emailSuffix: String = context.getString(R.string.login_email)

    /**
     * User's full email, null if none
     */
    fun full(): String? = if (get() == null) null else get() + emailSuffix
}