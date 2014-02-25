package ca.mcgill.mymcgill.object;

import ca.mcgill.mymcgill.activity.ScheduleActivity;
import ca.mcgill.mymcgill.activity.ebill.EbillActivity;
import ca.mcgill.mymcgill.activity.inbox.InboxActivity;
import ca.mcgill.mymcgill.activity.transcript.TranscriptActivity;

public enum HomePage {
    SCHEDULE,
    TRANSCRIPT,
    EMAIL,
    EBILL;

    public Class<?> getHomePageClass(){
        switch(this){
            case SCHEDULE:
                return ScheduleActivity.class;
            case TRANSCRIPT:
                return TranscriptActivity.class;
            case EMAIL:
                return InboxActivity.class;
            case EBILL:
                return EbillActivity.class;
            default:
                return ScheduleActivity.class;
        }
    }
}
