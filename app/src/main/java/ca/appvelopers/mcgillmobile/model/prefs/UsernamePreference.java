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

package ca.appvelopers.mcgillmobile.model.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.guerinet.utils.prefs.StringPreference;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.R;

/**
 * {@link SharedPreferences} helper for the user's username (McGill email)
 * @author Julien Guerinet
 * @since 2.0.4
 */
@Singleton
public class UsernamePreference extends StringPreference {
    /**
     * McGill email suffix
     */
    private final String emailSuffix;

    /**
     * Default Constructor
     *
     * @param prefs   {@link SharedPreferences} instance
     * @param context App context
     */
    @Inject
    public UsernamePreference(SharedPreferences prefs, Context context) {
        super(prefs, "username", null);
        emailSuffix = context.getString(R.string.login_email);
    }

    /**
     * @return User's full McGill email
     */
    public String full() {
        return (get() == null) ? null : get() + emailSuffix;
    }
}
