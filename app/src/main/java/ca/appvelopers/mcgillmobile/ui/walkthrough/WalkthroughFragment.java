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

package ca.appvelopers.mcgillmobile.ui.walkthrough;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.DrawerItem;
import ca.appvelopers.mcgillmobile.model.Faculty;
import ca.appvelopers.mcgillmobile.ui.view.FacultyAdapter;
import ca.appvelopers.mcgillmobile.ui.view.HomepageAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Author : Julien
 */
public class WalkthroughFragment extends Fragment {
    private static final String PAGE_NUMBER = "page_number";
    private int mPageNumber;
    private boolean mEmail;

    //Create the fragment with the page number in the arguments.
    public static WalkthroughFragment createInstance(int pageNumber, boolean email){
        WalkthroughFragment fragment = new WalkthroughFragment();

        Bundle args = new Bundle();
        args.putInt(PAGE_NUMBER, pageNumber);
        args.putBoolean(Constants.EMAIL, email);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mPageNumber = getArguments().getInt(PAGE_NUMBER);
        mEmail = getArguments().getBoolean(Constants.EMAIL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View pageView;

        //Normal Walkthrough
        if(!mEmail){
            switch(mPageNumber){
                //Welcome to the MyMcGill App
                case 0:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_0, null);
                    break;
                //Access all of your MyMcGill essentials easily
                case 1:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_1, null);
                    break;

                //Main Menu Explanation
                case 2:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_2, null);
                    break;

                //Horizontal Schedule
                case 3:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_3, null);
                    break;

                //Offline Access / Security
                case 4:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_4, null);

                    //Set the typeface for icons
                    TextView securityIcon = (TextView)pageView.findViewById(R.id.security_icon);
                    securityIcon.setTypeface(App.getIconFont());
                    TextView settingsIcon = (TextView)pageView.findViewById(R.id.settings_icon);
                    settingsIcon.setTypeface(App.getIconFont());
                    TextView emailIcon = (TextView)pageView.findViewById(R.id.email_icon);
                    emailIcon.setTypeface(App.getIconFont());

                    break;

                //Help/About/Bugs
                case 5:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_5, null);

                    //Set the typeface for the icon
                    TextView bugIcon = (TextView)pageView.findViewById(R.id.bug_icon);
                    bugIcon.setTypeface(App.getIconFont());
                    TextView helpIcon = (TextView)pageView.findViewById(R.id.help_icon);
                    helpIcon.setTypeface(App.getIconFont());

                    break;

                //Default Homepage / Faculty
                case 6:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_6, null);

                    Spinner homepage = (Spinner)pageView.findViewById(R.id.homepage);
                    final HomepageAdapter homepageAdapter = new HomepageAdapter(getActivity());
                    homepage.setAdapter(homepageAdapter);
                    homepage.setSelection(homepageAdapter.getPosition(App.getHomePage()));
                    homepage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            DrawerItem chosenDrawerItem = homepageAdapter.getItem(position);

                            Analytics.getInstance().sendEvent("Walkthrough", "Homepage",
                                    chosenDrawerItem.toString());

                            //Update it in the App
                            App.setHomePage(chosenDrawerItem);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    Spinner faculty = (Spinner)pageView.findViewById(R.id.faculty);
                    //Standard ArrayAdapter
                    final FacultyAdapter facultyAdapter = new FacultyAdapter(getActivity(), true);
                    faculty.setAdapter(facultyAdapter);
                    //Default selection is empty
                    faculty.setSelection(0);
                    faculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            //Get the chosen language
                            Faculty faculty = facultyAdapter.getItem(position);

                            //If the faculty is not null, send the GA
                            if(faculty != null){
                                Analytics.getInstance().sendEvent("Walkthrough", "Faculty",
                                        faculty.toString());
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });
                    break;

                default:
                    return null;
            }
        }
        //Email Walkthrough
        else{
            switch(mPageNumber){
                case 0:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_email_0, null);
                    break;

                case 1:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_email_1, null);
                    break;

                case 2:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_email_2, null);
                    break;

                case 3:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_email_3, null);
                    break;

                case 4:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_email_4, null);
                    break;

                case 5:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_email_5, null);
                    break;

                case 6:
                    pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_email_6, null);
                    break;

                default:
                    return null;
            }
        }

        return pageView;
    }
}
