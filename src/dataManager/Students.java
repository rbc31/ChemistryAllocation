package dataManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.json.JSONException;

import Exceptions.ConfigNotValidException;
import Exceptions.CustomValidationException;
import Exceptions.InvalidTypeException;
import exceptions.ColumnNotFoundException;
import exceptions.IllegalFormatException;
import exceptions.InvalidTableFormatException;
import exceptions.StudentNotFoundException;
import exceptions.UnexpectedException;
import main.Table;
import utils.GetStackTrace;
/**
 * File describes the class to hold the student data and methods
 * that pertain to student manipulation and editing
 * 
 * @author Robert Cobb <br>
 * Bath University<br>
 * Email: rbc31@bath.ac.uk
 * 
 */
public class Students {
	
	/**
	 * Logger for this class
	 */
	private static Logger logger = Logger.getLogger(Students.class.getName());
	
	/**
	 * Column index for the <b>unique</b> student username <br>
	 */
	public int USERNAME_COLUMN;
	
	/**
	 * The choices column indexes
	 */
	public int [] CHOICE_COLUMNS;
	
	/**
	 * Column index for the course field
	 */
	public int COURSE_COLUMN;
	
	/**
	 * Column index for the students name field
	 */
	public int NAME_COLUMN;
	
	/**
	 * List of column indexes to the keywords
	 */
	public int[] KEYWORD_COLUMNS;
	
	/**
	 * List of column indexes to the topic areas
	 */
	public int[] TOPIC_AREA_COLUMNS;
	
	/**
	 * The student data
	 */
	private Table data;
	
	/**
	 * Static field, holding the instance of the Students
	 * only one instance is allowed, this field holds the last
	 * constructed instance
	 */
	private static Students instance;
	
	/**
	 * Units that are natural science students
	 */
	private String[] NAT_SCI_UNITS;
	
	/**
	 * Constructs and loads the Student object.
	 * @throws FileNotFoundException 		Thrown if student file or config file was not found
	 * @throws IOException 					Thrown if occurred when reading from file
	 * @throws InvalidTableFormatException 	Thrown if student file is incorrectly formated
	 * @throws ConfigNotValidException 		Thrown if the configuration is not valid
	 * @throws IllegalFormatException       Thrown if the configuration is columns don't point to accurate columns in the input file
	 * @throws UnexpectedException          Thrown if internal data is inconsistent
	 */
	private Students() throws FileNotFoundException, IOException, ConfigNotValidException, InvalidTableFormatException, IllegalFormatException, UnexpectedException {

		String location = null;
		
		try {
			Config config 		= Config.getConfig();
			location 			= config.getStringValue(Config.STUDENT_INPUT_FILE);
			USERNAME_COLUMN 	= config.getIntValue(Config.STUDENT_USERNAME_COLUMN);
			COURSE_COLUMN		= config.getIntValue(Config.STUDENT_COURSE_COL);
			NAME_COLUMN 		= config.getIntValue(Config.STUDENT_NAME_COL);
			KEYWORD_COLUMNS		= config.getIntListValue(Config.STUDENT_KEYWORD_COLUMNS);
			TOPIC_AREA_COLUMNS	= config.getIntListValue(Config.STUDENT_TOPIC_AREA_COLUMNS);
			CHOICE_COLUMNS      = config.getIntListValue(Config.STUDENT_PREFERENCE_COLUMNS);
			NAT_SCI_UNITS       = config.getStrListValue(Config.STUDENT_NAT_SCI_UNITS);
		} catch (JSONException e) {
			logger.severe("Encountered JSONException whilst trying to load chemistry project configuration. Stack Trace: " + GetStackTrace.getStackTrace(e));
			throw new ConfigNotValidException(e);
		} catch (FileNotFoundException e) {
			logger.severe("Encountered FileNotFound whilst trying to load chemistry project configuration. Stack Trace: " + GetStackTrace.getStackTrace(e));
			throw e;
		} catch (IOException e) {
			logger.severe("Encountered IOException whilst trying to load chemistry project configuration. Stack Trace: " + GetStackTrace.getStackTrace(e));
			throw e;
		} catch (InvalidTypeException e) {
			logger.severe("Encountered InvalidTypeException whilst trying to load chemistry project configuration. Stack Trace: " + GetStackTrace.getStackTrace(e));
			throw new ConfigNotValidException(e);
		} catch (ConfigNotValidException e) {
			logger.severe("Encountered ConfigNotValidException whilst trying to load chemistry project configuration. Stack Trace: " + GetStackTrace.getStackTrace(e));
			throw e;
		} catch (CustomValidationException e) {
			logger.severe("Encountered CustomValidationException whilst trying to load chemistry project configuration. Stack Trace: " + GetStackTrace.getStackTrace(e));
			throw new ConfigNotValidException(e);
		}
		
		loadFile(location);
		this.data.randomiseRows();
		validateColumns();
		usernameIsUnique();
	}
	
	
	/**
	 * Constructs and loads the Student object.
	 * @param filePath		- The filePath of the student file 
	 * @param usernameCol 	- The username column index
	 * @param nameCol 		- The name column index
	 * @param courseCol 	- The course column index
	 * @param keywords 		- The keyword column indexes
	 * @param topicArea 	- The topic area column indexes
	 * @param preferences 	- The preference column indexes
	 * @param natsci 		- The natural science strings
	 * @throws FileNotFoundException 		Thrown if student file or config file was not found
	 * @throws IOException 					Thrown if occurred when reading from file
	 * @throws InvalidTableFormatException 	Thrown if student file is incorrectly formated
	 * @throws IllegalFormatException       Thrown if the configuration is columns don't point to accurate columns in the input file
	 * @throws UnexpectedException          Thrown if internal data is inconsistent
	 */
	public Students(String filePath, int usernameCol, int nameCol, int courseCol, int[] keywords, int[] topicArea, int[] preferences, String[] natsci) throws FileNotFoundException, InvalidTableFormatException, IOException, IllegalFormatException, UnexpectedException {
		
		loadFile(filePath);
		
		this.USERNAME_COLUMN 	= usernameCol;
		this.COURSE_COLUMN		= courseCol;
		this.NAME_COLUMN 		= nameCol;
		this.KEYWORD_COLUMNS	= keywords;
		this.TOPIC_AREA_COLUMNS	= topicArea;
		this.CHOICE_COLUMNS     = preferences;
		this.NAT_SCI_UNITS      = natsci;
		
		validateColumns();
		usernameIsUnique();
		instance = this;
	}
	
	
	/**
	 * Loads the given file as the studentFile populating the data property of this object
	 * @param filePath - The file to load
	 * @throws FileNotFoundException Thrown if the given file does not exist
	 * @throws InvalidTableFormatException Thrown if the given file is in an invalid format
	 * @throws IOException - Thrown if occurred when reading from file
	 */
	private void loadFile(String filePath) throws FileNotFoundException, InvalidTableFormatException, IOException {
		try {
			this.data = Table.parseTableFromCSVFile(new File(filePath));
			//this.data.randomiseRows();
		} catch (FileNotFoundException e) {
			logger.severe("Encountered FileNotFoundException whilst trying to load student table");
			throw new FileNotFoundException("The Student file was not found at location: "+filePath);
		} catch (IOException e) {
			logger.severe("Encountered IOException whilst trying to load student table");
			throw new IOException("Error when attempting to read student file, error message: " + e.getMessage());
		} catch (InvalidTableFormatException e) {
			logger.severe("Encountered InvalidFormatException whilst trying to load student data");
			throw new InvalidTableFormatException("Error when attempting to read student file, error message: "+ e.getMessage());
		}
	}
	
	/**
	 * Validates that the given columnIndex points to a real column in the {@link #data} table
	 * @param columnName - The name of the column to be put in the logging and exception if column index not valid
	 * @param columnIndex - The index of the column to validate
	 * @throws IllegalFormatException - Thrown if the column index does not point to a valid column
	 * in the {@link #data} table
	 */
	private void validColumn(String columnName, int columnIndex) throws IllegalFormatException {
		int colCount = this.data.getColCount();
		if (columnIndex < 0 || columnIndex >= colCount) {
			String message = "The student <" + columnName + "> column index <" + columnIndex + "> is not valid for table with " + colCount + "columns";
			logger.warning(message);
			throw new IllegalFormatException(message);
		}
	}
	
	/**
	 * Validates that the {@link #data} table contains the required columns
	 * <ul>
	 * <li> username column </li>
	 * <li> course column </li>
	 * <li> name column </li>
	 * <li> keywords columns </li>
	 * <li> topic area columns </li>
	 * <li> choice columns </li>
	 * </ul>
	 * @throws IllegalFormatException Thrown if the one of the column indexes above is not valid
	 */
	private void validateColumns() throws IllegalFormatException {
		validColumn("username",USERNAME_COLUMN);
		validColumn("course",COURSE_COLUMN);
		validColumn("name",NAME_COLUMN);
		
		for (int i=0;i<KEYWORD_COLUMNS.length;i++) {
			validColumn("keyword column "+(i+1),KEYWORD_COLUMNS[i]);
		}
		
		for (int i=0;i<TOPIC_AREA_COLUMNS.length;i++) {
			validColumn("Topic area column "+(i+1),TOPIC_AREA_COLUMNS[i]);
		}
		
		for (int i=0;i<CHOICE_COLUMNS.length;i++) {
			validColumn("Choice column "+(i+1),CHOICE_COLUMNS[i]);
		}
	}
	
	/**
	 * Throws IllegalFormatException if username column is not unique
	 * @throws IllegalFormatException if username column is not unique
	 * @throws UnexpectedException Thrown if internal data is inconsistent
	 */
	private void usernameIsUnique() throws IllegalFormatException, UnexpectedException {
		try {
			if(!this.data.colIsUniqueKey(USERNAME_COLUMN)) {
				logger.warning("Student username is not unique");
				throw new IllegalFormatException("Student username coloumn, is not unique");
			}
		} catch (ColumnNotFoundException e) {
			/* never reached */
			logger.severe("ColumnNotFoundException occurred when checking username is unique. Stack trace:" + GetStackTrace.getStackTrace(e));
			throw new UnexpectedException(e);
		}
		
	}
	
	/**
	 * Returns the latest instance of the student object,
	 * creates an object if one does not exist
	 * @return The student object, holding all student related data
	 * @throws FileNotFoundException 		Thrown if student file or config file was not found
	 * @throws IOException 					Thrown if occurred when reading from file
	 * @throws InvalidTableFormatException 	Thrown if student file is incorrectly formated
	 * @throws ConfigNotValidException 		Thrown if the configuration is not valid
	 * @throws IllegalFormatException 		Thrown if the columns are not in the correct place
	 * @throws UnexpectedException          Thrown if internal data is inconsistent
	 */
	public static Students getInstance() throws FileNotFoundException, IllegalFormatException, IOException, ConfigNotValidException, InvalidTableFormatException, UnexpectedException  {
		if (instance == null) {
			instance = new Students();
		}
		return instance;
	}
	
	/**
	 * Clears the static instance of the students objecct.
	 */
	public static void clearInstance() {
		instance = null;
	}
	
	/**
	 * Gets the user name of the given student
	 * @param n - the index of the student to get the supervisor of
	 * @return Student user name
	 * @throws StudentNotFoundException  - Thrown is n isn't a valid index
	 */
	public String getUsername(int n) throws StudentNotFoundException {
		try {
			return this.data.getValue(n, USERNAME_COLUMN);
		}catch (IndexOutOfBoundsException  e) {
			logger.severe("Index out of bounds exception occured when getting username of student " + n);
			throw new StudentNotFoundException(n);
		}
	}
	
	/**
	 * Gets the index of the student with the given user name
	 * @param student - the user name of the student to get
	 * @return the index of the user name in the spreadsheet
	 * @throws StudentNotFoundException Thrown if student is not found
	 */
	public int getIndex(String student) throws StudentNotFoundException {
		int temp = this.data.getRowIndex(this.USERNAME_COLUMN, student, false);
		if (temp == -1) {
			logger.severe("Attempted to get the index of an unknwon username \""+student+"\"");
			throw new StudentNotFoundException(student);
		}else {
			return temp;
		}
	}
	
	/**
	 * Returns the number of students in the spreadsheet
	 * @return The number of students in the spreadsheet
	 */
	public int size() {
		return this.data.size();
	}
	
	/**
	 * Forces a reload of the Student object, will also return the Student object reloaded
	 * Note: Any references to Students before this call will be out dated AFTER this call
	 * @return The Student object loaded
	 * @throws FileNotFoundException 		Thrown if student file or config file was not found
	 * @throws IOException 					Thrown if occurred when reading from file
	 * @throws InvalidTableFormatException 	Thrown if student file is incorrectly formated
	 * @throws ConfigNotValidException 		Thrown if the configuration is not valid
	 * @throws IllegalFormatException 		Thrown if the columns are not in the correct place
	 * @throws UnexpectedException          Thrown if internal data is inconsistent
	 */
	public static Students forceLoad() throws FileNotFoundException, IllegalFormatException, IOException, ConfigNotValidException, InvalidTableFormatException, UnexpectedException {
		instance = new Students();
		return instance;
	}

	/**
	 * Gets the given students nth choice 
	 * Note: choices start a 0
	 * @param index - The student to get the choice of
	 * @param choice - The nth choice of the given student to get
	 * @return The nth choice of the given student
	 * @throws IllegalArgumentException Thrown if the choice is not valid
	 * @throws StudentNotFoundException Thrown if the student is not valid
	 */
	public String getChoice(int index, int choice) throws IllegalArgumentException, StudentNotFoundException {

		if (choice >= 0 && choice < this.CHOICE_COLUMNS.length) {
			
			if (this.size() > index && index >= 0) {
				return this.data.getValue(index, this.CHOICE_COLUMNS[choice]);
			}else {
				logger.severe(index + " is not a valid stuednt index");
				throw new StudentNotFoundException(index);
			}
		}else {
			logger.severe(choice + " is not a valid choice");
			throw new IllegalArgumentException(choice + " is not a valid choice");
		}

	}

	/**
	 * Gets the given students nth choice 
	 * Note: choices start a 0
	 * @param student - the username of the student to get the choice of
	 * @param choice - The nth choice of the given student to get
	 * @return The nth choice of the given student
	 * @throws StudentNotFoundException Thrown if the student does not exist
	 * @throws IllegalArgumentException Thrown if the choice is invalid
	 * or the student username is not present in the table
	 */
	public String getChoice(String student, int choice) throws IllegalArgumentException, StudentNotFoundException {
		return this.getChoice(this.getIndex(student),choice);
	}
	
	/**
	 * Gets a copy of the student data.<br>
	 * The data returned can be manipulated in any way without affecting the internal
	 * copy of the data
	 * @return - A copy of the student data
	 */
	public Table getData() {
		return this.data.copy();
	}

	/**
	 * Gets The name of the student as it is presented in the spreadsheet
	 * @param n - The index of the student to get the name of
	 * @return The name of the student
	 * @throws StudentNotFoundException Thrown if student is not valid
	 */
	public String getName(int n) throws StudentNotFoundException {
		try {
			return data.getValue(n, NAME_COLUMN);
		}catch (IndexOutOfBoundsException  e) {
			logger.severe("Index out of bounds exception occured when getting name of student "+n);
			throw new StudentNotFoundException(n);
		}
	}

	/**
	 * Returns true if the given student is a natural science student
	 * @param username - The username of the student to query
	 * @return true if the student is a natural science student
	 * @throws IndexOutOfBoundsException Thrown if username is not present in student table
	 * @throws StudentNotFoundException Thrown is student does not exist
	 */
	public boolean isNatSci(String username) throws StudentNotFoundException {
		return isNatSci(this.getIndex(username));
	}
	
	/**
	 * Returns true if the given student is a natural science student
	 * @param index - The index of the student to query
	 * @return true if the student is a natural science student
	 * @throws StudentNotFoundException Thrown if the student was not found
	 */
	public boolean isNatSci(int index) throws StudentNotFoundException {
		try {
			String course = data.getValue(index, COURSE_COLUMN).trim();
			for (int i =0;i<NAT_SCI_UNITS.length;i++) {
				if (course.equalsIgnoreCase(NAT_SCI_UNITS[i])) {
					return true;
				}
			}
			return false;
		}catch (IndexOutOfBoundsException  e) {
			logger.severe("Index out of bounds exception occured when getting course of student " + index);
			throw new StudentNotFoundException(index);
		}
	}
	
	/**
	 * Gets the number of keywords
	 * @return The number of keywords
	 */
	public int getNumberOfKeywords() {
		return this.KEYWORD_COLUMNS.length;
	}
	
	/**
	 * Gets the keywords of the given student
	 * @param student - The index of the student to get the keywords of
	 * @return String array of keywords
	 * @throws StudentNotFoundException Thrown if the student doesn't exist
	 */
	public String [] getKeywords(int student) throws StudentNotFoundException {
		try {
			int len = getNumberOfKeywords();
		
			String[] toReturn = new String[len];
			
			for (int i=0; i<len; i++) {
				toReturn[i] = this.data.getValue(student, this.KEYWORD_COLUMNS[i]);
			}
			
			return toReturn;
		}catch (IndexOutOfBoundsException e) {
			logger.severe("Student " + student + " was not found");
			throw new StudentNotFoundException(student);
		}
	}
	
	/**
	 * Gets the keywords of the given student
	 * @param student - The username of the student
	 * @return String array of keywords
	 * @throws StudentNotFoundException Thrown if the student doesn't exist
	 */
	public String [] getKeywords(String student) throws StudentNotFoundException {
		return getKeywords(this.getIndex(student));
	}

	/**
	 * Gets the number of topic area choices
	 * @return The number of topic area choices
	 */
	public int getNumberOfTopicChoices() {
		return this.TOPIC_AREA_COLUMNS.length;
	}
	
	/**
	 * Gets the number of preference choices
	 * @return The number of preference choices
	 */
	public int getNumOfPreferenceChoice() {
		return this.CHOICE_COLUMNS.length;
	}
	
	/**
	 * Gets the topic area choices of the student
	 * @param student - The index to get the topic area choices of
	 * @return The string list of topic area choices
	 * @throws StudentNotFoundException Thrown if the given student does not exist
	 */
	public String [] getTopicAreaChoices(int student) throws StudentNotFoundException {
		try {
			int len = getNumberOfTopicChoices();
			String[] toReturn = new String[len];
			
			for (int i=0;i<len;i++) {
				toReturn[i] = this.data.getValue(student, this.TOPIC_AREA_COLUMNS[i]);
			}
			
			return toReturn;
		}catch (IndexOutOfBoundsException e) {
			logger.severe("Student " + student + " was not found");
			throw new StudentNotFoundException(student);
		}
	}
	
	/**
	 * Gets the topic area choices of the student
	 * @param student - The username of the student to get the choices of
	 * @return The string list of topic area choices
	 * @throws StudentNotFoundException Thrown if the given student does not exist
	 */
	public String [] getTopicAreaChoices(String student) throws StudentNotFoundException {
		return getTopicAreaChoices(this.getIndex(student));
	}
	
	/**
	 * Gets The name of the student as it is presented in the spreadsheet
	 * @param student - The username of the student
	 * @return The name of the student
	 * @throws StudentNotFoundException Thrown if student does not exist
	 */
	public String getName(String student) throws StudentNotFoundException {
		return this.getName(this.getIndex(student));
	}

}
