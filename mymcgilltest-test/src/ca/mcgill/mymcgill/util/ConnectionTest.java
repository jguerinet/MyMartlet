package ca.mcgill.mymcgill.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.exception.MinervaLoggedOutException;
import ca.mcgill.mymcgill.activity.LoginActivity;

import android.test.AndroidTestCase;
import android.widget.ListView;

public class ConnectionTest extends AndroidTestCase{
	
	public static LoginActivity mActivityClass;
	
	protected void setUp() throws Exception {
		super.setUp();
		mActivityClass = new LoginActivity();
	}
	
	@Test
	public static void testGetUrl() throws IOException{
		Connection conn = Connection.getInstance();
		String actual ="";
		
		actual = conn.getUrl(mActivityClass, Connection.minervaHomepage);	
		
		String file = "res/raw/minerva_home.txt";
		InputStream in = conn.getClass().getClassLoader().getResourceAsStream(file);
		assertNotNull(in);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		assertNotNull(reader);		
		
		String text ="";
		StringBuilder sb = new StringBuilder();
		
		while((text = reader.readLine()) != null){
			sb.append(text);
		}
		
		String expected = sb.toString();
		
		// sb is the actual page
		
		assertEquals("Retrieved page is different", expected, actual);
	}
	
}
