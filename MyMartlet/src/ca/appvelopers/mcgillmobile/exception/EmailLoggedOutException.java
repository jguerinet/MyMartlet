package ca.appvelopers.mcgillmobile.exception;

public class EmailLoggedOutException extends LoggedOutException {
	public EmailLoggedOutException() {
		super("User is logged out of Email.");
	}
}
