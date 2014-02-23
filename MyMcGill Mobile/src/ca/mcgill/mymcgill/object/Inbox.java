package ca.mcgill.mymcgill.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;
import javax.mail.search.FlagTerm;

import ca.mcgill.mymcgill.util.Constants;

/**
 * Created by Ryan Singzon on 15/02/14.
 */
public class Inbox implements Serializable{

    Properties mProperties = null;
    private Session session = null;
    private Store store = null;
    private Folder inbox = null;
    private String mUserName;
    private String mPassword;

    private List<Email> mEmails = new ArrayList<Email>();
    private int mNumNewEmails;

    public Inbox(String username, String password){
        this.mUserName = username;
        this.mPassword = password;
        retrieveEmail();
    }

    public void retrieveEmail(){
        mProperties = new Properties();
        mProperties.setProperty("mail.host", Constants.MAIL_HOST);
        mProperties.setProperty("mail.port", Constants.MAIL_PORT);
        mProperties.setProperty("mail.transport.protocol", Constants.MAIL_PROTOCOL);
        session = Session.getInstance(mProperties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mUserName, mPassword);
                    }
                });
        try {
            store = session.getStore(Constants.MAIL_PROTOCOL);
            store.connect();
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Message messages[] = inbox.search(new FlagTerm(
                    new Flags(Flag.SEEN), false));
            ;

            mNumNewEmails = messages.length;

            for (int i = 0; i < messages.length; i++) {

                Message message = messages[i];
                Address[] from = message.getFrom();


                if(mEmails.isEmpty()){
                    Email newEmail = new Email(message.getSubject(), from[0].toString(), message.getSentDate().toString(), message.toString(), false);
                    mEmails.add(newEmail);
                }

                //Check list of emails to see if it already exists
                for(Email email : mEmails){
                    if(email.getDate().equals(message.getSentDate().toString()) && email.getSubject().equals(message.getSubject())){
                        //Do not add email
                    }
                    else{
                        Email newEmail = new Email(message.getSubject(), from[0].toString(), message.getSentDate().toString(), message.toString(), false);
                        mEmails.add(newEmail);
                    }
                }

                //processMessageBody(message);
            }
            inbox.close(true);
            store.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public List<Email> getEmails(){
        return mEmails;
    }

    public int getNumNewEmails(){
        return mNumNewEmails;
    }

}
