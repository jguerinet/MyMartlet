package ca.appvelopers.mcgillmobile.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import ca.appvelopers.mcgillmobile.activity.main.MainActivity;

/**
 * Author: Julien Guerinet
 * Date: 2015-01-17 11:30 PM
 * Copyright (c) 2014 Appvelopers. All rights reserved.
 */

public class BaseFragment extends Fragment {
    //The reference to MainActivity
    public MainActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.mActivity = (MainActivity)getActivity();
    }
}