package ca.mcgill.mymcgill.object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.junit.Test;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.transcript.TranscriptActivity;
import ca.mcgill.mymcgill.util.Help;

public class TranscriptTest extends TestCase {

	@Test
	public void testGetCGPA() throws IOException {
		
		String file = "res/raw/test_transcript.txt";
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		String text ="";
		StringBuilder sb = new StringBuilder();
		
		while((text = reader.readLine()) != null){
			sb.append(text);
		}
		
		text = sb.toString();

		Transcript testTranscript = new Transcript(text);
		double gpa = testTranscript.getCgpa();
		assertEquals("CGPA is incorrect", 3.92, gpa, 0.01);
	}
	
	@Test
	public void testGetTotalCredits() {
		TranscriptActivity testAct = new TranscriptActivity();
		
		String text = Help.readFromFile(testAct, R.raw.test_transcript);
		Transcript testTranscript = new Transcript(text);
		double credits = testTranscript.getTotalCredits();
		assertEquals("Total credits is incorrect", 80, credits, 0.01);
	}
}
