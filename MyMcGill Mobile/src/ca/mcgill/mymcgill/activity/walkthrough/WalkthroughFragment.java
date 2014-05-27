package ca.mcgill.mymcgill.activity.walkthrough;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.mcgill.mymcgill.R;

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
                pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_0, container);
                break;
            //Access all of your MyMcGill essentials easily
            case 1:
                pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_1, container);
                break;

            //Main Menu Explanation
            case 2:
                pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_2, container);
               break;

            //Offline Access
            case 3:
                pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_3, container);
                break;

            //Security
            case 4:
                pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_4, container);
                break;

            //Help/About Pages
            case 5:
                pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_5, container);
               break;

            //Default Homepage
            case 6:
                pageView = View.inflate(getActivity(), R.layout.fragment_walkthrough_6, container);
               break;

            default:
                return null;
        }

        return pageView;
    }
}
