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

package ca.appvelopers.mcgillmobile;

import android.content.Context;
import android.net.ConnectivityManager;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Dagger module for all network injections
 * @author Julien Guerinet
 * @since 2.0.4
 */
@Module(includes = AppModule.class)
public class NetworkModule {
    /**
     * Config server injections
     */
    public static final String CONFIG = "config";

    /**
     * @param context App context
     * @return The {@link ConnectivityManager} instance
     */
    @Provides
    public ConnectivityManager provideConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * @return The {@link OkHttpClient} instance
     */
    @Provides
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

    /**
     * @return The {@link OkHttpClient} instance for the config server
     */
    @Provides
    @Named(CONFIG)
    public OkHttpClient provideConfigOkHttpClient() {
        return new OkHttpClient();
    }
}
