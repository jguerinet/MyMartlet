package ca.mcgill.mymcgill.object;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

import ca.mcgill.mymcgill.util.Constants;

/**
 * Created by Ryan Singzon on 15/02/14.
 */
public class Inbox implements Serializable{

    Properties mProperties = null;
    private String mUserName;
    private String mPassword;

    private List<Email> mEmails = new ArrayList<Email>();
    private int mNumNewEmails = 0;
    private int numEmails;
    private int emailsToRetrieve = 10;

    public Inbox(String username, String password){
        this.mUserName = username;
        this.mPassword = password;
        retrieveEmail();
    }

    //Fetches the user's emails from their McGill email account
    public void retrieveEmail(){

        //Set properties for McGill email server
        mProperties = new Properties();
        mProperties.setProperty("mail.host", Constants.MAIL_HOST);
        mProperties.setProperty("mail.port", Constants.MAIL_PORT);
        mProperties.setProperty("mail.transport.protocol", Constants.MAIL_PROTOCOL);
        Session session = Session.getInstance(mProperties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mUserName, mPassword);
                    }
                });

        //Open a connection to the McGill server and fetch emails
        try {
            Store store = session.getStore(Constants.MAIL_PROTOCOL);
            store.connect();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            //Get total number of emails
            numEmails = inbox.getMessageCount();

            //Retrieve the number of emails the user specifies (default 10)
            Message messages[] = inbox.getMessages(numEmails - emailsToRetrieve, numEmails);

            //Add each message to the inbox object
            for (int i = messages.length-1; i > 0; i--) {

                Message message = messages[i];

                //Get email senders
                List<String> from = new ArrayList<String>();
                for(Address address : message.getFrom()){
                    from.add(address.toString());
                }

                String body = "";
                try{
                    body = getText(message);
                }catch(Exception e){}


                boolean emailExists = false;

                //Check to see if the email already exists in the inbox
                for(Email email : mEmails){
                    if(email.getDate().equals(message.getSentDate().toString()) && email.getSubject().equals(message.getSubject())){
                        emailExists = true;
                    }
                }

                //If the email does not exist, add it to the inbox
                if(!emailExists){
                    Email newEmail = new Email(message.getSubject(), from, message.getSentDate().toString(), body, message.isSet(Flag.SEEN));
                    mEmails.add(newEmail);

                    //Increment the unread message count if unread
                    if(!message.isSet(Flag.SEEN)){
                        mNumNewEmails++;
                    }
                }
            }

            inbox.close(true);
            store.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    //Gets the message body from the Message object
    public String getText(Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }

    public String processMultiPart(Multipart content) {
        String processedMultiPart = "";
        try {
            int multiPartCount = content.getCount();
            for (int i = 0; i < multiPartCount; i++) {
                BodyPart bodyPart = content.getBodyPart(i);
                Object o;

                o = bodyPart.getContent();
                if (o instanceof String) {
                    processedMultiPart += o;
                } else if (o instanceof Multipart) {
                    processMultiPart((Multipart) o);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return processedMultiPart;
    }

    public List<Email> getEmails(){
        return mEmails;
    }

    public int getNumNewEmails(){
        return mNumNewEmails;
    }
    
    // JDA
    public void decrementNumNewEmails(){
        //mNumNewEmails--;
    }

}
