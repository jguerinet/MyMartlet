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

package ca.appvelopers.mcgillmobile.model.retrofit;

import java.util.List;

import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.util.thread.ConfigDownloader.Config;
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
    Call<Config> config(@Header("If-Modified-Since") String ifModifiedSince);

    /**
     * @param ifModifiedSince If-Modified-Since date to add, null if none
     * @return List of parsed {@link Place}s
     */
    @GET("places")
    Call<List<Place>> places(@Header("If-Modified-Since") String ifModifiedSince);

    /**
     * @param ifModifiedSince If-Modified-Since date to add, null if none
     * @return List of parsed {@link PlaceType}s
     */
    @GET("categories")
    Call<List<PlaceType>> categories(@Header("If-Modified-Since") String ifModifiedSince);
}
