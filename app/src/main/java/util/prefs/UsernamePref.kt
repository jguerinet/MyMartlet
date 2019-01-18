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

package com.guerinet.mymartlet.util.prefs

import android.content.Context
import android.content.SharedPreferences
import com.guerinet.mymartlet.R
import com.guerinet.suitcase.prefs.NullStringPref

/**
 * Stores and loads the user's username (McGill email)
 * @author Julien Guerinet
 * @since 1.0.0
 */
class UsernamePref(context: Context, prefs: SharedPreferences) :
        NullStringPref(prefs, "username", null) {

    private val emailSuffix: String = context.getString(R.string.login_email)

    /** User's full email, null if none */
    val full: String?
        get() = super.value?.run { super.value + emailSuffix }
}