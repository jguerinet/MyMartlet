package ca.appvelopers.mcgillmobile.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.appvelopers.mcgillmobile.R;

/**
 * Author : Julien
 * Date :  2014-06-03 9:24 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class HelpFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_help, null);

        return view;
    }
}