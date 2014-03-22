package ca.mcgill.mymcgill.object;

import java.io.Serializable;

/**
 * Author: Julien
 * Date: 2014-03-22 17:30
 */
public class ScheduleSemester implements Serializable{
    private String mSeason;
    private int mYear;

    public ScheduleSemester(String season, int year){
        this.mSeason = season;
        this.mYear = year;
    }

    public String getSeason(){
        return this.mSeason;
    }

    public int getYear(){
        return this.mYear;
    }
}
