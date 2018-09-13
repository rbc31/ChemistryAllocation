package dataManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.json.JSONException;

import Exceptions.ConfigNotValidException;
import Exceptions.CustomValidationException;
import Exceptions.InvalidTypeException;
import exceptions.ColumnNotFoundException;
import exceptions.IllegalFormatException;
import exceptions.InvalidTableFormatException;
import exceptions.SupervisorNotFoundException;
import exceptions.UnexpectedException;
import main.Table;
import utils.GetStackTrace;
/**
 * Supervisor class, holds all the supervisor data needed in the program <br>
 * 
 * Controlled so that only one instance is accessible at one time
 * 
 * @author Robert Cobb <br>
 * Bath University<br>
 * Email: rbc31@bath.ac.uk
 * 
 */
public class Supervisors {
	
	/**
	 * Logger for this class
	 */
	private static Logger logger = Logger.getLogger(Supervisors.class.getName());
	
	/**
	 * Column index for the <b>unique</b> supervisor name <br>
	 */
	private int NAME_COLUMN;
	
	/**
	 * Column index for the Capacity column
	 */
	private int CAPACITY_COLUMN;
	
	/**
	 * Column index for the topic column
	 */
	private int TOPIC_COLUMN;
	
	/**
	 * List of keywords columns 
	 */
	private int[] KEYWORD_COLUMNS;
	
	/**
	 * Holds the supervisor data in a tale
	 */
	private Table data;
	
	/**
	 * Static field, holding the instance of the Supervisors
	 * only one instance is allowed, this field holds the last
	 * constructed instance
	 */
	private static Supervisors instance;
	
	/**
	 * Constructs and loads the Supervisor object.
	 * @throws FileNotFoundException Thrown if supervisor file was not found
	 * @throws IOException Thrown if occurred when reading from file
	 * @throws InvalidTableFormatException Thrown if supervisor file is incorrectly formated
	 * @throws ConfigNotValidException Thrown if the config file is not valid
	 * @throws IllegalFormatException Thrown if the config file data does not correspond to correct columns in the supervisor spreadsheet
	 * @throws UnexpectedException          Thrown if internal data is inconsistent
	 */
	public Supervisors() throws FileNotFoundException, IOException, InvalidTableFormatException, ConfigNotValidException, IllegalFormatException, UnexpectedException {		
		String location = null;

		
		try {
			Config config = Config.getConfig();
			location			= config.getStringValue(Config.SUPERVISOR_INPUT_FILE);
			NAME_COLUMN 		= config.getIntValue(Config.SUPERVISOR_NAME_COL);
			CAPACITY_COLUMN 	= config.getIntValue(Config.SUPERVISOR_CAPACITY_COL);
			TOPIC_COLUMN 		= config.getIntValue(Config.SUPERVISOR_TOPIC_COL);
			KEYWORD_COLUMNS 	= config.getIntListValue(Config.SUPERVISOR_KEYWORD_COLUMNS);
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
		validateColumns();
		nameIsUnique();
	}
	
	/**
	 * Constructs and loads the Supervisor object.
	 * Setting the static instance field to this instance created
	 * @param filePath  - The supervisor file
	 * @param nameCol - The name column in the supervisor file
	 * @param capacityCol - The capacity column in the supervisor file
	 * @param topicCol - The topic column in the supervisor file
	 * @param keywords - The keyword columns in the supervisor file
	 * @throws FileNotFoundException Thrown if supervisor file was not found
	 * @throws IOException Thrown if occurred when reading from file
	 * @throws InvalidTableFormatException Thrown if supervisor file is incorrectly formated
	 * @throws IllegalFormatException Thrown if the given data does not correspond to correct columns in the supervisor spreadsheet
	 * @throws UnexpectedException          Thrown if internal data is inconsistent
	 */
	public Supervisors(String filePath, int nameCol, int capacityCol, int topicCol, int[] keywords) throws IllegalFormatException, FileNotFoundException, InvalidTableFormatException, IOException, UnexpectedException {
		loadFile(filePath);
		
		this.NAME_COLUMN 		= nameCol;
		this.CAPACITY_COLUMN  	= capacityCol;
		this.TOPIC_COLUMN		= topicCol;
		this.KEYWORD_COLUMNS 	= keywords;
		
		validateColumns();
		nameIsUnique();
	}
	
	/**
	 * Clears the current static instance
	 */
	public static void clearInstance() {
		instance = null;
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
			String message = "The supervisor " + columnName + " column index <" + columnIndex + "> is not valid for table with " + colCount + " column(s)";
			logger.warning(message);
			throw new IllegalFormatException(message);
		}
	}
	
	/**
	 * Validates that the {@link #data} table contains the required columns
	 * <ul>
	 * <li> name column </li>
	 * <li> capacity column </li>
	 * <li> topic area column </li>
	 * <li> keywords columns </li>
	 * </ul>
	 * @throws IllegalFormatException Thrown if the one of the column indexes above is not valid
	 * @throws UnexpectedException Thrown if internal unexpected error occurs
	 */
	private void validateColumns() throws IllegalFormatException, UnexpectedException {
		validColumn("name",NAME_COLUMN);
		validColumn("capacity",CAPACITY_COLUMN);
		validColumn("topic area",TOPIC_COLUMN);
		
		for (int i=0;i<KEYWORD_COLUMNS.length;i++) {
			validColumn("keyword column "+(i+1),KEYWORD_COLUMNS[i]);
		}
		
		try {
			ArrayList<String> capacityCol = this.data.getColumn(CAPACITY_COLUMN);
		
			for (int i=0;i<capacityCol.size();i++) {
				try {
					Integer.parseInt(capacityCol.get(i));
				}catch (NumberFormatException e) {
					logger.severe("Encountered a NumberFormatException when checking capacity column");
					throw new IllegalFormatException("Capacity column could not be interpreted as an integer error at value " + capacityCol.get(i));
				}
			}
			
		}catch (ColumnNotFoundException e) {
			//never reached
			logger.severe("Encountered a ColumnNotFoundException when checking capacity column. Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new UnexpectedException(e);
		}
	}

	/**
	 * Loads the given file as the supervisor file populating the data property of this object
	 * @param filePath - The file to load
	 * @throws FileNotFoundException Thrown if the given file does not exist
	 * @throws InvalidTableFormatException Thrown if the given file is in an invalid format
	 * @throws IOException - Thrown if occurred when reading from file
	 */
	private void loadFile(String filePath) throws FileNotFoundException, InvalidTableFormatException, IOException {
		try {
			this.data = Table.parseTableFromCSVFile(new File(filePath));
		} catch (FileNotFoundException e) {
			logger.severe("Encountered a FileNotFoundException whilst trying to load supervisor data with message: "+e.getMessage());
			throw new FileNotFoundException("The Supervisor file was not found at location: \""+filePath+"\"");
		} catch (IOException e) {
			logger.severe("Encountered a IOException whilst trying to load supervisor data" + e.getMessage());
			throw new IOException("Error when attempting to read supervisor file, error message: " + e.getMessage());
		} catch (InvalidTableFormatException e) {
			logger.severe("Encountered a InvalidTableFormatException whilst trying to load supervisor data, error message: "+e.getMessage());
			throw new InvalidTableFormatException("Supervisor table formatted incorrectly. error message: "+e.getMessage());
		}
	}
	
	/**
	 * Throws IllegalFormatException if name column is not unique
	 * @throws IllegalFormatException If name column is not unique
	 * @throws UnexpectedException Thrown if internal data is inconsistent
	 */
	private void nameIsUnique() throws IllegalFormatException, UnexpectedException {
		try {
			if (!this.data.colIsUniqueKey(NAME_COLUMN)) {
				throw new IllegalFormatException("Supervisor name column is not unqiue. Column index: " + NAME_COLUMN);
			}
		} catch (ColumnNotFoundException e) {
			/* never reached */
			logger.severe("ColumnNotFoundException occurred when checking name is unique. Stack trace:" + GetStackTrace.getStackTrace(e));
			throw new UnexpectedException(e);
		}
	}
	
	/**
	 * returns the latest instance of the supervisors object,
	 * creates an object if one does not exist
	 * @return The supervisor object, holding all supervisor related data
	 * @throws FileNotFoundException Thrown if supervisor file was not found
	 * @throws IOException Thrown if occurred when reading from file
	 * @throws InvalidTableFormatException Thrown if supervisor file is incorrectly formated
	 * @throws ConfigNotValidException Thrown if the config file is not valid
	 * @throws IllegalFormatException Thrown if the config file data does not correspond to correct columns in the supervisor spreadsheet
	 * @throws UnexpectedException          Thrown if internal data is inconsistent
	 */
	public static Supervisors getInstance() throws FileNotFoundException, IOException, InvalidTableFormatException, ConfigNotValidException, IllegalFormatException, UnexpectedException {
		if (Supervisors.instance == null) {
			Supervisors.instance = new Supervisors();
		}
		return Supervisors.instance;
	}
	
	/**
	 * Returns the supervisor capacity of a given supervisor
	 * @param supervisor - the index of the supervisor in the underlying spreadsheet
	 * @return the integer capacity of the supervisor
	 * @throws NumberFormatException Thrown if the underlying file does not have 
	 * an integer value as expected
	 * @throws SupervisorNotFoundException Thrown if supervisor index is not valid
	 */
	public int getSupervisorCapcity(int supervisor) throws NumberFormatException, SupervisorNotFoundException {
		try {
			return Integer.parseInt(this.data.getValue(supervisor, CAPACITY_COLUMN));
		}catch (NumberFormatException e) {
			//should never be reached
			logger.warning("Supervisor at <"+supervisor+"> does not have an integer capacity assosiated with them, "
					+ "is the file formatted correctly? [cell ("+supervisor+","+CAPACITY_COLUMN+") should be an integer]");
			
			throw new NumberFormatException("Supervisor at <"+supervisor+"> does not have an integer capacity assosiated with them, "
					+ "is the file formatted correctly? [cell ("+supervisor+","+CAPACITY_COLUMN+") should be an integer]");
		}catch (IndexOutOfBoundsException e) {
			throw new SupervisorNotFoundException(supervisor);
		}
	}
	
	/**
	 * Returns the supervisor capacity of a given supervisor
	 * @param supervisor - the name of the supervisor in the underlying spreadsheet
	 * @return the integer capacity of the supervisor
	 * @throws NumberFormatException Thrown if the underlying file does not have 
	 * an integer value as expected
	 * @throws SupervisorNotFoundException  Thrown if supervisor index is not valid
	 */
	public int getSupervisorCapcity(String supervisor) throws NumberFormatException, SupervisorNotFoundException  {
		return getSupervisorCapcity(getIndex(supervisor));
	}
	
	/**
	 * Gets the name of the supervisor at the given index
	 * @param n - the index of the supervisor to get the name of
	 * @return The name of the supervisor
	 * @throws SupervisorNotFoundException Thrown if supervisor does not exist
	 */
	public String getSupervisorName(int n) throws SupervisorNotFoundException {
		try {
			return data.getValue(n, NAME_COLUMN);
		}catch (IndexOutOfBoundsException e) {
			logger.severe("Supervisor index: <" + n + "> was not found");
			throw new SupervisorNotFoundException(n);
		}
	}
	
	/**
	 * Gets the topic the given supervisor is associated with
	 * @param supervisor - The index of the supervisor to get the topic of
	 * @return The topic associated to the supervisor
	 * @throws SupervisorNotFoundException Thrown if supervisor does not exist
	 */
	public String getSupervisorTopic(int supervisor) throws SupervisorNotFoundException {
		try {
			return this.data.getValue(supervisor, TOPIC_COLUMN);
		}catch (IndexOutOfBoundsException e) {
			logger.severe("Supervisor index: \""+supervisor+ "\" was not found");
			throw new SupervisorNotFoundException(supervisor);
		}
	}
	
	/**
	 * Gets the topic the given supervisor is associated with
	 * @param supervisor - The name of the supervisor to get the topic of
	 * @return The topic associated to the supervisor
	 * @throws SupervisorNotFoundException Thrown if supervisor doesnt exist
	 */
	public String getSupervisorTopic(String supervisor) throws SupervisorNotFoundException {
		return getSupervisorTopic(getIndex(supervisor));
	}
	
	/**
	 * Returns the number of supervisors in the spreadsheet
	 * @return The number of supervisors in the spreadsheet
	 */
	public int size() {
		return data.size();
	}
	
	/**
	 * Forces a reload of the Supervisor object, will also return the Supervisor object reloaded
	 * Note: Any references to Supervisors before this call will be out dated AFTER this call
	 * @return The Supervisor object loaded
	 * @throws FileNotFoundException Thrown if supervisor file was not found
	 * @throws IOException Thrown if occurred when reading from file
	 * @throws InvalidTableFormatException Thrown if supervisor file is incorrectly formated
	 * @throws ConfigNotValidException Thrown if the config file is not valid
	 * @throws IllegalFormatException Thrown if the config file data does not correspond to correct columns in the supervisor spreadsheet
	 * @throws UnexpectedException          Thrown if internal data is inconsistent
	 */
	public static Supervisors forceLoad() throws FileNotFoundException, IOException, InvalidTableFormatException, ConfigNotValidException, IllegalFormatException, UnexpectedException {
		instance = new Supervisors();
		return instance;
	}

	/**
	 * Returns a copy of the supervisor data.
	 * Data returned can be modified without affecting internal data
	 * @return A copy of the supervisor data
	 */
	public Table getData() {
		return this.data.copy();
	}

	/**
	 * Gets the index of the given supervisor
	 * @param supervisor - The supervisor name to get the index of
	 * @return The index of the given supervisor
	 * @throws SupervisorNotFoundException Thrown if supervisor does not exist
	 */
	public int getIndex(String supervisor) throws SupervisorNotFoundException {
		int value = this.data.getRowIndex(NAME_COLUMN, supervisor, false);
		if (value == -1 ) {
			logger.severe("Supervisor index: \""+supervisor+ "\" was not found");
			throw new SupervisorNotFoundException(supervisor);
		}
		return value;
	}
	
	/**
	 * Returns the number of keywords each supervisor has
	 * @return The number of keywords each supervisor has
	 */
	public int getNumberOfKeywords() {
		return this.KEYWORD_COLUMNS.length;
	}
	
	/**
	 * Gets the keywords of a given supervisor
	 * @param supervisor - The index of the supervisor to get the keywords of
	 * @return String array of keywords
	 * @throws SupervisorNotFoundException Thrown if supervisor was not found
	 */
	public String [] getKeywords(int supervisor) throws SupervisorNotFoundException {
		try {
			int len = getNumberOfKeywords();
			
			String[] toReturn = new String[len];
			
			for (int i=0;i<len;i++) {
				toReturn[i] = this.data.getValue(supervisor, this.KEYWORD_COLUMNS[i]);
			}
			
			return toReturn;
		}catch (IndexOutOfBoundsException e) {
			logger.severe("Supervisor index: \""+supervisor+ "\" was not found");
			throw new SupervisorNotFoundException(supervisor);
		}
	}
	
	/**
	 * Gets the keywords of a given supervisor
	 * @param supervisor - The name of the supervisor to get the keywords of
	 * @return String array of keywords
	 * @throws SupervisorNotFoundException Thrown if supervisor was not found
	 */
	public String [] getKeywords(String supervisor) throws SupervisorNotFoundException {
		return getKeywords(this.getIndex(supervisor));
	}
}
