package ca.appvelopers.mcgillmobile.object;

import android.content.Context;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.DesktopActivity;
import ca.appvelopers.mcgillmobile.activity.MapActivity;
import ca.appvelopers.mcgillmobile.activity.MyCoursesActivity;
import ca.appvelopers.mcgillmobile.activity.RegistrationActivity;
import ca.appvelopers.mcgillmobile.activity.ScheduleActivity;
import ca.appvelopers.mcgillmobile.activity.courseslist.CoursesListActivity;
import ca.appvelopers.mcgillmobile.activity.ebill.EbillActivity;
import ca.appvelopers.mcgillmobile.activity.transcript.TranscriptActivity;

public enum HomePage {
    SCHEDULE,
    TRANSCRIPT,
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

    public String toString(Context context){
        switch(this){
            case SCHEDULE:
                return context.getResources().getString(R.string.homepage_schedule);
            case TRANSCRIPT:
                return context.getResources().getString(R.string.homepage_transcript);
            case MY_COURSES:
                return context.getResources().getString(R.string.homepage_mycourses);
            case SEARCH_COURSES:
                return context.getResources().getString(R.string.homepage_search);
            case WISHLIST:
                return context.getResources().getString(R.string.homepage_wishlist);
            case EBILL:
                return context.getResources().getString(R.string.homepage_ebill);
            case CAMPUS_MAP:
                return context.getResources().getString(R.string.homepage_map);
            case DESKTOP:
                return context.getResources().getString(R.string.homepage_desktop);
            default:
                return context.getResources().getString(R.string.homepage_schedule);
        }
    }
}
