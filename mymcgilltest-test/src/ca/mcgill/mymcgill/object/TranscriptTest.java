package ca.mcgill.mymcgill.object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import android.test.AndroidTestCase;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.transcript.TranscriptActivity;
import ca.mcgill.mymcgill.util.Help;

public class TranscriptTest extends AndroidTestCase {

	private String transcriptToText() throws IOException {
		String file = "res/raw/test_transcript.txt";
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		String text ="";
		StringBuilder sb = new StringBuilder();
		
		while((text = reader.readLine()) != null){
			sb.append(text);
		}
		

		return sb.toString();
	}
	
	
	@Test
	public void testGetCGPA() {
		Transcript testTranscript = null;
		try {
			String text = transcriptToText();
			testTranscript = new Transcript(text);
		} catch (Exception e) {
			fail("Error: File Not Found");
		}

		double gpa = testTranscript.getCgpa();
		assertEquals("CGPA is incorrect", 3.92, gpa, 0.01);
	}
	
	@Test
	public void testConstructor() {
		Transcript test = new Transcript("test");
		assertNotNull(test);
	}
	
	
	@Test
	public void testGetTotalCredits() {
		Transcript testTranscript = null;
		try {
			String text = transcriptToText();
			testTranscript = new Transcript(text);
		} catch (Exception e) {
			fail("Error: File Not Found");
		}
		
		double credits = testTranscript.getTotalCredits();
		assertEquals("Total credits is incorrect", 109, credits, 0.01);
	}
	
	@Test
	public void testGetSemester() {
		Transcript testTranscript = null;
		try {
			String text = transcriptToText();
			testTranscript = new Transcript(text);
		} catch (Exception e) {
			fail("Error: File Not Found");
		}
		
		List<Semester> list = testTranscript.getSemesters();
		if (list.isEmpty()) {
			fail("List is empty");
		} else if (list.get(0).getSemesterName(mContext).equals("Fall 2011")) {
			fail("First Semester Name is incorrect");
		} else if (list.get(0).getProgram().equals("Electrical Engineering")) {
			fail("Program is incorrect");
		} else if (list.get(0).getBachelor().equals("Bachelor of Engineering")) {
			fail("Bachelor is incorrect");
		} else if (list.get(0).getTermCredits() == (15+29)) {
			fail("First term credits is incorrect");
		} else if (!list.get(0).isFullTime()) {
			fail("First term should be seen as full time");
		} else if (list.get(0).getCourses().get(0).getCredits() == 3) {
			fail("First term's first course should be worth 3 credits");
		} else {
			assertEquals("Term GPA is incorrect", 3.94, list.get(0).getTermGPA(), 0.01);
		}
		
	}
	
}
