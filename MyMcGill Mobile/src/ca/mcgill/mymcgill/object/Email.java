package ca.mcgill.mymcgill.object;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.content.Context;
import ca.mcgill.mymcgill.util.Load;

/**
 * Created by Ryan Singzon on 15/02/14.
 */
public class Email implements Serializable{
	
    private String mSubject;
    private String mSender;
    private String mDate;
    private String mBody;
    private boolean isRead;
    
    private final static String port = "587";
	private final static String host = "smtp.mcgill.ca";
	
	private final static boolean debug = true;
	
	public String	from,
					password,
					subject,
					message;
	
	public List<String>	to,
						cc,
						bcc;

    public Email(String subject, List<String> sender, String date, String body, boolean isRead){
        this.mSubject = subject;
        this.to = sender;
        this.mDate = date;
        this.mBody = body;
        this.isRead = isRead;

        // real info here
        this.password = "";
        this.from = "";
    }
    
    // Second Constructor for reply/send emails (to add context)
    public Email(String subject, List<String> sender, String body, Context context){
        this.mSubject = subject;
        this.to = sender;
        this.mBody = body;

        this.password = Load.loadPassword(context);
        this.from = Load.loadFullUsername(context);
    }

    public void markAsRead() {
    	isRead = true;
    	
    	Properties props = this.setProperties();
		Authenticator auth = this.setAuthenticator();
		
		Session session = Session.getInstance(props, auth);
		session.setDebug(debug);
		try {
			Store store = session.getStore("imaps");
			store.connect(host, 587, from, password);
			// Get folder
			Folder folder = store.getFolder("Inbox");
			if (folder != null && !folder.exists()) {
				folder.open(Folder.READ_WRITE);
				folder.getMessage(0).getContent();	// number is incorrect
				MimeMessage source = (MimeMessage) folder.getMessage(0);
				MimeMessage copy = new MimeMessage(source);
				folder.getMessage(0).setFlag(Flags.Flag.SEEN, true);
				folder.close(false);
				store.close();
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
	/**
	 * Sends a simple email. I.e. no attachment, only subject and message sent to the recipients
	 * that were specified. No validation of the values is done, so method will not succeed if
	 * email object has not been filled properly.
	 */
	public void send(){
		Properties props = this.setProperties();
		Authenticator auth = this.setAuthenticator();
		
		Session session = Session.getInstance(props, auth);
		session.setDebug(debug);
		
		MimeMessage message = new MimeMessage(session);
		
		try{
			message.setFrom(new InternetAddress(from));
		
			for(String s: to){
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(s));
			}
//			for(String s: cc){
//				message.addRecipient(Message.RecipientType.CC, new InternetAddress(s));
//			}
//			for(String s: bcc){
//				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(s));
//			}

			message.setSubject(mSubject);
			message.setText(mBody);
			
			Transport.send(message);
			
		}catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	private Properties setProperties(){
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.port", port);
		props.put("mail.smtp.starttls.enable", true);
		props.setProperty("mail.smtp.host", host);
		
		props.put("mail.smtp.auth", true);
		
		return props;
	}
	
	private Authenticator setAuthenticator(){
		final String user = this.from;
		final String pass = this.password;
		Authenticator auth = new Authenticator() {
			private PasswordAuthentication passAuth = new PasswordAuthentication(
					user, pass);
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
				return passAuth;
			}
		};
		
		return auth;
	}
    
    
    public String getSubject(){
        return mSubject;
    }

    public String getSender(){
    	String s = "";
    	for(int i = 0; i < to.size(); i++) {
            if(i > 0) s += ";";
    		s = s + to.get(i);
    	}
        return s;
    }
    
    public List<String> getSenderList(){
        return to;
    }

    public String getDate(){
        return mDate;
    }

    public String getBody() {
        return mBody;
    }

    public boolean isRead() {
        return isRead;
    }    
}
