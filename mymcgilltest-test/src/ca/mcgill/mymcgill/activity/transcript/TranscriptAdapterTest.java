package ca.mcgill.mymcgill.activity.transcript;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import android.content.Context;
import android.test.AndroidTestCase;

import ca.mcgill.mymcgill.object.Transcript;

public class TranscriptAdapterTest extends AndroidTestCase {
	TranscriptAdapter test; 
	
	
	private TranscriptAdapter testSetUp() throws IOException {
		
		String file = "res/raw/test_transcript.txt";
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		String text = "";
		StringBuilder sb = new StringBuilder();
		
		while((text = reader.readLine()) != null){
			sb.append(text);
		}
		
		return new TranscriptAdapter(new TranscriptActivity(), new Transcript(sb.toString()));
		 
	}
	
	@Test
	public void testConstructor() {		

		assertNotNull(test);
	}	
	
	@Test
	public void testGetCount() {

		assertEquals("Incorrect number of semesters", 8, test.getCount());
	}	
	
	@Test
	public void testGetSemester() {
		assertTrue("Incorrect Semester", test.getItem(0).getSemesterName().contains("Fall") && test.getItem(0).getSemesterName().contains("2011"));
	}

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		try 
		{
			this.test = this.testSetUp();
		}
		catch (IOException e)
		{
			
		}
		
		
	}
	
	
}
