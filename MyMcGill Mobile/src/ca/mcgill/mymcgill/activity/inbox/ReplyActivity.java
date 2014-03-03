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
	
	private static final int FILE_CODE = 100;
	
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
			this.setTitle("Reply Email");
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
            	this.startActivityForResult(attachIntent, FILE_CODE);
            	
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		email = (Email) getIntent().getSerializableExtra(Constants.EMAIL);
		
		TextView attachText = (TextView) findViewById(R.id.attachText);
		if (attachFilePath == null) attachText.setText("no files attached");
		else attachText.setText("Files Attached: " + attachFilePath);

	}

	public void sendMessage(View v) {
		EditText body = (EditText) findViewById(R.id.emailBody);
		replyEmail = new Email(emailSubject.getText().toString(), email.getSenderList(), body.getText().toString(), this);
		//Toast.makeText(this, "Sending : " + attachFilePath, Toast.LENGTH_SHORT).show();
		new Thread(new Runnable() {
            @Override
            public void run() {
				replyEmail.send(attachFilePath);
				finish();
                    };          
        }).start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == FILE_CODE){
			if(resultCode == RESULT_OK){				
				attachFilePath = data.getStringExtra("file");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}

