package ca.mcgill.mymcgill.activity;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.R.layout;
import ca.mcgill.mymcgill.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EbillActivity extends Activity {
	public ArrayList<Ebill> ebillList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ebill);
		
		populateEbill();
		populateListView();
	}
	
	private void populateEbill(){
		ebillList = new ArrayList<Ebill>();
		ebillList.add(new Ebill("Statement Date","Due Date","Amount Due"));
	}

	
	public void addEbill(Ebill ebill)
	{
		ebillList.add(ebill);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ebill, menu);
		return true;
	}
	private void populateListView(){
		ArrayAdapter<Ebill> adapter = new listAdapter();
		ListView list = (ListView) findViewById(R.id.ebill_listview);
		list.setAdapter(adapter);
	}
	
	private class listAdapter extends ArrayAdapter<Ebill>{
		public listAdapter(){
			super(EbillActivity.this,R.layout.item_ebill,ebillList);
		}
		
		public View getView(int position,View convertView, ViewGroup parent){
			View ebillView = convertView;
			if(ebillView == null)
			{
				ebillView = getLayoutInflater().inflate(R.layout.item_ebill,parent,false);
			} 
			
			Ebill ebill = ebillList.get(position);
			TextView ebillStatement = (TextView) ebillView.findViewById(R.id.ebill_statement);
			TextView ebillDue = (TextView) ebillView.findViewById(R.id.ebill_due);
			TextView ebillAmount = (TextView) ebillView.findViewById(R.id.ebill_amount);
			
			ebillStatement.setText(ebill.getStatementDate());
			ebillDue.setText(ebill.getDueDate());
			ebillAmount.setText(ebill.getAmountDue());
			
			return ebillView;
			
		}
	}
	
	
	public class Ebill {
		private String statementDate;
		private String dueDate;
		private String amountDue;
		
		public Ebill(String statementDate,String dueDate,String amountDue)
		{
			this.statementDate = statementDate;
			this.dueDate = dueDate;
			this.amountDue = amountDue;
		}

		public String getStatementDate() {
			return statementDate;
		}

		public void setStatementDate(String statementDate) {
			this.statementDate = statementDate;
		}

		public String getDueDate() {
			return dueDate;
		}

		public void setDueDate(String dueDate) {
			this.dueDate = dueDate;
		}

		public String getAmountDue() {
			return amountDue;
		}

		public void setAmountDue(String amountDue) {
			this.amountDue = amountDue;
		}
		
		
	}
}


