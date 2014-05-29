package ca.mcgill.mymcgill.activity.walkthrough;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Faculty;
import ca.mcgill.mymcgill.object.HomePage;

/**
 * Author : Yulric
 * Date :  17/11/13 10:28 AM
 */
public class WalkthroughFragment extends Fragment {
    private static final String PAGE_NUMBER = "page_number";
    private int mPageNumber;

    //Create the fragment with the page number in the arguments.
    public static WalkthroughFragment createInstance(int pageNumber){
        WalkthroughFragment fragment = new WalkthroughFragment();

        Bundle args = new Bundle();
        args.putInt(PAGE_NUMBER, pageNumber);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mPageNumber = getArguments().getInt(PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View pageView;

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

            //Offline Access / Security
            case 3:
                pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_3, null);

                //Set the typeface for the icon
                TextView securityIcon = (TextView)pageView.findViewById(R.id.security_icon);
                securityIcon.setTypeface(App.getIconFont());

                break;

            //Help/About/Bugs
            case 4:
                pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_4, null);

                //Set the typeface for the icon
                TextView bugIcon = (TextView)pageView.findViewById(R.id.bug_icon);
                bugIcon.setTypeface(App.getIconFont());

                break;

            //Default Homepage / Faculty
            case 5:
                pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_5, null);

                Spinner homepage = (Spinner)pageView.findViewById(R.id.homepage);
                //Standard ArrayAdapter
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, HomePage.getHomePageStrings(getActivity()));
                //Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Apply the adapter to the spinner
                homepage.setAdapter(adapter);
                homepage.setSelection(App.getHomePage().ordinal());
                homepage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        //Get the chosen language
                        HomePage chosenHomePage = HomePage.values()[position];
                        //Update it in the ApplicationClass
                        App.setHomePage(chosenHomePage);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });

                Spinner faculty = (Spinner)pageView.findViewById(R.id.faculty);
                //Standard ArrayAdapter
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                        Faculty.getFacultyStrings(getActivity()));
                //Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Apply the adapter to the spinner
                faculty.setAdapter(adapter);
                faculty.setSelection(0);
                faculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        //Get the chosen language
                        Faculty faculty = Faculty.values()[position];
                        //Update it in the ApplicationClass
                        App.setFaculty(faculty);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
               break;

            default:
                return null;
        }

        return pageView;
    }
}
