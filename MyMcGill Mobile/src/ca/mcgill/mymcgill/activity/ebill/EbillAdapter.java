package ca.mcgill.mymcgill.activity.ebill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Ebill;

/**
 * Author: Julien
 * Date: 16/02/14, 3:30 PM
 */
public class EbillAdapter extends BaseAdapter {
    private List<Ebill> mEbills;
    private Context mContext;

    public EbillAdapter(Context context, List<Ebill> eBills){
        this.mContext = context;
        this.mEbills = eBills;
    }

    @Override
    public int getCount() {
        return mEbills.size();
    }

    @Override
    public Ebill getItem(int position) {
        return mEbills.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_ebill_item, null);
        }

        //Quick Check
        assert (view != null);

        //Get the current ebill item
        Ebill ebillItem = getItem(position);

        //Fill out the info
        TextView statementDate = (TextView)view.findViewById(R.id.ebill_statement_date);
        statementDate.setText(" " + ebillItem.getStatementDate());

        TextView dueDate = (TextView)view.findViewById(R.id.ebill_due_date);
        dueDate.setText(" " + ebillItem.getDueDate());

        TextView amountDue = (TextView)view.findViewById(R.id.ebill_amount);
        amountDue.setText(" " + ebillItem.getAmountDue());

        return view;
    }
}
