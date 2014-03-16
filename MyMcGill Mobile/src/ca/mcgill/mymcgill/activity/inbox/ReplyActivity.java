package ca.mcgill.mymcgill.activity.inbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.base.BaseActivity;
import ca.mcgill.mymcgill.object.Email;
import ca.mcgill.mymcgill.util.Constants;


public class ReplyActivity extends BaseActivity {

	Email email;
	EditText emailSubject;
	Email replyEmail;
	String attachFilePath;
	LinearLayout layout;
	Context ReplyContext;
	boolean isSending;
	EditText emails;
	
	private static final int FILE_CODE = 100;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_reply);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		email = (Email) getIntent().getSerializableExtra(Constants.EMAIL);
		TextView attachText = (TextView) findViewById(R.id.attachText);
		ReplyContext = getApplicationContext();
		
		attachFilePath = null;	
		layout = (LinearLayout) findViewById(R.id.LinearLayout1);
		emails = (EditText) findViewById(R.id.emailRecipient);
		emailSubject = (EditText)findViewById(R.id.emailSubject);
		
		//sending a fresh email
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
			emails.setText(email.getSender());
			
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
		List<String> Senders =null;
		
		if(isSending){
			Senders = Arrays.asList(emails.getText().toString().split(";"));
		}
		else{
			Senders=email.getSenderList();
		}
		
		replyEmail = new Email(emailSubject.getText().toString(), Senders, body.getText().toString(), ReplyContext);
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

