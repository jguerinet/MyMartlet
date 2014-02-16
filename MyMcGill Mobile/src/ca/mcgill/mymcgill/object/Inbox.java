package ca.mcgill.mymcgill.object;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ryan Singzon on 15/02/14.
 */
public class Inbox implements Serializable{

    private List<Email> mEmails;
    private int mNumNewEmails;

    public Inbox(List<Email> emails){
        this.mEmails = emails;
        mNumNewEmails = 0;
    }

    public List<Email> getEmails(){
        return mEmails;
    }

    public int getNumNewEmails(){
        return mNumNewEmails;
    }

}
