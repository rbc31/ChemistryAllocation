package exceptions;

/**
 * Describes an error where a supervisor was not found either by index or name 
 * @author Rob
 *
 */
public class SupervisorNotFoundException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new SupervisorNotFoundException.
	 * @param supervisor - The name of the supervisor that was not found
	 */
	public SupervisorNotFoundException(String supervisor) {
		super("Supervisor "+ supervisor + " was not found.");
	}
	
	/**
	 * Constructs a new SupervisorNotFoundException.
	 * @param index - The index of the supervisor that was not found
	 */
	public SupervisorNotFoundException(int index) {
		super("Supervisor at index" + index + "was not found.");
	}
}
