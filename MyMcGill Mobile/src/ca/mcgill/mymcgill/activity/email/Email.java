package ca.mcgill.mymcgill.activity.email;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/*
 * Data model and basic implementation with smtp working with gmail for the moment.
 * Password is stored as a string. Security to be discussed given the public implementation
 * of this class.
 */

/**
 * 
 * @author Omar
 *
 */

public class Email {
	private final static String port = "587";
	private final static String host = "smtp.gmail.com";
	
	private final static boolean debug = true;
	
	public String	from,
					password,
					subject,
					message;
	
	public List<String>	to,
						cc,
						bcc;
	
	public Email(){
		to = new ArrayList<String>();
		cc = new ArrayList<String>();
		bcc = new ArrayList<String>();
	}
	
	/**
	 * Sends a simple email. I.e. no attachment, only subject and messgae sent to the recipients
	 * that were specified. No validation of the values is done, so method will not succeed if
	 * email object has not been filled properly.
	 */
	public void send(){
		Properties props = setProperties();
		Authenticator auth = setAuthenticator();
		
		Session session = Session.getInstance(props, auth);
		session.setDebug(debug);
		
		MimeMessage message = new MimeMessage(session);
		
		try{
			message.setFrom(new InternetAddress(this.from));
		
			for(String s: to){
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(s));
			}
			for(String s: cc){
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(s));
			}
			for(String s: bcc){
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(s));
			}

			message.setSubject(this.subject);
			message.setText(this.message);
			
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
}
