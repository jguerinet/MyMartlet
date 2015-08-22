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
import ca.appvelopers.mcgillmobile.ui.settings.HomepageAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * One page in the walkthrough
 * @author Julien Guerinet
 * @author Rafi Uddin
 * @version 2.0.0
 * @since 1.0.0
 */
public class WalkthroughFragment extends Fragment {
    private static final String PAGE_NUMBER = "page_number";
    /**
     * The page number this fragment represents
     */
    private int mPageNumber;
    /**
     * True if this is the email walkthrough, false otherwise
     */
    private boolean mEmail;

    /**
     * Creates an instance of the WalkthroughFragment with the arguments bundled
     *
     * @param pageNumber The page number
     * @param email      True if this is the email walkthrough, false otherwise
     * @return A WalkthroughFragment instance
     */
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view;

        //Normal Walkthrough
        if(!mEmail){
            switch(mPageNumber){
                //Welcome to the MyMcGill App
                case 0:
                    return inflater.inflate(R.layout.fragment_walkthrough_0, container, false);
                //Access all of your MyMcGill essentials easily
                case 1:
                    return inflater.inflate(R.layout.fragment_walkthrough_1, container, false);
                //Main Menu Explanation
                case 2:
                    return inflater.inflate(R.layout.fragment_walkthrough_2, container, false);
                //Horizontal Schedule
                case 3:
                    return inflater.inflate(R.layout.fragment_walkthrough_3, container, false);
                //Offline Access / Security
                case 4:
                    view = inflater.inflate(R.layout.fragment_walkthrough_4, container, false);

                    //Set the typeface for icons
                    TextView securityIcon = (TextView)view.findViewById(R.id.security_icon);
                    securityIcon.setTypeface(App.getIconFont());
                    TextView settingsIcon = (TextView)view.findViewById(R.id.settings_icon);
                    settingsIcon.setTypeface(App.getIconFont());
                    TextView emailIcon = (TextView)view.findViewById(R.id.email_icon);
                    emailIcon.setTypeface(App.getIconFont());

                    return view;
                //Help/About/Bugs
                case 5:
                    view = inflater.inflate(R.layout.fragment_walkthrough_5, container, false);

                    //Set the typeface for the icon
                    TextView bugIcon = (TextView)view.findViewById(R.id.bug_icon);
                    bugIcon.setTypeface(App.getIconFont());
                    TextView helpIcon = (TextView)view.findViewById(R.id.help_icon);
                    helpIcon.setTypeface(App.getIconFont());

                    return view;
                //Default Homepage / Faculty
                case 6:
                    view = inflater.inflate(R.layout.fragment_walkthrough_6, container, false);

                    Spinner homepage = (Spinner)view.findViewById(R.id.homepage);
                    final HomepageAdapter homepageAdapter = new HomepageAdapter();
                    homepage.setAdapter(homepageAdapter);
                    homepage.setSelection(homepageAdapter.getPosition(App.getHomePage()));
                    homepage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view,
                                                   int position, long l){
                            DrawerItem chosenDrawerItem = homepageAdapter.getItem(position);

                            Analytics.getInstance().sendEvent("Walkthrough", "Homepage",
                                    chosenDrawerItem.toString());

                            //Update it in the App
                            App.setHomePage(chosenDrawerItem);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    Spinner faculty = (Spinner)view.findViewById(R.id.faculty);
                    final FacultyAdapter facultyAdapter = new FacultyAdapter();
                    faculty.setAdapter(facultyAdapter);
                    faculty.setSelection(0);
                    faculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view,
                                                   int position, long l) {
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

                    return view;
                default:
                    return null;
            }
        }
        //Email Walkthrough
        else{
            switch(mPageNumber){
                case 0:
                    return inflater.inflate(R.layout.fragment_walkthrough_email_0,
                            container, false);
                case 1:
                    return inflater.inflate(R.layout.fragment_walkthrough_email_1,
                            container, false);
                case 2:
                    return inflater.inflate(R.layout.fragment_walkthrough_email_2,
                            container, false);
                case 3:
                    return inflater.inflate(R.layout.fragment_walkthrough_email_3,
                            container, false);
                case 4:
                    return inflater.inflate(R.layout.fragment_walkthrough_email_4,
                            container, false);
                case 5:
                    return inflater.inflate(R.layout.fragment_walkthrough_email_5,
                            container, false);
                case 6:
                    return inflater.inflate(R.layout.fragment_walkthrough_email_6,
                            container, false);
                default:
                    return null;
            }
        }
    }
}
