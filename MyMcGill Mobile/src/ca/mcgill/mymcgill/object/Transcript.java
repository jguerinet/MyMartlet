package ca.mcgill.mymcgill.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

/**
 * Created by Ryan Singzon on 30/01/14.
 *
 * This class will retrieve and parse transcript data from Minerva and hold all the information
 * that is shown on the transcript
 *
 */
public class Transcript implements Serializable{

    private double mCgpa;
    private int mTotalCredits;
    private String mScholarships;
    private String mRawTranscript;
    private Document transcript;
    private List<Semester> semesters = new ArrayList<Semester>();

    //Constructor for the Transcript object
    public Transcript(String rawTranscript){

        this.mRawTranscript = rawTranscript;
        parseTranscript(rawTranscript);
    }

    //Create an array of semesters and set CGPA and total credits
    private void parseTranscript(String transcriptString){

        //TODO: Parse transcript
/*
        transcript = Jsoup.parse(transcriptString);

        //Extract program, scholarships, total credits, and CGPA
        Element mainTable = transcript.getElementsByClass("dataentrytable").get(0);

        //Scholarships listed in 5th <tr> tag
        Elements rows = mainTable.getElementsByTag("tr");
        Element scholarships = rows.get(4);

        //Course information starts on the 7th <tr> tag
        //A row with nothing but &nbsp; indicates the end of a semester
*/



    }

    //Getter for CGPA
    public double getCgpa(){
        return mCgpa;
    }

    //Getter for totalCredits
    public int getTotalCredits(){
        return mTotalCredits;
    }

    //Getter for semesters
    public List<Semester> getSemesters(){
        return semesters;
    }

    public String getmScholarships(){
        return mScholarships;
    }

    public String getmRawTranscript(){
        return mRawTranscript;
    }



}
