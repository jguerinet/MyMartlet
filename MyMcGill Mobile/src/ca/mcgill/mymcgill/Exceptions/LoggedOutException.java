package ca.mcgill.mymcgill.Exceptions;

public class LoggedOutException extends Exception {
	
	public LoggedOutException() { 
		super("User is Logged out");
	}
	
	public LoggedOutException(String message) { 
		super(message); 
	}
	
	public LoggedOutException(String message, Throwable cause) { 
		super(message, cause); 
	}
	
	public LoggedOutException(Throwable cause) { 
		super(cause); 
	}
}
