package ca.mcgill.mymcgill.activity.inbox;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
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

        List<Email> emails = new ArrayList<Email>();
        emails.add(new Email("Test subject 1", "Bob Joe", "January 12", "This is a test message," +
                "blahl balhblh blah hello heloo", true));

        emails.add(new Email("HTML Content Test", "Bob Joe", "Feb 12", "<title>My email</title>" +
                "<br><br>" +
                "<h1>Header</h1>" +
                "<br>" +
                "<h2>Sub header</h2>" +
                "<img src='http://images6.fanpop.com/image/photos/32900000/Cat-cats-32958715-1440-900.jpg'"+
                "blahl balhblh blah hello heloo", false));

        emails.add(new Email("Scrolling box test", "Bob Joe", "Feb 12", "This is a test message," +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj" +
                "blahl balhblh blah hello heloo lskdjf sdlfkjlskdjf  sldkfj sldfj sldkfj sdflkj"
                , false));


        mInbox = new Inbox(emails);


        if(mInbox == null){
            TextView errorMessage = (TextView)findViewById(R.id.inbox_error);
            errorMessage.setVisibility(View.VISIBLE);
        }
        else{
            //Load emails
            loadInfo();
        }
    }

    //Populates the list with the emails contained in the Inbox object
    private void loadInfo(){
        //Get the number of new emails
        mTotalNew.setText(getResources().getString(R.string.inbox_newMessages, mInbox.getNumNewEmails()));

        //Load adapter
        InboxAdapter adapter = new InboxAdapter(InboxActivity.this, mInbox);
        setListAdapter(adapter);

    }
}
