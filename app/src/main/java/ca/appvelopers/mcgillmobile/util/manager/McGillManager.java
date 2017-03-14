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

package ca.appvelopers.mcgillmobile.util.manager;

import org.threeten.bp.DayOfWeek;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.exception.MinervaException;
import ca.appvelopers.mcgillmobile.util.DayUtils;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.PasswordPreference;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.util.retrofit.CourseResultConverter;
import ca.appvelopers.mcgillmobile.util.retrofit.EbillConverter;
import ca.appvelopers.mcgillmobile.util.retrofit.McGillService;
import ca.appvelopers.mcgillmobile.util.retrofit.RegistrationErrorConverter;
import ca.appvelopers.mcgillmobile.util.retrofit.ScheduleConverter;
import ca.appvelopers.mcgillmobile.util.retrofit.TranscriptConverter;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
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
    /**
     * {@link UsernamePreference} instance
     */
    private final UsernamePreference usernamePref;
    /**
     * {@link PasswordPreference} passwordPref;
     */
    private final PasswordPreference passwordPref;
    /**
     * The {@link McGillService} instance
     */
    private final McGillService mcGillService;
    /**
     * True if the client has been initialized (in terms of the cookies), false otherwise
     */
    private boolean initialized;

	/**
	 * Default Constructor
     * @param loggingInterceptor {@link HttpLoggingInterceptor} instance
     * @param usernamePref       {@link UsernamePreference} instance
     * @param passwordPref       {@link PasswordPreference} instance
     */
    @Inject
	protected McGillManager(HttpLoggingInterceptor loggingInterceptor,
            UsernamePreference usernamePref, PasswordPreference passwordPref) {
        this.usernamePref = usernamePref;
        this.passwordPref = passwordPref;

        //Set up the client here in order to have access to the login methods
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        //Save the cookies per URL host
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        // Use the cookies for the given URL host
                        List<Cookie> cookies = cookieStore.get(url.host());

                        if (!initialized && cookies != null) {
                            // If there are cookies, then it's initialized
                            initialized = true;
                        } else if (cookies == null) {
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
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        //Get the request and the response
                        Request request = chain.request();
                        okhttp3.Response response = chain.proceed(request);

                        //If this is the login request, don't continue
                        if (request.method().equalsIgnoreCase("POST")) {
                            //This is counting on the fact that the only POST request is for login
                            return response;
                        }

                        //Go through the cookies
                        for (String cookie: response.headers().values("Set-Cookie")) {
                            //Check if one of the cookies is an empty Session Id
                            if (cookie.contains("SESSID=;")) {
                                //Try logging in (if there's an error, it will be thrown)
                                login();

                                //Successfully logged them back in, try retrieving the data again
                                return chain.proceed(request);
                            }
                        }

                        //If we have the session Id in the cookies, return the original response
                        return response;
                    }
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
	 * Attempts to log into Minerva
     * @param username Username to use for the login
     * @param password Password to use for the login
     * @throws IOException
     */
	public void login(String username, String password) throws IOException {
        //If it's not initialized, call login with nothing to set up the cookies
        if (!initialized) {
            mcGillService.login("", "").execute();
        }

        //Create the POST request with the given username and password
        String response = mcGillService.login(username, password).execute().body().string();

        if (!response.contains("WELCOME")) {
            //If we're not on the Welcome page, then the user entered wrong info
            throw new MinervaException();
        }
    }

    /**
     * Logs the user in with the stored username and password
     * @throws IOException
     */
    public void login() throws IOException {
        login(usernamePref.full(), passwordPref.get());
    }

    /**
     * Returns the URL to register for courses for the given parameters
     *
     * @param courses    A list of courses to (un)register for
     * @param dropCourse True if the user is dropping courses, false otherwise
     * @return The proper registration URL
     */
    public static String getRegistrationURL(List<? extends Course> courses, boolean dropCourse) {
        // Get the term from the first course (they'll all have the same term
        Term term = courses.get(0).getTerm();
        //Start the URL with the term
        String url = "https://horizon.mcgill.ca/pban1/bwckcoms.P_Regs?term_in=" + term.toString();

        //Add random Minerva stuff that is apparently necessary
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

        //Lots of junk
        for (int i = 0; i < 7; i ++) {
            url += "&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY" +
		            "&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&" +
		            "GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY";
        }

        //More junk
        url += "&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY" +
                "&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY";

        //Insert the CRNs into the URL
        for (Course course : courses) {
            //Use a different URL if courses are being dropped
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

    /**
     * Builds the Course Search URL String
     *
     * @return The course search URL to use for this course search
     */
    public static String getSearchURL(Term term, String subject, String courseNumber, String title,
            int minCredit, int maxCredit, int startHour, int startMinute, boolean startAM,
            int endHour, int endMinute, boolean endAM, List<DayOfWeek> days) {
        String url = "term_in=" + term.toString() +
                "&sel_subj=dummy" +
                "&sel_day=dummy" +
                "&sel_schd=dummy" +
                "&sel_insm=dummy" +
                "&sel_camp=dummy" +
                "&sel_levl=dummy" +
                "&sel_sess=dummy" +
                "&sel_instr=dummy" +
                "&sel_ptrm=dummy" +
                "&sel_attr=dummy" +
                "&sel_subj=" + subject +
                "&sel_crse=" + courseNumber +
                "&sel_title=" + title +
                "&sel_schd=%25" +
                "&sel_from_cred=" + minCredit +
                "&sel_to_cred=" + maxCredit +
                "&sel_levl=%25" +
                "&sel_ptrm=%25" +
                "&sel_instr=%25" +
                "&sel_attr=%25" +
                "&begin_hh=" + startHour +
                "&begin_mi=" + startMinute +
                "&begin_ap=" + (startAM ? 'a' : 'p') +
                "&end_hh=" + endHour +
                "&end_mi=" + endMinute +
                "&end_ap=" + (endAM ? 'a' : 'p');

        for (DayOfWeek day : days) {
            url += "&sel_day=" + DayUtils.getDayChar(day);
        }

        return url;
    }
}
