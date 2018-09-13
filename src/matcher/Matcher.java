package matcher;

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
import utils.GetStackTrace;
import utils.MatchingUtils;

/**
 * Class to perform all matching functions
 * 
 * @author Robert Cobb <br>
 * Bath University<br>
 * Email: rbc31@bath.ac.uk
 *
 */
public class Matcher {
		
	/**
	 * The logger for this class
	 */
	private static Logger logger = Logger.getLogger(Matcher.class.getName());
	
	/**
	 * Attempts to match the given set of students to the given set of superiors
	 * whilst adhering to supervisor and topic area hard constraints and maximising
	 * student preferences.
	 * 
	 * @param students - The student object that holds the student to match
	 * @param supervisors - The supervisor object that holds the supervisors 
	 * to match student to
	 * @param percentage - The percentage to cap the topic areas at
	 * for example if set to 5, only 5 percent of students will be allowed
	 * to have matching with any of the topic areas
	 * @param warnings A non null ArrayList of string. warnings will be added to this list and 
	 * the caller should monitor this variable for warnings
	 * @return The matching found as a hashmap of students to supervisors
	 * a set of key value pairs where the key is the student username
	 * and the value is the supervisor name they are matched to.
	 * Note: that a student who was not matched will not have a value in the
	 * hashmap
	 * @throws UnexpectedException Thrown if an unexpected exception occurred
	 * @throws ConfigNotValidException Thrown if the config is not valid
	 */ 
	public static HashMap<String, String> allocate(Students students, Supervisors supervisors, int percentage, ArrayList<String> warnings) throws UnexpectedException, ConfigNotValidException{
		try {
			
			//Check that all student chose valid supervisors
			
			for (int i=0;i<students.size();i++) {
				for (int j=0;j<students.getNumOfPreferenceChoice();j++) {
					try {
						supervisors.getIndex(students.getChoice(i, j));
					}catch (SupervisorNotFoundException e) {
						warnings.add("Student <" + students.getName(i) + "> chose a non existant supervisor <" + students.getChoice(i, j) + ">");
					}
				}
			}
			
			
			
			
			String [] TOPIC_AREAS = Config.getConfig().getStrListValue(Config.MATCHING_TOPIC_AREAS);
			logger.info("Narrow run stated with capped percentage of "+percentage);
			// Nodes = source + student + supervisors + categories + sinks
			
			//set up list of nodes
			ArrayList<String> nodeNames 			= new ArrayList<String>();
			HashMap<String,Integer>  nodeNamesMap 	= new HashMap<String,Integer>();
			
			// ** Construct node name list
			logger.info("Construct node name list");
			nodeNames.add("source"); //source node is number 1
			
			int studentStartIndex = 1;
			logger.info("Adding students to node name list");
			for (int i =0;i<students.size();i++) {
				logger.info("Adding <"+students.getUsername(i)+'>');
				nodeNames.add(students.getUsername(i));
			}
			int studentLastIndex = nodeNames.size();//first supervisor index, before is students
			
			int supervisorStartIndex = studentLastIndex;
			logger.info("Adding supervisros to node name list");
			for (int i =0;i<supervisors.size();i++) {
				logger.info("Adding <"+supervisors.getSupervisorName(i) +'>');
				nodeNames.add(supervisors.getSupervisorName(i));
			}
			int superviosrLastIndex  = nodeNames.size();
			
			int supervisorNatSciStartIndex = superviosrLastIndex;
			logger.info("Adding all supervisors again as natural science supervisors to node name list");
			for (int i =0;i<supervisors.size();i++) {
				logger.info("Adding <"+supervisors.getSupervisorName(i)+" nat_sci" +'>');
				nodeNames.add(supervisors.getSupervisorName(i)+" nat_sci");
			}
			int supervisorNatSciLastIndex  = nodeNames.size();
			
			int topicAreaStartIndex = supervisorNatSciLastIndex;
			logger.info("Adding topic areas to node name list");
			for (int i=0;i<TOPIC_AREAS.length;i++) {
				logger.info("Adding <"+TOPIC_AREAS[i] +'>');
				nodeNames.add(TOPIC_AREAS[i]);
			}
			int topicAreaLastIndex  = nodeNames.size();
			
			nodeNames.add("sink");
			logger.info("Adding sink");
			
			
			// ** Creating node map
			for (int i=0;i<nodeNames.size();i++) {
				if (nodeNamesMap.get(nodeNames.get(i)) == null) {
					nodeNamesMap.put(nodeNames.get(i), i);
				}else {
					logger.severe("Collision in node creation");
					throw new UnexpectedException("Internal Error: Collision in node creation map");
				}
			}
			
			
			// ** Creating capacities matrix
			int [] [] capacities = new int [nodeNames.size()][nodeNames.size()];
			
			logger.info("Setting source to student capacities to 1");
			//set capacities from source to student nodes to 1
			for (int i=studentStartIndex;i<studentLastIndex;i++) {
				capacities[0][i] = 1;
			}
			
			
			for (int i=0;i<students.size();i++) {
				String student = students.getUsername(i);
				
				for (int j=0;j<supervisors.size();j++) {
					String supervisor = supervisors.getSupervisorName(j);
					
					int flow = MatchingUtils.getFlowBetween(students, supervisors, student, supervisor);
					
					if (flow > 0) {
						String supervisorNodeName = supervisor;
						if (students.isNatSci(student)) {
							supervisorNodeName += " nat_sci";
						}
						
						logger.info("Setting <"+student+"> to match with "+supervisorNodeName);
						try {
							capacities[nodeNamesMap.get(student)][nodeNamesMap.get(supervisorNodeName)] = flow;	
						}catch (IndexOutOfBoundsException e) {
							logger.severe("IndexOutOfBoundsException when trying to set capacity edge of <" +
									student + "> and <" + supervisorNodeName +'>');
							throw new UnexpectedException("Internal error: failed to find arc between <" +
									student + "> and <" + supervisorNodeName +'>');
						}
						
					}
					
				}
			}
			
			//** add capacities from supervisor nat sci node to master supervisor node
			logger.info("Linking supervisor nat sci notes to master nodes...");
			for (int i=supervisorNatSciStartIndex;i<supervisorNatSciLastIndex;i++) {
				String slaveNodeName = nodeNames.get(i);
				String masterNodeName = slaveNodeName.replace(" nat_sci", "").trim();
				int index = supervisors.getIndex(masterNodeName);
				
				int masterNode = nodeNamesMap.get(masterNodeName);
				
				if (masterNode == -1) {
					logger.severe("Failed to find master node for <"+masterNodeName+'>');
					throw new UnexpectedException("Failed to find master node for <"+masterNodeName+'>');
				}
				
				capacities[i][masterNode] = supervisors.getSupervisorCapcity(index)-1;
				logger.info("setting capacity from "+slaveNodeName + " to "+masterNodeName + " at " +(supervisors.getSupervisorCapcity(index)-1));
			}
			
			
			logger.info("Connecting supervisor to topic areas");
			for (int i=0;i<supervisors.size();i++) {
				String topicArea = supervisors.getSupervisorTopic(i).toLowerCase();
				
				//check the topic area is valid
				boolean valid = false;
				for (int j=0;j<TOPIC_AREAS.length;j++) {
					if (topicArea.equals(TOPIC_AREAS[j])) {
						valid = true;
						break;
					}
				}
				
				if (valid) {
					int topicAreaNodeNum = nodeNamesMap.get(topicArea);
					
					//set capacity equal to lab capacity
					capacities[supervisorStartIndex+i][topicAreaNodeNum] = supervisors.getSupervisorCapcity(i);
				}else {
					warnings.add("Topic Area <"+topicArea+"> is not recognised for supervisor <"+ supervisors.getSupervisorName(i) +'>');
				}
			}
			
	
			// ** Setting capacities from topic areas to sink
			int sinkNode = nodeNames.size()-1;// sink node is last node
			
			//calculate the capacity for each topic area
			double capacity = ((double)students.size()/100) * percentage;
			logger.info("Capacity of topic area is <" + (int)Math.floor(capacity)+'>');
			
			//add to matrix 
			for (int i=topicAreaStartIndex;i< topicAreaLastIndex;i++) {
				capacities[i][sinkNode] = (int) Math.floor(capacity);
			}
			
			//logger.info("Capacity matrix created: \n"+ matrixToStr(capacities));
			
			int [] [] costs = new int [nodeNames.size()] [nodeNames.size()];
			
			logger.info("Creating cost matrix...");
			
			for (int i=0;i<students.size();i++) {
				String studentName = students.getUsername(i);
				
				for (int j=0;j<supervisors.size();j++) {
					String supervisorName = supervisors.getSupervisorName(j);
					
					int cost = MatchingUtils.getCostBetween(students, supervisors, studentName, supervisorName);
					
					if (students.isNatSci(studentName)) {
						supervisorName += " nat_sci";
					}
					int studentNode 	= nodeNamesMap.get(studentName);
					int supervisorNode 	= nodeNamesMap.get(supervisorName);
					
					costs[studentNode][supervisorNode] = cost;
					
				}
				
			}
			
			logger.info("Performing match...");
			MinCostMaxFlow.getMaxFlow(capacities, costs, 0, sinkNode);
			int [][] flow2 = MinCostMaxFlow.flow;
			
			
			//** Interpret matching data into return hashmap
			HashMap<String,String> match = new HashMap<String,String>();
			
			//for each student node
			for (int i = studentStartIndex;i<studentLastIndex;i++) {
				boolean matched = false;
				//find the edge node they got matched too
				for (int j=0;j<flow2[i].length;j++) {
					if (flow2[i][j] == 1) {
						if (matched) { // if 2 matches found log error
							logger.severe("Student "+ nodeNames.get(i)+" matched twice");
							throw new UnexpectedException("Error, student "+ nodeNames.get(i)+" matched twice!");
						}else {//put matching in hashmap
							matched = true;
							match.put(nodeNames.get(i), nodeNames.get(j));
						}
					}
				}
			}
			
			for (String username: match.keySet()) {
				if (match.get(username).contains("nat_sci")) {
					String temp = match.get(username).replace("nat_sci", "").trim();
					match.put(username, temp);
				}
			}
			
			//log and return matching data
			logger.info("Match created: "+match);
			
			return match;
		}catch (StudentNotFoundException e) {
			logger.severe("Encountered an unexpected StudentNotFoundException when performing a narror run");
			logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new UnexpectedException(e);
		} catch (IOException | JSONException | CustomValidationException | InvalidTypeException | ConfigNotValidException e) {
			logger.severe("Encountered an exception when performing a narror run");
			logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new ConfigNotValidException(e);
		} catch (SupervisorNotFoundException e) {
			logger.severe("Encountered an unexpected SupervisorNotFoundException when performing a narror run");
			logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
			throw new UnexpectedException(e);
		}
	}	
}
