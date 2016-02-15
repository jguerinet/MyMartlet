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

import ca.appvelopers.mcgillmobile.model.Term;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Retrofit service to use to get information from McGill
 * @author Julien Guerinet
 * @since 2.2.0
 */
public interface McGillService {
    /**
     * Get the login page to fill out the user info
     *
     * @return The McGill login page {@link ResponseBody}
     */
    @GET("twbkwbis.P_WWWLogin")
    Call<ResponseBody> loginPage();

    /**
     * Creates the POST request that logs the user in
     *
     * @param body The {@link ResponseBody} instance, wrapping the filled out login form
     * @return The McGill login {@link ResponseBody}
     */
    @Headers({
            "Host: horizon.mcgill.ca",
            "Origin: https://horizon.mcgill.ca",
            "DNT: 1",
            "Connection: keep-alive",
            "Referer: https://horizon.mcgill.ca/pban1/twbkwbis.P_WWWLogin"
    })
    @POST("twbkwbis.P_ValLogin")
    Call<ResponseBody> login(@Body RequestBody body);

    /**
     * Retrieves the user's schedule for a given term
     *
     * @param term The term to retrieve the schedule for, in String format
     * @return The schedule {@link Response}
     */
    @GET("bwskfshd.P_CrseSchdDetl?term_in={term}")
    Call<Response> schedule(@Path("term") Term term);

    /**
     * Retrieves the user's transcript
     *
     * @return The transcript {@link Response}
     */
    @GET("bzsktran.P_Display_Form?user_type=S&tran_type=V")
    Call<Response> transcript();

    /**
     * Retrieves the user's ebill
     *
     * @return The ebill {@link Response}
     */
    @GET("bztkcbil.pm_viewbills")
    Call<Response> ebill();

    /**
     * Registers or unregisters someone to a list of courses
     *
     * @param url The end of the registration URL
     * @return The registration {@link Response}
     */
    @GET("bwckcoms.P_Regs?term_in={url}")
    Call<Response> registration(@Path("url") String url);

    /**
     * Searches for a list of classes
     *
     * @param url The end of the search URL
     * @return The search {@link Response}
     */
    @GET("bwskfcls.P_GetCrse?{url}")
    Call<Response> search(@Path("url") String url);
}
