
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
/* TODO
 *  Find where mymcgill redirects
 *  
 */
public class Login {
	private List<String> cookies;
	private HttpsURLConnection conn;
	private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "https://mymcgill.mcgill.ca/portal/page/portal/Login" ;
		String mymcgill = "https://mymcgill.mcgill.ca/portal/pls/portal/";
		
		Login http = new Login();
		CookieHandler.setDefault(new CookieManager());
		
		String page,postParams,result;
		try {
			page = http.getPageContent(url);
			// TODO add username and password
			postParams = http.getFormParams(page, "", "");
			http.sendPost(url, postParams);
			result = http.getPageContent(mymcgill);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendPost(String url,String postParams) throws Exception
	{
		URL obj = new URL("https://login.mcgill.ca/sso/auth");
		conn = (HttpsURLConnection) obj.openConnection();
		
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		conn.setRequestProperty("Accept-Encoding","gzip,deflate,sdch");
		conn.setRequestProperty("Accept-Language","en-GB,en-US;q=0.8,en;q=0.6");
		conn.setRequestProperty("Cache-Control","max-age=0");
		conn.setRequestProperty("Connection","keep-alive");
		conn.setRequestProperty("Content-Length","606");
		conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		conn.setRequestProperty("Cookie","JSESSIONID=VISTA=yyppSpgBgwnt8nRdY1C3HnJG8vz252Jq7q2yLyGf2nvvvQVxMv61!-1311929143!pmycoursesapp2.e38Nc3qNbN8Kai0LbxuPc3eSbNyTe6fznA5Pp7ftolbGmkTy; BIGipServerPortal_PROD_Login_Pool-http=2970622892.24862.0000; PBack=0; JSESSIONIDVISTA=yyppSpgBgwnt8nRdY1C3HnJG8vz252Jq7q2yLyGf2nvvvQVxMv61!-1311929143!pmycoursesapp2.ncs.mcgill.ca!80!-1!NONE; SESSdbe2636110680a18092a41d7f7cf0fc3=5v24ooi0rkuaqk6legg79j3p36");
		conn.setRequestProperty("Host","login.mcgill.ca");
		conn.setRequestProperty("Origin","https://mymcgill.mcgill.ca");
		conn.setRequestProperty("Referer","https://mymcgill.mcgill.ca/portal/page/portal/Login");
		conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36");
		
		conn.setDoOutput(true);
		conn.setDoInput(true);
		
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();
		
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while((inputLine = in.readLine()) != null)
		{
			response.append(inputLine);
		}
		
		in.close();
	}
	
	private String getPageContent(String url) throws Exception
	{
		URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();
		conn.setRequestMethod("GET");
		conn.setUseCaches(false);
		
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36");
		conn.setRequestProperty("Accept-Language","en-GB,en-US;q=0.8,en;q=0.6");
		
		if(cookies != null)
		{
			for(String cookie : this.cookies)
			{
				conn.addRequestProperty("Cookie",cookie.split(";",1)[0]);
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
	
	public String getFormParams(String html,String username,String password) throws UnsupportedEncodingException
	{
		System.out.println("Extracting form's data...");
		Document doc = Jsoup.parse(html);
		
		//Element user = doc.getElementById("username");
		//Element pass = doc.getElementById("password");
		
		Elements inputElements = doc.getElementsByTag("input");		
		List<String> paramList = new ArrayList<String>();
		
		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");
	 
			if (key.equals("username"))
				value = username;
			else if (key.equals("password"))
				value = password;
			paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
		}
		
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
	
	public List<String> getCookies()
	{
		return cookies;
	}
	public void setCookies(List<String> cookies)
	{
		this.cookies = cookies;
	}
	

}
