package ca.mcgill.mymcgill.activity.inbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Email;
import ca.mcgill.mymcgill.util.Constants;


public class ReplyActivity extends Activity {

	Email email;
	EditText emailSubject;
	Email replyEmail;
	String attachFilePath;
	LinearLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_reply);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		boolean isSending;
		email = (Email) getIntent().getSerializableExtra(Constants.EMAIL);
		TextView attachText = (TextView) findViewById(R.id.attachText);
		attachFilePath = null;
		
		
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
			//get Layout
			layout = (LinearLayout) findViewById(R.id.LinearLayout1);
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
            	Intent attachIntent = new Intent(ReplyActivity.this,ca.mcgill.mymcgill.activity.inbox.AttachActivity.class);
            	//attachIntent.putExtra(name, value)
            	this.startActivity(attachIntent);
            	
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		attachFilePath = (String) getIntent().getStringExtra("file");
		Toast.makeText(this,attachFilePath, Toast.LENGTH_SHORT).show();
		TextView attachText = (TextView) findViewById(R.id.attachText);
		if (attachFilePath == null) attachText.setText(attachText.getText() + " no files attached");
		else attachText.setText(attachText.getText() + attachFilePath);
		
		

	}

	public void sendMessage(View v) {
		EditText body = (EditText) findViewById(R.id.emailBody);
		replyEmail = new Email(emailSubject.getText().toString(), email.getSenderList(), body.getText().toString(), this);
		
		new Thread(new Runnable() {
            @Override
            public void run() {
				replyEmail.send();
				if (attachFilePath != null) ; // attach file
				finish();
                    };          
        }).start();
	}
}