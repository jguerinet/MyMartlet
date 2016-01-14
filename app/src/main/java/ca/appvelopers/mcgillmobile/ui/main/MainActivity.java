/*
 * Copyright 2014-2016 Appvelopers
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

package ca.appvelopers.mcgillmobile.ui.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;

import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;

/**
 * The MainActivity the contains the side drawer and the main views for the fragments
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class MainActivity extends DrawerActivity {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpToolbar(false);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
//        //Only show the menu in portrait mode for the schedule
//        if(mPage == Homepage.SCHEDULE){
//            return getResources().getConfiguration().orientation !=
//                    Configuration.ORIENTATION_LANDSCAPE;
//        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

//        //Reload the menu and the view if this is the schedule
//        if(mPage == Homepage.SCHEDULE){
//            //Reload the menu
//            invalidateOptionsMenu();
//
//            //Reload the view
//            mScheduleFragment.updateView(newConfig.orientation);
//        }
    }
}