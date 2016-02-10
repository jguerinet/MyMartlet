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

import com.google.gson.Gson;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    /* OKHTTP */

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

    /* RETROFIT */

    /**
     * @param client {@link OkHttpClient} instance to use for the config server
     * @param gson   {@link Gson} instance for the converting
     * @return The {@link Retrofit} instance to use for the config server
     */
    public Retrofit provideConfigRetrofit(@Named(CONFIG) OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl("http://mymartlet.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
