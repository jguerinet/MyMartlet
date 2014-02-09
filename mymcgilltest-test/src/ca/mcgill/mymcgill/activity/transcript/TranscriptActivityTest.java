package ca.mcgill.mymcgill.activity.transcript;

import org.junit.Test;

import ca.mcgill.mymcgill.R;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class TranscriptActivityTest extends ActivityInstrumentationTestCase2<TranscriptActivity> {

	private TranscriptActivity transAct;
	private TextView cgpaText, creditsText;
	
	
	public TranscriptActivityTest() {
		super(TranscriptActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		
		transAct = getActivity();
		cgpaText = (TextView) transAct.findViewById(R.id.transcript_cgpa);
		creditsText = (TextView) transAct.findViewById(R.id.transcript_credits);
	}

	@Test
	public void testPreconditions() {
		assertNotNull("transAct is null", transAct);
	    assertNotNull("cgpaText is null", cgpaText);
		assertNotNull("creditsText is null", creditsText);
	}
	
	@Test
	public void testCgpaLabel() {
	    final String expected = "CGPA: 0.0";
	    final String actual = cgpaText.getText().toString();
	    assertEquals("Not the same", expected, actual);
	}
	
	@Test
	public void testCreditsLabel() {
	    final String expected = "Total Credits: 0";
	    final String actual = creditsText.getText().toString();
	    assertEquals("Not the same", expected, actual);
	}
	
	
	@Test
	public void testLoadInfo() {
		// To-Do
		assertFalse("Transcript not loaded", false);
	}
}
