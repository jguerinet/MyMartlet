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
import android.util.Log;
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

    public void markAsRead(Context context) {
    	password = Load.loadPassword(context);
        from = Load.loadFullUsername(context);
    	
    	///Set properties for McGill email server
        Properties mProperties = new Properties();
        mProperties.setProperty("mail.host", Constants.MAIL_HOST);
        mProperties.setProperty("mail.port", Constants.MAIL_PORT);
        mProperties.setProperty("mail.transport.protocol", Constants.MAIL_PROTOCOL);
        Session session = Session.getInstance(mProperties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });

        //Open a connection to the McGill server and fetch emails
        try {
            Store store = session.getStore(Constants.MAIL_PROTOCOL);
            store.connect();
			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_WRITE);
			int count = inbox.getMessageCount();
			Log.e("Count", "" + count);
			for (int i = count-1; i > count-10; i--) {
				inbox.getMessage(i).setFlag(Flags.Flag.SEEN, true);
				Log.e("Count", "" + count);
			}
			// folder.getMessage(count-1).getContent(); // number is incorrect
			// folder.getMessage(count-1).setFlag(Flags.Flag.SEEN, false);
			inbox.close(true);
			store.close();
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
