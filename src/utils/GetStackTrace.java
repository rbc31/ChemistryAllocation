package utils;

import java.io.PrintWriter;
import java.io.StringWriter;
/**
 * Utils to get the stack trace of an exception
 * 
 * @author Robert Cobb
 * University of Bath
 */
public class GetStackTrace {
	
	/**
	 * Returns the stack trace of the given exception
	 * @param e - The exception to get the stack trace of
	 * @return String detailing stack trace
	 */
	public static String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw  = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString(); 
	}
}
