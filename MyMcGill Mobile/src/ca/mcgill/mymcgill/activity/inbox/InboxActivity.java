package ca.mcgill.mymcgill.activity.inbox;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Inbox;
import ca.mcgill.mymcgill.object.Email;

/**
 * Created by Ryan Singzon on 14/02/14.
 */
public class InboxActivity extends ListActivity{

    private Inbox mInbox;
    private TextView mTotalNew;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_inbox);

        //TODO: Get stored email from ApplicationClass


        //Get views
        mTotalNew = (TextView)findViewById(R.id.inbox_total_new);

        /**
         * Testing: Create fake email inboxes to test the UI
         */
        List<Email> emails = new ArrayList<Email>();
        emails.add(new Email("TEST", "From Test", "January 12", "Message body"));
        emails.add(new Email("My email", "Ryan Singzon", "123", "This is a message, lsdkjflksdjf, hello"));
        mInbox = new Inbox(emails);

        //Refresh email page
        loadInfo();
    }

    private void loadInfo(){
        //Get the number of new emails
        mTotalNew.setText(getResources().getString(R.string.inbox_newMessages, mInbox.getNumNewEmails()));

        //Load adapter
        InboxAdapter adapter = new InboxAdapter(InboxActivity.this, mInbox);
        setListAdapter(adapter);

    }
}
