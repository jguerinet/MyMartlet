package ca.mcgill.mymcgill.object;

import java.io.Serializable;

/**
 * Created by Ryan Singzon on 15/02/14.
 */
public class Email implements Serializable{

    private String mSubject;
    private String mSender;
    private String mDate;

    public Email(String subject, String sender, String date){
        this.mSubject = subject;
        this.mSender = sender;
        this.mDate = date;
    }

    public String getSubject(){
        return mSubject;
    }

    public String getSender(){
        return mSender;
    }

    public String getDate(){
        return mDate;
    }

}
