package exceptions;
/**
 * Custom class that signifies an unexpected error
 * 
 * @author Robert Cobb <br>
 * Bath University<br>
 * Email: rbc31@bath.ac.uk
 */
public class UnexpectedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Throws a new UnexpectedException Exception
	 */
	public UnexpectedException() {
		super();
	}
	
	/**
	 * Throws a new UnexpectedException exception 
	 * with the given error message
	 * @param message - The error message
	 */
	public UnexpectedException(String message) {
		super(message);
	}

	/**
	 * Creates a new UnexpectedException based off of the given exception
	 * @param e - The exception to base the new exception off
	 */
	public UnexpectedException(Exception e) {
		super(e);
	}
}
