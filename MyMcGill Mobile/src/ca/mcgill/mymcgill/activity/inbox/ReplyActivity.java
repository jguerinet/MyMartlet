package ca.mcgill.mymcgill.activity.inbox;

import ca.mcgill.mymcgill.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import ca.mcgill.mymcgill.activity.LoginActivity;
import ca.mcgill.mymcgill.object.ConnectionStatus;
import ca.mcgill.mymcgill.object.Email;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Constants;
import ca.mcgill.mymcgill.util.Save;


public class ReplyActivity extends Activity {

	Email email;
	
	Email replyEmail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_reply);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		email = (Email) getIntent().getSerializableExtra(Constants.EMAIL);
		
        //Display email sender
        EditText emails = (EditText) findViewById(R.id.replyEmails);
        emails.setText(email.getSender());
        
		//Display subject
        EditText emailSubject = (EditText)findViewById(R.id.replySubject);
        emailSubject.setText("RE: " + email.getSubject());
	}
	
	public void sendMessage(View v) {
		EditText body = (EditText) findViewById(R.id.replyBody);
		replyEmail = new Email(email.getSubject(), email.getSenderList(), "No Date", body.toString(), false);
		new Thread(new Runnable() {
            @Override
            public void run() {
				replyEmail.send();
				finish();
                    };
                
            
        }).start();
		
		//replyEmail.send();
	}

	public Email getEmail() {
		return replyEmail;
	}
		
		

}