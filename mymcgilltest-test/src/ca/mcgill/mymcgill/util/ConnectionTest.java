package ca.mcgill.mymcgill.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import android.test.AndroidTestCase;

public class ConnectionTest extends AndroidTestCase{
	
	@Test
	public static void testGetUrl() throws IOException{
		Connection conn = Connection.getInstance();
		String actual = conn.getUrl(Connection.minervaHomepage);
		
		String file = "res/raw/minerva_home.txt";
		InputStream in = conn.getClass().getClassLoader().getResourceAsStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		String text ="";
		StringBuilder sb = new StringBuilder();
		
		while((text = reader.readLine()) != null){
			sb.append(text);
		}
		
		String expected = sb.toString();
		
		assertEquals("Retrieved page is different", expected, actual);
	}
	
	@Test
	public static void testConnect(){
		
	}
}
