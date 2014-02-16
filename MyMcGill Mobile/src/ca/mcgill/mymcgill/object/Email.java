package ca.mcgill.mymcgill.object;

import java.io.Serializable;

/**
 * Created by Ryan Singzon on 15/02/14.
 */
public class Email implements Serializable{

    private String mSubject;
    private String mSender;
    private String mDate;
    private String mBody;

    public Email(String subject, String sender, String date, String body){
        this.mSubject = subject;
        this.mSender = sender;
        this.mDate = date;
        this.mBody = body;
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

    public String getBody() {
        return mBody;
    }

}
