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

package ca.appvelopers.mcgillmobile.model;

import android.content.SharedPreferences;

import com.guerinet.utils.prefs.IntPreference;

import javax.inject.Named;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.AppModule;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for the {@link SharedPreferences} values
 * @author Julien Guerinet
 * @since 2.0.4
 */
@Module(includes = AppModule.class)
public class PrefsModule {

    /* PREFERENCE NAMES */
    public static final String VERSION = "version";

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return Stored version {@link IntPreference}, defaults to -1
     */
    @Provides
    @Singleton
    @Named(VERSION)
    protected IntPreference provideVersion(SharedPreferences prefs) {
        return new IntPreference(prefs, VERSION, -1);
    }
}
