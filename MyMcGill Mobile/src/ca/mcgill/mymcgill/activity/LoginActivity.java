package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ca.mcgill.mymcgill.R;

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

        //Set up the OnClickListener for the login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the username text, append the mcgill email at the end
                String username = usernameView.getText().toString().trim() + getResources().getString(R.string.login_email);

                //Get the password text
                String password = passwordView.getText().toString().trim();


            }
        });
    }
}