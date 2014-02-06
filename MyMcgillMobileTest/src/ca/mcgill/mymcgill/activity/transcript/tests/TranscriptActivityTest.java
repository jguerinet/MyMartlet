package ca.mcgill.mymcgill.activity.transcript.tests;

import android.test.ActivityInstrumentationTestCase2;
import ca.mcgill.mymcgill.activity.transcript.TranscriptActivity;

public class TranscriptActivityTest extends
		ActivityInstrumentationTestCase2<TranscriptActivity> {
	private TranscriptActivity transcriptActivityTest;

	public TranscriptActivityTest() {
		super(TranscriptActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		transcriptActivityTest = getActivity();
	}

	public void testPrecondition() {

	}
}
