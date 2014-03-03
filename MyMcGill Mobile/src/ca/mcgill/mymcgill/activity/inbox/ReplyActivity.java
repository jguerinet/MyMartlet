package ca.mcgill.mymcgill.activity.inbox;

import ca.mcgill.mymcgill.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ca.mcgill.mymcgill.activity.EmailActivity;
import ca.mcgill.mymcgill.activity.LoginActivity;
import ca.mcgill.mymcgill.object.ConnectionStatus;
import ca.mcgill.mymcgill.object.Email;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Constants;
import ca.mcgill.mymcgill.util.Save;


public class ReplyActivity extends Activity {

	Email email;
	EditText emailSubject;
	Email replyEmail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_reply);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		boolean isSending;
		email = (Email) getIntent().getSerializableExtra(Constants.EMAIL);
		
		if (email == null)
		{
			isSending = true;
			this.setTitle("Send Email");
		}
		else isSending = false;
		
		// TODO Modify for Forward Email
		if (!isSending)
		{
			//Display email sender
			EditText emails = (EditText) findViewById(R.id.emailRecipient);
			emails.setText(email.getSender());
			
			//Display subject
			emailSubject = (EditText)findViewById(R.id.emailSubject);
			if (email.getSubject().contains("RE:")) {
				emailSubject.setText(email.getSubject());
			} else {
				emailSubject.setText("RE: " + email.getSubject());
			}			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// add attachment menu item
		menu.add(Menu.NONE, Constants.MENU_ITEM_ADD_ATTACH, Menu.NONE,R.string.reply_add_attachment);
		return super.onCreateOptionsMenu(menu);
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {   
            case Constants.MENU_ITEM_ADD_ATTACH:
            	// TODO add attachements
            	this.startActivity(new Intent(ReplyActivity.this,ca.mcgill.mymcgill.activity.inbox.AttachActivity.class));
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }

	public void sendMessage(View v) {
		EditText body = (EditText) findViewById(R.id.emailBody);
		replyEmail = new Email(emailSubject.getText().toString(), email.getSenderList(), body.getText().toString(), this);
		new Thread(new Runnable() {
            @Override
            public void run() {
				replyEmail.send();
				finish();
                    };          
        }).start();
	}
}