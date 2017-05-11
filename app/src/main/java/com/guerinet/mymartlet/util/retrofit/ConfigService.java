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

package com.guerinet.mymartlet.util.retrofit;

import com.guerinet.mymartlet.model.Term;
import com.guerinet.mymartlet.model.place.Category;
import com.guerinet.mymartlet.model.place.Place;
import com.guerinet.mymartlet.util.service.ConfigDownloadService;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Retrofit service to use for the config server
 * @author Julien Guerinet
 * @since 2.2.0
 */
public interface ConfigService {

    /**
     * @param ifModifiedSince If-Modified-Since date to add, null if noen
     * @return Config variables
     */
    @GET("config")
    Call<ConfigDownloadService.Config> config(@Header("If-Modified-Since") String ifModifiedSince);

    /**
     * @param ifModifiedSince If-Modified-Since date to add, null if none
     * @return List of parsed {@link Place}s
     */
    @GET("places")
    Call<List<Place>> places(@Header("If-Modified-Since") String ifModifiedSince);

    /**
     * @param ifModifiedSince If-Modified-Since date to add, null if none
     * @return List of parsed {@link Category}s
     */
    @GET("categories")
    Call<List<Category>> categories(@Header("If-Modified-Since") String ifModifiedSince);

    /**
     * @param ifModifiedSince If-Modified-Since date to add, null if none
     * @return List of parsed registration {@link Term}s
     */
    @GET("registration-terms")
    Call<List<Term>> registrationTerms(@Header("If-Modified-Since") String ifModifiedSince);
}
