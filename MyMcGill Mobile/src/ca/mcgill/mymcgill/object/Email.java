package ca.mcgill.mymcgill.object;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ca.mcgill.mymcgill.activity.inbox.InboxActivity;
import ca.mcgill.mymcgill.activity.inbox.ReplyActivity;
import ca.mcgill.mymcgill.util.Constants;
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

        this.password = "";
        this.from = "joshua.alfaro@mail.mcgill.ca";
    }

	/**
	 * Sends a simple email. I.e. no attachment, only subject and messgae sent to the recipients
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
			message.setFrom(new InternetAddress("joshua.alfaro@mail.mcgill.ca"));
		
//			for(String s: to){
//				message.addRecipient(Message.RecipientType.TO, new InternetAddress(s));
//			}
//			for(String s: cc){
//				message.addRecipient(Message.RecipientType.CC, new InternetAddress(s));
//			}
//			for(String s: bcc){
//				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(s));
//			}

			message.addRecipient(Message.RecipientType.TO, new InternetAddress("ryan.singzon@mail.mcgill.ca"));
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
    		s = s + to.get(i) +";";
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
    
    public void read() {
    	isRead = true;
    }

}
