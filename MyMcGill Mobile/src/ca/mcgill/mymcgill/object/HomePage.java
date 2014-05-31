package ca.mcgill.mymcgill.object;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.DesktopActivity;
import ca.mcgill.mymcgill.activity.MapActivity;
import ca.mcgill.mymcgill.activity.MyCoursesActivity;
import ca.mcgill.mymcgill.activity.RegistrationActivity;
import ca.mcgill.mymcgill.activity.ScheduleActivity;
import ca.mcgill.mymcgill.activity.courseslist.CoursesListActivity;
import ca.mcgill.mymcgill.activity.ebill.EbillActivity;
import ca.mcgill.mymcgill.activity.inbox.InboxActivity;
import ca.mcgill.mymcgill.activity.transcript.TranscriptActivity;

public enum HomePage {
    SCHEDULE,
    TRANSCRIPT,
    EMAIL,
    MY_COURSES,
    SEARCH_COURSES,
    WISHLIST,
    EBILL,
    CAMPUS_MAP,
    DESKTOP;

    public Class<?> getHomePageClass(){
        switch(this){
            case SCHEDULE:
                return ScheduleActivity.class;
            case TRANSCRIPT:
                return TranscriptActivity.class;
            case EMAIL:
                return InboxActivity.class;
            case MY_COURSES:
                return MyCoursesActivity.class;
            case SEARCH_COURSES:
                return RegistrationActivity.class;
            case WISHLIST:
                return CoursesListActivity.class;
            case EBILL:
                return EbillActivity.class;
            case CAMPUS_MAP:
                return MapActivity.class;
            case DESKTOP:
                return DesktopActivity.class;
            default:
                return ScheduleActivity.class;
        }
    }

    /**
     * Get the titles of all of the homepages
     * @param context The app context
     * @return A list of the homepage strings
     */
    public static List<String> getHomePageStrings(Context context){
        //NOTE : ORDER MATTERS HERE
        List<String> strings = new ArrayList<String>();
        strings.add(context.getResources().getString(R.string.homepage_schedule));
        strings.add(context.getResources().getString(R.string.homepage_transcript));
        strings.add(context.getResources().getString(R.string.homepage_email));
        strings.add(context.getResources().getString(R.string.homepage_mycourses));
        strings.add(context.getResources().getString(R.string.homepage_search));
        strings.add(context.getResources().getString(R.string.homepage_wishlist));
        strings.add(context.getResources().getString(R.string.homepage_ebill));
        strings.add(context.getResources().getString(R.string.homepage_map));
        strings.add(context.getResources().getString(R.string.homepage_desktop));

        return strings;
    }
}
