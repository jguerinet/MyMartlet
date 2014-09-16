package ca.appvelopers.mcgillmobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.LoginActivity;
import ca.appvelopers.mcgillmobile.activity.SplashActivity;
import ca.appvelopers.mcgillmobile.exception.MinervaLoggedOutException;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.ConnectionStatus;
import ca.appvelopers.mcgillmobile.object.Semester;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Author: Julien, Shabbir, Rafi, Joshua
 * Date: 22/01/14, 8:09 PM
 * Update: 29/01/14
 * This package will hold the logic for logging someone in to MyMcGill
 */
public class Connection {
	
	private String username;
	private String password;
	private List<String> cookies;
	private HttpsURLConnection conn;
	private ConnectionStatus status = ConnectionStatus.CONNECTION_FIRSTACCESS;
	
	// Constants
    public static final String TRANSCRIPT = "https://horizon.mcgill.ca/pban1/bzsktran.P_Display_Form?user_type=S&tran_type=V";
    public static final String EBILL = "https://horizon.mcgill.ca/pban1/bztkcbil.pm_viewbills";
    public static final String REGISTRATION_ERROR = "http://www.is.mcgill.ca/whelp/sis_help/rg_errors.htm";

    private static final String MINERVA_HOST = "horizon.mcgill.ca";
    private static final String MINERVA_ORIGIN = "https://horizon.mcgill.ca";
	private static final String LOGIN_PAGE = "https://horizon.mcgill.ca/pban1/twbkwbis.P_WWWLogin";
	private static final String LOGIN_POST = "https://horizon.mcgill.ca/pban1/twbkwbis.P_ValLogin";
    private static final String SCHEDULE = "https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchdDetl?term_in=";
    private static final String COURSE_SEARCH = "https://horizon.mcgill.ca/pban1/bwskfcls.P_GetCrse?";
    private static final String COURSE_REGISTRATION = "https://horizon.mcgill.ca/pban1/bwckcoms.P_Regs?term_in=";
	
    private final String USER_AGENT = "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> Mobile Safari/<WebKit Rev>";
	
	// Singleton architecture
	private static Connection http = new Connection();
	private Connection(){
		status = ConnectionStatus.CONNECTION_FIRSTACCESS;
	}
	
	// Accessor method
	public static Connection getInstance(){
        if(http.username == null){
            http.username = Load.loadFullUsername(App.getContext());
        }
        if(http.password == null){
            http.password = Load.loadPassword(App.getContext());
        }
		return http;
	}
	
	//Setting username
    public void setUsername(String username){
        this.username = username;
    }

    //Setting password
    public void setPassword(String password){
        this.password = password;
    }

    /**
     * Download all of the info (upon opening of the app)
     * @param context The app context
     * @param infoDownloader The task that is currently downloading the info (to update the progress)
     */
    public void downloadAll(Context context, SplashActivity.InfoDownloader infoDownloader){
        Connection connection = getInstance();

        //Update : downloading transcript
        infoDownloader.publishNewProgress(context.getString(R.string.updating_transcript));

        //Download the transcript
        if(!Test.LOCAL_TRANSCRIPT){
            Parser.parseTranscript(connection.getUrl(context, TRANSCRIPT));
        }

        List<Semester> semesters = App.getTranscript().getSemesters();
        //Find the latest semester
        for(Semester semester : semesters){
            Term term = semester.getTerm();

            //Update : downloading transcript
            infoDownloader.publishNewProgress(context.getString(R.string.updating_semester, term.toString(context)));

            //Download the schedule
            Parser.parseClassList(term, connection.getUrl(context, getScheduleURL(term)));
        }

        //Set the default term if there is none set yet
        if(App.getDefaultTerm() == null){
            App.setDefaultTerm(Term.dateConverter(Calendar.getInstance().getTime()));
        }

        //Update : downloading transcript
        infoDownloader.publishNewProgress(context.getString(R.string.updating_ebill));

        //Download the ebill and user info
        String ebillString = Connection.getInstance().getUrl(context, EBILL);
        Parser.parseEbill(ebillString);

        //Update : downloading transcript
        infoDownloader.publishNewProgress(context.getString(R.string.updating_user));

        Parser.parseUserInfo(ebillString);
    }

	public ConnectionStatus connectToMinerva(Context context){
        //First check if the user is connected to the internet
        if(!isNetworkAvailable(context)){
            return ConnectionStatus.CONNECTION_NO_INTERNET;
        }
        
        if(status == ConnectionStatus.CONNECTION_FIRSTACCESS){
			// make sure cookies is turn on
			CookieHandler.setDefault(new CookieManager());
        }

    	String postParams;
    	status = ConnectionStatus.CONNECTION_AUTHENTICATING;

		try {
			// 1. Send a "GET" request, so that you can extract the form's data.
			String page = http.getPageContent(LOGIN_PAGE);
			postParams = http.getFormParams(page, username, password);
			
			// search for "Authorization Failure"
			if (postParams.contains("WRONG_INFO"))
			{
				status = ConnectionStatus.CONNECTION_WRONG_INFO;
				return ConnectionStatus.CONNECTION_WRONG_INFO;
			}
			
			
			// 2. Construct above post's content and then send a POST request
			// for authentication
			String Post1Resp = http.sendPost(LOGIN_POST, LOGIN_PAGE,postParams, MINERVA_HOST, MINERVA_ORIGIN);

			// Check is connection was actually made
			if (!Post1Resp.contains("WELCOME"))
			{
				status = ConnectionStatus.CONNECTION_WRONG_INFO;
				return ConnectionStatus.CONNECTION_WRONG_INFO;
			}
			
		} catch (MinervaLoggedOutException e) {
			//throw if still logged out bubble it up
			e.printStackTrace();
			status = ConnectionStatus.CONNECTION_MINERVA_LOGOUT;
			return ConnectionStatus.CONNECTION_MINERVA_LOGOUT;
		}
		catch (Exception e) {
            e.printStackTrace();
            status = ConnectionStatus.CONNECTION_OTHER;
			return ConnectionStatus.CONNECTION_OTHER;
		}
		
		status = ConnectionStatus.CONNECTION_OK;
        return ConnectionStatus.CONNECTION_OK;
    }
	
	/**
	 *  The method getURL with retrieve a webpage as text
	 */
	public String getUrl(final Context context, String url){
        return getUrl(context, url, true);
    }

    public String getUrl(final Context context, String url, boolean showErrors){
        //Initial internet check
        if(!isNetworkAvailable(context)){
        	if(context instanceof Activity){
                if(showErrors){
                    final Activity activity = (Activity) context;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogHelper.showNeutralAlertDialog(activity, activity.getResources().getString(R.string.error),
                                    activity.getResources().getString(R.string.error_no_internet));
                        }
                    });
                }
        	}

            //Return empty String
            return "";
        }

        String	result = null;
        try {
            result = http.getPageContent(url);
        } catch (MinervaLoggedOutException e) {
            //User has been logged out, so we need to log him back in
            final ConnectionStatus connectionResult = Connection.getInstance().connectToMinerva(context);

            //Successfully logged him back in, try retrieving the stuff again
            if(connectionResult == ConnectionStatus.CONNECTION_OK){
                try{
                    result = http.getPageContent(url);
                } catch (Exception exception){
                    //Another logged out exception: doesn't work so this will just return null
                }
            }
            //Wrong credentials: back to login screen
            else if(connectionResult == ConnectionStatus.CONNECTION_WRONG_INFO){
            	if(context instanceof Activity){
            		final Activity activity = (Activity) context;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Clear.clearAllInfo(activity);

                            //Go back to LoginContext
                            Intent intent = new Intent(activity, LoginActivity.class);
                            intent.putExtra(Constants.CONNECTION_STATUS, connectionResult);
                            activity.startActivity(intent);

                            //Finish this activity
                            activity.finish();
                        }
                    });
            	}


                //Return empty String
                result = "";
            }
            //No internet: show no internet dialog
            else if(connectionResult == ConnectionStatus.CONNECTION_NO_INTERNET){
            	if(context instanceof Activity){
                    if(showErrors){
                        final Activity activity = (Activity) context;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogHelper.showNeutralAlertDialog(activity, activity.getResources().getString(R.string.error),
                                        activity.getResources().getString(R.string.error_no_internet));
                            }
                        });
                    }
            	}

                //Return empty String
                result = "";
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

        //Other problems will return null
        return result;
	}
	
	/**
	 *  The method
	 * 
	  */
	private String sendPost(String url, String Referer, String postParams, String postHost, String postOrigin) throws Exception {
			 
		URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();
	 
		// Acts like a browser
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Host", postHost);
		conn.setRequestProperty("Origin", postOrigin);
		conn.setRequestProperty("DNT", "1");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
			"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		for (String cookie : this.cookies) {
			conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
		}
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Referer", Referer);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
	 
		conn.setDoOutput(true);
		conn.setDoInput(true);
	 
		// Send post request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();
	 
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);
	 
		BufferedReader in = 
	             new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();
	 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//System.out.println(response.toString());
		return response.toString();
	  }
	 
	/**
	 * The method
	 * @throws IOException 
	 * 
	 */
	private String getPageContent(String url) throws MinervaLoggedOutException, IOException {
		//Initial check for internet connection
        URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();
	 
		// default is GET
		conn.setRequestMethod("GET");
	 
		conn.setUseCaches(false);
	 
		// act like a browser
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null) {
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		
		//check Response Code
		switch(responseCode){
		case HttpsURLConnection.HTTP_OK:
			//all is good
			break;
		case HttpsURLConnection.HTTP_MOVED_TEMP:
		case HttpsURLConnection.HTTP_MOVED_PERM:
			//follow the new link
			String nextLocation = conn.getHeaderField("Location");
			return getPageContent(nextLocation);
		default:
			// all is ignored. carry on
			break;
		}
		
		//check headers
		if(!areHeadersOK(conn.getHeaderFields())){
			switch(status){
			case CONNECTION_MINERVA_LOGOUT:
				//reconnect
				if(status !=ConnectionStatus.CONNECTION_AUTHENTICATING)//if not trying to authenticate
					throw new MinervaLoggedOutException();
				
			default:
				break;
			
			}
		}
	 
		BufferedReader in = 
	            new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();
	 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	 
		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));
		
		return response.toString();
	 
	  }
	 
	  public String getFormParams(String html, String username, String password)
			throws UnsupportedEncodingException {
	 
		System.out.println("Extracting form's data...");
	 
		Document doc = Jsoup.parse(html);
	 
		// Google form id
		List<String> paramList = new ArrayList<String>();
		Elements forms = doc.getElementsByTag("form");
		for (Element formElement : forms) {
			String elemName = formElement.attr("name");
			if (elemName.equals("loginform1")){//get the right login form
				
				Elements inputElements = formElement.getElementsByTag("input");
				
				for (Element inputElement : inputElements) {
					String key = inputElement.attr("name");
					String value;
			 
					if (key.equals("sid")){
						value = username;
						paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
					}
					else if (key.equals("PIN")){
						value = password;
						paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
					}
				}
				
			}	
		}
		
		
		
	 
		// build parameters list
		StringBuilder result = new StringBuilder();
		for (String param : paramList) {
			if (result.length() == 0) {
				result.append(param);
			} else {
				result.append("&").append(param);
			}
		}
		return result.toString();
	  }
	 
	  public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	  }

    // Determine if network is available
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
	
    // Check headers for bad connection Response
    private boolean areHeadersOK(Map <String, List<String>> Headers){
    	
    	//check for minerva logout
    	List<String> setCookies = Headers.get("Set-Cookie");
    	for(String aCookie: setCookies){    	
	    	if(aCookie.contains("SESSID=;")){
	    		if(status !=ConnectionStatus.CONNECTION_AUTHENTICATING)//if not trying to authenticate
	    			status = ConnectionStatus.CONNECTION_MINERVA_LOGOUT;
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