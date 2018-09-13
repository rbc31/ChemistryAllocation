package exceptions;

/**
 * Describes an error where a student was not found either by index or username 
 * @author Rob
 *
 */
public class StudentNotFoundException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new StudentNotFoundException.
	 * @param student - The name of the student that was not found
	 */
	public StudentNotFoundException(String student) {
		super("Student "+ student + " was not found.");
	}
	
	/**
	 * Constructs a new StudentNotFoundException.
	 * @param index - The index of the student that was not found
	 */
	public StudentNotFoundException(int index) {
		super("Student at index" + index + "was not found.");
	}
}
