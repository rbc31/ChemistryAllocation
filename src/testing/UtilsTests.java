package testing;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.MatchingUtils;
import utils.ToString;

/**
 * @author Rob
 *
 */
public class UtilsTests {

	/**
	 * Empty string list should map to empty string
	 */
	@Test
	public void test_empty_string_array_toString() {
		
		String [] input = new String[] {};
		
		assertEquals("", ToString.arrayToString(input));
	}
	
	/**
	 * basic string array test
	 */
	@Test
	public void test_string_array_toString() {
		
		String [] input = new String[] {"a","b","c","d"};
		
		assertEquals("\"a\", \"b\", \"c\", \"d\"", ToString.arrayToString(input));
		
		assertEquals("a+b+c+d", ToString.arrayToString(input,"+",false));
	}

	/**
	 * basic integer array test
	 */
	@Test
	public void test_integer_array_toString() {
		
		Integer [] input = new Integer[] {9,8,7,6,5,4,3,2,1,0};
		
		assertEquals("\"9\",\"8\",\"7\",\"6\",\"5\",\"4\",\"3\",\"2\",\"1\",\"0\"", ToString.arrayToString(input,",",true));
		
		assertEquals("9|8|7|6|5|4|3|2|1|0", ToString.arrayToString(input,"|",false));
	}
	
	/**
	 * basic empty string list remove duplicates test
	 */
	@Test
	public void test_remove_duplicates_empty_input() {
		String [] input = new String[] {};
		
		String [] output = MatchingUtils.removeDuplicates(input);
		
		assertArrayEquals(input,output);
	}
	
	/**
	 *duplicate string removal test when there are no dupliactes
	 */
	@Test
	public void test_remove_duplicates_no_duplicates_input() {
		String [] input = new String[] {"String #1","String #2", "String #3", "String #4", "String #5"};
		
		String [] output = MatchingUtils.removeDuplicates(input);
		
		assertArrayEquals(input,output);
	}
	
	
	/**
	 *duplicate string removal test when there is one dupliactes
	 */
	@Test
	public void test_remove_duplicates_one_duplicates_input() {
		String [] input = new String[] {"String #1","String #2", "String #3", "String #1", "String #5"};
		String [] expectedOutput = new String[] {"String #1","String #2", "String #3", "String #5"};
		 
		String [] output = MatchingUtils.removeDuplicates(input);
		
		assertArrayEquals(expectedOutput,output);
	}
	
	/**
	 *duplicate string removal test when there is one string with 10 copies 
	 */
	@Test
	public void test_remove_duplicates_all_duplicates_input() {
		String [] input = new String[] {"String #1","String #1", "String #1", "String #1", "String #1", "String #1","String #1", "String #1", "String #1", "String #1"};
		String [] expectedOutput = new String[] {"String #1"};
		 
		String [] output = MatchingUtils.removeDuplicates(input);
		
		assertArrayEquals(expectedOutput,output);
	}
	
	/**
	 *duplicate string removal test when there is 3 repeated strings
	 */
	@Test
	public void test_remove_duplicates_multiple_type_duplicates_input() {
		String [] input = new String[] {"String #1","String #2", "String #1", "String #2", "String #3", "String #3","String #2", "String #3", "String #1", "String #3"};
		String [] expectedOutput = new String[] {"String #1","String #2", "String #3"};
		 
		String [] output = MatchingUtils.removeDuplicates(input);
		
		assertArrayEquals(expectedOutput,output);
	}
	
	/**
	 *Test different values in the suffix function
	 */
	@Test
	public void test_suffix_range_of_values() {
		assertEquals("st",ToString.getSuffix(1));
		assertEquals("nd",ToString.getSuffix(2));
		assertEquals("rd",ToString.getSuffix(3));
		assertEquals("th",ToString.getSuffix(4));
		assertEquals("th",ToString.getSuffix(5));
		assertEquals("th",ToString.getSuffix(6));
		assertEquals("th",ToString.getSuffix(7));
		assertEquals("th",ToString.getSuffix(8));
		assertEquals("th",ToString.getSuffix(9));
		assertEquals("th",ToString.getSuffix(10));
		assertEquals("th",ToString.getSuffix(11));
		assertEquals("th",ToString.getSuffix(12));
		assertEquals("th",ToString.getSuffix(13));
		assertEquals("th",ToString.getSuffix(14));
		assertEquals("th",ToString.getSuffix(15));
		assertEquals("th",ToString.getSuffix(16));
		assertEquals("th",ToString.getSuffix(17));
		assertEquals("th",ToString.getSuffix(18));
		assertEquals("th",ToString.getSuffix(19));
		assertEquals("th",ToString.getSuffix(20));
		assertEquals("st",ToString.getSuffix(21));
		assertEquals("nd",ToString.getSuffix(22));
		assertEquals("rd",ToString.getSuffix(23));
		assertEquals("rd",ToString.getSuffix(23));
		assertEquals("th",ToString.getSuffix(100));
		assertEquals("st",ToString.getSuffix(101));
		assertEquals("nd",ToString.getSuffix(102));
		assertEquals("rd",ToString.getSuffix(103));
		
		try {
			ToString.getSuffix(0);
			fail("Expected error");
		}catch (IllegalArgumentException e) {
			//pass
		}
		
		try {
			ToString.getSuffix(-1);
			fail("Expected error");
		}catch (IllegalArgumentException e) {
			//pass
		}
		
		try {
			ToString.getSuffix(-100);
			fail("Expected error");
		}catch (IllegalArgumentException e) {
			//pass
		}
	}
}
