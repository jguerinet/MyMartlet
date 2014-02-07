package ca.mcgill.mymcgill.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

import ca.mcgill.mymcgill.objects.Ebill;

public class EbillActivity extends Activity {
	private ArrayList<Ebill> ebillList = new ArrayList<Ebill>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ebill);

		populateEbill();
		populateListView();
	}
	
	private String readFromFile(String filename) {
    	
    	//create return string
        String ret = "";

        try {
        	File file = new File(filename);

            InputStream inputStream =  new FileInputStream(file);
            

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (IOException e) {
            System.out.println("Can not read file: " + e.toString());
        }
        catch(Exception e){
        	System.out.println("Exception: " + e.toString());
        }
        finally{
        	
        	//always return something
        	return ret;
        }
    }
	
	//populate ebill with parsed contents
	private void populateEbill(){
		String fileContent = readFromFile("ebills.htm");

		Document doc = Jsoup.parse(fileContent);
		Element ebillTable = doc.getElementsByClass("datadisplaytable").first();
		Elements ebillRows = ebillTable.getElementsByTag("tr");
		getEBill(ebillRows);
	}
	
	//parser algorithm
	private void getEBill(Elements rows){
		for (int i = 2; i < rows.size(); i+=2) {
			Element row = rows.get(i);
			Elements cells = row.getElementsByTag("td");
			String statementDate = cells.get(0).text();
			String dueDate = cells.get(3).text();
			String amountDue = cells.get(5).text();
			Ebill eBill = new Ebill(statementDate, dueDate, amountDue);
			addEbill(eBill);
		}
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


}


