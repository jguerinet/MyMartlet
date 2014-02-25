package ca.mcgill.mymcgill.activity.inbox;

import ca.mcgill.mymcgill.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;


public class ReplyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_reply);
		getActionBar().setDisplayHomeAsUpEnabled(true);


	}

}