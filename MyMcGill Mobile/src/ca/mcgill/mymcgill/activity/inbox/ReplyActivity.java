package ca.mcgill.mymcgill.activity.inbox;

import ca.mcgill.mymcgill.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ReplyActivity extends Activity implements View.OnClickListener {

	EditText personsEmail, intro, personsName, stupidThings, hatefulAction,
			outro;
	String emailAdd, beginning, name, stupidAction, hatefulAct, out;
	Button sendEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reply);
		initializeVars();
		sendEmail.setOnClickListener(this);
	}

	private void initializeVars() {
		// TODO Auto-generated method stub

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub


	}
//
//	private void convertEditTextVarsIntoStringsAndYesThisIsAMethodWeCreated() {
//		// TODO Auto-generated method stub
//		emailAdd = personsEmail.getText().toString();
//		beginning = intro.getText().toString();
//		name = personsName.getText().toString();
//		stupidAction = stupidThings.getText().toString();
//		hatefulAct = hatefulAction.getText().toString();
//		out = outro.getText().toString();
//	}
//
//	@Override
//	protected void onPause() {
//		// TODO Auto-generated method stub
//		super.onPause();
//		finish();
//	}

}