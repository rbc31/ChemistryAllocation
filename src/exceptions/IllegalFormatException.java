package exceptions;
/**
 * Basic custom exception class<br>
 * Extends{@link Exception} <br>
 * 
 * @author Robert Cobb | rbc31
 * @see Exception
 *
 */
public class IllegalFormatException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor creates a new IllegalFormatException
	 * with the given detail message
	 * @param message - the detail message to give the exception.
	 */
	public IllegalFormatException(String message) {
		super(message);
	}

}
