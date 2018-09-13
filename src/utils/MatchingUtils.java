package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.json.JSONException;

import Exceptions.ConfigNotValidException;
import Exceptions.CustomValidationException;
import Exceptions.InvalidTypeException;
import dataManager.Config;
import dataManager.Students;
import dataManager.Supervisors;
import exceptions.StudentNotFoundException;
import exceptions.SupervisorNotFoundException;
import exceptions.UnexpectedException;
/**
 * Class holding various utilities for matching
 * @author Robert Cobb
 * University of Bath
 *
 */
public class MatchingUtils {

	/**
	 * the logger for this class
	 */
	private static Logger logger = Logger.getLogger(MatchingUtils.class.getName());
	
	/**
	 * Returns the input list without any duplicates
	 * case is ignored
	 * @param input A list of string to remove duplicates from
	 * @return The input list with duplicates removed
	 * If the input contains the same string with different case
	 * only the first occurrence will be returned in the list
	 */
	public static String[] removeDuplicates(String [] input) {
		ArrayList<String> toReturn = new ArrayList<String>(input.length);
		
		for (String toAdd : input) {
			boolean duplicate = false;
			for (String element : toReturn) {
				if (element.equalsIgnoreCase(toAdd)) {
					duplicate = true;
				}
			}
			if (!duplicate) {
				toReturn.add(toAdd);
			}
		}
		String [] toReturnActually = new String[toReturn.size()];
		toReturn.toArray(toReturnActually);	
		return toReturnActually;
	}
	
	/**
	 * Gets the number of keywords in common between the given arrays
	 * Duplicates are ignored, case is ignored
	 * @param set1 - The list of words in set 1
	 * @param set2 - The list of words in set 2
	 * @return The number of keywords in common between the 2 sets
	 */
	public static int getCardinalityOfConjunctionOfSet(String[] set1, String[] set2) {
		int incommon = 0;
		set1 = removeDuplicates(set1);
		for (int i=0;i<set1.length;i++) {
			for (int j=0;j<set2.length;j++) {
				if (set1[i].equalsIgnoreCase(set2[j])) {
					incommon +=1;
				}
			}
		}
		return incommon;
	}
	
	/**
	 * Gets the flow between the given student and given supervisor.
	 * will return either 1 or 0.
	 * 1 if the student can be allocated to the given supervisor
	 * otherwise 0
	 * @param students - The students object
	 * @param supervisors - The supervisors object
	 * @param student - The username of the student to get the flow between
	 * @param supervisor - The name of the supervisor to get the flow between
	 * @return 1 if the student can be allocated to the given supervisor
	 * otherwise 0
	 * @throws SupervisorNotFoundException Thrown if the given supervisor was not found in the given supervisor object
	 * @throws StudentNotFoundException Thrown if the given student was not found in the given student object
	 * @throws ConfigNotValidException Thrown if the config is not valid
	 */
	public static int getFlowBetween(Students students, Supervisors supervisors, String student, String supervisor) throws SupervisorNotFoundException, StudentNotFoundException, ConfigNotValidException {
		
		try {
			// 1 - Has the student picked the supervisor? if so return 1
			
			for (int i=0;i<students.getNumOfPreferenceChoice();i++) {
				if (students.getChoice(student, i).equalsIgnoreCase(supervisor)) {
					return 1;
				}
			}
			
			// 2 - Is topic area enabled and is the supervisor in the given topic
			
			if (Config.getConfig().getBooleanValue(Config.MATCHING_ENABLE_TOPIC_AREA_ALLOCATION)) {
				String supervisorTopic = supervisors.getSupervisorTopic(supervisor);
				
				String [] topicChoices = students.getTopicAreaChoices(student);
				for(String topicChoice:topicChoices) {
					if (topicChoice.equalsIgnoreCase(supervisorTopic)) {
						return 1;
					}
				}
			}
			
			
			// Is keyword allocation enabled and is the conjunction of the 2 keywords sets > the threshold
			
			if (Config.getConfig().getBooleanValue(Config.MATCHING_ENABLE_KEYWORD_ALLOCATION)) {
				String[] studentKeywords = students.getKeywords(student);
				String[] supervisorKeywords = supervisors.getKeywords(supervisor);
				
				int incommon = getCardinalityOfConjunctionOfSet(studentKeywords, supervisorKeywords);
				
				if (incommon > Config.getConfig().getIntValue(Config.MATCHING_KEYWORD_LOWER_BOUND_TO_ALLOCATE)) {
					return 1;
				}
			}
			
			return 0;
		}catch (CustomValidationException | ConfigNotValidException | JSONException | IOException | InvalidTypeException e) {
			logger.severe("Encountered an unexpected exception when getting the flow between " + student + " and " + supervisor);
			logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new ConfigNotValidException(e);
		}
	}
	
	
	/**
	 * Gets the cost between the given student and given supervisor under the matching rules in the config
	 * @param students - The students object
	 * @param supervisors - The supervisors object
	 * @param student - The username of the student to get the flow between
	 * @param supervisor - The name of the supervisor to get the flow between
	 * @return 1 if the student can be allocated to the given supervisor
	 * otherwise 0
	 * @throws SupervisorNotFoundException Thrown if the given supervisor was not found in the given supervisor object
	 * @throws StudentNotFoundException Thrown if the given student was not found in the given student object
	 * @throws ConfigNotValidException Thrown if the config is not valid
	 */
	public static int getCostBetween(Students students, Supervisors supervisors, String student, String supervisor) throws ConfigNotValidException, StudentNotFoundException, SupervisorNotFoundException {
		
		try {
			int curr = 0;
		
			
			// curr += preference weight
			boolean picked = false;
			for (int i=0;i<students.getNumOfPreferenceChoice();i++) {
				if (students.getChoice(student, i).equalsIgnoreCase(supervisor)) {
					picked = true;
					curr += Config.getConfig().getIntListValue(Config.MATCHING_CHOICE_PREFERENCE_WEIGHTS)[i];
				}
			}
			
			if (!picked) {
				curr += Config.getConfig().getIntValue(Config.MATCHING_NO_MATCH_WEIGHT);
			}
			
			
			// curr += topic area weight
			if (Config.getConfig().getBooleanValue(Config.MATCHING_ENABLE_TOPIC_AREA_ALLOCATION)) {
				 
				 picked = false;
				 String [] topicAreas = students.getTopicAreaChoices(student);
				 
				 for (int i=0;i<topicAreas.length;i++) {
					 if (topicAreas[i].equalsIgnoreCase(supervisors.getSupervisorTopic(supervisor)) ) {
						 picked = true;
						 curr += Config.getConfig().getIntListValue(Config.MATCHING_TOPIC_AREA_PREFERENCE_WEIGHTS)[i];
					 }
				 }
				 
				 if (!picked) {
					 curr+= Config.getConfig().getIntValue(Config.MATCHING_NO_TOPIC_AREA_MATCH_WEIGHT);
				 }
				 
			}
			
			// curr += keyword allocation weight 
			if (Config.getConfig().getBooleanValue(Config.MATCHING_ENABLE_KEYWORD_ALLOCATION)) {
				 
				 picked = false;
				 String[] studentKeywords = students.getKeywords(student);
				 String[] supervisorKeywords = supervisors.getKeywords(supervisor);
				
				 int incommon = getCardinalityOfConjunctionOfSet(studentKeywords, supervisorKeywords);
					 
				 if (incommon > Config.getConfig().getIntValue(Config.MATCHING_KEYWORD_LOWER_BOUND_TO_ALLOCATE)) {
					 picked = true;
					 curr += Config.getConfig().getIntListValue(Config.MATCHING_KEYWORDS_PREFERENCE_WEIGHTS)[incommon-1];		
				 }
				 
				 if (!picked) {
					 curr+= Config.getConfig().getIntValue(Config.MATCHING_NO_KEYWORDS_IN_COMMON_WEIGHT);
				 }
			}
	
			return curr;
		
		}catch (CustomValidationException | ConfigNotValidException | JSONException | IOException | InvalidTypeException e) {
			logger.severe("Encountered an exception when getting the flow between " + student + " and " + supervisor);
			logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new ConfigNotValidException(e);
		}
	}
	
	/**
	 * Rounds the given number two 2 decimal places
	 * @param number - The number to round
	 * @return the number rounded to 2 decimal places
	 */
	private static double round2dp(double number) { 
		return (int) Math.round((double) number*100)/100.0;
	}
	
	/**
	 * Creates a human readable summary of the given matching
	 * @param matching - The hash map matching of student usrenames to supervisor names
	 * @param students - The students object of the students object
	 * @param supervisors - The supervisors object of the superviors object
	 * @param TOPIC_AREAS String list of topic areas
	 * @return Returns a string representation of the matching
	 * @throws UnexpectedException Thrown if data is inconsistent
	 */
	public static String evaulateMatching(HashMap<String,String> matching,Students students, Supervisors supervisors, String[] TOPIC_AREAS) throws UnexpectedException {
		StringBuilder str = new StringBuilder();
		
		int numOfStudents = matching.keySet().size();
		double percentageAllocated = round2dp((double)(numOfStudents*100)/students.size());
		
		str.append("Number of students allocated: "+numOfStudents + " ("+percentageAllocated+ "%)");
		str.append('\n');
		str.append("Number of students not allocated: "+(students.size()-numOfStudents) + " ("+round2dp(100-percentageAllocated)+"%)");
		str.append('\n');
		str.append('\n');
		
		int[] choices = getChoiceStats(matching,students);
		
		str.append("Break down of student matches via choices:\n");
		for (int i=0;i<choices.length;i++) {
			str.append("           " +(i+1) + ": "+choices[i] + "  (" + round2dp((double)(choices[i]*100)/students.size()) +"%)\n");
		}
		str.append('\n');
		
		int numAlloctedToChoice = 0;
		for (int i=0;i<choices.length;i++) {
			numAlloctedToChoice += choices[i];
		}
		str.append("Number of people allocated by choice: " + numAlloctedToChoice);
		str.append('\n');
		str.append("Number of people allocated on keyword/topic area: " + (numOfStudents-numAlloctedToChoice)) ;
		str.append('\n');
		str.append('\n');
		str.append("Number of supervisors with no students: "+numOfNonMatchedSupervisors(matching,supervisors));
		str.append('\n');
		str.append('\n');
		
		int [] topicAreaStats = getTopicAreaStats(matching,supervisors, TOPIC_AREAS);
		
		str.append("Number of students in each topic area:\n");
		for (int i=0;i<TOPIC_AREAS.length;i++) {
			str.append("              "+TOPIC_AREAS[i] + ": "+topicAreaStats[i] + "  (" + round2dp((double)(topicAreaStats[i]*100)/(double)students.size()) + "%)\n");
		}
		
		return str.toString();
	}
	
	/**
	 * Gets the number of superviors with no matchings under the given matching
	 * @param matching - The matching hashmap of student usernames to supervisor names
	 * @param supervisors - The supervisor object that contains all the supervisor data
	 * @return The number of supervisors that were matched to no students.
	 * @throws UnexpectedException Thrown if data is inconsistent
	 */
	private static int numOfNonMatchedSupervisors(HashMap<String, String> matching, Supervisors supervisors) throws UnexpectedException {
		HashMap<String,Boolean> matched = new HashMap<String,Boolean>();
	
	
		for (String student: matching.keySet()) {
			matched.put(matching.get(student), true);
		}
		
		try {
			int noMatches = 0;
			for (int i=0;i<supervisors.size();i++) {
				String supervisor;
				
					supervisor = supervisors.getSupervisorName(i);
				
				
				if (matched.get(supervisor) == null) {
					noMatches++;
				}
			}
			return noMatches;
		} catch (SupervisorNotFoundException e) {
			logger.severe("SupervisorNotFoundException occurred when not expected.");
			logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new UnexpectedException(e);
		}
	}

	/**
	 * Gets the number of students allocated to each topic area ordered by the topic areas
	 * array
	 * @param matching - The matching hashmap
	 * @param supervisors - The supervisors object
	 * @param TOPIC_AREAS - The list of topic areas
	 * @return a list of students allocated in each topic area
	 * @throws UnexpectedException Thrown if a supervisor if not found - thread issue?
	 */
	private static int[] getTopicAreaStats(HashMap<String,String> matching, Supervisors supervisors, String [] TOPIC_AREAS) throws UnexpectedException {
		try {
			int [] toReturn = new int[TOPIC_AREAS.length];
		
		
			for (String student: matching.keySet()) {
				String supervisor = matching.get(student);
				
				String topic  = null;
				topic = supervisors.getSupervisorTopic(supervisor).toLowerCase();
				
				
				boolean found = false;
				for (int j=0;j<TOPIC_AREAS.length;j++) {
					if (topic.equals(TOPIC_AREAS[j])) {
						toReturn[j]++;
						found = true;
						break;
					}
				}
				
				if (!found) {
					logger.warning("Matcher: Warning: supervisor topic area <"+topic+"> not found");
				}
			}
			return toReturn;
		} catch (SupervisorNotFoundException e) {
			logger.severe("Encountered an unexpected SupervisorNotFoundException when in getTopicAreaStats");
			logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new UnexpectedException(e);
		}
	}
	

	/**
	 * Gets a list of the number of students who got each choice under the given matching
	 * if 10 students got their first choice array index [0] will be 10 <br>
	 * Note: a student that is not matched will not be represented in this array
	 * @param matching - The matching hashmap, each key value pair is a student username
	 * with a supervisor name
	 * @param students - The student object where the student data is kept
	 * @return A list of the number of students who got each choice under the given matching
	 * @throws UnexpectedException Thrown if data is inconsistent
	 */
	private static int[] getChoiceStats(HashMap<String,String> matching, Students students) throws UnexpectedException {
		int [] toReturn = new int [students.getNumOfPreferenceChoice()];
		try {
			for (String student : matching.keySet()) {
				for (int j=0;j<students.getNumOfPreferenceChoice();j++) {
					if (students.getChoice(student, j).equals(matching.get(student))) {
						toReturn[j]++;
						break;
					}
				}
			}
			return toReturn;
		}catch (StudentNotFoundException e) {
			logger.severe("StudentNotFoundException occurred when not expected.");
			logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new UnexpectedException(e);
		}
	}
}
