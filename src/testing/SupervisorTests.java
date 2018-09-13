package testing;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import org.junit.Test;

import Exceptions.ConfigNotValidException;
import dataManager.Config;
import dataManager.Supervisors;
import exceptions.IllegalFormatException;
import exceptions.InvalidTableFormatException;
import exceptions.SupervisorNotFoundException;
import main.Table;

/**
 * Unit tests for supervisors object
 * @author Rob
 *
 */
public class SupervisorTests {

	/**
	 * Basic test that when loading supervisors spreadsheet data recieved from supervisors object is correct
	 * @throws Exception if test failure
	 */
	@Test
	public void test_valid_supervisors()throws Exception {
		
		Supervisors s = new Supervisors("test_files/supervisor_files/valid1.csv", 0,
				1,2, new int[] {3,4,5,6,7});

		assertEquals(5,s.getNumberOfKeywords());
		assertEquals(6,s.size());
		
		assertEquals("supervisor_1",s.getSupervisorName(0));
		assertEquals("supervisor_2",s.getSupervisorName(1));
		assertEquals("supervisor_3",s.getSupervisorName(2));
		assertEquals("supervisor_4",s.getSupervisorName(3));
		assertEquals("supervisor_5",s.getSupervisorName(4));
		assertEquals("supervisor_6",s.getSupervisorName(5));
		
		try {
			s.getSupervisorName(-1);
			fail("Expected supervisor");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getSupervisorName(Integer.MAX_VALUE);
			fail("Expected supervisor");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getSupervisorName(6);
			fail("Expected supervisor");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		assertEquals(0,s.getIndex("supervisor_1"));
		assertEquals(1,s.getIndex("supervisor_2"));
		assertEquals(2,s.getIndex("supervisor_3"));
		assertEquals(3,s.getIndex("supervisor_4"));
		assertEquals(4,s.getIndex("supervisor_5"));
		assertEquals(5,s.getIndex("supervisor_6"));
		
		try {
			s.getIndex(null);
			fail("Expected supervisor");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getIndex("");
			fail("Expected supervisor");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getIndex("not a real supervisor");
			fail("Expected supervisor");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		assertEquals(6, s.getSupervisorCapcity("supervisor_1"));
		assertEquals(5, s.getSupervisorCapcity("supervisor_2"));
		assertEquals(4, s.getSupervisorCapcity("supervisor_3"));
		assertEquals(3, s.getSupervisorCapcity("supervisor_4"));
		assertEquals(2, s.getSupervisorCapcity("supervisor_5"));
		assertEquals(1, s.getSupervisorCapcity("supervisor_6"));
		
		assertEquals(6, s.getSupervisorCapcity(0));
		assertEquals(5, s.getSupervisorCapcity(1));
		assertEquals(4, s.getSupervisorCapcity(2));
		assertEquals(3, s.getSupervisorCapcity(3));
		assertEquals(2, s.getSupervisorCapcity(4));
		assertEquals(1, s.getSupervisorCapcity(5));
		
		try {
			s.getSupervisorCapcity(-1);
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getSupervisorCapcity(6);
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getSupervisorCapcity(Integer.MAX_VALUE);
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getSupervisorCapcity(null);
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getSupervisorCapcity("");
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getSupervisorCapcity("hello");
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		
		assertEquals("organic", 		s.getSupervisorTopic("supervisor_1"));
		assertEquals("organic", 		s.getSupervisorTopic("supervisor_2"));
		assertEquals("inorganic", 		s.getSupervisorTopic("supervisor_3"));
		assertEquals("inorganic", 		s.getSupervisorTopic("supervisor_4"));
		assertEquals("physical", 		s.getSupervisorTopic("supervisor_5"));
		assertEquals("computational", 	s.getSupervisorTopic("supervisor_6"));
		
		assertEquals("organic", 		s.getSupervisorTopic(0));
		assertEquals("organic", 		s.getSupervisorTopic(1));
		assertEquals("inorganic", 		s.getSupervisorTopic(2));
		assertEquals("inorganic", 		s.getSupervisorTopic(3));
		assertEquals("physical", 		s.getSupervisorTopic(4));
		assertEquals("computational", 	s.getSupervisorTopic(5));
		
		try {
			s.getSupervisorTopic(-1);
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getSupervisorTopic(6);
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getSupervisorTopic(Integer.MAX_VALUE);
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getSupervisorTopic(null);
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getSupervisorTopic("");
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getSupervisorTopic("hello");
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		String[] expected1 = new String[] {"keyword_1", "keyword_2", "keyword_3", "keyword_4", "keyword_5"};
		String[] expected2 = new String[] {"keyword_5", "keyword_1", "keyword_2", "keyword_3", "keyword_4"};
		String[] expected3 = new String[] {"keyword_4", "keyword_5", "keyword_1", "keyword_2", "keyword_3"};

		assertArrayEquals(expected1, s.getKeywords("supervisor_1"));
		assertArrayEquals(expected2, s.getKeywords("supervisor_2"));
		assertArrayEquals(expected3, s.getKeywords("supervisor_3"));
		assertArrayEquals(expected1, s.getKeywords("supervisor_6"));
		assertArrayEquals(expected1, s.getKeywords(0));
		assertArrayEquals(expected2, s.getKeywords(1));
		assertArrayEquals(expected3, s.getKeywords(2));
		assertArrayEquals(expected1, s.getKeywords(5));
	
		try {
			s.getKeywords(-1);
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getKeywords(6);
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getKeywords(Integer.MAX_VALUE);
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getKeywords(null);
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getKeywords("");
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
		try {
			s.getKeywords("hello");
			fail("Expected exception");
		}catch (SupervisorNotFoundException e) {
			//pass
		}
		
	}
	
	/**
	 * test supervisors object when columns are not valid
	 * @throws Exception if test failure
	 */
	@Test
	public void test_invalid_config() throws Exception {
		
		try {
			new Supervisors("test_files/supervisor_files/valid1.csv", -1,
					1,2, new int[] {3,4,5,6,7});
			fail("Expected exception");
		}catch (IllegalFormatException e) {
			//pass
		}
		
		try {
			new Supervisors("test_files/supervisor_files/valid1.csv", 8,
					1,2, new int[] {3,4,5,6,7});
			fail("Expected exception");
		}catch (IllegalFormatException e) {
			//pass
		}
		
		try {
			new Supervisors("test_files/supervisor_files/valid1.csv", 0,
					-1,2, new int[] {3,4,5,6,7});
			fail("Expected exception");
		}catch (IllegalFormatException e) {
			//pass
		}
		
		try {
			new Supervisors("test_files/supervisor_files/valid1.csv", 0,
					8,2, new int[] {3,4,5,6,7});
			fail("Expected exception");
		}catch (IllegalFormatException e) {
			//pass
		}
		
		try {
			new Supervisors("test_files/supervisor_files/valid1.csv", 0,
					1,-2, new int[] {3,4,5,6,7});
			fail("Expected exception");
		}catch (IllegalFormatException e) {
			//pass
		}
		
		try {
			new Supervisors("test_files/supervisor_files/valid1.csv", 0,
					1,9, new int[] {3,4,5,6,7});
			fail("Expected exception");
		}catch (IllegalFormatException e) {
			//pass
		}
		
		try {
			new Supervisors("test_files/supervisor_files/valid1.csv", 0,
					1,2, new int[] {});
			
		}catch (IllegalFormatException e) {
			fail("Did not expect exception");
		}
		
	}
	
	/**
	 * Test that IllegalFormatException is thrown when the supervisor sheet contains a duplicate username
	 * @throws Exception if test failure
	 */
	@Test
	public void test_duplicate_name() throws Exception {
		
		try {
			new Supervisors("test_files/supervisor_files/duplicate_name.csv", 0,
					1,2, new int[] {3,4,5,6,7});
			fail("Expected exception");
		}catch (IllegalFormatException e) {
			//pass
		}
	}
	
	/**
	 * Test that FileNotFoundException is thrown when the config file does not exist
	 * @throws Exception if test failure
	 */
	@Test
	public void test_file_not_exists() throws Exception {
		
		try {
			new Supervisors("does_not_exist.csv", 0,
					1,2, new int[] {3,4,5,6,7});
			fail("Expected exception");
		}catch (FileNotFoundException e) {
			//pass
		}
	}
	
	/**
	 * Test that InvalidTableFormatException is thrown when the 
	 * table file is not valid
	 * @throws Exception if test failure
	 */
	@Test
	public void test_file_corrupt() throws Exception {
		
		try {
			new Supervisors("test_files/supervisor_files/corrupt.csv", 0,
					1,2, new int[] {3,4,5,6,7});
			fail("Expected exception");
		}catch (InvalidTableFormatException e) {
			//pass
		}
	}
	
	/**
	 * Test IllegalFormatException is thrown when a supervisor has a non integer config
	 * @throws Exception if test failure
	 */
	@Test
	public void test_file_capacity_invalid() throws Exception {
		
		try {
			new Supervisors("test_files/supervisor_files/invalid_capacity.csv", 0,
					1,2, new int[] {3,4,5,6,7});
			fail("Expected exception");
		}catch (IllegalFormatException e) {
			//pass
		}
	}

	/**
	 * Test that getData returns an exact copy of the underlying table object
	 * @throws Exception if test failure
	 */
	@Test
	public void test_copy_data() throws Exception {
		
		Supervisors s = new Supervisors("test_files/supervisor_files/valid1.csv", 0,
				1,2, new int[] {3,4,5,6,7});
		
		Table expected = Table.parseTableFromCSVFile(new File("test_files/supervisor_files/valid1.csv"));
		
		Table actual = s.getData();
		

		assertArrayEquals(expected.getHeaders(), actual.getHeaders());
		
		assertEquals(expected.size(), actual.size());
		assertEquals(expected.getColCount(), actual.getColCount());
		
		for (int i=0;i<expected.getColCount();i++) {
			assertEquals(expected.getColumn(i),actual.getColumn(i));
		}
	}
	
	/**
	 * Test that when config file doesn't exist constructing supervisors object fails wiht filenotfound error
     * @throws Exception - Thrown if failure
	 */
	@Test
	public void test_supervisors_when_config_file_does_not_exist() throws Exception {
		Config.configFile = "does_not_exist.json";
		Config.clearConfig();
		
		Supervisors.clearInstance();
		try {
			Supervisors.getInstance();
			fail("Expected FileNotFoundException");
		}catch (FileNotFoundException e) {
			//pass
		}
	}
	
	/**
	 * Test that when config file is invalid json supervisor object construction throws ConfigNotValid error
     * @throws Exception - Thrown if failure
	 */
	@Test
	public void test_supervisors_when_config_file_is_not_valid_json() throws Exception {
		Config.configFile = "test_files/config_files/corrupt.json";
		Config.clearConfig();
		
		Supervisors.clearInstance();
		try {
			Supervisors.getInstance();
			fail("Expected ConfigNotValidException");
		}catch (ConfigNotValidException e) {
			//pass
		}
	}
	
	
	/**
	 * Test that when config file is invalid that students object construction throws ConfigNotValidException
	 * @throws Exception - Thrown if failure
	 */
	@Test
	public void test_supervisors_when_config_file_is_not_valid() throws Exception {
		Config.configFile = "test_files/config_files/invalid18.json";
		Config.clearConfig();
		
		Supervisors.clearInstance();
		try {
			Supervisors.getInstance();
			fail("Expected ConfigNotValidException");
		}catch (ConfigNotValidException e) {
			//pass
		}
	}
	
	/**
	 * Test that when config file is valid that supervisors object can be constructed correctly
	 * @throws Exception - Thrown if failure
	 */
	@Test
	public void test_supervisors_when_config_file_is_valid() throws Exception {
		Config.configFile = "test_files/config_files/simpleValid1.json";
		Config.clearConfig();
		Supervisors.clearInstance();
		Supervisors.getInstance();
		Supervisors.getInstance();
	}
	
}
