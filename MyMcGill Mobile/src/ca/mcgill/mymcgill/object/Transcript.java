package ca.mcgill.mymcgill.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan Singzon on 30/01/14.
 *
 * This class will retrieve and parse transcript data from Minerva and hold all the information
 * that is shown on the transcript
 *
 */
public class Transcript implements Serializable{

    private int cgpa;
    private int totalCredits;
    private String transcriptString;
    private List<Semester> semesters = new ArrayList<Semester>();

    //Constructor for the Transcript object
    public Transcript(){

        //Retrieve transcript data from Minerva as a string
        transcriptString = getTranscript();
        parseTranscript(transcriptString);
    }

    //Logs into Minerva and returns the user's transcript as a string
    private String getTranscript(){
        String transcript = "";
        return transcript;
    }

    //Create an array of semesters and set CGPA and total credits
    private void parseTranscript(String transcriptString){
        //TODO Parse transcript and obtain array of semesters and CGPA and total credits
    }

    //Getter for CGPA
    public int getCgpa(){
        return cgpa;
    }

    //Getter for totalCredits
    public int getTotalCredits(){
        return totalCredits;
    }

    //Getter for semesters
    public List<Semester> getSemesters(){
        return semesters;
    }
}
