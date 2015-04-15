package ca.appvelopers.mcgillmobile.exception;

public class MinervaLoggedOutException extends LoggedOutException {

	public MinervaLoggedOutException() { 
		super("User is Logged out from Minerva");
	}
	
	public MinervaLoggedOutException(String message) { 
		super(message); 
	}
	
	public MinervaLoggedOutException(String message, Throwable cause) { 
		super(message, cause); 
	}
	
	public MinervaLoggedOutException(Throwable cause) { 
		super(cause); 
	}
	
}
