package testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import Exceptions.ConfigNotValidException;
import Exceptions.CustomValidationException;
import Exceptions.InvalidTypeException;
import config.Data;
import dataManager.ChemistryProjectGlobalValidation;
import dataManager.Config;
import dataManager.Students;
import dataManager.Supervisors;
/**
 * Configuration tests
 * @author Rob
 *
 */
public class ConfigTests {

	/**
	 * 
	 * @throws Exception if error
	 */
	@Test
	public void test_valid_config() throws Exception {
		Config.configFile = "test_files/config_files/valid1.json";
		Config.clearConfig();
		
		Students students = Students.forceLoad();
		Supervisors supervisors = Supervisors.forceLoad();
		
		assertEquals(1, supervisors.size());
		assertEquals(1, supervisors.getSupervisorCapcity("test_supervisor"));
		assertEquals(1, supervisors.getNumberOfKeywords());
		assertTrue("test_keyword".equals(supervisors.getKeywords("test_supervisor")[0]));
		assertEquals("test_topic_area", supervisors.getSupervisorTopic("test_supervisor"));
		assertEquals(0,supervisors.getIndex("test_supervisor"));
		
		assertEquals(1, students.size());
		assertEquals("test_name", students.getName("test_username"));
		assertEquals(1, students.getNumberOfKeywords());
		assertEquals(5, students.getNumOfPreferenceChoice());
		assertTrue("Test keyword 1".equals(students.getKeywords("test_username")[0]));
		assertEquals(0,students.getIndex("test_username"));
		
		
	
		assertEquals("Test pick 1", students.getChoice("test_username", 0));
		assertEquals("Test pick 2", students.getChoice("test_username", 1));
		assertEquals("Test pick 3", students.getChoice("test_username", 2));
		assertEquals("Test pick 4", students.getChoice("test_username", 3));
		assertEquals("Test pick 5", students.getChoice("test_username", 4));
			
	}
	
	
	/**
	 * Test when STUDENT_COURSE_COL is missing that an appropriate exception is thrown
	 * @throws Exception if error
	 */
	@Test
	public void test_invalid_config_1()  throws Exception {
		Config.configFile  = "test_files/config_files/invalid1.json";
		String missingData = "STUDENT_COURSE_COL";
		
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when STUDENT_KEYWORD_COLUMNS is missing that an appropriate exception is thrown
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_2()  throws Exception {
		Config.configFile  = "test_files/config_files/invalid2.json";
		String missingData = "STUDENT_KEYWORD_COLUMNS";
		
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when STUDENT_NAT_SCI_UNITS is missing that an appropriate exception is thrown
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_3() throws Exception {
		
		Config.configFile  = "test_files/config_files/invalid3.json";
		String missingData = "STUDENT_NAT_SCI_UNITS";
		
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when STUDENT_PREFERENCE_COLUMNS is missing that an appropriate exception is thrown
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_4() throws Exception {
		
		Config.configFile  = "test_files/config_files/invalid4.json";
		String missingData = "STUDENT_PREFERENCE_COLUMNS";
		
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when STUDENT_NAME_COL is missing that an appropriate exception is thrown
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_5() throws Exception {
		Config.configFile  = "test_files/config_files/invalid5.json";
		String missingData = "STUDENT_NAME_COL";
		
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when STUDENT_TOPIC_AREA_COLUMNS is missing that an appropriate exception is thrown
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_6() throws Exception {
		
		Config.configFile  = "test_files/config_files/invalid6.json";
		String missingData = "STUDENT_TOPIC_AREA_COLUMNS";
		
		constructObjectsExpectFailure(missingData);
	}

	
	/**
	 * Test when STUDENT_USERNAME_COLUMN is missing that an appropriate exception is thrown
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_7() throws Exception {
		
		Config.configFile  = "test_files/config_files/invalid7.json";
		String missingData = "STUDENT_USERNAME_COLUMN";
		
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when STUDENT_INPUT_FILE is missing that an appropriate exception is thrown
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_8() throws Exception {
		
		Config.configFile  = "test_files/config_files/invalid8.json";
		String missingData = "STUDENT_INPUT_FILE";
		
		constructObjectsExpectFailure(missingData);
	}
	
	
	/**
	 * Test when all student data is missing  is missing that an appropriate exception is thrown
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_9() throws Exception {
		
		Config.configFile  = "test_files/config_files/invalid9.json";
		String missingData = "STUDENT_USERNAME_COL";
		
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when student data is a boolean, check that the appropriate exception is thrown
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_10() throws Exception {
		
		Config.configFile  = "test_files/config_files/invalid10.json";
		String missingData = "expected to be an object";
		Config.clearConfig();
		
		
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when student data is a boolean, check that the appropriate exception is thrown
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_11() throws Exception {
		Config.configFile  = "test_files/config_files/invalid11.json";
		String missingData = "NOT_REAL";
		Config.clearConfig();
		
		try {
			Config.getConfig();
			fail("Expected exception");
		} catch (InvalidTypeException e) {
			assertTrue(e.getMessage().contains(missingData));
		}

		try {
			Students.forceLoad();
			fail("Expected exception");
		} catch (ConfigNotValidException e) {
			assertTrue(e.getMessage().contains(missingData));
		}
		
		try {
			new Supervisors();
			fail("Expected exception");
		} catch (ConfigNotValidException e) {
			assertTrue(e.getMessage().contains(missingData));
		}
	}
	
	/**
	 * Test when student data is a boolean, check that the appropriate exception is thrown
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_12() throws Exception {
		
		Config.configFile  = "test_files/config_files/invalid12.json";
		String missingData = "";
		
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when student username is not an integer type
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_13() throws Exception {
		Config.configFile  = "test_files/config_files/invalid13.json";
		String missingData = "STUDENT_USERNAME";
		
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when student username is s negative integer
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_14() throws Exception {
		Config.configFile  = "test_files/config_files/invalid14.json";
		String missingData = "STUDENT_USERNAME";
		
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when student course is not an integer type
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_15() throws Exception {
		Config.configFile  = "test_files/config_files/invalid15.json";
		String missingData = "STUDENT_COURSE";
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when student course is not a positive integer
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_16() throws Exception {
		Config.configFile  = "test_files/config_files/invalid16.json";
		String missingData = "STUDENT_COURSE";
		
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test when student input sile is not a string type
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_17() throws Exception {
		Config.configFile  = "test_files/config_files/invalid17.json";
		String missingData = "STUDENT_INPUT";
		constructObjectsExpectFailure(missingData);
	}
	
	/**
	 * Test ChemistryProjectGlobalValidation toJson method returns null
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_chemistry_project_validation_null_Serialization() throws Exception {
		assertEquals(null, new ChemistryProjectGlobalValidation().toJSONOBJ());
	}
	
	/**
	 * Test STring list function.
	 * 
	 * A data object has a key "list" that is going to be verified as a string list
	 * 
	 * Does it fail when the string list has an integer in it?
	 * Does it fail when the string list doesn't exist?
	 * Does it fail when the string list exists but is the wrong type
	 * @throws Exception - if failure
	 */
	@Test
	public void test_validate_string_list() throws Exception {
		ArrayList<Data> rawList = new ArrayList<Data>();
		rawList.add(new Data("subData", "integer", 12, false, false, false));
		Data list = new Data("list", "description", rawList, false, false, false);
		
		HashMap<String, Data> rawParent = new HashMap<String, Data>();
		rawParent.put("list", list);
		
		Data parent = new Data("parent", "parent", rawParent, false,false,false);
		
		ChemistryProjectGlobalValidation c = new ChemistryProjectGlobalValidation();
		
		//fail becuase list contains an integer element
		assertNotEquals(null, c.containsStringList(parent, "list"));
		
		//fail because key doesnt exist
		assertNotEquals(null, c.containsStringList(parent, "non existant "));
		
		//fail because key exists but is not list type
		assertNotEquals(null, c.containsStringList(list, "subData"));
	}
	
	/**
	 * Tests validation of a list of integers 
	 * 
	 * Testing scenarios where:
	 * -> The type is not a list
	 * -> The key does not exist
	 * -> The list contains a non integer type
	 * -> The list contains an integer less than 0	
	 * @throws Exception Thrown if error
	 */
	@Test
	public void test_validate_integer_list_greater_than_0() throws Exception {
		ChemistryProjectGlobalValidation c = new ChemistryProjectGlobalValidation();
		
		HashMap<String, Data> rawParent = new HashMap<String, Data>();
		
		ArrayList<Data> rawList1 = new ArrayList<Data>();
		ArrayList<Data> rawList2 = new ArrayList<Data>();
		ArrayList<Data> rawList3 = new ArrayList<Data>();
		
		rawList1.add(new Data("1","1",1,false,false,false));
		rawList1.add(new Data("2","2",Integer.MAX_VALUE,false,false,false));
		rawList1.add(new Data("3","3",Integer.MIN_VALUE,false,false,false));
		rawList1.add(new Data("4","4",99,false,false,false));
		
		rawList2.add(new Data("1","1",1,false,false,false));
		rawList2.add(new Data("2","2",Integer.MAX_VALUE,false,false,false));
		rawList2.add(new Data("3","3",99,false,false,false));
		rawList2.add(new Data("4","4","String",false,false,false));
		
		rawList3.add(new Data("1","1",1,false,false,false));
		rawList3.add(new Data("2","2",Integer.MAX_VALUE,false,false,false));
		rawList3.add(new Data("3","3",99,false,false,false));
		rawList3.add(new Data("4","4",6,false,false,false));
		
		Data list1 = new Data("list #1","list #1", rawList1, false, false, false);
		Data list2 = new Data("list #2","list #2", rawList2, false, false, false);
		Data list3 = new Data("list #3","list #3", rawList3, false, false, false);
		Data errorType = new Data("invalid type","invalid type", 12, false, false, false);
		
		rawParent.put("list #1", list1);
		rawParent.put("list #2", list2);
		rawParent.put("list #3", list3);
		rawParent.put("invalid type", errorType);
		
		
		Data parent = new Data("parent", "parent", rawParent, false,false,false);
		
		assertNotEquals(null, c.containsListOfGreaterThanOrEqualTo0Integer(parent, "list #1"));
		assertNotEquals(null, c.containsListOfGreaterThanOrEqualTo0Integer(parent, "list #2"));
		assertNotEquals(null, c.containsListOfGreaterThanOrEqualTo0Integer(parent, "invalid type"));
		assertNotEquals(null, c.containsListOfGreaterThanOrEqualTo0Integer(parent, "non existant"));
		assertEquals(null, c.containsListOfGreaterThanOrEqualTo0Integer(parent, "list #3"));
	}
	
	/**
	 * Test when supervisor name column is invalid
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_18() throws Exception {
		
		for (int i=18;i<21;i++) {
			Config.configFile  = "test_files/config_files/invalid" + String.valueOf(i) +".json";
			Config.clearConfig();
			String missingData = "SUPERVISOR_NAME_COL";
			constructObjectsExpectFailure(missingData);
		}
	}
	
	/**
	 * Verifies that when constructing the config a customValidationException
	 * is thrown and ConfigNotValid exceptions are thrown when students and supervisors
	 * objects are thrown
	 * @param missingData - a string to make sure is in the exceptions
	 * @throws Exception - Thrown if error
	 */
	private void constructObjectsExpectFailure(String missingData) throws Exception {
		try {
			Config.getConfig();
			fail("Expected exception");
		} catch (CustomValidationException e) {
			assertTrue(e.getMessage().contains(missingData));
		}

		try {
			Students.forceLoad();
			fail("Expected exception");
		} catch (ConfigNotValidException e) {
			assertTrue(e.getMessage().contains(missingData));
		}
		
		try {
			new Supervisors();
			fail("Expected exception");
		} catch (ConfigNotValidException e) {
			assertTrue(e.getMessage().contains(missingData));
		}
	}
	
	
	/**
	 * Test when supervisor capacity column is invalid
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_21() throws Exception {
		
		for (int i=21;i<24;i++) {
			Config.configFile  = "test_files/config_files/invalid" + String.valueOf(i) +".json";
			Config.clearConfig();
			String missingData = "SUPERVISOR_CAPACITY_COL";
			constructObjectsExpectFailure(missingData);
		}
	}
	
	/**
	 * Test when supervisor topic area is invalid
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_24() throws Exception {
		
		for (int i=24;i<27;i++) {
			Config.configFile  = "test_files/config_files/invalid" + String.valueOf(i) +".json";
			Config.clearConfig();
			String missingData = "SUPERVISOR_TOPIC_COL";
			constructObjectsExpectFailure(missingData);
		}
	}
	
	/**
	 * Test when supervisor file location is invalid
	 * @throws Exception - if test failure
	 */
	@Test
	public void test_invalid_config_27() throws Exception {
		
		for (int i=27;i<29;i++) {
			Config.configFile  = "test_files/config_files/invalid" + String.valueOf(i) +".json";
			Config.clearConfig();
			String missingData = "SUPERVISOR_INPUT_FILE";
			constructObjectsExpectFailure(missingData);
		}
	}
	
	/**
	 * Test the set string value function in the config
	 * @throws Exception - if error
	 */
	@Test
	public void test_set_string_value() throws Exception{
		Config.configFile = "test_files/config_files/valid1.json";
		
		Config.clearConfig();
		Config config = Config.getConfig();
		
		config.setStringValue("STUDENT_INPUT_FILE", "a value");
		config.setStringValue("MATCHING_SOMETHING", "another value");
		config.setStringValue("SUPERVISOR_INPUT_FILE", "a third value");
		
		assertEquals("a value", config.getStringValue("STUDENT_INPUT_FILE"));
		assertEquals("another value", config.getStringValue("MATCHING_SOMETHING"));
		assertEquals("a third value", config.getStringValue("SUPERVISOR_INPUT_FILE"));
		
		config.setNonPersistantCache("STUDENT_INPUT_FILE", "overridin data");
		
		assertEquals("overridin data", config.getStringValue("STUDENT_INPUT_FILE"));
		
		Config.configFile = "temp.json";
		config.save("temp.json");
		
		Config.clearConfig();
		
		config = Config.getConfig();

		assertEquals("a value", config.getStringValue("STUDENT_INPUT_FILE"));
		assertEquals("another value", config.getStringValue("MATCHING_SOMETHING"));
		assertEquals("a third value", config.getStringValue("SUPERVISOR_INPUT_FILE"));
		
		try {
			config.setStringValue("INPUT_FILE", "a value");
			fail("Expected exception");
		}catch (IllegalArgumentException e) {
			//pass
		}
	}
}
