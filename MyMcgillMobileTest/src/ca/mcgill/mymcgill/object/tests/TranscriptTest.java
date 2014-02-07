package ca.mcgill.mymcgill.object.tests;

import static org.junit.Assert.*;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.transcript.TranscriptActivity;
import ca.mcgill.mymcgill.object.Transcript;
import ca.mcgill.mymcgill.util.Help;

public class TranscriptTest {

	@Test
	public void testGetCGPA() {
		//TranscriptActivity testAct = new TranscriptActivity();
		
		String text = Help.readFromFile(TranscriptActivity.this, R.raw.cousched);
		Transcript testTranscript = new Transcript(text);
		double gpa = testTranscript.getCgpa();
		assertEquals("CGPA is incorrect", 3, gpa, 0.01);
	}
}
