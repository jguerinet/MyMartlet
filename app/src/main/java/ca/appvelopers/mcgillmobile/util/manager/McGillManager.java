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
import ca.appvelopers.mcgillmobile.model.prefs.PasswordPreference;
import ca.appvelopers.mcgillmobile.model.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.model.retrofit.CourseResultConverter;
import ca.appvelopers.mcgillmobile.model.retrofit.EbillConverter;
import ca.appvelopers.mcgillmobile.model.retrofit.McGillService;
import ca.appvelopers.mcgillmobile.model.retrofit.RegistrationErrorConverter;
import ca.appvelopers.mcgillmobile.model.retrofit.ScheduleConverter;
import ca.appvelopers.mcgillmobile.model.retrofit.TranscriptConverter;
import ca.appvelopers.mcgillmobile.util.DayUtils;
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
                        //Use the cookies for the given URL host
                        List<Cookie> cookies = cookieStore.get(url.host());

                        //If there are cookies, then it's initialized
                        if (!initialized && cookies != null) {
                            initialized = true;
                        }

                        return cookies == null ? new ArrayList<Cookie>() : cookies;
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
                .addConverterFactory(new CourseResultConverter())
                .addConverterFactory(new EbillConverter())
                .addConverterFactory(new RegistrationErrorConverter())
                .addConverterFactory(new ScheduleConverter())
                .addConverterFactory(new TranscriptConverter())
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

    /* URL BUILDERS */

    /**
     * Returns the URL to register for courses for the given parameters
     *
     * @param term       The course term
     * @param classes    A list of classes to (un)register for
     * @param dropCourse True if the user is dropping courses, false otherwise
     * @return The proper registration URL
     */
    public static String getRegistrationURL(Term term, List<Course> classes,
            boolean dropCourse) {
        String registrationURL = term.toString();

        //Add random Minerva stuff that is apparently necessary
        registrationURL += "&RSTS_IN=DUMMY&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY" +
                "&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY" +
                "&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&REG_BTN=DUMMY&MESG=DUMMY";

        if(dropCourse){
            for(Course classItem : classes){
                registrationURL += "&RSTS_IN=DW&assoc_term_in=" + term.toString() +
                        "&CRN_IN=" + classItem.getCRN() + "&start_date_in=DUMMY&end_date_in=DUMMY" +
                        "&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&" +
                        "TITLE=DUMMY&MESG=DUMMY";
            }
        }
        else{
            registrationURL += "&RSTS_IN=&assoc_term_in=" + term.toString() +
		            "&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&" +
                    "SEC=DUMMYLEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY";
        }

        //Lots of junk
        for(int i = 0; i < 7; i++){
            registrationURL += "&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY" +
		            "&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&" +
		            "GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY";
        }

        //More junk
        registrationURL += "&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY" +
                "&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&" +
		        "GMOD=DUMMY&TITLE=DUMMY";

        //Insert the CRNs into the URL
        for(Course classItem : classes){
            //Use a different URL if courses are being dropped
            if(!dropCourse){
                registrationURL += "&RSTS_IN=RW&CRN_IN=" + classItem.getCRN();
            }
            else{
                registrationURL += "&RSTS_IN=&CRN_IN=";
            }
            registrationURL += "&assoc_term_in=&start_date_in=&end_date_in=";
        }

        registrationURL += "&regs_row=9&wait_row=0&add_row=10&REG_BTN=Submit+Changes";
        return registrationURL;
    }

	/**
	 * Builder for the course search URL
	 */
	public static class SearchURLBuilder {
		/**
		 * Course term
		 */
		private Term term;
		/**
		 * Course subject
		 */
		private String subject;
		/**
		 * Course number
		 */
		private String courseNumber = "";
		/**
		 * Course title
		 */
		private String title = "";
		/**
		 * Course min credit
		 */
		private int minCredit = 0;
		/**
		 * Course max credit
		 */
		private int maxCredit = 0;
		/**
		 * Course start hour
		 */
		private int startHour = 0;
		/**
		 * Course start minute
		 */
		private int startMinute = 0;
		/**
		 * True if the start time is AM, false otherwise
		 */
		private boolean startAM = true;
		/**
		 * Course end hour
		 */
		private int endHour = 0;
		/**
		 * Course end minute
		 */
		private int endMinute = 0;
		/**
		 * True if the end time is AM, false otherwise
		 */
		private boolean endAM = true;
		/**
		 * Course days
		 */
		private List<DayOfWeek> days = new ArrayList<>();

		/**
		 * Default Constructor
		 *
		 * @param term    Course term
		 * @param subject Course subject
		 */
		public SearchURLBuilder(Term term, String subject) {
			this.term = term;
			this.subject = subject;
		}

		/* SETTERS */

		/**
		 * @param courseNumber Course number
		 * @return The builder instance
		 */
		public SearchURLBuilder courseNumber(String courseNumber) {
			this.courseNumber = courseNumber;
			return this;
		}

		/**
		 * @param title Course title
		 * @return The builder instance
		 */
		public SearchURLBuilder title(String title) {
			this.title = title;
			return this;
		}

		/**
		 * @param minCredit Course min credits
		 * @return The builder instance
		 */
		public SearchURLBuilder minCredits(int minCredit) {
			this.minCredit = minCredit;
			return this;
		}

		/**
		 * @param maxCredit Course max credits
		 * @return The builder instance
		 */
		public SearchURLBuilder maxCredits(int maxCredit) {
			this.maxCredit = maxCredit;
			return this;
		}

		/**
		 * @param startHour Course start hour
		 * @return The builder instance
		 */
		public SearchURLBuilder startHour(int startHour) {
			this.startHour = startHour;
			return this;
		}

		/**
		 * @param startMinute Course start minute
		 * @return The builder instance
		 */
		public SearchURLBuilder startMinute(int startMinute) {
			this.startMinute = startMinute;
			return this;
		}

		/**
		 * @param startAM True if the course is AM, false if PM
		 * @return The builder instance
		 */
		public SearchURLBuilder startAM(boolean startAM) {
			this.startAM = startAM;
			return this;
		}

		/**
		 * @param endHour Course end hour
		 * @return The builder instance
		 */
		public SearchURLBuilder endHour(int endHour) {
			this.endHour = endHour;
			return this;
		}

		/**
		 * @param endMinute Course end minute
		 * @return The builder instance
		 */
		public SearchURLBuilder endMinute(int endMinute) {
			this.endMinute = endMinute;
			return this;
		}

		/**
		 * @param endAM True if the ending time is AM, false if PM
		 * @return The builder instance
		 */
		public SearchURLBuilder endAM(boolean endAM) {
			this.endAM = endAM;
			return this;
		}

		/**
		 * @param day Adds a course day
		 * @return The builder instance
		 */
		public SearchURLBuilder addDay(DayOfWeek day) {
			this.days.add(day);
			return this;
		}

		/**
		 * Builds the Course Search URL String
		 *
		 * @return The course search URL to use for this course search
		 */
		public String build() {
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
}
