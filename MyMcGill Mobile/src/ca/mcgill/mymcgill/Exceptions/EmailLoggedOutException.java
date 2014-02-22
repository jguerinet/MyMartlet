package ca.mcgill.mymcgill.Exceptions;

public class EmailLoggedOutException extends LoggedOutException{
	public EmailLoggedOutException() {
		super("User is logged out of Email.");
	}
}
