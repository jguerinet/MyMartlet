package ca.appvelopers.mcgillmobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.LoginActivity;
import ca.appvelopers.mcgillmobile.exception.MinervaLoggedOutException;
import ca.appvelopers.mcgillmobile.object.ConnectionStatus;
import ca.appvelopers.mcgillmobile.object.Season;
import ca.appvelopers.mcgillmobile.object.Semester;

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
	public final static String minervaLoginPage = "https://horizon.mcgill.ca/pban1/twbkwbis.P_WWWLogin";
	public final static String minervaLoginPost = "https://horizon.mcgill.ca/pban1/twbkwbis.P_ValLogin";
	public final static String minervaSchedule = "https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchdDetl?term_in=201401";
    public final static String minervaEbill = "https://horizon.mcgill.ca/pban1/bztkcbil.pm_viewbills";
    public final static String minervaTranscript = "https://horizon.mcgill.ca/pban1/bzsktran.P_Display_Form?user_type=S&tran_type=V";
	public final static String minervaHomepage = "https://horizon.mcgill.ca/pban1/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu";
	public final static String minervaHost = "horizon.mcgill.ca";
	public final static String minervaOrigin = "https://horizon.mcgill.ca";
	public final static String minervaSchedulePrefix = "https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchdDetl?term_in=";
    public final static String minervaRegistration = "https://horizon.mcgill.ca/pban1/bwckgens.p_proc_term_date?p_calling_proc=P_CrseSearch&search_mode_in=NON_NT&p_term=201405";
    public final static String minervaRegistrationError = "http://www.is.mcgill.ca/whelp/sis_help/rg_errors.htm";
	
	public final static String myMcGillLoginPage = "https://mymcgill.mcgill.ca/portal/page/portal/myMcGill";
	public final static String myMcGillLoginPortal = "https://mymcgill.mcgill.ca/portal/page/portal/Login";
	public final static String myMcGillLoginSSO= "https://login.mcgill.ca/sso/auth";
	
	public final static String myMcGillLoginSSOHost= "login.mcgill.ca";
	public final static String myMcGillHost = "mymcgill.mcgill.ca";
	public final static String myMcGillOrigin = "https://mymcgill.mcgill.ca";

    private static final String COURSE_SEARCH_URL = "https://horizon.mcgill.ca/pban1/bwskfcls.P_GetCrse?";
    private static final String REGISTRATION_URL = "https://horizon.mcgill.ca/pban1/bwckcoms.P_Regs?term_in=";
	
    private final String USER_AGENT = "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> Mobile Safari/<WebKit Rev>";
	
	// Singleton architecture
	private static Connection http = new Connection();
	private Connection(){
		status = ConnectionStatus.CONNECTION_FIRSTACCESS;
	}
	
	// Accessor method
	public static Connection getInstance(){
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

    //Download all of the info (upon login)
    public void downloadAll(Activity activity){
        Connection connection = getInstance();

        //Download the transcript
        Parser.parseTranscript(connection.getUrl(activity, minervaTranscript));

        //Set the default Semester
        List<Semester> semesters = App.getTranscript().getSemesters();
        //Find the latest semester
        Semester defaultSemester = semesters.get(0);
        for(Semester semester : semesters){
            //If the year is higher than the current year, switch
            if(semester.getYear() > defaultSemester.getYear()){
                defaultSemester = semester;
            }
            //If same year and the month is higher than th default month, change it
            else if(semester.getYear() == defaultSemester.getYear()){
                if(Integer.valueOf(semester.getSeason().getSeasonNumber()) >
                        Integer.valueOf(defaultSemester.getSeason().getSeasonNumber())){
                    defaultSemester = semester;
                }
            }
        }
        App.setDefaultSemester(defaultSemester);

        //Download the schedule
        String scheduleString = connection.getUrl(activity, defaultSemester.getURL());
        Parser.parseClassList(defaultSemester.getSeason(), defaultSemester.getYear(), scheduleString);

        //Download the ebill and user info
        String ebillString = Connection.getInstance().getUrl(activity, minervaEbill);
        Parser.parseEbill(ebillString);
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
			String page = http.getPageContent(minervaLoginPage);
			postParams = http.getFormParams(page, username, password);
			
			// search for "Authorization Failure"
			if (postParams.contains("WRONG_INFO"))
			{
				status = ConnectionStatus.CONNECTION_WRONG_INFO;
				return ConnectionStatus.CONNECTION_WRONG_INFO;
			}
			
			
			// 2. Construct above post's content and then send a POST request
			// for authentication
			String Post1Resp = http.sendPost(minervaLoginPost, minervaLoginPage,postParams, minervaHost, minervaOrigin);

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
	 * 
	  */
	public String getUrl(final Activity activity, String url){
        //Initial internet check
        if(!isNetworkAvailable(activity)){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogHelper.showNeutralAlertDialog(activity, activity.getResources().getString(R.string.error),
                            activity.getResources().getString(R.string.error_no_internet));
                }
            });

            //Return empty String
            return "";
        }

        String	result = null;
        try {
            result = http.getPageContent(url);
        } catch (MinervaLoggedOutException e) {
            //User has been logged out, so we need to log him back in
            final ConnectionStatus connectionResult = Connection.getInstance().connectToMinerva(activity);

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
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Clear.clearAllInfo(activity);

                        //Go back to LoginActivity
                        Intent intent = new Intent(activity, LoginActivity.class);
                        intent.putExtra(Constants.CONNECTION_STATUS, connectionResult);
                        activity.startActivity(intent);

                        //Finish this activity
                        activity.finish();
                    }
                });

                //Return empty String
                result = "";
            }
            //No internet: show no internet dialog
            else if(connectionResult == ConnectionStatus.CONNECTION_NO_INTERNET){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelper.showNeutralAlertDialog(activity, activity.getResources().getString(R.string.error),
                                activity.getResources().getString(R.string.error_no_internet));
                    }
                });

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

    /**
     * Get the URL to look for courses for the given parameters
     * @param season The course season
     * @param year The course year
     * @param subject The course subject
     * @param courseNumber The course number
     * @return The proper search URL
     */
    public static String getCourseURL(Season season, int year, String subject, String courseNumber){
        return COURSE_SEARCH_URL
                + "term_in=" + year + season.getSeasonNumber() +
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
                "&sel_title=" +
                "&sel_schd=%25" +
                "&sel_from_cred=" +
                "&sel_to_cred=" +
                "&sel_levl=%25" +
                "&sel_ptrm=%25" +
                "&sel_instr=%25" +
                "&sel_attr=%25" +
                "&begin_hh=0" +
                "&begin_mi=0" +
                "&begin_ap=a" +
                "&end_hh=0" +
                "&end_mi=0" +
                "&end_ap=a" +
                "%20Response%20Headersview%20source";
    }

    /**
     * Get the URL to look for courses for the given parameters
     * @param season The course season
     * @param year The course year
     * @param crns A list of CRNs to to register for
     * @return The proper search URL
     */
    public static String getRegistrationURL(Season season, int year, int[] crns){
        String registrationURL;
        registrationURL = REGISTRATION_URL + year + season.getSeasonNumber();

        //Add random Minerva crap that is apparently necessary
        registrationURL += "&RSTS_IN=DUMMY&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY" +
                "&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY" +
                "&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&REG_BTN=DUMMY&MESG=DUMMY";

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
        for(int i = 0; i < Constants.MAX_CRNS; i++){
            registrationURL += "&RSTS_IN=RW&CRN_IN=";
            if(crns[i] != 0){
                registrationURL += crns[i];
            }
            registrationURL += "&assoc_term_in=&start_date_in=&end_date_in=";
        }

        registrationURL += "&regs_row=9&wait_row=0&add_row=10&REG_BTN=Submit+Changes";
        return registrationURL;
    }
}
