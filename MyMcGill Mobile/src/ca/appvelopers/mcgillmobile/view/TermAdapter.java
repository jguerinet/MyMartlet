package ca.appvelopers.mcgillmobile.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.Term;

/**
 * Author : Julien
 * Date :  2014-06-10 7:25 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class TermAdapter extends BaseAdapter {
    private Context mContext;
    private List<Term> mTerms;

    public TermAdapter(Context context, List<Term> terms){
        this.mContext = context;
        this.mTerms = terms;
    }

    @Override
    public int getCount() {
        return mTerms.size();
    }

    @Override
    public Term getItem(int i) {
        return mTerms.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.spinner_dropdown, null);
        }

        //Semester name
        Term term = getItem(i);
        ((TextView)view).setText(term.toString(mContext));

        return view;
    }
}
