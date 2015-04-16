package ca.appvelopers.mcgillmobile.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.appvelopers.mcgillmobile.activity.main.MainActivity;

/**
 * Author: Julien Guerinet
 * Date: 2015-01-17 11:30 PM
 * Copyright (c) 2014 Appvelopers. All rights reserved.
 */

public class BaseFragment extends Fragment {
    //The reference to MainActivity
    protected MainActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.mActivity = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //Set the orientation to sensor
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        return null;
    }

    public void hideLoadingIndicator(){
        mActivity.showFragmentSwitcherProgress(false);
    }

    /**
     * Locks the fragment in portrait mode
     */
    public void lockPortraitMode(){
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}