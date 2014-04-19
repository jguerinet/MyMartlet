package ca.mcgill.mymcgill.object;

import ca.mcgill.mymcgill.activity.DesktopActivity;
import ca.mcgill.mymcgill.activity.MapActivity;
import ca.mcgill.mymcgill.activity.MyCoursesActivity;
import ca.mcgill.mymcgill.activity.ScheduleActivity;
import ca.mcgill.mymcgill.activity.ebill.EbillActivity;
import ca.mcgill.mymcgill.activity.inbox.InboxActivity;
import ca.mcgill.mymcgill.activity.transcript.TranscriptActivity;

public enum HomePage {
    SCHEDULE,
    TRANSCRIPT,
    EMAIL,
    MY_COURSES,
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
}
