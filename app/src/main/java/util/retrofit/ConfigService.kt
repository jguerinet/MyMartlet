/*
 * Copyright 2014-2018 Julien Guerinet
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

package com.guerinet.mymartlet.util.retrofit

import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.model.place.Category
import com.guerinet.mymartlet.model.place.Place
import com.guerinet.mymartlet.util.service.ConfigDownloadService
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * Retrofit service used for the config server
 * @author Julien Guerinet
 * @since 1.0.0
 */
interface ConfigService {

    /**
     * Retrieves the [ConfigDownloadService.Config]. Uses the [ims] header
     */
    @GET("config")
    fun config(@Header("If-Modified-Since") ims: String?): Call<ConfigDownloadService.Config>

    /**
     * Retrieves the list of [Place]. Uses the [ims] header
     */
    @GET("places")
    fun places(@Header("If-Modified-Since") ims: String?): Call<List<Place>>

    /**
     * Retrieves the list of [Category]s. Uses the [ims] header
     */
    @GET("categories")
    fun categories(@Header("If-Modified-Since") ims: String?): Call<List<Category>>

    /**
     * Retrieves the list of [Term]s one can currently register for. Uses the [ims] header
     */
    @GET("registration-terms")
    fun registrationTerms(@Header("If-Modified-Since") ims: String?): Call<List<Term>>
}
