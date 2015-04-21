/*
 * Copyright 2014-2015 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.ui.ebill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Statement;

/**
 * Author: Julien
 * Date: 16/02/14, 3:30 PM
 */
public class EbillAdapter extends BaseAdapter {
    private List<Statement> mEbills;
    private Context mContext;

    public EbillAdapter(Context context, List<Statement> eBills){
        this.mContext = context;
        this.mEbills = eBills;
    }

    @Override
    public int getCount() {
        return mEbills.size();
    }

    @Override
    public Statement getItem(int position) {
        return mEbills.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        //Items are not clickable
        return false;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_ebill, null);
        }

        //Quick Check
        assert (view != null);

        //Get the current ebill item
        Statement statement = getItem(position);

        //Fill out the info
        TextView statementDate = (TextView)view.findViewById(R.id.ebill_statement_date);
        statementDate.setText(" " + statement.getDate());

        TextView dueDate = (TextView)view.findViewById(R.id.ebill_due_date);
        dueDate.setText(" " + statement.getDueDate());

        TextView amountDue = (TextView)view.findViewById(R.id.ebill_amount);
        amountDue.setText(" " + statement.getAmount());

        return view;
    }
}
