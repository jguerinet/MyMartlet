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

import android.content.SharedPreferences;

import com.guerinet.utils.prefs.StringPreference;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.util.Encryption;

/**
 * {@link SharedPreferences} helper for the user's password
 * @author Julien Guerinet
 * @since 2.0.4
 */
@Singleton
public class PasswordPreference extends StringPreference {

    /**
     * Default Constructor
     *
     * @param prefs {@link SharedPreferences} instance
     */
    @Inject
    public PasswordPreference(SharedPreferences prefs) {
        super(prefs, "password", null);
    }

    @Override
    public String get() {
        return Encryption.decode(super.get());
    }

    @Override
    public void set(String password) {
        super.set(Encryption.encode(password));
    }
}
