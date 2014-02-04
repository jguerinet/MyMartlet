package ca.mcgill.mymcgill.util;

import android.annotation.SuppressLint;
import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ca.mcgill.mymcgill.R;

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
	
	// constants

	public final static String minervaLoginPage = "https://horizon.mcgill.ca/pban1/twbkwbis.P_WWWLogin";
	public final static String minervaLoginPost = "https://horizon.mcgill.ca/pban1/twbkwbis.P_ValLogin";
	public final static String minervaSchedule = "https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchdDetl?term_in=201401";
    public final static String minervaTranscript = "https://horizon.mcgill.ca/pban1/bzsktran.P_Display_Form?user_type=S&tran_type=V";
	private final String USER_AGENT = "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> Mobile Safari/<WebKit Rev>";
	
	//singleton architecture
	private static Connection http = new Connection();
	private Connection(){
		//set some default value to know it was undefined
		username = "undefined";
		password = "undefined";
	}
	
	//accessor method
	public static Connection getInstance(){
		return http;
	}
	
	
	@SuppressLint("NewApi")	//getting errors
	public int connect(Context context, String user, String pass){
		
		//load uname and pass
    	username = user + context.getResources().getString(R.string.login_email);
    	password = pass;
    	String postParams;

		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());
		
		try {
			// 1. Send a "GET" request, so that you can extract the form's data.
			String page = http.GetPageContent(minervaLoginPage);
			postParams = http.getFormParams(page, username, password);
			
			// 2. Construct above post's content and then send a POST request
			// for authentication
			String Post1Resp = http.sendPost(minervaLoginPost, minervaLoginPage,postParams);
			
			//TODO: VERIFY PROPER LOGIN HAS OCCURED
			
		} catch (Exception e) {
			return Constants.CONNECTION_OTHER;
		}

        return Constants.CONNECTION_OK;
    }
	
	public String getUrl(String url) {
		
		String result;
		try {
		result = http.GetPageContent(url);
		} catch (Exception e) {
			return "00000";
		}
		
		return result;
	}
	

	private String sendPost(String url, String Referer, String postParams) throws Exception {
			 
		URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();
	 
		// Acts like a browser
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Host", "horizon.mcgill.ca");
		conn.setRequestProperty("Origin", "https://horizon.mcgill.ca");
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
		StringBuffer response = new StringBuffer();
	 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		return response.toString();
		// System.out.println(response.toString());
	 
	  }
	 
	  private String GetPageContent(String url) throws Exception {
	 
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
	 
		BufferedReader in = 
	            new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
	 
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
					String value = inputElement.attr("value");
			 
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
				result.append("&" + param);
			}
		}
		return result.toString();
	  }
	 
	  public List<String> getCookies() {
		return cookies;
	  }
	 
	  public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	  }
	 
	
	
}
