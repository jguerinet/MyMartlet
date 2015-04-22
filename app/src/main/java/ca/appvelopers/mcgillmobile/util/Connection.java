/*
 * Copyright 2014-2015 Appvelopers
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

package ca.appvelopers.mcgillmobile.util;

import android.util.Log;

import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.ConnectionStatus;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Day;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.exception.MinervaException;
import ca.appvelopers.mcgillmobile.model.exception.NoInternetException;
import okio.BufferedSink;

/**
 * All of the connection logic
 * @author Shabbir Hussain
 * @author Rafi Uddin
 * @author Joshua David Alfaro
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class Connection {
	private static final String TAG = "Connection";
	/**
	 * Login URL
	 */
	private static final String LOGIN_PAGE_URL =
			"https://horizon.mcgill.ca/pban1/twbkwbis.P_WWWLogin";
	/**
	 * Login POST URL
	 */
	private static final String LOGIN_POST_URL =
			"https://horizon.mcgill.ca/pban1/twbkwbis.P_ValLogin";
	/**
	 * Schedule URL
	 */
    private static final String SCHEDULE_URL =
			"https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchdDetl?term_in=";
	/**
	 * Transcript URL
	 */
	public static final String TRANSCRIPT_URL =
			"https://horizon.mcgill.ca/pban1/bzsktran.P_Display_Form?user_type=S&tran_type=V";
	/**
	 * Ebill URL
	 */
	public static final String EBILL_URL = "https://horizon.mcgill.ca/pban1/bztkcbil.pm_viewbills";
	/**
	 * Course Search URL
	 */
    private static final String COURSE_SEARCH_URL =
			"https://horizon.mcgill.ca/pban1/bwskfcls.P_GetCrse?";
	/**
	 * Course Registration URL
	 */
    private static final String COURSE_REGISTRATION_URL =
			"https://horizon.mcgill.ca/pban1/bwckcoms.P_Regs?term_in=";
	/**
	 * Course Registration Errors URL
	 */
	public static final String REGISTRATION_ERROR_URL =
			"http://www.is.mcgill.ca/whelp/sis_help/rg_errors.htm";
	/**
	 * Singleton instance
	 */
	private static Connection connection;
	/**
	 * The user's username
	 */
	private String mUsername;
	/**
	 * The user's password
	 */
	private String mPassword;
	/**
	 * The list of cookies
	 */
	private List<String> mCookies;
	/**
	 * The OkHttpClient
	 */
	private OkHttpClient mClient;

	/**
	 * @return The connection instance
	 */
	public static Connection getInstance(){
		//If the connection is null, create it
		if(connection == null){
			connection = new Connection();
		}
		return connection;
	}

	/**
	 * Default Constructor
	 */
	private Connection(){
		//Get the username and password from the SharedPrefs
		this.mUsername = Load.loadFullUsername(App.getContext());
		this.mPassword = Load.loadPassword(App.getContext());
		//Set up the client
		this.mClient = new OkHttpClient();
		//Set up the list of cookies
		this.mCookies = new ArrayList<>();
		//Set up the cookie handler
		CookieHandler.setDefault(new CookieManager());
	}

	/* SETTERS */

	/**
	 * @param username The username to use
	 */
    public void setUsername(String username){
        this.mUsername = username;
    }

	/**
	 * @param password The password to use
	 */
    public void setPassword(String password){
        this.mPassword = password;
    }

	/* HELPERS */

	/**
	 * Sends a GET request and returns the body in String format
	 *
	 * @param url       The URL
	 * @param autoLogin True if we should try reconnecting the user automatically, false otherwise
	 * @return The page contents in String format
	 * @throws MinervaException
	 * @throws IOException
	 * @throws NoInternetException
	 */
	public String get(String url, boolean autoLogin) throws MinervaException, IOException,
			NoInternetException{
		//Check if the user is connected to the internet
		if(!Help.isConnected()){
			throw new NoInternetException();
		}

		//Create the request
		Request.Builder builder = getDefaultRequest(url).get();

		//Add the cookies if there are any
		for(String cookie : mCookies){
			builder.addHeader("Cookie", cookie.split(";", 1)[0]);
		}

		Log.d(TAG, "Sending 'GET' request to: " + url);

		//Execute the request, get the response
		Response response = mClient.newCall(builder.build()).execute();

		int responseCode = response.code();
		Log.d(TAG, "Response Code: " + responseCode);

		//Check the response code
		switch(responseCode){
			//If this is a redirect, go to the new link
			case HttpsURLConnection.HTTP_MOVED_TEMP:
			case HttpsURLConnection.HTTP_MOVED_PERM:
				String nextLocation = response.header("Location");
				return get(nextLocation, autoLogin);
			default:
				//All is ignored, carry on
				break;
		}

		//Check the headers
		if(!validateHeaders(response.headers())){
			//We've been logged out of Minerva. Try logging back in if needed
			if(autoLogin){
				//Launch the login process
				final ConnectionStatus status = login();

				//Successfully logged them back in, try retrieving the stuff again
				if(status == ConnectionStatus.OK){
					return get(url, false);
				}
				//No internet: show no internet dialog
				else if(status == ConnectionStatus.NO_INTERNET){
					throw new NoInternetException();
				}
				//Wrong credentials: back to login screen
				else if(status == ConnectionStatus.WRONG_INFO){
					throw new MinervaException();
				}
			}
			//If not, throw the exception
			else{
				throw new MinervaException();
			}
		}

		//Get the response cookies and set them
		mCookies = response.headers("Set-Cookie");

		//Return the body in String format
		return response.body().string();
	}

	/**
	 * Sends a GET request and returns the body in String format with auto-login enabled
	 *
	 * @param url The URL
	 * @return The page contents in String format
	 * @throws MinervaException
	 * @throws IOException
	 * @throws NoInternetException
	 */
	public String get(String url) throws MinervaException, IOException,
			NoInternetException{
		return get(url, true);
	}

	/**
	 *  Sends a post request and returns the response body in String format
	 *
	 * @param url        The URL
	 * @param referer    The referer
	 * @param postParams The post parameters
	 * @return The response body in String format
	 * @throws IOException
	 */
	private String post(String url, String referer, final String postParams) throws IOException{
		//Create the request
		Request.Builder builder = getDefaultRequest(url)
				.post(new RequestBody() {
					@Override
					public MediaType contentType(){
						return MediaType.parse("application/x-www-form-urlencoded");
					}

					@Override
					public void writeTo(BufferedSink sink) throws IOException{
						sink.writeString(postParams, Charset.forName("UTF-8"));
					}
				})
				.header("Host", "horizon.mcgill.ca")
				.header("Origin", "https://horizon.mcgill.ca")
				.header("DNT", "1")
				.header("Connection", "keep-alive")
				.header("Referer", referer);

		//Add the cookies if there are any
		for(String cookie : mCookies){
			builder.addHeader("Cookie", cookie.split(";", 1)[0]);
		}

		Log.d(TAG, "Sending 'POST' request to: " + url);

		//Execute the request, get the result
		Response response = mClient.newCall(builder.build()).execute();

		Log.d(TAG, "Response Code: " + String.valueOf(response.code()));

		//Return the response body
		return response.body().string();
	}

	/**
	 * Attempts to log into Minerva
	 *
	 * @return The resulting connection status
	 */
	public ConnectionStatus login(){
		try {
			//1. Get Minerva's login page and determine the login parameters
			String postParams = getLoginParameters(get(LOGIN_PAGE_URL, false));
			
			//Search for "Authorization Failure"
			if(postParams.contains("WRONG_INFO")){
				return ConnectionStatus.WRONG_INFO;
			}

			//2. Construct above post's content and then send a POST request for authentication
			String response = post(LOGIN_POST_URL, LOGIN_PAGE_URL, postParams);

			//Check that the connection was actually made
			if (!response.contains("WELCOME")){
				return ConnectionStatus.WRONG_INFO;
			}
		} catch (MinervaException e) {
			//This should never happen
			Log.e(TAG, "MinervaException during login", e);
			return ConnectionStatus.MINERVA_LOGOUT;
		} catch (IOException e) {
			Log.e(TAG, "IOException during login", e);
			return ConnectionStatus.ERROR_UNKNOWN;
		} catch(NoInternetException e){
			return ConnectionStatus.NO_INTERNET;
		}

        return ConnectionStatus.OK;
    }

	/**
	 * Gets the parameters to use for logging into Minerva
	 *
	 * @param html The login HTML page
	 * @return String representing the parameters to use for logging into Minerva
	 * @throws UnsupportedEncodingException
	 */
	private String getLoginParameters(String html) throws UnsupportedEncodingException{
		List<String> params = new ArrayList<>();
		Log.d(TAG, "Extracting form's data...");

		//Parse the HTML document with JSoup
		Document doc = Jsoup.parse(html);

		//Google Form Id
		Elements forms = doc.getElementsByTag("form");
		//Go through the forms
		for (Element formElement : forms) {
			//Find the one with name 'loginform1'
			if (formElement.attr("name").equals("loginform1")){
				//Go through the input elements
				for (Element inputElement : formElement.getElementsByTag("input")){
					//Get the key of the input element
					String key = inputElement.attr("name");

					//Find the username and password elements
					if(key.equals("sid") || key.equals("PIN")){
						String value;
						//Username
						if(key.equals("sid")){
							value = mUsername;
						}
						//Password
						else{
							value = mPassword;
						}

						//Add this to the list if params
						params.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
					}
				}

			}
		}

		//Go through the parameters
		StringBuilder result = new StringBuilder();
		for (String param : params){
			//No & for the first parameter
			if (result.length() == 0){
				result.append(param);
			}
			else{
				result.append("&").append(param);
			}
		}

		return result.toString();
	}

	/**
	 * Gets the request builder with the default headers set up
	 *
	 * @param url The URL to connect to
	 * @return The request builder
	 */
	private Request.Builder getDefaultRequest(String url){
		return new Request.Builder()
				.url(url)
				.cacheControl(new CacheControl.Builder().noCache().build())
				.header("User-Agent", "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) " +
						"AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> " +
						"Mobile Safari/<WebKit Rev>")
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.header("Accept-Language", "en-US,en;q=0.5");
	}

	/**
	 * Check the headers for a bad connection
	 *
	 * @param headers The headers received from the response
	 * @return True if the headers are validated, false otherwise
	 */
    private boolean validateHeaders(Headers headers){
    	//Check for Minerva logout
    	List<String> setCookies = headers.values("Set-Cookie");
    	for(String cookie: setCookies){
	    	if(cookie.contains("SESSID=;")){
			    return false;
	    	}
    	}
    	return true;
    }

    /* URL BUILDERS */

	/**
	 * Returns the URL to download the schedule for a given term
	 *
	 * @param term The term
	 * @return The schedule URL
	 */
    public static String getScheduleURL(Term term){
        return Connection.SCHEDULE_URL + term.getYear() + term.getSeason().getSeasonNumber();
    }

    /**
     * Returns the URL to register for courses for the given parameters
     *
     * @param term       The course term
     * @param classes    A list of classes to (un)register for
     * @param dropCourse True if the user is dropping courses, false otherwise
     * @return The proper registration URL
     */
    public static String getRegistrationURL(Term term, List<Course> classes, boolean dropCourse){
        String registrationURL = COURSE_REGISTRATION_URL + term.getYear() +
		        term.getSeason().getSeasonNumber();

        //Add random Minerva stuff that is apparently necessary
        registrationURL += "&RSTS_IN=DUMMY&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY" +
                "&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY" +
                "&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&REG_BTN=DUMMY&MESG=DUMMY";

        if(dropCourse){
            for(Course classItem : classes){
                registrationURL += "&RSTS_IN=DW&assoc_term_in=" + term.getYear() +
		                term.getSeason().getSeasonNumber() + "&CRN_IN=" + classItem.getCRN() +
                        "&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY" +
                        "&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY";
            }
        }
        else{
            registrationURL += "&RSTS_IN=&assoc_term_in=" + term.getYear() +
		            term.getSeason().getSeasonNumber() + "&CRN_IN=DUMMY&start_date_in=DUMMY&" +
		            "end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMYLEVL=DUMMY&CRED=DUMMY&" +
		            "GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY";
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
		 * The course term
		 */
		private Term mTerm;
		/**
		 * The course subject
		 */
		private String mSubject;
		/**
		 * The course number
		 */
		private String mCourseNumber = "";
		/**
		 * The course title
		 */
		private String mTitle = "";
		/**
		 * The course min credit
		 */
		private int mMinCredit = 0;
		/**
		 * The course max credit
		 */
		private int mMaxCredit = 0;
		/**
		 * The course start hour
		 */
		private int mStartHour = 0;
		/**
		 * The course start minute
		 */
		private int mStartMinute = 0;
		/**
		 * True if the start time is AM, false otherwise
		 */
		private boolean mStartAM = true;
		/**
		 * The course end hour
		 */
		private int mEndHour = 0;
		/**
		 * The course end minute
		 */
		private int mEndMinute = 0;
		/**
		 * True if the end time is AM, false otherwise
		 */
		private boolean mEndAM = true;
		/**
		 * The course days
		 */
		private List<Day> mDays = new ArrayList<>();

		/**
		 * Default Constructor
		 *
		 * @param term    The course term
		 * @param subject The course subject
		 */
		public SearchURLBuilder(Term term, String subject){
			this.mTerm = term;
			this.mSubject = subject;
		}

		/* SETTERS */

		/**
		 * @param courseNumber Sets the course number
		 * @return The builder instance
		 */
		public SearchURLBuilder courseNumber(String courseNumber){
			this.mCourseNumber = courseNumber;
			return this;
		}

		/**
		 * @param title Sets the course title
		 * @return The builder instance
		 */
		public SearchURLBuilder title(String title){
			this.mTitle = title;
			return this;
		}

		/**
		 * @param minCredit Sets the course min credits
		 * @return The builder instance
		 */
		public SearchURLBuilder minCredits(int minCredit){
			this.mMinCredit = minCredit;
			return this;
		}

		/**
		 * @param maxCredit Sets the course max credits
		 * @return The builder instance
		 */
		public SearchURLBuilder maxCredits(int maxCredit){
			this.mMaxCredit = maxCredit;
			return this;
		}

		/**
		 * @param startHour Sest the course start hour
		 * @return The builder instance
		 */
		public SearchURLBuilder startHour(int startHour){
			this.mStartHour = startHour;
			return this;
		}

		/**
		 * @param startMinute Sets the course start minute
		 * @return The builder instance
		 */
		public SearchURLBuilder startMinute(int startMinute){
			this.mStartMinute = startMinute;
			return this;
		}

		/**
		 * @param startAM Sets if the course starting time is AM or PM
		 * @return The builder instance
		 */
		public SearchURLBuilder startAM(boolean startAM){
			this.mStartAM = startAM;
			return this;
		}

		/**
		 * @param endHour Sets the course end hour
		 * @return The builder instance
		 */
		public SearchURLBuilder endHour(int endHour){
			this.mEndHour = endHour;
			return this;
		}

		/**
		 * @param endMinute Sets the course end minute
		 * @return The builder instance
		 */
		public SearchURLBuilder endMinute(int endMinute){
			this.mEndMinute = endMinute;
			return this;
		}

		/**
		 * @param endAM Sets if the course ending time is AM or PM
		 * @return The builder instance
		 */
		public SearchURLBuilder endAM(boolean endAM){
			this.mEndAM = endAM;
			return this;
		}

		/**
		 * @param day Adds a course day
		 * @return The builder instance
		 */
		public SearchURLBuilder addDay(Day day){
			this.mDays.add(day);
			return this;
		}

		/**
		 * Builds the Course Search URL String
		 *
		 * @return The course search URL to use for this course search
		 */
		public String build(){
			String url = COURSE_SEARCH_URL +
					"term_in=" + mTerm.getYear() + mTerm.getSeason().getSeasonNumber() +
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
					"&sel_subj=" + mSubject +
					"&sel_crse=" + mCourseNumber +
					"&sel_title=" + mTitle +
					"&sel_schd=%25" +
					"&sel_from_cred=" + mMinCredit +
					"&sel_to_cred=" + mMaxCredit +
					"&sel_levl=%25" +
					"&sel_ptrm=%25" +
					"&sel_instr=%25" +
					"&sel_attr=%25" +
					"&begin_hh=" + mStartHour +
					"&begin_mi=" + mStartMinute +
					"&begin_ap=" + (mStartAM ? 'a' : 'p') +
					"&end_hh=" + mEndHour +
					"&end_mi=" + mEndMinute +
					"&end_ap=" + (mEndAM ? 'a' : 'p');

			for(Day day : mDays){
				url += "&sel_day=" + day.getDayChar();
			}

			return url;
		}
	}
}
