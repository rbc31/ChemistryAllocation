package output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

import org.json.JSONException;

import Exceptions.ConfigNotValidException;
import Exceptions.CustomValidationException;
import Exceptions.InvalidTypeException;
import dataManager.Config;
import dataManager.Students;
import dataManager.Supervisors;
import exceptions.ColumnNotFoundException;
import exceptions.InvalidTableFormatException;
import exceptions.RowNotFoundException;
import exceptions.StudentNotFoundException;
import exceptions.SupervisorNotFoundException;
import exceptions.UnexpectedException;
import main.Table;
import utils.MatchingUtils;
import utils.ToString;
import utils.GetStackTrace;
/**
 * Output class with static methods to produce output from the matching
 * 
 * @author Robert Cobb <br>
 * Bath University<br>
 * Email: rbc31@bath.ac.uk
 */
public class Output {

	/**
	 * The logger for this class
	 */
	private static Logger logger = Logger.getLogger(Output.class.getName());
	
	/**
	 * Saves the student output to the given file location
	 * @param students - The student object that contains all the student data
	 * @param supervisors - The supervisors object that contains all the supervisor data
	 * @param matching - The Hashmap matching of student usernames to supervisor names
	 * @param fileExtension - The file location to save the output to
	 * @throws ConfigNotValidException - Thrown if the config is not valid
	 * @throws UnexpectedException - Thrown if an unexpected error occurs
	 * @throws FileNotFoundException - Thrown if file to save could not be saved 
	*/
	public static void saveStudentOutput(Students students, Supervisors supervisors, HashMap<String,String> matching,
			String fileExtension) throws  ConfigNotValidException, UnexpectedException, FileNotFoundException {
		
		Config config = null;
		try {
			config = Config.getConfig();
		} catch (JSONException | CustomValidationException | InvalidTypeException | IOException  e) {
			logger.severe("Encountered an Exception whilst trying to load config. Stack trace:" + GetStackTrace.getStackTrace(e));
			throw new ConfigNotValidException(e);
		}
		
		logger.info("Saving student output");
		
		//create the table to save, based of the old students table
		Table tbl_students = students.getData();
		tbl_students.addColumn("Matched supervisor","No Matching");
		tbl_students.addColumn("Matched Reason","-");
		int col =-1;
		int reasonCol;
		try {
			col = tbl_students.getColIndex("Matched supervisor", false);
			reasonCol = tbl_students.getColIndex("Matched Reason", false); 
		} catch (ColumnNotFoundException e) {
			String message = "ColumnNotFoundException encountered when trying to get column index of column just added! (Matched supervisor)";
			logger.severe(message + " Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new UnexpectedException(message);
		}
		try {
			//add each matching to the table
			for (String student : matching.keySet()) {
				int studentIndex = students.getIndex(student);
				
				if (studentIndex != -1) {
					tbl_students.setValue(studentIndex, col, matching.get(student));
					tbl_students.setValue(studentIndex, reasonCol, getMatchingReason(students, supervisors, student, matching.get(student), config));
				}else {
					logger.severe("Student in matching not found in student table when creating student output \""+student+"\"");
					throw new IllegalArgumentException("Student "+student+" not found in student table");
				}
			}
			logger.info("Saving student output to file");
			tbl_students = sortTable(tbl_students,config.getIntValue(Config.STUDENT_USERNAME_COLUMN));
			tbl_students.saveToCSVFile(new File(fileExtension));
		}catch (StudentNotFoundException | SupervisorNotFoundException e) {
			logger.severe("An entity was unexpectedly not found. Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new UnexpectedException(e);
		}
	}

	
	/**
	 * Returns the choice rank with which the given student chose the given supervisor.
	 * If the student chose the supervisor multiple times, the first rank is picked.
	 * @param students - The student object
	 * @param student - The username of the student
	 * @param supervisor - The name of the supervisor
	 * @return The index with which the given student chose the supervisor or -1 if the 
	 * student did not pick the supervisor
	 * @throws StudentNotFoundException Thrown if the given student is not found
	 */
	public static int getChoiceIndex(Students students, String student, String supervisor) throws StudentNotFoundException {
		for (int i=0;i<students.getNumOfPreferenceChoice();i++) {
			if (supervisor.equals(students.getChoice(student, i))) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Gets the number of keywords the given student has in common with the given supervisor
	 * as long as the number in common is greater than the matching threshold
	 * @param students - The students object
	 * @param supervisors - The supervisor object
	 * @param student - The username of the student to compare
	 * @param supervisor - The name of the given supervisor to compare
	 * @param config  The config object
	 * @return The number of keywords in common between the given student and the given supervisor
	 * if the number of keywords in common is greater than the matching lower bound constant
	 * @throws StudentNotFoundException Thrown if the given student is not found in the students object
	 * @throws SupervisorNotFoundException Thrown if the given supervisor is not found in the supervisors object
	 */
	public static int getKeyWorkdsInCommon(Students students, Supervisors supervisors, String student, String supervisor, Config config) throws StudentNotFoundException, SupervisorNotFoundException {
		
		if (config.getBooleanValue(Config.MATCHING_ENABLE_KEYWORD_ALLOCATION)) {
			
			int incommon = MatchingUtils.getCardinalityOfConjunctionOfSet(students.getKeywords(student), supervisors.getKeywords(supervisor));
			
			if (incommon > config.getIntValue(Config.MATCHING_KEYWORD_LOWER_BOUND_TO_ALLOCATE)) {
				return incommon;
			}else {
				return -1;
			}
		}else {
			return -1;
		}
	}
	
	/**
	 * Gets the topic area rank of the given student and superivsor.
	 * e.g. If the given supervisor topic area is the students first choice for topic area
	 * 1 will be returned.
	 * @param students - The students object
	 * @param supervisors - The supervisors object
	 * @param student - The username of the student to get the rank choice from
	 * @param supervisor - The name of the supervisor to get the rank choice of
	 * @param config - The config object
	 * @return The rank order of the topic area of the given supervisor in relation to the given 
	 * student. -1 is returned if the supervisors topic area is not among the students topic area choices
	 * @throws SupervisorNotFoundException Thrown if the given supervisor is not found in the given supervisor object
	 * @throws StudentNotFoundException Throw if the given student is not found in the give student object
	 */
	public static int getTopicAreaRank(Students students, Supervisors supervisors, String student, String supervisor, Config config) throws SupervisorNotFoundException, StudentNotFoundException {
		if (config.getBooleanValue(Config.MATCHING_ENABLE_TOPIC_AREA_ALLOCATION)) {
			String supervisorTopic = supervisors.getSupervisorTopic(supervisor);
			
			String [] topicChoices = students.getTopicAreaChoices(student);
			for(int i=0;i<topicChoices.length;i++) {
				if (topicChoices[i].equalsIgnoreCase(supervisorTopic)) {
					return i;
				}
			}
			return -1;
		}else {
			return -1;
		}
	}
	
	/**
	 * Sorts the given table into order based on the given column index
	 * @param input - The input table to sort
	 * @param columnIndex - The column index to sort on
	 * @return A sorted table 
	 * @throws UnexpectedException Thrown if internal error occurred
	 */
	public static Table sortTable(Table input, int columnIndex) throws UnexpectedException {
		Table toReturn = new Table(input.getHeaders());
		
		//take the biggest from the input table, remove it and add to output table
		while (input.size() > 0) {
			int smallestIndex = 0;
			String smallestValue = input.getValue(0, columnIndex);
			
			for (int i=0;i<input.size();i++) {
				String  value = input.getValue(i, columnIndex);
				
				if (smallestValue.compareTo(value) > 0) {
					smallestIndex = i;
					smallestValue = value;
				}
			}
			
			ArrayList<String> toAdd = new ArrayList<String>();
			
			for (int i=0;i<input.getColCount();i++) {
				toAdd.add(input.getValue(smallestIndex, i));
			}
			
			try {
				toReturn.addRecord(toAdd);
				input.removeRecord(smallestIndex);
			} catch (RowNotFoundException | InvalidTableFormatException | IllegalArgumentException e) {
				logger.severe("Unexpected Error encountered whilst trying to sort table. Stack Trace: " + GetStackTrace.getStackTrace(e));
				throw new UnexpectedException(e);
			}
		}
		return toReturn;
	}
	
	
	/**
	 * Returns a string description as to why the given student could have been allocated to the geiven supervisor
	 * @param students - The students object
	 * @param supervisors - The supervisors object
	 * @param student - The username of the student to get the reason for
	 * @param supervisor - The name of the supervisor to get the reason for
	 * @param config - The config object
	 * @return A string description detailing why the given student could have been allocated to the given supervisor
	 * @throws StudentNotFoundException Thrown if the given student was not found in the given student object
	 * @throws IllegalArgumentException Thrown if the given student could not be allocated to the given supervisor under the given config
	 * @throws SupervisorNotFoundException Thrown if the given supervisor was not found in the given supervisor object
	 */
	public static String getMatchingReason(Students students, Supervisors supervisors, String student, String supervisor, Config config) throws StudentNotFoundException, IllegalArgumentException, SupervisorNotFoundException {
		
		String toReturn = student + " was allocated to " + supervisor + " because ";

		int choiceIndex = getChoiceIndex(students, student, supervisor);
		int keywordsInCommon = getKeyWorkdsInCommon(students, supervisors, student, supervisor, config);
		int topicAreaRank   = getTopicAreaRank(students, supervisors, student, supervisor, config);
		
		if (choiceIndex != -1) { //student picked supervisor
			toReturn += student + " picked " + supervisor + " as their " + (choiceIndex+1) + ToString.getSuffix(choiceIndex+1) +" choice";
		
			if (keywordsInCommon != -1) {
				if (topicAreaRank != -1) { // allocation based on choice, keywords and topic area
					toReturn += ", they have " + keywordsInCommon + " keywords in common and " + 
							supervisor + " is in "  + student + "'s  " + (topicAreaRank+1) + 
							ToString.getSuffix(topicAreaRank+1) + " prefered topic area.";
				}else {// allocation based on choice and keywords
					toReturn += " and they have " + keywordsInCommon + " keywords in common.";
				}
			}else {
				if (topicAreaRank != -1) { // allocation based on topic area and choice
					toReturn += " and " + supervisor + " is in " + student + "'s  " + 
							(topicAreaRank+1) + ToString.getSuffix(topicAreaRank+1) + " prefered topic area.";
				}else {// allocation not on choice, topic area rank, or keywords???? that doesn't make sense
					toReturn += ".";
					return toReturn;
				}
			}
		}else { // student didn't pick supervisor
			if (keywordsInCommon != -1) {
				if (topicAreaRank != -1) {
					toReturn += "they have " + keywordsInCommon + " keywords in common and " + 
							supervisor + " is in "  + student + "'s  " + (topicAreaRank+1) + 
							ToString.getSuffix(topicAreaRank+1) + " prefered topic area.";
				}else {
					toReturn += "they have " + keywordsInCommon + " keywords in common.";
				}
			}else {
				if (topicAreaRank != -1) {
				toReturn += supervisor + " is in " + student + "'s  " + 
						(topicAreaRank+1) + ToString.getSuffix(topicAreaRank+1) + " prefered topic area.";
				}else {
					throw new IllegalArgumentException("The matching between " + student + " to " + supervisor + " is illegal");
				}
			}
		}
		return toReturn;		 
	}
	
	/**
	 * Saves the supervisor output to the given file
	 * @param supervisors - The supervisor object with all the supervisor data
	 * @param students - The student object that contains all the student data
	 * @param matching - The Hashmap matching of student usernames to supervisor names
	 * @param fileExtension - The file location to save the output to
	 * @throws FileNotFoundException Thrown if thrown when writing to file
	 * @throws UnexpectedException Thrown if unexpected error occurs
	 */
	public static void saveSupervisorOutput(Supervisors supervisors, Students students, 
			HashMap<String,String> matching, String fileExtension) throws FileNotFoundException, UnexpectedException {
		try {
			logger.info("Saving the Supervisor output");
		
			
			//create supervisor table off old table
			Table tbl_supervisors = supervisors.getData();
			
			int lastMaxMatch = 0;
			
			HashMap<String,Integer> numberOfMatchesSoFar = new HashMap<String,Integer>();
			
			for (int i=0;i<supervisors.size();i++) {
				numberOfMatchesSoFar.put(supervisors.getSupervisorName(i),0);
			}
			
			ArrayList<String> keys = new ArrayList<String>(matching.size());
			for (String username: matching.keySet()) {
				keys.add(username);
			}
			Collections.sort(keys);
			
			for (String username: keys) {
				int index     		= students.getIndex(username);
				String name 		= students.getName(index);
				String supervisor 	= matching.get(username);
				int supervisorIndex = supervisors.getIndex(supervisor);
				
				if (supervisorIndex == -1) {
					logger.severe("Supervisor in matching not present in supervisor data");
					throw new IllegalArgumentException("Supervisor in matching not present in supervisor data");
				}
				
				int matchesSoFar 	= -1;
				
				try {
					matchesSoFar = numberOfMatchesSoFar.get(supervisor);
				}catch (NullPointerException e) {
					logger.severe("Supervisor is matching was not present in supervisor data");
					throw new IllegalArgumentException("Supervisor in matching not present in supervisor data");
				}
				
				if (matchesSoFar+1>lastMaxMatch) {
					tbl_supervisors.addColumn("Matching "+(matchesSoFar+1), "-");
					lastMaxMatch++;
				}
				
				int colIndex = -1;
				try {
					colIndex = tbl_supervisors.getColIndex("Matching "+(matchesSoFar+1),false);
				} catch (ColumnNotFoundException e) {
					String message = "ColumnNotFoundException encountered when trying to get column index of collumn just added! (Matching "+(matchesSoFar+1)+")";
					logger.severe(message);
					throw new UnexpectedException(message);
				}
				tbl_supervisors.setValue(supervisorIndex, colIndex, name + " ("+username+')');
				
				
				//increment matching in hashmap
				Integer temp = numberOfMatchesSoFar.get(supervisor);
				int temp2 = temp.intValue();
				temp2++;
				numberOfMatchesSoFar.put(supervisor, temp2);
			}
			logger.info("Saving Supervisor output to file");
			tbl_supervisors.saveToCSVFile(new File(fileExtension));
		} catch (SupervisorNotFoundException e) {
			logger.severe("Encountered an unexpected SupervisorNotFoundException when in saveSupervisorOutput");
			logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new UnexpectedException(e);
		} catch (StudentNotFoundException e) {
			logger.severe("Encountered an unexpected StudentNotFoundException when in saveSupervisorOutput");
			logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new UnexpectedException(e);
		}
	}
}
