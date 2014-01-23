package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Constants;

/**
 * Author: Julien
 * Date: 22/01/14, 7:34 PM
 */
public class LoginActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get the necessary views
        final EditText usernameView = (EditText) findViewById(R.id.login_username);
        final EditText passwordView = (EditText) findViewById(R.id.login_password);
        Button login = (Button) findViewById(R.id.login_button);

        //Check if an error message needs to be displayed, display it if so
        int connectionStatus = getIntent().getIntExtra(Constants.CONNECTION_STATUS, -1);
        if(connectionStatus == Constants.CONNECTION_WRONG_INFO){
            showErrorDialog(getResources().getString(R.string.login_error_wrong_data));
        }
        else if(connectionStatus == Constants.CONNECTION_OTHER){
            showErrorDialog(getResources().getString(R.string.login_error_other));
        }

        //Set up the OnClickListener for the login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the username text, append the mcgill email at the end
                String username = usernameView.getText().toString().trim() + getResources().getString(R.string.login_email);

                //Get the password text
                String password = passwordView.getText().toString().trim();

                //Connect
                int connectionStatus = Connection.connect(username, password);

                //If the connection was successful, go to MainActivity
                if(connectionStatus == Constants.CONNECTION_OK){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                //If the wrong data was given, show an alert dialog explaning this
                else if(connectionStatus == Constants.CONNECTION_WRONG_INFO){
                    showErrorDialog(getResources().getString(R.string.login_error_wrong_data));
                }
                //Show general error dialog
                else if(connectionStatus == Constants.CONNECTION_OTHER){
                    showErrorDialog(getResources().getString(R.string.login_error_other));
                }
            }
        });
    }

    public void showErrorDialog(String errorMessage){
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle(getResources().getString(R.string.error))
                .setMessage(errorMessage)
                .setNeutralButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}