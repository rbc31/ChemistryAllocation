package dataManager;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.json.JSONObject;

import Exceptions.ConfigNotValidException;
import config.Data;
import config.ETYPE;
import config.validation.ValidationObject;
import utils.GetStackTrace;

/**
 * 
 * Global validation object for the chemistry project data
 * 
 * Validates the student, supervisor and matching data is valid.
 * @author Rob
 *
 */
public class ChemistryProjectGlobalValidation implements ValidationObject {

	/**
	 * Logger for this class
	 */
	private static Logger logger = Logger.getLogger(Config.class.getName());
	
	
	/**
	 * Returns null, This class does not support serialisation
	 */
	@Override
	public JSONObject toJSONOBJ() {
		return null;
	}
	
	/**
	 * Validates that the parent Data contains the key data and that subData is a Integer >= 0
	 * @param parent - The parent Data to do the look up on
	 * @param key - The key to look up in the parent data
	 * @return Validation error,  null if there is no validation
	 */
	public String containsGreaterThanOrEqualTo0(Data parent, String key) {
		logger.info("Checking that " + key + " exists and is a positive integer");
		
		try {
			Data sub = parent.getSubData(key);
			
			Boolean toReturn =  sub.getType() == ETYPE.INTEGER && ((Integer)sub.getData()) >= 0;
			logger.info(key + " exists and is a positive integer: " + toReturn);
			if (toReturn) {
				return null;
			}else {
				return key + " exists but is not a positive integer as expected.";
			}
		}catch (IllegalArgumentException e) {
			logger.warning("Encountered IllegalArgumentException whilst trying to validate " + key);
			return key + " does not exist!";
		}
	}

	/**
	 * Validates that the given parent Data has a String at the given subkey
	 * @param parent - Parent to do look up on
	 * @param key - The subkey data to verify is a String
	 * @return Error message or null if valid
	 */
	public String containsString(Data parent, String key) {
		logger.info("Checking that " + key + " exists and is a String type");
		
		try {
			Data sub = parent.getSubData(key);
			
			Boolean toReturn =  sub.getType() == ETYPE.STRING ;
			logger.info(key + " exists and is a String: "+toReturn);
			if (toReturn) {
				return null;
			}else {
				return key + " exists but is not a String as expected.";
			}
		}catch (IllegalArgumentException e) {
			logger.warning("Encountered IllegalArgumentException whilst trying to validate " + key);
			return key + " does not exist";
		}
	}
	
	/**
	 * Validates that the given parent Data has an String list at the given subkey
	 * @param parent - Parent to do look up on
	 * @param key - The subkey data to verify is a list of Strings
	 * @return Error message or null if valid
	 */
	public String containsStringList(Data parent, String key) {
		logger.info("Checking that " + key + " exists and is a list of Strings");
		
		try {
			Data sub = parent.getSubData(key);

			if (sub.getType() == ETYPE.LIST) {
				ArrayList<?> list = (ArrayList<?>) sub.getData();
				for (Object o : list) {
					if (o instanceof Data) {
						Boolean valid = ((Data)o).getType() == ETYPE.STRING;
						if (!valid) {
							logger.warning("One of the elements of the " + key + " was not valid");
							return "The element " + ((Data)o).getName() + " was expected to be a String but it is actually " + ((Data)o).getType();
						}
					}else {
						logger.severe("Unexpected, one element of the list is not Data");
						return "Internal data structure error.";
					}
				}
				return null;
				
			}else {
				logger.warning("Key " + key + " was expected to be a list but was actually a " + sub.getType());
				return "Key " + key + " was expected to be a list but was actually a " + sub.getType();
			}
			
		}catch (IllegalArgumentException e) {
			logger.warning("Encountered IllegalArgumentException whilst trying to validate " + key);
			return "Key " + key + " does not exist.";
		}
	}
	
	/**
	 * Validates that the given parent Data has an integer list of positive integers at the given subkey
	 * @param parent - Parent to do look up on
	 * @param key - The subkey data to verify is a list of positive integers
	 * @return Error message or null if valid
	 */
	public String containsListOfGreaterThanOrEqualTo0Integer(Data parent, String key) {
		logger.info("Checking that " + key + " exists and is a list of positive integers");
		
		try {
			Data sub = parent.getSubData(key);

			if (sub.getType() == ETYPE.LIST) {
				ArrayList<?> list = (ArrayList<?>) sub.getData();
				for (Object o : list) {
					if (o instanceof Data) {
						String valid = containsGreaterThanOrEqualTo0(sub,((Data)o).getName());
						if (valid != null) {
							logger.warning("One of the elements of the " +key + " was not valid");
							return valid;
						}
					}else {
						logger.severe("Unexpected, one element of the list is not Data");
						return "Internal data structure error.";
					}
				}
				return null;
				
			}else {
				logger.warning("Key " + key + " was expected to be a list but was actually a " + sub.getType());
				return "Key " + key + " was expected to be a list but was actually a " + sub.getType();
			}
			
		}catch (IllegalArgumentException e) {
			logger.warning("Encountered IllegalArgumentException whilst trying to validate " + key);
			return "Key " + key + " does not exist.";
		}
	}
	
	/**
	 * Validates the student object
	 * @param studentObj - The student object to validate
	 * @return Error message or null if valid
	 */
	public String validateStudentObject(Data studentObj) {
		if (studentObj.getType() == ETYPE.OBJECT) {
			
			String error = null;
			
			error = containsGreaterThanOrEqualTo0(studentObj, Config.STUDENT_USERNAME_COLUMN);
			
			if (error != null) {
				logger.warning("Student validation failed because of key: " + Config.STUDENT_USERNAME_COLUMN);
				return "Student validation failed because of key: " + Config.STUDENT_USERNAME_COLUMN + ". Error: " + error;
			}
			
			error = containsGreaterThanOrEqualTo0(studentObj, Config.STUDENT_COURSE_COL);
			
			if (error != null) {
				logger.warning("Student validation failed because of key: " + Config.STUDENT_COURSE_COL);
				return "Student validation failed because of key: " + Config.STUDENT_COURSE_COL + ". Error: " + error;
			}
			
			error = containsGreaterThanOrEqualTo0(studentObj, Config.STUDENT_NAME_COL);
	
			if (error != null) {
				logger.warning("Student validation failed because of key: " + Config.STUDENT_NAME_COL);
				return "Student validation failed because of key: " + Config.STUDENT_NAME_COL + ". Error: " + error;
			}
			
			error = containsListOfGreaterThanOrEqualTo0Integer(studentObj, Config.STUDENT_KEYWORD_COLUMNS);
			
			if (error != null) {
				logger.warning("Student validation failed because of key: " + Config.STUDENT_KEYWORD_COLUMNS);
				return "Student validation failed because of key: " + Config.STUDENT_KEYWORD_COLUMNS + ". Error: " + error;
			}
			
			error = containsListOfGreaterThanOrEqualTo0Integer(studentObj, Config.STUDENT_TOPIC_AREA_COLUMNS);
			if (error != null) {
				logger.warning("Student validation failed because of key: " + Config.STUDENT_TOPIC_AREA_COLUMNS);
				return "Student validation failed because of key: " + Config.STUDENT_TOPIC_AREA_COLUMNS + ". Error: " + error;
			}
			
			error = containsListOfGreaterThanOrEqualTo0Integer(studentObj, Config.STUDENT_PREFERENCE_COLUMNS);
			if (error != null) {
				logger.warning("Student validation failed because of key: " + Config.STUDENT_PREFERENCE_COLUMNS);
				return "Student validation failed because of key: " + Config.STUDENT_PREFERENCE_COLUMNS + ". Error: " + error;
			}
			
			error = containsString(studentObj, Config.STUDENT_INPUT_FILE);
			if (error != null) {
				logger.warning("Student validation failed because of key: " + Config.STUDENT_INPUT_FILE);
				return "Student validation failed because of key: " + Config.STUDENT_INPUT_FILE + ". Error: " + error;
			}
			
			error = containsStringList(studentObj, Config.STUDENT_NAT_SCI_UNITS);
			if (error != null) {
				logger.warning("Student validation failed because of key: " + Config.STUDENT_NAT_SCI_UNITS);
				return "Student validation failed because of key: " + Config.STUDENT_NAT_SCI_UNITS + ". Error: " + error;
			}
			return null;
		}else {
			logger.severe("Student validation failed. Was expecting a Data Object got: " + studentObj.getType());
			return "Student Data was expected to be an object when its actually a " + studentObj.getType();
		}
	}
	
	/**
	 * Validates the supervisor object
	 * @param supervisorObj - The student object to validate
	 * @return Error message or null if valid
	 */
	public String validateSupervisorObject(Data supervisorObj) {
		
		if (supervisorObj.getType() == ETYPE.OBJECT) {
			
			String error = null;
			
			error = containsGreaterThanOrEqualTo0(supervisorObj, Config.SUPERVISOR_NAME_COL);
			if (error != null) {
				logger.warning("Supervisor validation failed because of key: " + Config.SUPERVISOR_NAME_COL);
				return error;
			}
			
			error = containsGreaterThanOrEqualTo0(supervisorObj, Config.SUPERVISOR_CAPACITY_COL);
			if (error != null) {
				logger.warning("Supervisor validation failed because of key: " + Config.SUPERVISOR_CAPACITY_COL);
				return error;
			}
			
			error = containsGreaterThanOrEqualTo0(supervisorObj, Config.SUPERVISOR_TOPIC_COL);
			if (error != null) {
				logger.warning("Supervisor validation failed because of key: " + Config.SUPERVISOR_TOPIC_COL);
				return error;
			}
			
			error = containsListOfGreaterThanOrEqualTo0Integer(supervisorObj, Config.SUPERVISOR_KEYWORD_COLUMNS);
			if (error != null) {
				logger.warning("Supervisor validation failed because of key: " + Config.SUPERVISOR_KEYWORD_COLUMNS);
				return error;
			}
			
			error = containsString(supervisorObj, Config.SUPERVISOR_INPUT_FILE);
			if (error != null) {
				logger.warning("Supervisor validation failed because of key: " + Config.SUPERVISOR_INPUT_FILE);
				return error;
			}
			return null;
		}else {
			logger.severe("Supervisor validation failed. Was expecting a Data Object got: " + supervisorObj.getType());
			return "Internal data structure error.";
		}
	}

	/**
	 * Validates that the given parent Data has a boolean data sub key
	 * @param parent - Parent to do look up on
	 * @param key - The subkey data to verify is a boolean
	 * @return Error message or null if valid
	 */
	public String containsBoolean(Data parent, String key) {
		logger.info("Checking that " + key + " exists and is a Boolean type");
		
		try {
			Data sub = parent.getSubData(key);
			
			Boolean toReturn =  sub.getType() == ETYPE.BOOLEAN ;
			logger.info(key + " exists and is a Boolean: " + toReturn);
			if (toReturn) {
				return null;
			}else {
				return key + " exists but is not a Boolean as expected.";
			}
		}catch (IllegalArgumentException e) {
			logger.warning("Encountered IllegalArgumentException whilst trying to validate " + key);
			return key + " does not exist";
		}
	}
	
	/**
	 * Validates the matching object part of the chemistry config data
	 * @param matchingObj - The matching object
	 * @return The validation error message  spoon full of sug{@docRoot}
	 */
	public String validateMatchingObject(Data matchingObj) {
		if (matchingObj.getType() == ETYPE.OBJECT) {
			
			String error = null;
			
			error = containsGreaterThanOrEqualTo0(matchingObj, Config.MATCHING_NO_MATCH_WEIGHT);
			
			if (error != null) {
				logger.warning("Matching validation failed because of key: " + Config.MATCHING_NO_MATCH_WEIGHT);
				return error;
			}
			
			error = containsGreaterThanOrEqualTo0(matchingObj, Config.MATCHING_NO_TOPIC_AREA_MATCH_WEIGHT);
			if (error != null) {
				logger.warning("Matching validation failed because of key: " + Config.MATCHING_NO_TOPIC_AREA_MATCH_WEIGHT);
				return error;
			}
			
			error = containsGreaterThanOrEqualTo0(matchingObj, Config.MATCHING_NO_KEYWORDS_IN_COMMON_WEIGHT);				
			if (error != null) {
				logger.warning("Matching validation failed because of key: " + Config.MATCHING_NO_KEYWORDS_IN_COMMON_WEIGHT);
				return error;
			}
			
			error = containsGreaterThanOrEqualTo0(matchingObj, Config.MATCHING_KEYWORD_LOWER_BOUND_TO_ALLOCATE);
			if (error != null) {
				logger.warning("Matching validation failed because of key: " + Config.MATCHING_KEYWORD_LOWER_BOUND_TO_ALLOCATE);
				return error;
			}
			
			error = containsListOfGreaterThanOrEqualTo0Integer(matchingObj, Config.MATCHING_CHOICE_PREFERENCE_WEIGHTS);
			if (error != null) {
				logger.warning("Matching validation failed because of key: " + Config.MATCHING_CHOICE_PREFERENCE_WEIGHTS);
				return error;
			}
			
			error = containsListOfGreaterThanOrEqualTo0Integer(matchingObj, Config.MATCHING_TOPIC_AREA_PREFERENCE_WEIGHTS);
			if (error != null) {
				logger.warning("Matching validation failed because of key: " + Config.MATCHING_TOPIC_AREA_PREFERENCE_WEIGHTS);
				return error;
			}
			
			error = containsListOfGreaterThanOrEqualTo0Integer(matchingObj, Config.MATCHING_KEYWORDS_PREFERENCE_WEIGHTS);
			if (error != null) {
				logger.warning("Matching validation failed because of key: " + Config.MATCHING_KEYWORDS_PREFERENCE_WEIGHTS);
				return error;
			}
			
			error = containsBoolean(matchingObj, Config.MATCHING_ENABLE_TOPIC_AREA_ALLOCATION);
			if (error != null) {
				logger.warning("Matching validation failed because of key: " + Config.MATCHING_ENABLE_TOPIC_AREA_ALLOCATION);
				return error;
			}
			
			error = containsBoolean(matchingObj, Config.MATCHING_ENABLE_KEYWORD_ALLOCATION);
			if (error != null) {
				logger.warning("Matching validation failed because of key: " + Config.MATCHING_ENABLE_KEYWORD_ALLOCATION);
				return error;
			}
			
			error = containsStringList(matchingObj, Config.MATCHING_TOPIC_AREAS);
			if (error != null) {
				logger.warning("Matching validation failed because of key: " + Config.MATCHING_TOPIC_AREAS);
				return error;
			}
			return null;
		}else {
			logger.severe("Matching validation failed. Was expecting a Data Object got: " + matchingObj.getType());
			return "Internal data structure error.";
		}
	}
	
	/**
	 * Gets the length of the list of the given sub data under the given parent
	 * @param parent - The parent to do the lookup on
	 * @param key - The key too look up on the parent
	 * @return The size of the list
	 * @throws ConfigNotValidException Thrown if the given subkey on the given parent is not a list
	 */
	public int getLengthOfList(Data parent, String key) throws ConfigNotValidException {
		Data list = parent.getSubData(key);
		
		if (list.getType() == ETYPE.LIST) {
			return ((ArrayList<?>) list.getData()).size();
		}else {
			throw new ConfigNotValidException("Key <" + key + "> was not a list as expected.");
		}
	}
	

	/**
	 * Gets the validation error when validating the given object as a Chemistry configuration
	 * @param arg0 - The data object that is too be validated
	 * @return The validation error message or null if the input is valid
	 */
	public String getValidationError(Object arg0) {
		if (arg0 instanceof ArrayList<?>) {
			ArrayList<?> list = (ArrayList<?>) arg0;
			Data topLevelData = null;
			try {
				topLevelData = new Data("temp","temp",list,true, true, true);
			} catch (ConfigNotValidException  e) {
				logger.severe("Failed to create top level Data object");
				return "Internal error: Failed to create top level Data object";
			}
			
			String error = validateStudentObject(topLevelData.getSubData("STUDENT_DATA"));
			
			if (error != null) {
				logger.severe("Failed to validate Student object");
				return error;
			}
			
			error = validateSupervisorObject(topLevelData.getSubData("SUPERVISOR_DATA"));
			
			if (error != null) {
				logger.severe("Failed to validate Supervisor object");
				return error;
			}
			
			error = validateMatchingObject(topLevelData.getSubData("MATCHING_DATA"));
			
			if (error != null) {
				logger.severe("Failed to validate Matching object");
				return error;
			}
			
			
			try {
				int choiceStudentSize  = getLengthOfList(topLevelData.getSubData("STUDENT_DATA"),Config.STUDENT_PREFERENCE_COLUMNS);
				int choiceMatchingSize = getLengthOfList(topLevelData.getSubData("MATCHING_DATA"),Config.MATCHING_CHOICE_PREFERENCE_WEIGHTS);
			
				if (choiceStudentSize != choiceMatchingSize) {
					logger.severe("Failed to validate Matching object");
					return "The preference array in the matching data object must be the same size as the student preference columns array in the student data";
				}
				
				int keywordsStudentSize  = getLengthOfList(topLevelData.getSubData("STUDENT_DATA"),Config.STUDENT_KEYWORD_COLUMNS);
				int keywordsMatchingSize = getLengthOfList(topLevelData.getSubData("MATCHING_DATA"),Config.MATCHING_KEYWORDS_PREFERENCE_WEIGHTS);
			
				if (keywordsStudentSize != keywordsMatchingSize) {
					logger.severe("Failed to validate Matching object");
					return "The keyword preference array (length=" + keywordsMatchingSize + ") in the matching data object must be the same size as the student keyword preference columns array  (length=" + keywordsStudentSize + ") in the student data";
				}
				
				int topicAreaStudentSize  = getLengthOfList(topLevelData.getSubData("STUDENT_DATA"),Config.STUDENT_TOPIC_AREA_COLUMNS);
				int topicAreaMatchingSize = getLengthOfList(topLevelData.getSubData("MATCHING_DATA"),Config.MATCHING_TOPIC_AREA_PREFERENCE_WEIGHTS);
			
				if (topicAreaStudentSize != topicAreaMatchingSize) {
					logger.severe("Failed to validate Matching object");
					return "The topic area preference array in the matching data object must be the same size as the student topic area preference columns array in the student data";
				}
			}catch (ConfigNotValidException e) {
				logger.severe("Encountered ConfigNotValidException whilst trying to validate the size of the matching and student lists");
				logger.severe("GetStackTrace: " + GetStackTrace.getStackTrace(e));
				return "Preference columns are not the correct length with respect to function column weights";
			}
			
			return null;
		}else {
			logger.severe("Validation failed. Was expecting a HashMap Object got: " + arg0.getClass().getName());
			return "Internal error";
		}
	}

	/**
	 * Returns true if object is a valid chemistry configuration.
	 */
	@Override
	public boolean valid(Object arg0) {
		return null == this.getValidationError(arg0);
	}
	
}