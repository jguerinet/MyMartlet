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
		String Cgpa = cgpaText.getText().toString();
	    assertTrue("Does not contain 'CGPA'", Cgpa.contains("CGPA:"));
	}
	
	@Test
	public void testCreditsLabel() {
		String credits = creditsText.getText().toString();
		assertTrue("Does not contain 'CGPA'", credits.contains("Total Credits:"));
	}
	
	
	@Test
	public void testLoadInfo() {
		// To-Do
		assertFalse("Transcript not loaded", false);
	}
}
