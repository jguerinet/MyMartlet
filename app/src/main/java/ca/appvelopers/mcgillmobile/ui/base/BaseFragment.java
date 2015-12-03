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

package ca.appvelopers.mcgillmobile.ui.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.appvelopers.mcgillmobile.ui.main.MainActivity;

/**
 * The base fragment for all fragments involved in the main view
 * @author Julien Guerinet
 * @since 1.0.0
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class BaseFragment extends Fragment {
    /**
     * MainActivity reference
     */
    protected MainActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Set the activity reference
        this.mActivity = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //Set the orientation to sensor
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        return null;
    }

    /**
     * Hides the loading indicator. To be called by each fragment after the view is loaded
     */
    protected void hideLoadingIndicator(){
        mActivity.showFragmentSwitcherProgress(false);
    }

    /**
     * Locks the fragment in portrait mode
     */
    protected void lockPortraitMode(){
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}