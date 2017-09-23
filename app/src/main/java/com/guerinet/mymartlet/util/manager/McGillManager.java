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

package com.guerinet.mymartlet.util.manager;

import com.guerinet.mymartlet.model.Course;
import com.guerinet.mymartlet.model.Term;
import com.guerinet.mymartlet.model.exception.MinervaException;
import com.guerinet.mymartlet.util.dagger.prefs.PrefsModule;
import com.guerinet.mymartlet.util.dagger.prefs.UsernamePref;
import com.guerinet.mymartlet.util.retrofit.CourseResultConverter;
import com.guerinet.mymartlet.util.retrofit.EbillConverter;
import com.guerinet.mymartlet.util.retrofit.McGillService;
import com.guerinet.mymartlet.util.retrofit.RegistrationErrorConverter;
import com.guerinet.mymartlet.util.retrofit.ScheduleConverter;
import com.guerinet.mymartlet.util.retrofit.TranscriptConverter;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * All of the connection logic
 * @author Shabbir Hussain
 * @author Rafi Uddin
 * @author Joshua David Alfaro
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Singleton
public class McGillManager {

    private final UsernamePref usernamePref;
    /**
     * The {@link McGillService} instance
     */
    private final McGillService mcGillService;

	/**
	 * Default Constructor
     *
     * @param loggingInterceptor {@link HttpLoggingInterceptor} instance
     * @param usernamePref       {@link UsernamePref} instance
     */
    @Inject
	McGillManager(HttpLoggingInterceptor loggingInterceptor, UsernamePref usernamePref) {
        this.usernamePref = usernamePref;

        // Set up the client here in order to have access to the login methods
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        // Save the cookies per URL host
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        // Use the cookies for the given URL host
                        List<Cookie> cookies = cookieStore.get(url.host());

                        if (cookies == null) {
                            // If there are no cookies, use an empty list
                            cookies = new ArrayList<>();
                        }

                        List<Cookie> cookiesToUse = new ArrayList<>();

                        // Go through the cookies, remove the proxy ones
                        for (Cookie cookie : cookies) {
                            if (!cookie.name().toLowerCase().contains("proxy")) {
                                cookiesToUse.add(cookie);
                            }
                        }

                        return cookiesToUse;
                    }
                })
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    // Get the request and the response
                    Request request = chain.request();
                    okhttp3.Response response = chain.proceed(request);

                    // If this is the login request, don't continue
                    if (request.method().equalsIgnoreCase("POST")) {
                        // This is counting on the fact that the only POST request is for login
                        return response;
                    }

                    // Go through the cookies
                    for (String cookie: response.headers().values("Set-Cookie")) {
                        // Check if one of the cookies is an empty Session Id
                        if (cookie.contains("SESSID=;")) {
                            // Try logging in (if there's an error, it will be thrown)
                            login();

                            // Successfully logged them back in, try retrieving the data again
                            return chain.proceed(request);
                        }
                    }

                    // If we have the session Id in the cookies, return the original response
                    return response;
                })
                .build();

        mcGillService = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://horizon.mcgill.ca/pban1/")
                .addConverterFactory(new ScheduleConverter())
                .addConverterFactory(new TranscriptConverter())
                .addConverterFactory(new EbillConverter())
                .addConverterFactory(new CourseResultConverter())
                .addConverterFactory(new RegistrationErrorConverter())
                .build()
                .create(McGillService.class);
    }

    /* GETTERS */

    /**
     * @return {@link McGillService} instance
     */
    public McGillService getMcGillService() {
        return mcGillService;
    }

	/* HELPERS */

    /**
     * Handles determining whether the login was successful or not
     *
     * @param response Received response from the call
     * @throws IOException Thrown if there was an error during the login
     */
	private void handleLogin(Response<ResponseBody> response) throws IOException {
        // Create the POST request with the given username and password
        String responseString = response.body().string();

        if (!responseString.contains("WELCOME")) {
            // If we're not on the Welcome page, then the user entered wrong info
            throw new MinervaException();
        }
    }

    /**
     * Initializes the {@link McGillService} because a call needs to be made before anything
     *  happens for some reason
     */
    public void init() {
        // Create a blank call when initializing because the first call never seems to work
        try {
            mcGillService.login("", "").execute();
        } catch (IOException ignored) {}
    }

    /**
     * Attempts to log into Minerva asynchronously
     *
     * @param username Inputted username
     * @param password Inputted password
     * @param callback Callback
     */
    public void login(String username, String password, Callback<ResponseBody> callback) {
        mcGillService.login(username, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    handleLogin(response);
                    // If login goes smoothly, call the given callback
                    callback.onResponse(call, response);
                } catch (IOException e) {
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    /**
     * Logs the user in with the stored username and password
     *
     * @throws IOException Thrown if there was an error during login
     */
    public void login() throws IOException {
        // Create the POST request with the given username and password and handle the response
        handleLogin(mcGillService.login(usernamePref.full(), Hawk.get(PrefsModule.Hawk.PASSWORD))
                .execute());
    }

    /**
     * Returns the URL to register for courses for the given parameters
     *
     * @param courses    A list of courses to (un)register for
     * @param dropCourse True if the user is dropping courses, false otherwise
     * @return The proper registration URL
     */
    public static String getRegistrationURL(List<? extends Course> courses, boolean dropCourse) {
        // Get the currentTerm from the first course (they'll all have the same currentTerm
        Term term = courses.get(0).getTerm();
        // Start the URL with the currentTerm
        String url = "https://horizon.mcgill.ca/pban1/bwckcoms.P_Regs?term_in=" + term.toString();

        // Add random Minerva stuff that is apparently necessary
        url += "&RSTS_IN=DUMMY&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY" +
                "&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY" +
                "&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&REG_BTN=DUMMY&MESG=DUMMY";

        if (dropCourse) {
            for (Course course : courses) {
                url += "&RSTS_IN=DW&assoc_term_in=" + term.toString() + "&CRN_IN=" +
                        course.getCRN() + "&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&" +
                        "CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&" +
                        "TITLE=DUMMY&MESG=DUMMY";
            }
        }  else {
            url += "&RSTS_IN=&assoc_term_in=" + term.toString() + "&CRN_IN=DUMMY&" +
                    "start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&" +
                    "SEC=DUMMYLEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY";
        }

        // Lots of junk
        for (int i = 0; i < 7; i ++) {
            url += "&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY" +
		            "&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&" +
		            "GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY";
        }

        // More junk
        url += "&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY" +
                "&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY";

        // Insert the CRNs into the URL
        for (Course course : courses) {
            // Use a different URL if courses are being dropped
            if (!dropCourse) {
                url += "&RSTS_IN=RW&CRN_IN=" + course.getCRN();
            } else {
                url += "&RSTS_IN=&CRN_IN=";
            }
            url += "&assoc_term_in=&start_date_in=&end_date_in=";
        }

        url += "&regs_row=9&wait_row=0&add_row=10&REG_BTN=Submit+Changes";
        return url;
    }
}
