package ca.mcgill.mymcgill.object;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.lang.ClassNotFoundException;

import org.junit.Test;

import android.test.AndroidTestCase;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.transcript.TranscriptActivity;

import ca.mcgill.mymcgill.object.Transcript;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Help;

public class TranscriptTest extends AndroidTestCase {

	@Test
	public void testGetCGPA() throws IOException {
		Scanner scan = new Scanner(new File("test_transcript.txt"));
		String text = "";
		while(scan.hasNext())
		{
			text = text + scan.next();
		}
		Transcript testTranscript = new Transcript(text);
		double gpa = testTranscript.getCgpa();
		assertEquals("CGPA is incorrect", 3.92, gpa, 0.01);
		scan.close();
	}
	
	@Test
	public void testConstructor() {
		Transcript test = new Transcript("test");
		assertNotNull(test);
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
