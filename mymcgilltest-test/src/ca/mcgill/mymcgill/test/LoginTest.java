package ca.mcgill.mymcgill.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import ca.mcgill.mymcgill.activity.LoginActivity;
import ca.mcgill.mymcgill.util.Clear;
import ca.mcgill.mymcgill.util.Constants;

public class LoginTest extends ActivityInstrumentationTestCase2<LoginActivity> {
	public LoginActivity mActivityClass;
	public Intent mActivityIntent = null;
	// Views
	public TextView usernameView;
	public TextView passwordView;
	public CheckBox rememberView; 
	public Button loginButton;
	
	@SuppressWarnings("deprecation")
	public LoginTest(){
		super("ca.mcgill.mymcgill.activity.LoginActivity",LoginActivity.class);
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
	// Test Logout
	public void testClear() {
		Clear.clearUsername(this.getActivity());
		Clear.clearUsername(this.getActivity());
		if (Constants.USERNAME.equals(usernameView.toString())){
			fail();
		} else if (Constants.PASSWORD.equals(passwordView.toString())) {
			fail();
		}
	}
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		mActivityClass = this.getActivity();
		usernameView = (TextView) mActivityClass.findViewById(ca.mcgill.mymcgill.R.id.login_username);
		passwordView = (TextView) mActivityClass.findViewById(ca.mcgill.mymcgill.R.id.login_password);
		rememberView = (CheckBox) mActivityClass.findViewById(ca.mcgill.mymcgill.R.id.login_remember_username);
		loginButton = (Button) mActivityClass.findViewById(ca.mcgill.mymcgill.R.id.login_button);
		
		//String username = "";
		//String password = "";
		// TODO INSERT REAL USERNAME AND PASSWORD
		//usernameView.setText(username);
		//usernameView.setText(password);

		//TODO ENTER USER NAME AND PASSWORD
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		//TODO LOG OUT 
	}
	
	
	
}
