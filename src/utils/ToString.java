package utils;
/**
 * Utils to convert lists to string
 * @author Robert Cobb
 * University of Bath
 */
public class ToString {

	
	/**
	 * Returns a string representing the given array
	 * @param array - The array to convert to string
	 * @param delimeter - The delimiter to use in creating the string representation
	 * @param quote - If true, the individual elements will be wrapped in quotes, otherwise not
	 * @return the string representation of the array
	 */
	public static String arrayToString(Object [] array, String delimeter, boolean quote) {
		StringBuilder toReturn = new StringBuilder();
		
		String quoteStr = "";
		if (quote) {
			quoteStr = "\"";
		}
		
		for (int i=0;i<array.length-1;i++) {
			toReturn.append(quoteStr + array[i].toString() + quoteStr + delimeter);
		}
		
		if (array.length > 0) {
			toReturn.append(quoteStr + array[array.length-1].toString() + quoteStr);
		}
		
		return toReturn.toString();
	}
	
	/**
	 * Returns a string representing the given array. Each individual 
	 * element is escaped with quotes and delimited with a comma and space
	 * @param array - The array to convert to string
	 * @return the string representation of the array
	 */
	public static String arrayToString(Object [] array) {
		return arrayToString(array, ", ", true); 
	}
	
	/**
	 * Gets the suffix of the given index
	 * e.g. 1 -> st
	 * 		2 -> nd
	 * 		3 -> rd
	 * etc.
	 * @param index - The number to get the suffix of
	 * @return The suffix of the given integer
	 * @throws IllegalArgumentException - Thrown if the index <= 0
	 */
	public static String getSuffix (int index) throws IllegalArgumentException{
		if (index <= 0) {
			throw new IllegalArgumentException("Input to getSuffix function must be a positive integer");
		}
		
		if (index % 10 == 1 && index != 11) { // 1st, 21st, 31st, 41st 101st, 1001st etc.
			return "st";
		}
		
		if (index % 10 == 2 && index != 12) { // 2nd, 22nd, 32nd, 42nd, 102nd,
			return "nd";
		}
		
		if (index % 10 == 3 && index != 13) { // 3rd, 23rd, 33rd, 43rd, 103rd, 1003rd
			return "rd";
		}
		
		return "th";// 4th, 5th, 6th, 7th, 8th, 9th, 10th, 11th, 12th, 13th ...
		
	}
}
