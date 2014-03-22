package ca.mcgill.mymcgill.object;

import android.content.Context;

import java.io.Serializable;

import ca.mcgill.mymcgill.util.Connection;

/**
 * Author: Julien
 * Date: 2014-03-22 17:30
 */
public class ScheduleSemester implements Serializable{
    private Season mSeason;
    private int mYear;

    public ScheduleSemester(Season season, int year){
        this.mSeason = season;
        this.mYear = year;
    }

    public Season getSeason(){
        return this.mSeason;
    }

    public int getYear(){
        return this.mYear;
    }

    public String getURL(){
        return Connection.minervaSchedulePrefix + mYear + mSeason.getSeasonNumber();
    }

    public String toString(Context context){
        return mSeason.toString(context) + " " + mYear;
    }
}
