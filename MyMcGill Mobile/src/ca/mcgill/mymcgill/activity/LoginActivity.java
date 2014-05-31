package ca.mcgill.mymcgill.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.base.BaseActivity;
import ca.mcgill.mymcgill.object.ConnectionStatus;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Constants;
import ca.mcgill.mymcgill.util.DialogHelper;
import ca.mcgill.mymcgill.util.Load;
import ca.mcgill.mymcgill.util.Save;

/**
 * Author: Julien
 * Date: 22/01/14, 7:34 PM
 */
public class LoginActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get the necessary views
        final Button login = (Button) findViewById(R.id.login_button);
        final EditText usernameView = (EditText) findViewById(R.id.login_username);
        final EditText passwordView = (EditText) findViewById(R.id.login_password);
        //Set it to that when clicking the IME Action button it tries to log you in directly
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    login.performClick();
                    return true;
                }
                return false;
            }
        });
        final CheckBox rememberUsernameView = (CheckBox) findViewById(R.id.login_remember_username);
        //Remember Me box checked based on user's previous preference
        rememberUsernameView.setChecked(Load.loadRememberUsername(this));
        
        //Check if an error message needs to be displayed, display it if so
        ConnectionStatus connectionStatus = (ConnectionStatus)getIntent().getSerializableExtra(Constants.CONNECTION_STATUS);
        if(connectionStatus != null){
            DialogHelper.showNeutralAlertDialog(LoginActivity.this,
                    LoginActivity.this.getResources().getString(R.string.error),
                    connectionStatus.getErrorString(this));
        }

        //Fill out username text if it is present
        String username = Load.loadUsername(this);
        if(username != null){
            usernameView.setText(username);
        }

        //Set up the OnClickListener for the login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the username text
                final String username = usernameView.getText().toString().trim();

                //Get the password text
                final String password = passwordView.getText().toString().trim();

                //Check that both of them are not empty, create appropriate error messages if so
                if(TextUtils.isEmpty(username)){
                    DialogHelper.showNeutralAlertDialog(LoginActivity.this,
                            LoginActivity.this.getResources().getString(R.string.error),
                            getResources().getString(R.string.login_error_username_empty));
                    return;
                }
                else if(TextUtils.isEmpty(password)){
                    DialogHelper.showNeutralAlertDialog(LoginActivity.this,
                            LoginActivity.this.getResources().getString(R.string.error),
                            getResources().getString(R.string.login_error_password_empty));
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.please_wait));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the username and password
                        Connection.getInstance().setUsername(username + LoginActivity.this.getResources().getString(R.string.login_email));
                        Connection.getInstance().setPassword(password);
						final ConnectionStatus connectionStatus = Connection.getInstance().connectToMinerva(LoginActivity.this);
						// If the connection was successful, go to Homepage
						if (connectionStatus == ConnectionStatus.CONNECTION_OK) {
							// Store the login info.
							Save.saveUsername(LoginActivity.this, username);
                            Save.savePassword(LoginActivity.this, password);
                            Save.saveRememberUsername(LoginActivity.this, rememberUsernameView.isChecked());

                            Connection.getInstance().downloadAll(LoginActivity.this);
                            startActivity(new Intent(LoginActivity.this, App.getHomePage().getHomePageClass()));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            });
                            finish();
                        }
                        //Else show error dialog
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    DialogHelper.showNeutralAlertDialog(LoginActivity.this,
                                            LoginActivity.this.getResources().getString(R.string.error),
                                            connectionStatus.getErrorString(LoginActivity.this));
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}