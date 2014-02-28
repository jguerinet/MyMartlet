package ca.mcgill.mymcgill.object;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;
import javax.mail.search.FlagTerm;

import ca.mcgill.mymcgill.util.Constants;
import org.apache.commons.io.IOUtils;

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


    //Fetches the user's emails from their McGill email account
    public void retrieveEmail(){

        //Set properties for McGill email server
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

        //Open a connection to the McGill server and fetch emails
        try {
            store = session.getStore(Constants.MAIL_PROTOCOL);
            store.connect();
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Message messages[] = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));

            mNumNewEmails = messages.length;

            //Add each message to the inbox object
            for (int i = 0; i < messages.length; i++) {

                Message message = messages[i];
                Address[] from = message.getFrom();
                String body = "";

                boolean emailExists = false;

                body = getMessageBody(message);

                //Check to see if the email already exists in the inbox
                for(Email email : mEmails){
                    if(email.getDate().equals(message.getSentDate().toString()) && email.getSubject().equals(message.getSubject())){
                        emailExists = true;
                    }
                }

                //If the email does not exist, add it to the inbox
                if(!emailExists){
                    Email newEmail = new Email(message.getSubject(), from[0].toString(), message.getSentDate().toString(), body, false);
                    mEmails.add(newEmail);
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
    public String getMessageBody(Message message) {
        String body = "";
        try {
            Object content = message.getContent();

            //Check if the message content is a String
            if (content instanceof String) {
                body += content;
            }

            //If it is not, check if it is a Multipart
            else if (content instanceof Multipart) {
                Multipart multiPart = (Multipart) content;
                body += processMultiPart(multiPart);
            }

            else if (content instanceof InputStream) {
                InputStream inStream = (InputStream) content;

                StringWriter writer = new StringWriter();
                IOUtils.copy(inStream, writer, "UTF-8");
                body += writer.toString();

                /*int ch;
                while ((ch = inStream.read()) != -1) {
                    body += ch;
                }*/

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return body;
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
        mNumNewEmails--;
    }

}
