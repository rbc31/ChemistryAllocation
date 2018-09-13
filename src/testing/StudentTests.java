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
import dataManager.Students;
import exceptions.IllegalFormatException;
import exceptions.InvalidTableFormatException;
import exceptions.StudentNotFoundException;
import main.Table;


/**
 * Class tests the students object
 * @author Rob
 */
public class StudentTests {
	
	
	/**
	 * Tests that student object returns correct data when quiered. based on underlying file
	 * @throws Exception - Thrown if test failure
	 */
	@Test
	public void test_valid_students() throws Exception {
		Students s = new Students("test_files/student_files/valid1.csv", 1, 0, 17, 
				new int [] {8,9,10,11,12} , new int [] {14,15,16}, new int [] {2,3,4,5,6,7}, 
				new String[] {"nat sci"});

		//assert the size is correct
		assertEquals(10, s.size());
		
		assertEquals(3, s.getNumberOfTopicChoices());
		assertEquals(5, s.getNumberOfKeywords());
		assertEquals(6, s.getNumOfPreferenceChoice());
		
		assertEquals(0, s.getIndex("username_1"));
		assertEquals(1, s.getIndex("username_2"));
		assertEquals(2, s.getIndex("username_3"));
		assertEquals(3, s.getIndex("username_4"));
		assertEquals(4, s.getIndex("username_5"));
		assertEquals(5, s.getIndex("username_6"));
		assertEquals(6, s.getIndex("username_7"));
		assertEquals(7, s.getIndex("username_8"));
		assertEquals(8, s.getIndex("username_9"));
		assertEquals(9, s.getIndex("username_10"));
		
		try {
			s.getIndex("student_10");
			fail("Student should not be found");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getIndex(null);
			fail("Student should not be found");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getIndex("");
			fail("Student should not be found");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		assertEquals("username_1", s.getUsername(0));
		assertEquals("username_2", s.getUsername(1));
		assertEquals("username_3", s.getUsername(2));
		assertEquals("username_4", s.getUsername(3));
		assertEquals("username_5", s.getUsername(4));
		assertEquals("username_6", s.getUsername(5));
		assertEquals("username_7", s.getUsername(6));
		assertEquals("username_8", s.getUsername(7));
		assertEquals("username_9", s.getUsername(8));
		assertEquals("username_10", s.getUsername(9));
		
		try {
			s.getUsername(-1);
			fail("Student should not be found");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getUsername(10);
			fail("Student should not be found");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getUsername(Integer.MIN_VALUE);
			fail("Student should not be found");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		s = Students.getInstance();
		
		assertEquals("student_1",s.getName(0));
		assertEquals("student_2",s.getName(1));
		assertEquals("student_3",s.getName(2));
		assertEquals("student_4",s.getName(3));
		assertEquals("student_5",s.getName(4));
		assertEquals("student_6",s.getName(5));
		assertEquals("student_7",s.getName(6));
		assertEquals("student_8",s.getName(7));
		assertEquals("student_9",s.getName(8));
		assertEquals("student_10",s.getName(9));
		
		assertEquals("student_1",s.getName("username_1"));
		assertEquals("student_2",s.getName("username_2"));
		assertEquals("student_3",s.getName("username_3"));
		assertEquals("student_4",s.getName("username_4"));
		assertEquals("student_5",s.getName("username_5"));
		assertEquals("student_6",s.getName("username_6"));
		assertEquals("student_7",s.getName("username_7"));
		assertEquals("student_8",s.getName("username_8"));
		assertEquals("student_9",s.getName("username_9"));
		assertEquals("student_10",s.getName("username_10"));
		
		try {
			s.getName(-1);
			fail("Student should not be found");
		}catch (StudentNotFoundException e) {
			//pass
		}
		try {
			s.getName(10);
			fail("Student should not be found");
		}catch (StudentNotFoundException e) {
			//pass
		}
		try {
			s.getName(Integer.MAX_VALUE);
			fail("Student should not be found");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getName("");
			fail("Student should not be found");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getName(null);
			fail("Student should not be found");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getName("not real");
			fail("Student should not be found");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		assertEquals("supervisor_1",s.getChoice("username_1", 0));
		assertEquals("supervisor_1",s.getChoice("username_2", 1));
		assertEquals("supervisor_1",s.getChoice("username_3", 2));
		assertEquals("supervisor_1",s.getChoice("username_4", 3));
		assertEquals("supervisor_1",s.getChoice("username_5", 4));
		assertEquals("supervisor_1",s.getChoice("username_6", 5));
		assertEquals("supervisor_1",s.getChoice("username_7", 0));
		assertEquals("supervisor_1",s.getChoice("username_8", 1));
		assertEquals("supervisor_1",s.getChoice("username_9", 2));
		assertEquals("supervisor_1",s.getChoice("username_10", 3));
		
		assertEquals("supervisor_2",s.getChoice(0, 1));
		assertEquals("supervisor_2",s.getChoice(1, 2));
		assertEquals("supervisor_2",s.getChoice(2, 3));
		assertEquals("supervisor_2",s.getChoice(3, 4));
		assertEquals("supervisor_2",s.getChoice(4, 5));
		assertEquals("supervisor_2",s.getChoice(5, 0));
		
		assertEquals("supervisor_3",s.getChoice(6, 2));
		assertEquals("supervisor_3",s.getChoice(7, 3));
		assertEquals("supervisor_3",s.getChoice(8, 4));
		assertEquals("supervisor_3",s.getChoice(9, 5));
		
		try {
			s.getChoice("", 5);
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getChoice(null, 5);
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getChoice("USERNAME_1", 5);
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getChoice("username_1", -1);
		}catch (IllegalArgumentException e) {
			//pass
		}
		
		try {
			s.getChoice("username_1", Integer.MAX_VALUE);
		}catch (IllegalArgumentException e) {
			//pass
		}
		
		try {
			s.getChoice("username_1", 6);
		}catch (IllegalArgumentException e) {
			//pass
		}
		
		try {
			s.getChoice(-1, 0);
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getChoice(Integer.MAX_VALUE, 0);
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getChoice(10, 0);
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		String [] expected1 = new String[] {"organic", "physical", "inorganic"};
		String [] expected2 = new String[] {"compuational", "organic","physical"};

		assertArrayEquals(expected1, s.getTopicAreaChoices(0));
		assertArrayEquals(expected1, s.getTopicAreaChoices("username_5"));
		assertArrayEquals(expected1, s.getTopicAreaChoices("username_9"));
		
		assertArrayEquals(expected2, s.getTopicAreaChoices(1));
		assertArrayEquals(expected2, s.getTopicAreaChoices("username_6"));
		assertArrayEquals(expected2, s.getTopicAreaChoices("username_10"));
		
		try {
			s.getTopicAreaChoices(-1);
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getTopicAreaChoices(Integer.MAX_VALUE);
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getTopicAreaChoices(10);
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getTopicAreaChoices("");
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getTopicAreaChoices(null);
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getTopicAreaChoices("not valid");
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		String [] expectedkeyWords1 = new String[] {"keyword_1", "keyword_2", "keyword_3", "keyword_4", "keyword_5" };
		String [] expectedkeyWords2 = new String[] {"keyword_6", "keyword_7", "keyword_8", "keyword_9", "keyword_10" };

		assertArrayEquals(expectedkeyWords1, s.getKeywords(0));
		assertArrayEquals(expectedkeyWords1, s.getKeywords("username_5"));
		assertArrayEquals(expectedkeyWords1, s.getKeywords("username_9"));
		
		assertArrayEquals(expectedkeyWords2, s.getKeywords(1));
		assertArrayEquals(expectedkeyWords2, s.getKeywords("username_6"));
		assertArrayEquals(expectedkeyWords2, s.getKeywords("username_10"));
		
		try {
			s.getKeywords(-1);
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getKeywords(Integer.MAX_VALUE);
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getKeywords(10);
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getKeywords("");
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getKeywords(null);
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.getKeywords("not valid");
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		assertEquals(true,s.isNatSci(0));
		assertEquals(true,s.isNatSci("username_3"));
		assertEquals(true,s.isNatSci("username_5"));
		assertEquals(true,s.isNatSci(5));
		assertEquals(true,s.isNatSci(9));
		
		assertEquals(false,s.isNatSci(1));
		assertEquals(false,s.isNatSci("username_4"));
		assertEquals(false,s.isNatSci("username_7"));
		assertEquals(false,s.isNatSci(7));
		assertEquals(false,s.isNatSci(8));
		
		try {
			s.isNatSci(-1);
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.isNatSci(10);
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.isNatSci(Integer.MAX_VALUE);
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.isNatSci(Integer.MIN_VALUE);
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.isNatSci(null);
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.isNatSci("");
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
		
		try {
			s.isNatSci("Username_1");
			fail("Expected exception");
		}catch (StudentNotFoundException e) {
			//pass
		}
	}
	
	/**
	 * Test duplicate usernames throws the correct error
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_duplicate_usernames()throws Exception {
		try {
				new Students("test_files/student_files/duplicateusername.csv", 1, 0, 17, 
						new int [] {8,9,10,11,12} , new int [] {14,15,16}, new int [] {2,3,4,5,6,7}, 
						new String[] {"nat sci"});
			} catch (IllegalFormatException e) {
				// pass
			}
	}
	
	
	/**
	 * @throws Exception - If test failure
	 */
	@Test
	public void test_invalid_config() throws Exception {
		try {
			new Students("test_files/student_files/valid1.csv", 18, 0, 17, 
					new int [] {8,9,10,11,12} , new int [] {14,15,16}, new int [] {2,3,4,5,6,7}, 
					new String[] {"nat sci"});
			fail("Expected exceotion");
		} catch (IllegalFormatException e) {
			// pass
		} 
		
		try {
			new Students("test_files/student_files/valid1.csv", -1, 0, 17, 
					new int [] {8,9,10,11,12} , new int [] {14,15,16}, new int [] {2,3,4,5,6,7}, 
					new String[] {"nat sci"});
			fail("Expected exceotion");
		} catch (IllegalFormatException e) {
			// pass
		} 
		
		try {
			new Students("test_files/student_files/valid1.csv", 1, 18, 17, 
					new int [] {8,9,10,11,12} , new int [] {14,15,16}, new int [] {2,3,4,5,6,7}, 
					new String[] {"nat sci"});
			fail("Expected exceotion");
		} catch (IllegalFormatException e) {
			// pass
		} 
		
		try {
			new Students("test_files/student_files/valid1.csv", 1, 0, 18, 
					new int [] {8,9,10,11,12} , new int [] {14,15,16}, new int [] {2,3,4,5,6,7}, 
					new String[] {"nat sci"});
			fail("Expected exceotion");
		} catch (IllegalFormatException e) {
			// pass
		}
		
		try {
			new Students("test_files/student_files/valid1.csv", 1, 0, 17, 
					new int [] {18} , new int [] {14,15,16}, new int [] {2,3,4,5,6,7}, 
					new String[] {"nat sci"});
			fail("Expected exceotion");
		} catch (IllegalFormatException e) {
			// pass
		}
		
		try {
			new Students("test_files/student_files/valid1.csv", 1, 0, 17, 
					new int [] {8,9,10,11,12} , new int [] {18}, new int [] {2,3,4,5,6,7}, 
					new String[] {"nat sci"});
			fail("Expected exceotion");
		} catch (IllegalFormatException e) {
			// pass
		}
		
		try {
			new Students("test_files/student_files/valid1.csv", 1, 0, 17, 
					new int [] {8,9,10,11,12} , new int [] {14,15,16}, new int [] {18}, 
					new String[] {"nat sci"});
			fail("Expected exceotion");
		} catch (IllegalFormatException e) {
			// pass
		}
		
		try {
			new Students("test_files/student_files/valid1.csv", 1, 0, 17, 
					new int [] {} , new int [] {14,15,16}, new int [] {2,3,4,5,6,7}, 
					new String[] {"nat sci"});
			
			new Students("test_files/student_files/valid1.csv", 1, 0, 17, 
					new int [] {} , new int [] {}, new int [] {}, 
					new String[] {"nat sci"});
		} catch (IllegalFormatException e) {
			e.printStackTrace();
			fail("didn't expect exceptionexceotion");
		}
			
	}
	
	/**
	 * test correct failure when students object initialised with non existent file
	 * @throws Exception - Thrown if failure
	 */
	@Test
	public void test_no_student_file() throws Exception {
		
		try {
			new Students("no_file.csv", 1, 0, 17, 
					new int [] {8,9,10,11,12} , new int [] {14,15,16}, new int [] {18}, 
					new String[] {"nat sci"});
			fail("Expected exceotion");
		} catch (FileNotFoundException e) {
			// pass
		}
	}
	
	/**
	 * test correct failure when students object initialised with corrupt file
	 * @throws Exception - Thrown if failure
	 */
	@Test
	public void test_invalid_student_file() throws Exception {
		try {
			new Students("test_files/student_files/corrupt.csv", 1, 0, 17, 
					new int [] {8,9,10,11,12} , new int [] {14,15,16}, new int [] {18}, 
					new String[] {"nat sci"});
			fail("Expected exception");
		} catch (InvalidTableFormatException e) {
			// pass
		}
	}
	
	/**
	 * test that student file copy works correctly
	 * @throws Exception - Thrown if failure
	 */
	@Test
	public void test_copy_data() throws Exception {
		
		Students s = new Students("test_files/student_files/valid1.csv", 1, 0, 17, 
				new int [] {8,9,10,11,12} , new int [] {14,15,16}, new int [] {2,3,4,5,6,7}, 
				new String[] {"nat sci"});
		
		Table expected = Table.parseTableFromCSVFile(new File("test_files/student_files/valid1.csv"));
		
		Table actual = s.getData();
		

		assertArrayEquals(expected.getHeaders(), actual.getHeaders());
		
		assertEquals(expected.size(), actual.size());
		assertEquals(expected.getColCount(), actual.getColCount());
		
		for (int i=0;i<expected.getColCount();i++) {
			assertEquals(expected.getColumn(i),actual.getColumn(i));
		}
	}
	
	/**
	 * Test that when config file doesn't exist constructing students object fails wiht filenotfound error
     * @throws Exception - Thrown if failure
	 */
	@Test
	public void test_students_when_config_file_does_not_exist() throws Exception {
		Config.configFile = "does_not_exist.json";
		
		Students.clearInstance();
		try {
			Students.getInstance();
			fail("Expected FileNotFoundException");
		}catch (FileNotFoundException e) {
			//pass
		}
	}
	
	/**
	 * Test that when config file is invalid json student object construction throws ConfigNotValid error
     * @throws Exception - Thrown if failure
	 */
	@Test
	public void test_students_when_config_file_is_not_valid_json() throws Exception {
		Config.configFile = "test_files/config_files/corrupt.json";
		
		Students.clearInstance();
		try {
			Students.getInstance();
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
	public void test_students_when_config_file_is_not_valid() throws Exception {
		Config.configFile = "test_files/config_files/invalid18.json";
		Config.clearConfig();
		
		Students.clearInstance();
		try {
			Students.getInstance();
			fail("Expected ConfigNotValidException");
		}catch (ConfigNotValidException e) {
			//pass
		}
	}
	
	/**
	 * Test that when config file is valid that student object can be constructed correctly
	 * @throws Exception - Thrown if failure
	 */
	@Test
	public void test_students_when_config_file_is_valid() throws Exception {
		Config.configFile = "test_files/config_files/simpleValid1.json";
		Config.clearConfig();
		Students.clearInstance();
		Students.getInstance();
		
	}

}
