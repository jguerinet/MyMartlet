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

package com.guerinet.mymartlet.util.dagger

import com.guerinet.mymartlet.util.manager.McGillManager
import com.guerinet.mymartlet.util.retrofit.ConfigService
import com.guerinet.mymartlet.util.retrofit.McGillService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Singleton

/**
 * Dagger module for all network related injections
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Module(includes = arrayOf(AppModule::class))
class NetworkModule {

    /**
     * Provides the [HttpLoggingInterceptor] for logging the Http calls
     */
    @Provides
    @Singleton
    fun provideLogInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor({ message -> Timber.tag("OkHttp").i(message)})
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        return interceptor
    }

    /**
     * Provides the [OkHttpClient] to make all calls to the config server
     */
    @Provides
    @Singleton
    fun provideConfigOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
            OkHttpClient.Builder().addInterceptor(interceptor).build()

    /**
     * Provides the [Retrofit] instance to make calls to the config server
     */
    @Provides
    @Singleton
    fun provideConfigRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
            Retrofit.Builder().client(client)
                    .baseUrl("https://mymartlet.herokuapp.com/api/v2")
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()

    /**
     * Provides the [ConfigService] for the config calls, created from the given [Retrofit] instance
     */
    @Provides
    @Singleton
    fun provideConfigService(retrofit: Retrofit): ConfigService =
            retrofit.create(ConfigService::class.java)

    /**
     * Provides the [McGillService] for all McGill related calls. Retrieved from the [McGillManager]
     */
    @Provides
    @Singleton
    fun provideMcGillService(manager: McGillManager): McGillService = manager.mcGillService
}