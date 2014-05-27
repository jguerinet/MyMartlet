package ca.mcgill.mymcgill.activity.walkthrough;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        View pageView = null;

        switch(mPageNumber){
            //Welcome to the MyMcGill App
            case 0:
                break;
            //Access all of your MyMcGill essentials easily
            case 1:{
                break;
            }

            //Main Menu Explanation
            case 2:{
               break;
            }

            //Offline Access
            case 3:{
                break;
            }

            //Security
            case 4:{
                break;
            }

            //Help/About Pages
            case 5:{
               break;
            }

            //Default Homepage
            case 6:{
               break;
            }
            default:{
                return null;
            }
        }

        return pageView;
    }
}
