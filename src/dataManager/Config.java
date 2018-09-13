package dataManager;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.json.JSONException;
import org.json.JSONObject;


import Exceptions.ConfigNotValidException;
import Exceptions.CustomValidationException;
import Exceptions.InvalidTypeException;
import config.Data;
import config.JSONConfig;
import gui.ConfigSetterGUI;
import gui.EnableParentOnClose;

/**
 * Config for chemistry program
 * @author Robert Cobb
 *
 */
public class Config {

	/**
	 * Static reference to the config object
	 */
	private static Config currentObj;
	
	/**
	 * the location of the config file
	 */
	public static String configFile = "config.json";
	
	/**
	 * The config object
	 */
	private JSONConfig config;
	
	/**
	 * The validation object to validate the config file
	 */
	private ChemistryProjectGlobalValidation validationObj;
	
	/**
	 * Logger for this class
	 */
	private static Logger logger = Logger.getLogger(Config.class.getName());
	
	/**
	 * Student user name column key
	 * The data at this key is expected to be the column index of the students username in the students input file
	 */
	public final static String STUDENT_USERNAME_COLUMN 					= "STUDENT_USERNAME_COLUMN";
	
	/**
	 * Student course column key
	 * The data at this key is expected to be the column index of the students course in the students input file
	 */
	public final static String STUDENT_COURSE_COL						= "STUDENT_COURSE_COL";
	
	/**
	 * Student name column key
	 * The data at this key is expected to be the column index of the students name in the students input file
	 */
	public final static String STUDENT_NAME_COL							= "STUDENT_NAME_COL";
	
	/**
	 * Student preference columns key
	 * The data at this key is expected to be a list of column indexes of the students supervisor preferences
	 * in the students input file
	 */
	public final static String STUDENT_PREFERENCE_COLUMNS 				= "STUDENT_PREFERENCE_COLUMNS";
	
	/**
	 * Student natural science columns key
	 * The data at this key is expected to a string list of natural science units
	 */
	public final static String STUDENT_NAT_SCI_UNITS    				= "STUDENT_NAT_SCI_UNITS";
	
	/**
	 * Student keywords columns key
	 * The data at this key is expected to be a list of column indexes of students keyword
	 * choices in the students input file
	 */
	public final static String STUDENT_KEYWORD_COLUMNS      			= "STUDENT_KEYWORD_COLUMNS";
	
	/**
	 * Student topic area columns key
	 * The data at this key is expected to be a list of column indexes of students topic area
	 * choices columns in the students input file
	 */
	public final static String STUDENT_TOPIC_AREA_COLUMNS   			= "STUDENT_TOPIC_AREA_COLUMNS";
	
	/**
	 * Student input file key
	 * The data at this key is expected to be the location of the current student file, 
	 * if we are told to remember the student file location
	 */
	public final static String STUDENT_INPUT_FILE						= "STUDENT_INPUT_FILE";
	
	
	/**
	 * Supervisor name column key
	 */
	public final static String SUPERVISOR_NAME_COL						= "SUPERVISOR_NAME_COL";

	/**
	 * Supervisor capacity column key
	 */
	public final static String SUPERVISOR_CAPACITY_COL					= "SUPERVISOR_CAPACITY_COL";

	
	/**
	 * Supervisor topic columns key
	 */
	public final static String SUPERVISOR_TOPIC_COL						= "SUPERVISOR_TOPIC_COL";

	
	/**
	 * Supervisor keyword columns key
	 */
	public final static String SUPERVISOR_KEYWORD_COLUMNS 				= "SUPERVISOR_KEYWORD_COLUMNS";

	/**
	 * Supervisor input file key
	 */
	public final static String SUPERVISOR_INPUT_FILE					= "SUPERVISOR_INPUT_FILE";
	
	/**
	 * Matching topic area key
	 */
	public final static String MATCHING_TOPIC_AREAS    					= "MATCHING_TOPIC_AREAS"; 	

	/**
	 * Matching choice preference area key
	 */
	public final static String MATCHING_CHOICE_PREFERENCE_WEIGHTS 		= "MATCHING_CHOICE_PREFERENCE_WEIGHTS";

	/**
	 * Matching no match preference weight key
	 */
	public final static String MATCHING_NO_MATCH_WEIGHT 				= "MATCHING_NO_MATCH_WEIGHT";
	
	/**
	 * Matching enable topic area key
	 */
	public final static String MATCHING_ENABLE_TOPIC_AREA_ALLOCATION 	= "MATCHING_ENABLE_TOPIC_AREA_ALLOCATION";
	
	/**
	 * Matching no topic area matching weightkey
	 */
	public final static String MATCHING_NO_TOPIC_AREA_MATCH_WEIGHT   	= "MATCHING_NO_TOPIC_AREA_MATCH_WEIGHT";
	
	/**
	 * Matching topic area preference weights key
	 */
	public final static String MATCHING_TOPIC_AREA_PREFERENCE_WEIGHTS 	= "MATCHING_TOPIC_AREA_PREFERENCE_WEIGHTS";
	
	/**
	 * Matching enable keyword allocation key
	 */
	public final static String MATCHING_ENABLE_KEYWORD_ALLOCATION 		= "MATCHING_ENABLE_KEYWORD_ALLOCATION";
	
	/**
	 * Matching keyword lower bound allocation key
	 */
	public final static String MATCHING_KEYWORD_LOWER_BOUND_TO_ALLOCATE = "MATCHING_KEYWORD_LOWER_BOUND_TO_ALLOCATE";
	
	
	/**
	 * Matching no keywords in common weight key
	 */
	public final static String MATCHING_NO_KEYWORDS_IN_COMMON_WEIGHT 	= "MATCHING_NO_KEYWORDS_IN_COMMON_WEIGHT";
	
	
	/**
	 * Matching keyword preference weights key
	 */
	public final static String MATCHING_KEYWORDS_PREFERENCE_WEIGHTS 	= "MATCHING_KEYWORDS_PREFERENCE_WEIGHTS";
	
	
	/**
	 * Non persistent cache to allow components to set values for the duration of the program
	 * Will be discarded when program ends
	 */
	private HashMap<String,Object> nonPersistantCache;
	
	/**
	 * Gets the config object if present, or loads it if not
	 * @return The config object
	 * @throws FileNotFoundException - Thrown if occurred when loading config
	 * @throws JSONException - Thrown if occurred when loading config
	 * @throws IOException - Thrown if occurred when loading config
	 * @throws InvalidTypeException - Thrown if occurred when loading config
	 * @throws ConfigNotValidException - Thrown if occurred when loading config
	 * @throws CustomValidationException - Thrown if occurred when loading config
	 */
	public static Config getConfig() throws FileNotFoundException, JSONException, IOException, InvalidTypeException, ConfigNotValidException, CustomValidationException {
		if (currentObj == null) {
			reload();
		}
		return currentObj;
	}
	
	/**
	 * clears the static config reference
	 */
	public static void clearConfig() {
		currentObj = null;
	}
	
	/**
	 * Reloads the config file from the configFile
	 * @throws FileNotFoundException Thrown if the file is not found
	 * @throws JSONException Thrown if the config file is invalid
	 * @throws IOException Thrown if occurred when reading from file
	 * @throws InvalidTypeException Thrown if the config is not valid
	 * @throws ConfigNotValidException Thrown if the config is not valid
	 * @throws CustomValidationException Thrown if the config is not valid
	 */
	public static void reload() throws FileNotFoundException, JSONException, IOException, InvalidTypeException, ConfigNotValidException, CustomValidationException {
		currentObj = new Config(configFile);
	}
	

	
	
	/**
	 * Creates a new config object
	 * @param filePath - The file path of the config file
	 * @throws FileNotFoundException Thrown if the file is not found
	 * @throws JSONException Thrown if the config file is invalid
	 * @throws IOException Thrown if occurred when reading from file
	 * @throws InvalidTypeException Thrown if the config is not valid
	 * @throws CustomValidationException Thrown if the config is not valid
	 * @throws ConfigNotValidException Thrown if the config is not valid
	 */
	private Config(String filePath) throws FileNotFoundException, JSONException, IOException, InvalidTypeException, CustomValidationException, ConfigNotValidException {
		logger.info("Constructing a JSON config based off of <" + filePath + "> ");
		this.nonPersistantCache = new HashMap<String, Object>();
		this.config = new JSONConfig(filePath);
		this.validationObj = new ChemistryProjectGlobalValidation();
		this.config.addValidation(this.validationObj);
		logger.info("Validating config from file.");
		if (!this.validationObj.valid(this.config.getTopLevelData().getData())) {
			logger.severe("Validation of config from <" + filePath + "> failed!");
			throw new CustomValidationException("Failed to validate global configuration. " + this.validationObj.getValidationError(this.config.getTopLevelData().getData()));
		}
	}
	
	/**
	 * Launches a gui to edit the config.
	 * The caller JFrame will be de-enabled until the user has finished the config.
	 * @param caller Caller JFrame to disable whilst settings window is opened
	 */
	public void launchSetter(JFrame caller) {
		JFrame frame = new JFrame();
		frame.setTitle("Configuration settings");
		frame.setContentPane(new ConfigSetterGUI(this.config.getTopLevelData(), frame, caller, this.config, true));
		frame.setVisible(true);
		frame.setSize(600, 650);
		caller.setEnabled(false);
		frame.addWindowListener(new EnableParentOnClose(caller));
		Point l = caller.getLocation();
		l.x = l.x + caller.getWidth()/2 - 275;
		frame.setLocation(l);
	}
	
	/**
	 * Saves the config object to file
	 * @throws FileNotFoundException Thrown if config file path is not valid
	 */
	public void save() throws FileNotFoundException {
		save(this.config.getFilePath());
	}
	
	/**
	 * Saves the config object to file
	 * @param filePath - The location to save the config too
	 * @throws FileNotFoundException Thrown if config file path is not valid
	 */
	public void save(String filePath) throws FileNotFoundException {
		this.config.save(filePath);
	}
	
	/**
	 * Sets the data for the given key non persistantly. wont be saved to disk
	 * @param key - The key to save the data as
	 * @param data - The data to save
	 */
	public void setNonPersistantCache(String key, Object data) {
		this.nonPersistantCache.put(key, data);
	}
	
	/**
	 * Gets the sub key data of the given key
	 * @param key - A valid config key
	 * @return data, or null if key not valid
	 */
	private Object getSubKeyData(String key) {
		if (nonPersistantCache.containsKey(key)) {
			return nonPersistantCache.get(key);
		}else {
			if (key.startsWith("STUDENT")) {
				return this.config.getTopLevelData().getSubData("STUDENT_DATA").getSubData(key).getData();
			}
			if (key.startsWith("SUPERVISOR")) {
				return this.config.getTopLevelData().getSubData("SUPERVISOR_DATA").getSubData(key).getData();
			}
			if (key.startsWith("MATCHING")) {
				return this.config.getTopLevelData().getSubData("MATCHING_DATA").getSubData(key).getData();
			}
			return null;
		}
	}
	
	/**
	 * Gets the given key as an integer
	 * @param key - the key to get
	 * @return The given data key as a integer
	 */
	public int getIntValue(String key) {
		return (Integer) getSubKeyData(key);
	}
	
	/**
	 * Gets the given key as a String
	 * @param key - the key to get
	 * @return The given data key as a String
	 */
	public String getStringValue(String key) {
		return (String) getSubKeyData(key);
	}
	
	/**
	 * Gets the given key as a Boolean
	 * @param key - the key to get
	 * @return The given data key as a Boolean
	 */
	public boolean getBooleanValue(String key) {
		return (Boolean) getSubKeyData(key);
	}
	
	/**
	 * Gets the given key as a String array
	 * @param key - the key to get
	 * @return The given data key as a String array
	 */
	public String[] getStrListValue(String key) {
		ArrayList<?> list = (ArrayList<?>) getSubKeyData(key);
	
		ArrayList<String> toReturn = new ArrayList<String>();
		for (Object element:list) {
			toReturn.add((String) ((Data) element).getData());
		}
		
		String[] actualToReturn = new String[list.size()];
		
		for (int i=0;i<toReturn.size();i++) {
			actualToReturn[i] = toReturn.get(i);
		}
		return actualToReturn;
	}
	
	/**
	 * Gets the given key as a Integer array
	 * @param key - the key to get
	 * @return The given data key as a Integer array
	 */
	public int[] getIntListValue(String key) {
		ArrayList<?> list = (ArrayList<?>) getSubKeyData(key);
	
		ArrayList<Integer> toReturn = new ArrayList<Integer>();
		for (Object element:list) {
			toReturn.add((Integer) ((Data) element).getData());
		}
		
		int[] actualToReturn = new int[list.size()];
		
		for (int i=0;i<toReturn.size();i++) {
			actualToReturn[i] = toReturn.get(i);
		}
		return actualToReturn;
	}

	
	/**
	 * Sets the given string value as a string
	 * @param key - The key to set
	 * @param value - The value to set
	 * @throws ConfigNotValidException - Thrown if the data to set is not valid
	 * @throws CustomValidationException- Thrown if the data to set is not valid
	 * @throws IllegalArgumentException - Thrown if the key is not valid
	 */
	public void setStringValue(String key, String value) throws IllegalArgumentException, ConfigNotValidException, CustomValidationException {
		
		if (key.startsWith("STUDENT")) {
			this.config.getTopLevelData().getSubData("STUDENT_DATA").getSubData(key).setData(value);
			return;
		}
		if (key.startsWith("SUPERVISOR")) {
			this.config.getTopLevelData().getSubData("SUPERVISOR_DATA").getSubData(key).setData(value);
			return;
		}
		if (key.startsWith("MATCHING")) {
			this.config.getTopLevelData().getSubData("MATCHING_DATA").getSubData(key).setData(value);
			return;
		}
		
		throw new IllegalArgumentException("Top level Key is invalid");
	}
	
	/**
	 * saves the given matching data at the internal location.
	 * Matching saved as a json map
	 * @param matching - The data to save
	 * @throws FileNotFoundException Thrown if occured when saving data
	 */
	public static void saveMatching(HashMap<String, String> matching) throws FileNotFoundException {
		JSONObject j = new JSONObject(matching);
		
		PrintWriter pr = new PrintWriter(new File("matching.json"));
		pr.write(j.toString());
		pr.close();
	}
	
	/**
	 * Loads the given matching at the given location
	 * @param matchingFile - location of matching file to load - file should be a json map
	 * @return Hashmap of students to supervisors
	 * @throws IOException thrown if occurred when reading matching file
	 */
	public static HashMap<String,String> loadMatching(String matchingFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(matchingFile));
		
		String temp;
		StringBuilder data = new StringBuilder();
		
		
		while ((temp = br.readLine()) != null) {
			data.append(temp);
		}
		
		JSONObject o = new JSONObject(data.toString());
		
		Map<String,Object> temp2 = o.toMap();
		HashMap<String,String> toReturn = new HashMap<String,String>();
		for (String key : temp2.keySet()) {
			toReturn.put(key, (String) temp2.get(key));
		}
		br.close();
		return toReturn;
	}
	
	/**
	 * Loads the given matching at the internal location
	 * @return Hashmap of students to supervisors
	 * @throws IOException thrown if occurred when reading matching file
	 */
	public static HashMap<String,String> loadMatching() throws IOException {
		return loadMatching("matching.json");
	}
}
