package matcher;

import java.util.*;
import java.util.logging.Logger;

/**
 * MinCostMaxFlow class to calculate the maximum cost max flow
 * for network flows.
 * 
 * @author Robert Cobb <br>
 * Bath University<br>
 * Email: rbc31@bath.ac.uk
 *
 * Based off implementation: by jaehyunp (https://github.com/jaehyunp/stanfordacm/blob/master/code/MinCostMaxFlow.java)
 */
public class MinCostMaxFlow {
    
	/**
	 * The logger for this class
	 */
	private static Logger logger = Logger.getLogger(MinCostMaxFlow.class.getName());
	
	/**
	 * Definition of infinity. Half max size to avoid overflow errors
	 */
	private static final int INFINTY = Integer.MAX_VALUE/2;
    
	/**
	 * The flkow created by the matching algorithm
	 */
	public static int[][] flow;
    
    /**
     * Private hashmap that contains the directions of flow for each parent, child pair of nodes
     * used to talk between the getMaxFlow and getCheapestPath methods
     */
	private static  HashMap<String,String> globFlowDir;
    
	/**
	 * Gets the maximum flow, minimum cost solution from source to sink
	 * of the given input network
	 * @param cap - The capacity matrix, 
	 * cell i,j should be the capacity along a direct edge from i to j.
	 * If there is no edge between i and j then this should be 0
	 * @param cost - The cost matrix
	 * cell i,j should be the cost along the direct edge of 1 flow from i to j.
	 * @param source - The index of the source node in the cap and cost matrix
	 * @param sink - The index of the sink node in the cap and cost matrix
	 * @return A int array of 2 values, val[0] is the total flow found
	 * val[1] is the total cost found
	 */
    public static int [] getMaxFlow(int cap[][], int cost[][], int source, int sink) {
    	//logMessage("Starting to get max flow");
    	int n = cap.length;
    	int [][] flow = new int[n][n];
    	HashMap<Integer,Integer> parents = null;
    	
    	int totalcost = 0;
    	int totalFlow = 0;
    	
    	//while there is a flow to augment
    	while((parents = getCheapestPath(flow,cap,cost,source,sink))!= null) {
    		int flowAug = INFINTY;
    		
    		int current = sink;
    		//iterate over the path to find max flow that can be augmented
    		while (current != source) {
    			int parent = parents.getOrDefault(current,-1);
    			flowAug = globFlowDir.get(current + " to " + parent).equals("NORMAL_FLOW")? 
    					Math.min(flowAug, cap[parent][current] - flow[parent][current]) :
    						Math.min(flowAug, flow[current][parent]);
    			current = parent;
    		}
    		
    		//update the flows and calulate the costs, by iteratate along the path again
    		int costAug = 0;
    		current = sink;
    		while (current != source) {
    			int parent = parents.getOrDefault(current,-1);
    			
    			if (globFlowDir.get(current + " to " + parent).equals("NORMAL_FLOW")) {
    				flow[parent][current] += flowAug;
        			costAug += flowAug * cost[parent][current];
    			}else {
    				flow[current][parent] -= flowAug; //going back on an arc
    				costAug -= flowAug * cost[current][parent];
    			}
    			
    			current = parent;
    		}
    		//update total cost and flow
    		totalcost += costAug;
    		totalFlow += flowAug;
    		}
    	MinCostMaxFlow.flow = flow;
    	return new int[]{totalFlow,totalcost};
    }

    /**
     * Gets the cheapest flow to augment from the source to the sink using a variation on dikstras
     * @param flow - The flow matrix <br>
     * 	cell i,j represents the flow from i to j
     * @param cap - The capacity matrix <br>
     * 	cell i,j represents the capacity of flow from i to j, 0 is no arc connects i to j directly
     * @param cost - The cost matrix
     * 	cell i,j represents the cost per flow from i to j
     * @param source - The index of the source node
     * @param sink - The index of the sink node 
     * @return parents object, A hashmap with each nodes direct parent under the search.
     * To find the cheapest node from source to sink. Start at the sink and interatativly find the parent untril
     * you get to the source
     * <br>
     * null is returned if no flow can be augmented from source to sink
     */
	private static HashMap<Integer, Integer> getCheapestPath(int[][] flow, int[][] cap, int[][] cost, int source, int sink) {
		logger.info("Getting cheapest path...");
		int n               = flow.length;
		int [] dist 		= new int[n];
		boolean [] visited 	= new boolean[n];
		globFlowDir = new HashMap<String,String>();
		HashMap<Integer,Integer> parents = new HashMap<Integer,Integer>();
		
		Arrays.fill(dist, INFINTY);
		Arrays.fill(visited, false);
		
		dist[source] = 0;
		visited[source] = true;
		int next = -1;
		
		int current = source;
		
		while(true) {
			visited[current] = true;
			
			for (int i = 0; i < n; i++) {
				if ((cap[current][i] - flow[current][i] <= 0 && flow[i][current] <= 0)) {
					continue;
				}
				
				String flowDir = "";
				if (flow[i][current] <= 0) {//if i cant go backwards flow must go forwards
					flowDir ="NORMAL_FLOW";
				}else if (cap[current][i] - flow[current][i] <= 0) {//if i cant go forwards must go backwards
					flowDir = "BACKWARD_FLOW";
				}else if (cost[current][i] <= -cost[i][current]) {//going forward is same or cheaper
					flowDir = "NORMAL_FLOW";
				}else {
					flowDir = "BACKWARD_FLOW";
				}
				
				if (flowDir == "NORMAL_FLOW") {
					int temp = dist[current] + cost[current][i];
	
					if (temp < dist[i]) {
						dist[i] = temp;
						parents.put(i,current);
						globFlowDir.put(i + " to "+current, "NORMAL_FLOW");
						visited[i] = false;
					}
				}else { //flowDir = "BACKWARD_FLOW"
					int temp = dist[current] - cost[i][current];
					
					if (temp < dist[i]) {
						dist[i] = temp;
						parents.put(i,current);
						globFlowDir.put(i + " to "+current, "BACKWARD_FLOW");
						visited[i] = false;
					}
				}
			} 
			
			//find next node to expand
			int temp = INFINTY;
			next = -1;
			for (int j = 0; j < n; j++) {
				if (!visited[j] && dist[j] < temp) {
					temp = dist[j];
					next = j;
				}
			}
			if (next == -1) {
				break;
			}
			current = next;
		}
		return visited[sink] ? parents : null;
	}
}
