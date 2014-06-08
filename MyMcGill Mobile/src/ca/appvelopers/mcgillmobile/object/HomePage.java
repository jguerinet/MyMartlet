package ca.appvelopers.mcgillmobile.object;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.DesktopActivity;
import ca.appvelopers.mcgillmobile.activity.MapActivity;
import ca.appvelopers.mcgillmobile.activity.MyCoursesActivity;
import ca.appvelopers.mcgillmobile.activity.RegistrationActivity;
import ca.appvelopers.mcgillmobile.activity.ScheduleActivity;
import ca.appvelopers.mcgillmobile.activity.courseslist.CoursesListActivity;
import ca.appvelopers.mcgillmobile.activity.ebill.EbillActivity;
import ca.appvelopers.mcgillmobile.activity.inbox.InboxActivity;
import ca.appvelopers.mcgillmobile.activity.transcript.TranscriptActivity;

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
