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
import android.support.annotation.NonNull;

import com.guerinet.utils.prefs.StringPreference;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Julien Guerinet
 * @since 2.2.0
 */
@Singleton
public class CookiePreference extends StringPreference {
    /**
     * Default Constructor
     *
     * @param prefs        {@link SharedPreferences} instance
     */
    @Inject
    public CookiePreference(@NonNull SharedPreferences prefs) {
        super(prefs, "cookies", null);
    }

    /**
     * Concatenates the String of cookies and stores them in the {@link SharedPreferences
     * }
     * @param cookies The list of cookies to store
     */
    public void set(List<String> cookies) {
        String string = "";
        //Go through the cookies
        for (String cookie : cookies) {
            //Add a ; in front to differentiate them
            string += ";" + cookie.split(";", 1)[0];
        }

        //Remove the first ;
        super.set(string.substring(1));
    }

    @Override
    public String get() {
        throw new IllegalStateException("Should be using getCookies() instead");
    }

    /**
     * @return The list of cookies, an empty list if none
     */
    public String[] getCookies() {
        if (super.get() == null) {
            return new String[0];
        }
        //Split the cookies with the ;
        return super.get().split(";");
    }
}
