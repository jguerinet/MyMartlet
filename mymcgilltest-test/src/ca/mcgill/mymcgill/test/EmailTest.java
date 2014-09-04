package ca.mcgill.mymcgill.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.EditText;
//import ca.mcgill.mymcgill.activity.inbox.ReplyActivity;

public class EmailTest extends ActivityInstrumentationTestCase2<ReplyActivity> {
	public Activity testActivity;
	EditText subject,cc,body;
	Button emailButton;
	
	
	public EmailTest()
	{
		super(ReplyActivity.class);
	}
	
	@UiThreadTest
	public void testUIElements()
	{
		assertNotNull(subject.getText());
		assertNotNull(cc.getText());
		assertNotNull(emailButton.getText());
		assertNotNull(body.getText());
	}
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		// TODO get ui elements
		testActivity = (ReplyActivity) getActivity();
		subject = (EditText) testActivity.findViewById(ca.mcgill.mymcgill.R.id.emailSubject);
		cc = (EditText) testActivity.findViewById(ca.mcgill.mymcgill.R.id.emailCarbonCopy);
		emailButton = (Button) testActivity.findViewById(ca.mcgill.mymcgill.R.id.sendEmailButton);
		body = (EditText) testActivity.findViewById(ca.mcgill.mymcgill.R.id.emailBody);		
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		
	}
	
	
}
