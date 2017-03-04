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

package ca.appvelopers.mcgillmobile.util.dagger;

import com.squareup.moshi.Moshi;

import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.util.manager.McGillManager;
import ca.appvelopers.mcgillmobile.util.retrofit.ConfigService;
import ca.appvelopers.mcgillmobile.util.retrofit.McGillService;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
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
class NetworkModule {

    /* OKHTTP */

    /**
     * @return {@link HttpLoggingInterceptor} instance for HTTP logging
     */
    @Provides
    @Singleton
    HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(
                message -> Timber.tag("OkHttp").i(message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return interceptor;
    }

    /**
     * @param interceptor The {@link HttpLoggingInterceptor} instance
     * @return {@link OkHttpClient} instance for the config server
     */
    @Provides
    @Singleton
    OkHttpClient provideConfigOkHttpClient(HttpLoggingInterceptor interceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
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
    Retrofit provideConfigRetrofit(OkHttpClient client, Moshi moshi) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl("http://mymartlet.herokuapp.com/api/v2/")
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .build();
    }

    /* RETROFIT SERVICES */

    /**
     * @param retrofit {@link Retrofit} instance to use for the config server
     * @return {@link ConfigService} instance
     */
    @Provides
    @Singleton
    ConfigService provideConfigService(Retrofit retrofit) {
        return retrofit.create(ConfigService.class);
    }

    /**
     * @param manager {@link McGillManager} instance that has the {@link McGillService} instance
     * @return {@link McGillService} instance
     */
    @Provides
    @Singleton
    McGillService provideMcGillService(McGillManager manager) {
        return manager.getMcGillService();
    }
}
