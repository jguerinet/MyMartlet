package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.widget.EditText;
import android.widget.TextView;

import android.view.MenuItem;
import android.view.View;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.inbox.ReplyActivity;
import ca.mcgill.mymcgill.object.Email;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Constants;


/**
 * Created by Ryan Singzon on 14/02/14.
 * This activity will show a user's individual emails
 */
public class EmailActivity extends Activity {

	Email email;
	 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Get email from intent
        email = (Email) getIntent().getSerializableExtra(Constants.EMAIL);

        //Display subject
        TextView emailSubject = (TextView)findViewById(R.id.email_subject);
        emailSubject.setText(email.getSubject());

        //Display email sender
        TextView emailSender = (TextView)findViewById(R.id.email_sender);
        emailSender.setText(email.getSender());

        //Display date received
        TextView emailDate = (TextView)findViewById(R.id.email_date_received);
        emailDate.setText(email.getDate());

        //Display email body
        TextView emailBody = (TextView)findViewById(R.id.email_body);
        emailBody.setText(Html.fromHtml(email.getBody()));

		new Thread(new Runnable() {
			@Override
			public void run() {
				// mark as read
				// if(!email.isRead()) {
				email.markAsRead();
				// }
			};

		}).start();
    }

    //Returns to parent activity when the top left button is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
    
    
    // Joshua David Alfaro
    // Created so that I can see the activity
    // When the user clicks the reply button
    public void replyMessage(View view) {
    	Intent intent = new Intent(this, ReplyActivity.class);
		intent.putExtra(Constants.EMAIL, email);
    	startActivity(intent);
    }
}
