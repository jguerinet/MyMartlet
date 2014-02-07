package ca.mcgill.mymcgill.object.tests;

import static org.junit.Assert.*;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import ca.mcgill.mymcgill.object.Transcript;

public class TranscriptTest {

	@Test
	public void testGetCGPA() throws IOException {
		FileReader reader = new FileReader("Desktop/testTranscript.html");
		String text = reader.toString();
		Transcript testTranscript = new Transcript(text);
		double gpa = testTranscript.getCgpa();
		assertEquals("CGPA is incorrect", 3, gpa, 0.01);
		reader.close();
	}
}
