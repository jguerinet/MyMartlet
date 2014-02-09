package ca.mcgill.mymcgill.activity.transcript;

import org.junit.Test;

import android.test.ActivityInstrumentationTestCase2;

public class TranscriptActivityTest extends ActivityInstrumentationTestCase2<TranscriptActivity> {

	private TranscriptActivityTest tranActTest;
	
	
	public TranscriptActivityTest() {
		super(TranscriptActivity.class);
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testLoadInfo() {
		assertFalse("Transcript not loaded", false);
	}
	
	@Test
	public void testPreconditions() {
	    assertNotNull(“mFirstTestActivity is null”, mFirstTestActivity);
	    assertNotNull(“mFirstTestText is null”, mFirstTestText);
	}
}
