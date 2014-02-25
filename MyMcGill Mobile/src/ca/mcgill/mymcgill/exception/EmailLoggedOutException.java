package ca.mcgill.mymcgill.exception;

public class EmailLoggedOutException extends LoggedOutException{
	public EmailLoggedOutException() {
		super("User is logged out of Email.");
	}
}
