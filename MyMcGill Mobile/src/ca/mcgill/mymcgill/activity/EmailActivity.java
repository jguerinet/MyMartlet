package ca.mcgill.mymcgill.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.TextView;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Email;
import ca.mcgill.mymcgill.util.Constants;


/**
 * Created by Ryan Singzon on 14/02/14.
 * This activity will show a user's individual emails
 */
public class EmailActivity extends ListActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Get email from intent
        Email email = (Email) getIntent().getSerializableExtra(Constants.EMAIL);

        //Set title as the email subject
        setTitle(email.getSubject());

        //Display email sender
        TextView emailSender = (TextView)findViewById(R.id.email_sender);
        emailSender.setText(email.getSender());

        //Display date received
        TextView emailDate = (TextView)findViewById(R.id.email_date_received);
        emailDate.setText(email.getDate());

        //Display email body
        TextView emailBody = (TextView)findViewById(R.id.email_body);
        emailBody.setText(email.getBody());
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
