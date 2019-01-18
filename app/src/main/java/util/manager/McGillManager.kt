/*
 * Copyright 2014-2019 Julien Guerinet
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

package com.guerinet.mymartlet.util.manager

import com.guerinet.mymartlet.model.Course
import com.guerinet.mymartlet.model.exception.MinervaException
import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.prefs.UsernamePref
import com.guerinet.mymartlet.util.retrofit.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.orhanobut.hawk.Hawk
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 * All of the McGill connection logic
 *
 * @author Shabbir Hussain
 * @author Rafi Uddin
 * @author Joshua David Alfaro
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @param loggingInterceptor    [HttpLoggingInterceptor] instance
 * @param usernamePref          [UsernamePref] instance
 */
class McGillManager(loggingInterceptor: HttpLoggingInterceptor,
        private val usernamePref: UsernamePref) {

    val mcGillService: McGillService

    // Stores McGill related cookies
    private val cookieJar = object : CookieJar {
        private val cookieStore = mutableMapOf<String, List<Cookie>>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            // Save the cookies per URL host
            cookieStore[url.host()] = cookies
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            // Use the cookies for the given URL host (if none, use an empty list)
            val cookies = cookieStore[url.host()] ?: mutableListOf()

            // Go through the cookies and remove the proxy ones
            return cookies.filter { !it.name().toLowerCase().contains("proxy") }
        }
    }

    init {
        // Set up the client here in order to have access to the login methods
        val client = OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    // Get the request and the response
                    val request = chain.request()
                    val response = chain.proceed(request)

                    // If this is the login request, don't continue
                    if (request.method().equals("POST", ignoreCase = true)) {
                        // This is counting on the fact that the only POST request is for login
                        response
                    } else {
                        // Go through the cookies
                        val cookie = response.headers().values("Set-Cookie")
                            // Filter the cookies to check if there is an empty session Id
                            .firstOrNull { it.contains("SESSID=;") }

                        if (cookie != null) {
                            // Try logging in (if there's an error, it will be thrown)
                            login()

                            // Successfully logged them back in, try retrieving the data again
                            chain.proceed(request)
                        } else {
                            // If we have the session Id in the cookies, return original response
                            response
                        }
                    }
                }
                .build()

        mcGillService = Retrofit.Builder()
            .client(client)
            .baseUrl("https://horizon.mcgill.ca/pban1/")
            .addConverterFactory(ScheduleConverter())
            .addConverterFactory(TranscriptConverter())
            .addConverterFactory(EbillConverter())
            .addConverterFactory(CourseResultConverter())
            .addConverterFactory(RegistrationErrorConverter())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(McGillService::class.java)
    }

    /* HELPERS */

    /**
     * Returns a [Result] describing whether the login was successful or not given the [response]
     */
    private fun handleLogin(response: Response<ResponseBody>): Result {
        // Get the body, error out if empty
        val body = response.body()?.string() ?: return Result.Failure(MinervaException())

        if (!body.contains("WELCOME")) {
            // If we're not on the Welcome page, then the user entered wrong info
            return Result.Failure(MinervaException())
        }
        return Result.EmptySuccess()
    }

    /**
     * Initializes the [McGillService] because a call needs to be made before anything
     * happens for some reason
     */
    fun init() {
        // Create a blank call when initializing because the first call never seems to work
        try {
            mcGillService.login("", "").execute()
        } catch (ignored: IOException) {
        }
    }

    /**
     * Attempts to log into Minerva asynchronously with the [username], [password], and returns the
     *  corresponding [Result]
     */
    fun login(username: String, password: String): Result {
        return try {
            val response = mcGillService.login(username, password).execute()
            handleLogin(response)
        } catch (e: IOException) {
            Result.Failure(e)
        }
    }

    /**
     * Attempts to log the user in with the stored username and password and returns the
     *  corresponding result
     */
    fun login(): Result {
        val username = usernamePref.full ?: ""
        val password: String = Hawk.get(Prefs.PASSWORD)

        // Create the POST request with the given username and password and handle the response
        return handleLogin(mcGillService.login(username, password).execute())
    }

    companion object {

        /**
         * Prepares and returns the Url to use to (un)register [courses] (we are registering
         *  or unregistering based on [isUnregistering]
         */
        fun getRegistrationURL(courses: List<Course>, isUnregistering: Boolean): String {
            // Get the currentTerm from the first course (they'll all have the same currentTerm)
            val term = courses[0].term
            // Start the URL with the currentTerm and a bunch of apparently necessary junk
            var url = """https://horizon.mcgill.ca/pban1/bwckcoms.P_Regs?term_in=$term
                &RSTS_IN=DUMMY&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY
                &end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY
                &CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&REG_BTN=DUMMY&MESG=DUMMY
                """

            if (isUnregistering) {
                courses.forEach {
                    url += """&RSTS_IN=DW&assoc_term_in=$term&CRN_IN=${it.crn}&start_date_in=DUMMY
                        &end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&
                        GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY"""
                }
            } else {
                url += """&RSTS_IN=&assoc_term_in=$term&CRN_IN=DUMMY&start_date_in=DUMMY
                    &end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMYLEVL=DUMMY&CRED=DUMMY&
                    GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY"""
            }

            // Lots of junk
            for (i in 0..6) {
                url += """&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY
                    &end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&
                    GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY"""
            }

            // More junk
            url += """&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY&
                end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY
                &TITLE=DUMMY"""

            // Insert the CRNs into the URL
            courses.forEach {
                // Use a different URL if courses are being dropped
                url += """&RSTS_IN=${if (isUnregistering) "" else "RW"}RW&CRN_IN=${it.crn}
                    &assoc_term_in=&start_date_in=&end_date_in="""
            }

            url += "&regs_row=9&wait_row=0&add_row=10&REG_BTN=Submit+Changes"
            return url.trimMargin()
        }
    }
}
