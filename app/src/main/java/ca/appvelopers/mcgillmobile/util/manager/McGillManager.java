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

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.util.Pair;

import com.guerinet.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.threeten.bp.DayOfWeek;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.ConnectionStatus;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.exception.MinervaException;
import ca.appvelopers.mcgillmobile.model.exception.NoInternetException;
import ca.appvelopers.mcgillmobile.model.prefs.CookiePreference;
import ca.appvelopers.mcgillmobile.model.prefs.PasswordPreference;
import ca.appvelopers.mcgillmobile.model.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.model.retrofit.McGillService;
import ca.appvelopers.mcgillmobile.util.DayUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import retrofit2.Call;
import timber.log.Timber;

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
     * The {@link McGillService} instance
     */
    @Inject
    protected McGillService mcGillService;
    /**
     * {@link ConnectivityManager} instance
     */
    @Inject
    protected ConnectivityManager connectivityManager;
    /**
     * {@link UsernamePreference} instance
     */
    @Inject
    protected UsernamePreference usernamePref;
    /**
     * {@link PasswordPreference} passwordPref;
     */
    @Inject
    protected PasswordPreference passwordPref;
    /**
     * The {@link CookiePreference} instance
     */
    @Inject
    protected CookiePreference cookiePref;

	/**
	 * Default Constructor
	 */
    @Inject
	protected McGillManager(Context context) {
        //Inject this to get the username and password from Dagger
        App.component(context).inject(this);
		//Set up the cookie handler
		CookieHandler.setDefault(new CookieManager());
	}

	/* HELPERS */

    public String get(Call<Response> call) throws NoInternetException, MinervaException, IOException {
        return get(call, true);
    }

    public String get(Call<Response> call, boolean autoLogin) throws NoInternetException, MinervaException, IOException {
        //Check if the user is connected to the internet
        if (!Utils.isConnected(connectivityManager)) {
            throw new NoInternetException();
        }

        //Make the call
        Response response = call.execute().body();

        //Check for Minerva logout
        List<String> setCookies = response.headers().values("Set-Cookie");
        for (String cookie: setCookies) {
            if (cookie.contains("SESSID=;")) {
                if (autoLogin) {
                    //We've been logged out of Minerva. Try logging back in if needed
                    ConnectionStatus status = login();

                    if (status == ConnectionStatus.OK) {
                        //Successfully logged them back in, try retrieving the stuff again
                        return get(call.clone(), false);
                    } else if (status == ConnectionStatus.NO_INTERNET) {
                        //No internet: show error
                        throw new NoInternetException();
                    } else if (status == ConnectionStatus.WRONG_INFO) {
                        //Wrong credentials: back to login screen
                        throw new MinervaException();
                    }
                } else {
                    //If not, throw the exception
                    throw new MinervaException();
                }
            }
        }

        //Get the response cookies and set them
        cookiePref.set(response.headers("Set-Cookie"));

        //Return the body in String format
        return response.body().string();
    }

	/**
	 * Attempts to log into Minerva
	 *
	 * @return The resulting connection status
	 */
	public ConnectionStatus login(String username, String password) {
        //Don't continue of the user is not connected to the internet
        if (!Utils.isConnected(connectivityManager)) {
            return ConnectionStatus.NO_INTERNET;
        }

		try {
			//1. Get Minerva's login page and determine the login parameters
            String response = mcGillService.loginPage().execute().body().string();

            List<Pair<String, String>> params = new ArrayList<>();

            //Parse the HTML document with JSoup
            Document doc = Jsoup.parse(response);

            //Google Form Id
            Elements forms = doc.getElementsByTag("form");
            //Go through the forms
            for (Element formElement : forms) {
                //Find the one with name 'loginform1'
                if (formElement.attr("name").equals("loginform1")) {
                    //Go through the input elements
                    for (Element inputElement : formElement.getElementsByTag("input")) {
                        //Get the key of the input element
                        String key = inputElement.attr("name");

                        if (key.equals("sid")) {
                            //Username
                            params.add(new Pair<>(key, username));
                        } else if (key.equals("PIN")) {
                            //Password
                            params.add(new Pair<>(key, password));
                        }
                    }

                }
            }

            //Get the POST params in String format (remove the '?' at the beginning
            final String body = Utils.getQuery(params).substring(1);

			//Search for "Authorization Failure"
			if (body.contains("WRONG_INFO")) {
				return ConnectionStatus.WRONG_INFO;
			}

			//2. Construct above post's content and then send a POST request for authentication
            response = mcGillService.login(new RequestBody() {
                @Override
                public MediaType contentType() {
                    return MediaType.parse("application/x-www-form-urlencoded");
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    sink.writeString(body, Charset.forName("UTF-8"));
                }
            }).execute().body().string();

			if (!response.contains("WELCOME")) {
                //If we're not on the Welcome page, then the user entered wrong info
				return ConnectionStatus.WRONG_INFO;
			}
		} catch (IOException e) {
			Timber.e(e, "Exception during login");
			return ConnectionStatus.ERROR_UNKNOWN;
		}

        return ConnectionStatus.OK;
    }

    public ConnectionStatus login() {
        return login(usernamePref.full(), passwordPref.get());
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
    public static String getRegistrationURL(Term term, List<Course> classes, boolean dropCourse) {
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
