package ca.mcgill.mymcgill.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import ca.mcgill.mymcgill.activity.LoginActivity;
import ca.mcgill.mymcgill.util.Clear;
import ca.mcgill.mymcgill.util.Constants;
import ca.mcgill.mymcgill.util.Load;

public class LoginTest extends ActivityInstrumentationTestCase2<LoginActivity> {
	public LoginActivity mActivityClass;
	public Intent mActivityIntent = null;
	// Views
	public EditText usernameView;
	public EditText passwordView;
	public CheckBox rememberView; 
	public Button loginButton;
	
	@SuppressWarnings("deprecation")
	public LoginTest(){
		super("ca.mcgill.mymcgill.activity.LoginActivity",LoginActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		mActivityClass = this.getActivity();
		usernameView = (EditText) mActivityClass.findViewById(ca.mcgill.mymcgill.R.id.login_username);
		passwordView = (EditText) mActivityClass.findViewById(ca.mcgill.mymcgill.R.id.login_password);
		rememberView = (CheckBox) mActivityClass.findViewById(ca.mcgill.mymcgill.R.id.login_remember_username);
		loginButton = (Button) mActivityClass.findViewById(ca.mcgill.mymcgill.R.id.login_button);
		
		//String username = "";
		//String password = "";
		// TODO INSERT REAL USERNAME AND PASSWORD
		//usernameView.setText(username);
		//passwordView.setText(password);

		//TODO ENTER USER NAME AND PASSWORD
	}
	
	public void testUserNameField() throws Exception
	{
		assertNotNull(usernameView);
	}
	public void testPasswordField() throws Exception
	{
		assertNotNull(passwordView);
	}
	public void testRememberField() throws Exception
	{
		assertNotNull(rememberView);
	}
	
	// Joshua David Alfaro
	// Run after already logging in in the app or Null pointer will occur
	public void testClear() {
		String user = Load.loadFullUsername(this.getActivity());
		String pass = Load.loadPassword(this.getActivity());
		
		Clear.clearUsername(this.getActivity());
		Clear.clearPassword(this.getActivity());
		
		String username = Load.loadFullUsername(this.getActivity());
		String password = Load.loadPassword(this.getActivity());
		
		if (user.equals(username + "@mail.mcgill.ca") && !user.equals("")){
			fail();
		} else if (pass.equals(password) && !pass.equals("")) {
			fail();
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		//TODO LOG OUT 
	}
	
	
	
}
