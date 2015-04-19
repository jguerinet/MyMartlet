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
import ca.appvelopers.mcgillmobile.exception.MinervaLoggedOutException;
import ca.appvelopers.mcgillmobile.exception.NoInternetException;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.ConnectionStatus;
import ca.appvelopers.mcgillmobile.object.Term;
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
	 * The current connection status
	 */
	private ConnectionStatus mStatus;

	/* URL CONSTANTS */
	/**
	 * Minerva host
	 */
    private static final String MINERVA_HOST = "horizon.mcgill.ca";
	/**
	 * Minerva base URL
	 */
	private static final String MINERVA_ORIGIN = "https://horizon.mcgill.ca";
	/**
	 * Login URL
	 */
	private static final String LOGIN_PAGE = "https://horizon.mcgill.ca/pban1/twbkwbis.P_WWWLogin";
	/**
	 * Login POST URL
	 */
	private static final String LOGIN_POST = "https://horizon.mcgill.ca/pban1/twbkwbis.P_ValLogin";
	/**
	 * Schedule URL
	 */
    private static final String SCHEDULE = "https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchdDetl?term_in=";
	/**
	 * Transcript URL
	 */
	public static final String TRANSCRIPT =
			"https://horizon.mcgill.ca/pban1/bzsktran.P_Display_Form?user_type=S&tran_type=V";
	/**
	 * Ebill URL
	 */
	public static final String EBILL = "https://horizon.mcgill.ca/pban1/bztkcbil.pm_viewbills";
	/**
	 * Course Search URL
	 */
    private static final String COURSE_SEARCH =
			"https://horizon.mcgill.ca/pban1/bwskfcls.P_GetCrse?";
	/**
	 * Course Registration URL
	 */
    private static final String COURSE_REGISTRATION = "https://horizon.mcgill.ca/pban1/bwckcoms.P_Regs?term_in=";
	/**
	 * Course Registration Errors URL
	 */
	public static final String REGISTRATION_ERROR = "http://www.is.mcgill.ca/whelp/sis_help/rg_errors.htm";
	/**
	 * User Agent
	 */
    private final String USER_AGENT = "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) " +
			"AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> " +
			"Mobile Safari/<WebKit Rev>";
	/**
	 * Singleton instance
	 */
	private static Connection connection;

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
		//Set the status to first access when instantiating the connection singleton object
		this.mStatus = ConnectionStatus.CONNECTION_FIRST_ACCESS;
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

	/* GETTERS */

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
	 * Attempts to connect to Minerva
	 *
	 * @return The resulting connection status
	 */
	public ConnectionStatus connectToMinerva(){
		//Update the status to authenticating
    	mStatus = ConnectionStatus.AUTHENTICATING;
		try {
			//1. Get Minerva's login page and determine the login parameters
			String postParams = getLoginParameters(get(LOGIN_PAGE, false));
			
			//Search for "Authorization Failure"
			if(postParams.contains("WRONG_INFO")){
				mStatus = ConnectionStatus.WRONG_INFO;
				return ConnectionStatus.WRONG_INFO;
			}

			//2. Construct above post's content and then send a POST request for authentication
			String response = post(LOGIN_POST, LOGIN_PAGE, postParams);

			//Check that the connection was actually made
			if (!response.contains("WELCOME")){
				mStatus = ConnectionStatus.WRONG_INFO;
				return ConnectionStatus.WRONG_INFO;
			}
		} catch (MinervaLoggedOutException e) {
			//This should never happen
			Log.e(TAG, "MinervaLoggedOutException during login", e);
			mStatus = ConnectionStatus.MINERVA_LOGOUT;
			return ConnectionStatus.MINERVA_LOGOUT;
		} catch (IOException e) {
			Log.e(TAG, "IOException during login", e);
            mStatus = ConnectionStatus.ERROR_UNKNOWN;
			return ConnectionStatus.ERROR_UNKNOWN;
		} catch(NoInternetException e){
			mStatus = ConnectionStatus.NO_INTERNET;
			return ConnectionStatus.NO_INTERNET;
		}

		mStatus = ConnectionStatus.OK;
        return ConnectionStatus.OK;
    }
	 
	/**
	 * Sends a GET request and returns the body in String format
	 *
	 * @param url       The URL
	 * @param autoLogin True if we should try reconnecting the user automatically, false otherwise
	 * @return The page contents in String format
	 * @throws MinervaLoggedOutException
	 * @throws IOException
	 * @throws NoInternetException
	 */
	public String get(String url, boolean autoLogin) throws MinervaLoggedOutException, IOException,
			NoInternetException{
		//Check if the user is connected to the internet
		if(!Help.isConnected()){
			throw new NoInternetException();
		}

		//Create the request
		Request.Builder builder = new Request.Builder()
				.get()
				.url(url)
				.cacheControl(new CacheControl.Builder().noCache().build())
				.header("User-Agent", USER_AGENT)
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.header("Accept-Language", "en-US,en;q=0.5");

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
			switch(mStatus){
				//We've been logged out of Minerva
				case MINERVA_LOGOUT:
					//Try logging back in if needed
					if(autoLogin){
						//Launch the login process
						final ConnectionStatus status = connectToMinerva();

						//Successfully logged them back in, try retrieving the stuff again
						if(status == ConnectionStatus.OK){
							//TODO Catch exceptions here ?
							return connection.get(url, false);
						}
						//No internet: show no internet dialog
						else if(status == ConnectionStatus.NO_INTERNET){
							throw new NoInternetException();
						}
						//Wrong credentials: back to login screen
						else if(status == ConnectionStatus.WRONG_INFO){
							//TODO Locally broadcast this
							return null;
						}
					}
					//If not, throw the exception
					else{
						throw new MinervaLoggedOutException();
					}
				default:
					break;
			}
		}

		//Get the response cookies and set them
		setCookies(response.headers("Set-Cookie"));

		//Return the body in String format
		return response.body().string();
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
		Request.Builder builder = new Request.Builder()
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
				.url(url)
				.cacheControl(new CacheControl.Builder().noCache().build())
				.header("Host", MINERVA_HOST)
				.header("Origin", MINERVA_ORIGIN)
				.header("DNT", "1")
				.header("User-Agent", USER_AGENT)
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0" +
						".8")
				.header("Accept-Language", "en-US,en;q=0.5")
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
	 
	public void setCookies(List<String> cookies) {
		this.mCookies = cookies;
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
	    		if(mStatus != ConnectionStatus.AUTHENTICATING){
				    //If we're not trying to authenticate, update the status to logged out
				    mStatus = ConnectionStatus.MINERVA_LOGOUT;
			    }
	    		return false;
	    	}
    	}
    	return true;
    }

    /* URL Builders */

    public static String getScheduleURL(Term term){
        return Connection.SCHEDULE + term.getYear() + term.getSeason().getSeasonNumber();
    }

    /**
     * Get the URL to look for courses for the given parameters
     * @param term The course term
     * @param subject The course subject
     * @param courseNumber The course number
     * @return The proper search URL
     */
    public static String getCourseURL(Term term, String subject, String courseNumber,
                                      String title, int minCredit, int maxCredit, int startHour,
                                      int startMinute, char startAMPM, int endHour, int endMinute,
                                      char endAMPM, List<String> days){

        String courseSearchURL = COURSE_SEARCH
                + "term_in=" + term.getYear() + term.getSeason().getSeasonNumber() +
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
                "&begin_ap=" + startAMPM +
                "&end_hh=" + endHour +
                "&end_mi=" + endMinute +
                "&end_ap=" + endAMPM;
        
        if(days!=null){
	        for(String day : days){
	            courseSearchURL += "&sel_day=" + day;
	        }
        }

        return courseSearchURL;
    }

    /**
     * Get the URL to look for courses for the given parameters
     * @param term The course term
     * @param classes A list of classes to (un)register for
     * @param dropCourse Changes URL if courses are being dropped
     * @return The proper search URL
     */
    public static String getRegistrationURL(Term term, List<ClassItem> classes, boolean dropCourse){
        String registrationURL;
        registrationURL = COURSE_REGISTRATION + term.getYear() + term.getSeason().getSeasonNumber();

        //Add random Minerva crap that is apparently necessary
        registrationURL += "&RSTS_IN=DUMMY&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY" +
                "&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY" +
                "&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&REG_BTN=DUMMY&MESG=DUMMY";

        if(dropCourse){
            for(ClassItem classItem : classes){
                registrationURL += "&RSTS_IN=DW&assoc_term_in=" + term.getYear() + term.getSeason().getSeasonNumber() +
                        "&CRN_IN=" + classItem.getCRN() +
                        "&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY" +
                        "&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY";
            }
        }
        else{
            registrationURL += "&RSTS_IN=&assoc_term_in=" + term.getYear() + term.getSeason().getSeasonNumber() +
                    "&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY" +
                    "&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY";
        }

        //Lots of junk
        for(int i = 0; i < 7; i++){
            registrationURL += "&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY";
            registrationURL += "&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY";
            registrationURL += "&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY";
        }

        //More poop
        registrationURL += "&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY" +
                "&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY" +
                "&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY";

        //Insert the CRNs into the URL
        for(ClassItem classItem : classes){

            //Use a different URL if courses are being dropped
            if(!dropCourse){
                registrationURL += "&RSTS_IN=RW&CRN_IN=";
                registrationURL += classItem.getCRN();
            }
            else{
                registrationURL += "&RSTS_IN=&CRN_IN=";
            }
            registrationURL += "&assoc_term_in=&start_date_in=&end_date_in=";
        }

        registrationURL += "&regs_row=9&wait_row=0&add_row=10&REG_BTN=Submit+Changes";
        return registrationURL;
    }
}
