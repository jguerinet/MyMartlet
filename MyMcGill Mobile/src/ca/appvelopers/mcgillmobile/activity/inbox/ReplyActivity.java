package ca.appvelopers.mcgillmobile.activity.inbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.BaseActivity;
import ca.appvelopers.mcgillmobile.object.Email;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;


public class ReplyActivity extends BaseActivity {

	Email email;
	EditText emailSubject;
	Email replyEmail;
	String attachFilePath;
	LinearLayout layout;
	Context ReplyContext;
	boolean isSending;
	EditText emails;
	EditText carbonCopy;
	
	
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
		carbonCopy = (EditText) findViewById(R.id.emailCarbonCopy);
		
		attachFilePath = null;	
		layout = (LinearLayout) findViewById(R.id.LinearLayout1);
		emails = (EditText) findViewById(R.id.emailRecipient);
		emailSubject = (EditText)findViewById(R.id.emailSubject);
		
		
		
		//sending a fresh email
		if (email == null)
		{
			isSending = true;
			this.setTitle(R.string.send_email);
			
		}
		else isSending = false;

        if(isSending){
            GoogleAnalytics.sendScreen(this, "Email - Send");
        }
        else{
            GoogleAnalytics.sendScreen(this, "Email - Reply");
        }
		
		// TODO Modify for Forward Email
		if (!isSending)
		{
			this.setTitle(R.string.email_reply);
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
		menu.add(Menu.NONE, Constants.MENU_ITEM_ADD_ATTACH, Menu.NONE,R.string.send_add_attachment);
		return super.onCreateOptionsMenu(menu);
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {   
            case Constants.MENU_ITEM_ADD_ATTACH:
            	Intent attachIntent = new Intent(ReplyActivity.this, ca.appvelopers.mcgillmobile.activity.inbox.AttachActivity.class);
            	this.startActivityForResult(attachIntent, FILE_CODE);
            	
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
	@Override
	protected void onResume() {
		super.onResume();
		email = (Email) getIntent().getSerializableExtra(Constants.EMAIL);
		
		TextView attachText = (TextView) findViewById(R.id.attachText);
		if (attachFilePath == null) attachText.setText(R.string.send_no_attachments);
		else attachText.setText(R.string.send_attachments + attachFilePath);

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
		Senders.addAll(Arrays.asList(carbonCopy.getText().toString().split(";")));
		
		
//		String sendString = "";
//		for (int i = 0 ;i < Senders.size();i ++ )
//		{
//			sendString += Senders.get(i) + "\n";
//		}
//		//body.setText(sendString);
		replyEmail = new Email(emailSubject.getText().toString(), Senders, body.getText().toString(), ReplyContext);

        new SendEmail().execute();
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

    private class SendEmail extends AsyncTask<Void, Void, Void> {
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute(){
            //Show ProgressDialog
            mProgressDialog = new ProgressDialog(ReplyActivity.this);
            mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
        }

        //Retrieve content from inbox page
        @Override
        protected Void doInBackground(Void... params){
            try{
                replyEmail.send(attachFilePath);
                //Show Success Message
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ReplyActivity.this, getResources().getString(R.string.send_message_success), Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
            catch(MessagingException e){
                e.printStackTrace();
                //Show Error message
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ReplyActivity.this, getResources().getString(R.string.send_message_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

        //Update or create email object and display data
        @Override
        protected void onPostExecute(Void result){
            //Dismiss the progress dialog
            mProgressDialog.dismiss();
        }
    }
}

