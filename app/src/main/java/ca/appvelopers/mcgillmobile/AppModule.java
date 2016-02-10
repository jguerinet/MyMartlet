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

package ca.appvelopers.mcgillmobile;import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Base Dagger module
 * @author Julien Guerinet
 * @since 2.0.4
 */
@Module
public class AppModule {
    /**
     * App context
     */
    private Context context;

    /**
     * Default Constructor
     *
     * @param context App context
     */
    public AppModule(Context context) {
        this.context = context;
    }

    /* PROVIDERS */

    /**
     * @return App context
     */
    @Provides
    @Singleton
    protected Context provideContext() {
        return context;
    }

    /**
     * @param context App context
     * @return {@link SharedPreferences} instance
     */
    @Provides
    @Singleton
    protected SharedPreferences provideSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @return The {@link Gson} singleton instance
     */
    @Provides
    @Singleton
    protected Gson provideGson() {
        return new Gson();
    }
}
