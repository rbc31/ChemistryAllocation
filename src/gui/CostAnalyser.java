package gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.JSONException;

import Exceptions.ConfigNotValidException;
import Exceptions.CustomValidationException;
import Exceptions.InvalidTypeException;
import dataManager.Config;
import exceptions.InvalidTableFormatException;
import exceptions.RowNotFoundException;
import exceptions.UnexpectedException;
import main.Table;
import utils.GetStackTrace;
import utils.ToString;

/**
 * Class to show users the costs of matchings as well as which columns data is being pulled from
 * @author Rob
 *
 */
public class CostAnalyser extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The JFrame of the caller to de-enable and enable
	 */
	private JFrame caller;
	
	/**
	 * Reference to the config
	 */
	private Config config;
	
	/**
	 * The location of the student file
	 */
	private String studentFile;
	
	/**
	 * The location of the supervisor file
	 */
	private String supervisorFile;
	
	/**
	 * The tabs to hold the different tables
	 */
	private JTabbedPane tabs;
	
	/**
	 * Private logger for this class
	 */
	private static Logger logger = Logger.getLogger(CostAnalyser.class.getName());
	
	
	/**
	 * Constructs a new CostAnaylser Frame 
	 * @param caller - The caller JFrame, will be de-enabled until this frame is closed
	 * @param studentFile - The student file location
	 * @param supervisorFile - The supervisor file location
	 * @throws FileNotFoundException Thrown if occurred when loading config
	 * @throws JSONException Thrown if occurred when loading config
	 * @throws IOException Thrown if occurred when loading config
	 * @throws InvalidTypeException Thrown if occurred when loading config
	 * @throws ConfigNotValidException Thrown if occurred when loading config
	 * @throws CustomValidationException Thrown if occurred when loading config
	 */
	public CostAnalyser(JFrame caller, String studentFile, String supervisorFile) throws FileNotFoundException, JSONException, IOException, InvalidTypeException, ConfigNotValidException, CustomValidationException {
		this.caller 		= caller;
		this.config 		= Config.getConfig();
		this.studentFile 	= studentFile;
		this.supervisorFile = supervisorFile;
		
		this.caller.setEnabled(false);
		this.setTitle("Data viewer");
		this.addWindowListener(new EnableParentOnClose(caller));
		
		constructForm();
		this.pack();
		
		//position frame to be directly on top of caller
		Point l = caller.getLocation();
		l.x = l.x + caller.getWidth()/2 - 275;
		this.setLocation(l);
		this.setVisible(true);
		
	}
	
	/**
	 * Ads the given column header name 
	 * @param source - The source table to get the header name of
	 * @param target - The target table to add the row too
	 * @param keyName - The name of the column to add to the target column
	 * @param headerIndex - The index of the header to take from the source table
	 */
	private void addColumnHeaderToTable(Table source, Table target, String keyName, int headerIndex) {
		
		ArrayList<String> toAdd = new ArrayList<String>();
		toAdd.add(keyName);
		
		try {
			toAdd.add(source.getHeaders()[headerIndex]);
		}catch (IndexOutOfBoundsException e) {
			toAdd.add("Index out of bounds - Not valid");
		}
		
		try {
			target.addRecord(toAdd);
		} catch (IllegalArgumentException | InvalidTableFormatException e) {
			logger.severe("Encountered Exception adding a row of 2 columns to the target table. Stack Trace: " + GetStackTrace.getStackTrace(e));
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Creates the student table that contains header information
	 * @param studentFile - The student file location
	 * @return JTable or error label if error occurred
	 */
	public Component getStudentDataTable(String studentFile) {
		try {
			Table students = Table.parseTableFromCSVFile(new File(studentFile));
				
			Table toReturn = new Table(new String[] {"Name","Value"});
			
			addColumnHeaderToTable(students, toReturn, "USERNAME_COLUMN",config.getIntValue(Config.STUDENT_USERNAME_COLUMN));
			addColumnHeaderToTable(students, toReturn, "COURSE_COLUMN",config.getIntValue(Config.STUDENT_COURSE_COL));
			addColumnHeaderToTable(students, toReturn, "NAME_COLUMN",config.getIntValue(Config.STUDENT_NAME_COL));
			
			int [] keywordColumns		= config.getIntListValue(Config.STUDENT_KEYWORD_COLUMNS);
			for (int i=0;i<keywordColumns.length;i++) {
				addColumnHeaderToTable(students, toReturn, "KEYWORD #" + (i+1),keywordColumns[i]);
			}
			
			int [] topicAreaColumns		= config.getIntListValue(Config.STUDENT_TOPIC_AREA_COLUMNS);
			for (int i=0;i<topicAreaColumns.length;i++) {
				addColumnHeaderToTable(students, toReturn, "TOPIC AREA #" + (i+1),topicAreaColumns[i]);
			}
			
			int [] choiceColumns		= config.getIntListValue(Config.STUDENT_PREFERENCE_COLUMNS);
			for (int i=0;i<choiceColumns.length;i++) {
				addColumnHeaderToTable(students, toReturn, "CHOICE COLUMN #" + (i+1),choiceColumns[i]);
			}
			
			ArrayList<String> toAdd = new ArrayList<String>();
			toAdd.add("Natural science units");
			toAdd.add(ToString.arrayToString(config.getStrListValue(Config.STUDENT_NAT_SCI_UNITS)));
			
			toReturn.addRecord(toAdd);
			return createTable(toReturn);
		}catch (Exception e) {
			logger.severe("Encounted exception when trying to create student table");
			logger.severe(GetStackTrace.getStackTrace(e));
			return new JLabel("No student file, cannot display information.");
		}		
	}
	
	/**
	 * Creates the supervisor table that contains header information
	 * @param supervisorFile - The student file location
	 * @return JTable or error label if error occurred
	 */
	public Component getSupervisorDataTable(String supervisorFile) {
		try {
			Table supervisor = Table.parseTableFromCSVFile(new File(supervisorFile));
				
			Table toReturn = new Table(new String[] {"Name","Value"});
			
			addColumnHeaderToTable(supervisor, toReturn, "NAME_COLUMN",config.getIntValue(Config.SUPERVISOR_NAME_COL));
			addColumnHeaderToTable(supervisor, toReturn, "CAPACITY_COLUMN",config.getIntValue(Config.SUPERVISOR_CAPACITY_COL));
			addColumnHeaderToTable(supervisor, toReturn, "TOPIC_COLUMN",config.getIntValue(Config.SUPERVISOR_TOPIC_COL));
			
			int [] keywordColumns		= config.getIntListValue(Config.SUPERVISOR_KEYWORD_COLUMNS);
			for (int i=0;i<keywordColumns.length;i++) {
				addColumnHeaderToTable(supervisor, toReturn, "KEYWORD #" + (i+1),keywordColumns[i]);
			}
			
			return createTable(toReturn);
		}catch (Exception e) {
			logger.severe("Encounted exception when trying to create supervisor table");
			logger.severe(GetStackTrace.getStackTrace(e));
			return new JLabel("No supervisor file, cannot display information.");
		}		
	}
	
	/**
	 * Sorts the given table into order based on the given column index
	 * @param input - The input table to sort
	 * @param columnIndex - The column index to sort on
	 * @return A sorted table 
	 * @throws UnexpectedException Thrown if internal error occurred
	 */
	public Table sortTable(Table input, int columnIndex) throws UnexpectedException {
		Table toReturn = new Table(input.getHeaders());
		
		//take the biggest from the input table, remove it and add to output table
		while (input.size() > 0) {
			int smallestIndex = -1;
			int smallestValue = Integer.MAX_VALUE;
			
			for (int i=0;i<input.size();i++) {
				int value = Integer.valueOf(input.getValue(i, columnIndex));
				
				if (value < smallestValue) {
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
	 * Gets and creates a cost table based off the matching config
	 * @return A Table of costs
	 * @throws UnexpectedException If an unexpected error occurred
	 */
	private Table getCostTable() throws UnexpectedException {
		Table table = null;
		
		//this is the index of the column we want to sort on
		int costIndex = -1;
		
		
		boolean keyWordAllocation = this.config.getBooleanValue(Config.MATCHING_ENABLE_KEYWORD_ALLOCATION);
		boolean topicAreaAllocation = this.config.getBooleanValue(Config.MATCHING_ENABLE_TOPIC_AREA_ALLOCATION);
		
		
		//If only allocating on choice preferences
		if (!keyWordAllocation && !topicAreaAllocation) {
			costIndex = 1;
			table = new Table(new String[] {"Preference Rank", "Cost"});
			
			int[] preferenceList = this.config.getIntListValue(Config.MATCHING_CHOICE_PREFERENCE_WEIGHTS);
			for (int i=0;i<preferenceList.length; i++) {
				ArrayList<String> record = new ArrayList<String>();
				record.add(String.valueOf(i+1));
				record.add(String.valueOf(preferenceList[i]));
				try {
					table.addRecord(record);
				} catch (IllegalArgumentException | InvalidTableFormatException e) {
					logger.severe("Unexpected Error, Failed to add a row of length " + record.size() + "to table size " + table.getColCount() +". Stack Trace: " + GetStackTrace.getStackTrace(e));
					throw new UnexpectedException (e);
				}
			}
			
		//If keyword allocation and topic area allocation is enabled
		} else if(keyWordAllocation && topicAreaAllocation) {
			
			costIndex = 3;
			table = new Table(new String[] {"Preference Rank", "Number of keywords in common", "Topic Ares", "Cost"});
			
			int[] preferenceList = this.config.getIntListValue(Config.MATCHING_CHOICE_PREFERENCE_WEIGHTS);
			int[] keywordList 	 = this.config.getIntListValue(Config.MATCHING_KEYWORDS_PREFERENCE_WEIGHTS);
			int[] topicArea 	 = this.config.getIntListValue(Config.MATCHING_TOPIC_AREA_PREFERENCE_WEIGHTS);
			int lowerBound		 = this.config.getIntValue(Config.MATCHING_KEYWORD_LOWER_BOUND_TO_ALLOCATE);
			int noMatchKeywords  = this.config.getIntValue(Config.MATCHING_NO_KEYWORDS_IN_COMMON_WEIGHT);
			int noMatchTopicArea = this.config.getIntValue(Config.MATCHING_NO_TOPIC_AREA_MATCH_WEIGHT);
			int noMatchPreference = this.config.getIntValue(Config.MATCHING_NO_MATCH_WEIGHT);
			
			// For each preference choice add each keyword/topic area preference and when there is no topic area and keyword preferences
			for (int i=0;i<preferenceList.length; i++) {
				// For each keyword choice add each topic area choice *and* no topic area
				for (int j=0;j<keywordList.length;j++) {
					//For each topic area choice
					for (int k=0;k<topicArea.length;k++) {
						ArrayList<String> record = new ArrayList<String>();
					
						record.add(String.valueOf(i+1));
						record.add(String.valueOf(j+1));
						record.add(String.valueOf(k+1));
						if (j+1 > lowerBound) {
							record.add(String.valueOf(preferenceList[i] + keywordList[j] + topicArea[k]));
						}else {
							record.add(String.valueOf(preferenceList[i] + noMatchKeywords + topicArea[k]));
						}
						
						try {
							table.addRecord(record);
						} catch (IllegalArgumentException | InvalidTableFormatException e) {
							logger.severe("Unexpected Error, Failed to add a row of length " + record.size() + "to table size " + table.getColCount() +". Stack Trace: " + GetStackTrace.getStackTrace(e));
							throw new UnexpectedException (e);
						}
					}
					
					//
					
					ArrayList<String> record = new ArrayList<String>();
					
					record.add(String.valueOf(i+1));
					record.add(String.valueOf(j+1));
					record.add(String.valueOf(0));
					if (j+1 > lowerBound) {
						record.add(String.valueOf(preferenceList[i] + keywordList[j] + noMatchTopicArea));
					}else {
						record.add(String.valueOf(preferenceList[i] + noMatchKeywords + noMatchTopicArea));
					}
					
					try {
						table.addRecord(record);
					} catch (IllegalArgumentException | InvalidTableFormatException e) {
						logger.severe("Unexpected Error, Failed to add a row of length " + record.size() + "to table size " + table.getColCount() +". Stack Trace: " + GetStackTrace.getStackTrace(e));
						throw new UnexpectedException (e);
					}
				}
				
				for (int k=0;k<topicArea.length;k++) {
					ArrayList<String> record = new ArrayList<String>();
				
					record.add(String.valueOf(i+1));
					record.add(String.valueOf(0));
					record.add(String.valueOf(k+1));
					record.add(String.valueOf(preferenceList[i] + noMatchKeywords + topicArea[k]));
					
					
					try {
						table.addRecord(record);
					} catch (IllegalArgumentException | InvalidTableFormatException e) {
						logger.severe("Unexpected Error, Failed to add a row of length " + record.size() + "to table size " + table.getColCount() +". Stack Trace: " + GetStackTrace.getStackTrace(e));
						throw new UnexpectedException (e);
					}
				}
			}
			
			for (int j=0;j<keywordList.length;j++) {
				for (int k=0;k<topicArea.length;k++) {
					ArrayList<String> record = new ArrayList<String>();
				
					record.add(String.valueOf(0));
					record.add(String.valueOf(j+1));
					record.add(String.valueOf(k+1));
					if (j+1 > lowerBound) {
						record.add(String.valueOf(noMatchPreference + keywordList[j] + topicArea[k]));
					}else {
						record.add(String.valueOf(noMatchPreference + noMatchKeywords + topicArea[k]));
					}
					
					try {
						table.addRecord(record);
					} catch (IllegalArgumentException | InvalidTableFormatException e) {
						logger.severe("Unexpected Error, Failed to add a row of length " + record.size() + "to table size " + table.getColCount() +". Stack Trace: " + GetStackTrace.getStackTrace(e));
						throw new UnexpectedException (e);
					}
				}
				
				ArrayList<String> record = new ArrayList<String>();
				
				record.add(String.valueOf(0));
				record.add(String.valueOf(j+1));
				record.add(String.valueOf(0));
				if (j+1 > lowerBound) {
					record.add(String.valueOf(noMatchPreference + keywordList[j] + noMatchTopicArea));
				}else {
					record.add(String.valueOf(noMatchPreference + noMatchKeywords + noMatchTopicArea));
				}
				
				try {
					table.addRecord(record);
				} catch (IllegalArgumentException | InvalidTableFormatException e) {
					logger.severe("Unexpected Error, Failed to add a row of length " + record.size() + "to table size " + table.getColCount() +". Stack Trace: " + GetStackTrace.getStackTrace(e));
					throw new UnexpectedException (e);
				}
			}
			
		//If keyword allocation and preference allocation - no topic area allocation
		} else if(keyWordAllocation) {
			costIndex = 2;
			table = new Table(new String[] {"Preference Rank", "Number of keywords in common", "Cost"});
			
			int[] preferenceList = this.config.getIntListValue(Config.MATCHING_CHOICE_PREFERENCE_WEIGHTS);
			int[] keywordList 	 = this.config.getIntListValue(Config.MATCHING_KEYWORDS_PREFERENCE_WEIGHTS);
			int lowerBound		 = this.config.getIntValue(Config.MATCHING_KEYWORD_LOWER_BOUND_TO_ALLOCATE);
			int noMatchKeywords  = this.config.getIntValue(Config.MATCHING_NO_KEYWORDS_IN_COMMON_WEIGHT);
			
			for (int i=0;i<preferenceList.length; i++) {
				for (int j=0;j<keywordList.length;j++) {
					ArrayList<String> record = new ArrayList<String>();
				
					record.add(String.valueOf(i+1));
					record.add(String.valueOf(j+1));
					if (j+1 > lowerBound) {
						record.add(String.valueOf(preferenceList[i] + keywordList[j]));
					}else {
						record.add(String.valueOf(preferenceList[i] + noMatchKeywords));
					}
					
					try {
						table.addRecord(record);
					} catch (IllegalArgumentException | InvalidTableFormatException e) {
						logger.severe("Unexpected Error, Failed to add a row of length " + record.size() + "to table size " + table.getColCount() +". Stack Trace: " + GetStackTrace.getStackTrace(e));
						throw new UnexpectedException (e);
					}
				}
				
				ArrayList<String> record = new ArrayList<String>();
				
				record.add(String.valueOf(i+1));
				record.add(String.valueOf(0));
				record.add(String.valueOf(preferenceList[i] + noMatchKeywords));
				
				
				try {
					table.addRecord(record);
				} catch (IllegalArgumentException | InvalidTableFormatException e) {
					logger.severe("Unexpected Error, Failed to add a row of length " + record.size() + "to table size " + table.getColCount() +". Stack Trace: " + GetStackTrace.getStackTrace(e));
					throw new UnexpectedException (e);
				}
			}
		} else if(topicAreaAllocation) {
			costIndex = 2;
			table = new Table(new String[] {"Preference Rank", "Topic Area Rank", "Cost"});
			
			int[] preferenceList = this.config.getIntListValue(Config.MATCHING_CHOICE_PREFERENCE_WEIGHTS);
			int[] topicArea 	 = this.config.getIntListValue(Config.MATCHING_TOPIC_AREA_PREFERENCE_WEIGHTS);
			int noMatchTopicArea = this.config.getIntValue(Config.MATCHING_NO_TOPIC_AREA_MATCH_WEIGHT);
			int noMatchPreference = this.config.getIntValue(Config.MATCHING_NO_MATCH_WEIGHT);
			
			for (int i=0;i<preferenceList.length; i++) {
				for (int j=0;j<topicArea.length;j++) {
					ArrayList<String> record = new ArrayList<String>();
				
					record.add(String.valueOf(i+1));
					record.add(String.valueOf(j+1));
					record.add(String.valueOf(preferenceList[i] + topicArea[j]));
					try {
						table.addRecord(record);
					} catch (IllegalArgumentException | InvalidTableFormatException e) {
						logger.severe("Unexpected Error, Failed to add a row of length " + record.size() + "to table size " + table.getColCount() +". Stack Trace: " + GetStackTrace.getStackTrace(e));
						throw new UnexpectedException (e);
					}
				}
				
				ArrayList<String> record = new ArrayList<String>();
				
				record.add(String.valueOf(i+1));
				record.add(String.valueOf(0));
				record.add(String.valueOf(preferenceList[i] + noMatchTopicArea));
				
				try {
					table.addRecord(record);
				} catch (IllegalArgumentException | InvalidTableFormatException e) {
					logger.severe("Unexpected Error, Failed to add a row of length " + record.size() + "to table size " + table.getColCount() +". Stack Trace: " + GetStackTrace.getStackTrace(e));
					throw new UnexpectedException (e);
				}
			}
		
		
			for (int j=0;j<topicArea.length;j++) {
				ArrayList<String> record = new ArrayList<String>();
			
				record.add(String.valueOf(0));
				record.add(String.valueOf(j+1));
				record.add(String.valueOf(noMatchPreference + topicArea[j]));
				try {
					table.addRecord(record);
				} catch (IllegalArgumentException | InvalidTableFormatException e) {
					logger.severe("Unexpected Error, Failed to add a row of length " + record.size() + "to table size " + table.getColCount() +". Stack Trace: " + GetStackTrace.getStackTrace(e));
					throw new UnexpectedException (e);
				}
			}
		}
		
		table = sortTable(table, costIndex);
		return table;
	}
	
	/**
	 * Creates a JTable based off the input Table
	 * @param input - The input table to create the JTable from
	 * @return GUI JTable of the input table
	 */
	private JTable createTable (Table input) {
		DefaultTableModel model = new DefaultTableModel();
		JTable toReturn  = new JTable(model);
		
		String[] fields = input.getHeaders();
		
		for (int i =0;i<fields.length;i++) {
			model.addColumn(fields[i]);
		}
	
		for (int i=0;i<input.size();i++) {
			String[] row = new String[input.getColCount()];
			
			for (int j=0;j<fields.length;j++) {
				row[j] = input.getValue(i, j);
			}
			model.addRow(row);
		}
		return toReturn;
	}
	
	/**
	 * Gets the cost table gui element. A GUI table of costs or a JLabel in event of an error
	 * @return Component of costs of error
	 */
	public Component getCostTableGUI() {
		
		try {
			Table table = getCostTable();
			return createTable(table);
		} catch (UnexpectedException e) {
			return new JLabel("Error loading costs :" + e.getMessage());
		}
		
	}
	
	/**
	 * Constructs the JFrame
	 */
	public void constructForm() {
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new BoxLayout(framePanel, BoxLayout.Y_AXIS));
		this.add(framePanel);
		
		tabs = new JTabbedPane();
		
		tabs.addTab("Student data",new JScrollPane(getStudentDataTable(this.studentFile),JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		tabs.addTab("Supervisor data",new JScrollPane(getSupervisorDataTable(this.supervisorFile),JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		tabs.addTab("Costs", new JScrollPane(new JScrollPane(getCostTableGUI()),JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		framePanel.add(tabs);
		
		// Close button and panel to close the frame
		JPanel closePanel = new JPanel();
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(this);
		closeBtn.setActionCommand("CLOSE");
		closePanel.add(closeBtn);
		
		framePanel.add(closePanel);
	}

	/**
	 * when called will close the frame and enable the caller
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.dispose();
		this.caller.setEnabled(true);
	}
}
