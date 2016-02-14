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

import com.squareup.moshi.Moshi;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.model.retrofit.ConfigService;
import ca.appvelopers.mcgillmobile.model.retrofit.McGillService;
import ca.appvelopers.mcgillmobile.util.Passwords;
import ca.appvelopers.mcgillmobile.util.manager.McGillManager;
import dagger.Module;
import dagger.Provides;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import timber.log.Timber;

/**
 * Dagger module for all network injections
 * @author Julien Guerinet
 * @since 2.0.4
 */
@Module(includes = AppModule.class)
public class NetworkModule {
    /**
     * Injection Names
     */
    public static final String CONFIG = "config";
    public static final String MCGILL = "mcgill";

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
     * @return The {@link HttpLoggingInterceptor} instance for HTTP logging
     */
    @Provides
    @Singleton
    protected HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Timber.tag("OkHttp").i(message);
                    }
                });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return interceptor;
    }

    /**
     * @param interceptor The {@link HttpLoggingInterceptor} instance
     * @param manager     The {@link McGillManager} instance to get the cookies
     * @return The {@link OkHttpClient} instance
     */
    @Provides
    @Singleton
    @Named(MCGILL)
    public OkHttpClient provideOkHttpClient(HttpLoggingInterceptor interceptor,
            final McGillManager manager) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder builder = chain.request().newBuilder();

                        //Add the cookies if there are any
                        for(String cookie : manager.getCookies()){
                            builder.addHeader("Cookie", cookie.split(";", 1)[0]);
                        }

                        return chain.proceed(builder.build());
                    }
                })
                .build();
    }

    /**
     * @param interceptor The {@link HttpLoggingInterceptor} instance
     * @return The {@link OkHttpClient} instance for the config server
     */
    @Provides
    @Singleton
    @Named(CONFIG)
    public OkHttpClient provideConfigOkHttpClient(HttpLoggingInterceptor interceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //Add the authentication to the header
                        Request newRequest = chain.request()
                                .newBuilder()
                                .addHeader("Authorization", Credentials.basic(
                                        Passwords.CONFIG_USERNAME, Passwords.CONFIG_PASSWORD))
                                .build();

                        return chain.proceed(newRequest);
                    }
                })
                .build();
    }

    /* RETROFIT */

    /**
     * @param client {@link OkHttpClient} instance to use for the config server
     * @param moshi  {@link Moshi} instance for the converting
     * @return The {@link Retrofit} instance to use for the config server
     */
    @Provides
    @Singleton
    @Named(CONFIG)
    public Retrofit provideConfigRetrofit(@Named(CONFIG) OkHttpClient client, Moshi moshi) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl("http://mymartlet.herokuapp.com/api/v2/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();
    }

    /**
     * @param client {@link OkHttpClient} instance to use when connecting to McGill
     * @return The {@link Retrofit} instance to use when connecting to McGill
     */
    @Provides
    @Singleton
    @Named(MCGILL)
    public Retrofit provideMcGillRetrofit(@Named(MCGILL) OkHttpClient client) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl("https://horizon.mcgill.ca/pban1/")
                .build();
    }

    /* RETROFIT SERVICES */

    /**
     * @param retrofit The {@link Retrofit} instance to use for the config server
     * @return The {@link ConfigService} instance
     */
    @Provides
    @Singleton
    public ConfigService provideConfigService(@Named(CONFIG) Retrofit retrofit) {
        return retrofit.create(ConfigService.class);
    }

    /**
     * @param retrofit The {@link Retrofit} instance to use when connecting to McGill
     * @return The {@link McGillService} instance
     */
    @Provides
    @Singleton
    public McGillService provideMcGillService(@Named(MCGILL) Retrofit retrofit) {
        return retrofit.create(McGillService.class);
    }
}
