package gui;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.json.JSONException;

import Exceptions.ConfigNotValidException;
import Exceptions.CustomValidationException;
import Exceptions.InvalidTypeException;
import dataManager.Config;
import dataManager.Students;
import dataManager.Supervisors;
import exceptions.InvalidTableFormatException;
import exceptions.UnexpectedException;
import matcher.Matcher;
import output.Output;
import utils.GetStackTrace;
import utils.MatchingUtils;

/**
 * Main gui entrance to the chemistry allocation program
 * 
 * Class describes the gui that allows the user to run chemistry allocations
 * 
 * Contains a main method that if called with no args will run the gui,
 * If called with args this class will call the CLI class in the cli package
 * 
 * @author Robert Cobb <br>
 * Bath University<br>
 * Email: rbc31@bath.ac.uk
 */
public class ChemsitryProjectGUI extends JPanel {

	/**
	 * Logger for this class
	 */
	private static Logger logger = Logger.getLogger(ChemsitryProjectGUI.class.getName());
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Student file selector to let users pcik a student file
	 */
	private StudentFileSelector studentFS;
	
	/**
	 * Supervisor file selector to let users pick a supervisor file
	 */
	private SupervisorFileSelector supervisorFS;
	
	/**
	 * An Options gui to allow users to manipulate the options of the gui
	 */
	private AlgorithOptionsGui options;
	
	/**
	 * The gui to allow users to save student output too
	 */
	private SaveGUI studentOutput;
	
	/**
	 * The gui to allow users to save supervisor output
	 */
	private SaveGUI supervisorOutput;
	
	/**
	 * The font to use when constructing the gui
	 */
	private Font font;
	
	/**
	 * The private controller (Action Listener) to control elements of the frame
	 */
	private Controller controller;
	
	/**
	 * A start button to let users run the algorithm
	 */
	private JButton btn_start;

	/**
	 * Setting JButton to open settings panel
	 */
	private JButton settings;
	
	/**
	 * CostViewer panel button to open up the cost view of the program
	 */
	private JButton costViewer;
	
	/**
	 * Parent JFrame that this gui is being constructed on
	 */
	private JFrame parent;
	
	/**
	 * Default font to be used in form creation if one isn't specified
	 */
	private static final Font DEAFULT_FONT = new Font("TimesRoman", Font.PLAIN, 22);

	
	/**
	 * Constructs a new gui to allow users to interact with the chemistry
	 * project. 
	 * @param parent - Parent JFRame this gui is being put on
	 * @param font - The font to use when constructing the form, if null a default font
	 * will be used
	 */
	public ChemsitryProjectGUI (JFrame parent, Font font) {
		this.parent = parent;
		if (font == null) {
			this.font = DEAFULT_FONT;
		}else {
			this.font = font;
		}
		
		construct(this.font);
	}
	
	/**
	 * Constructs the chemistry project gui
	 * @param font - The font to use when constructing the frame
	 */
	private void construct(Font font) {
		logger.info("Constructing new Chemistry gui");
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
		ImageIcon settings = new ImageIcon("assets/settings.png");
		ImageIcon settingsScale = new ImageIcon(settings.getImage().getScaledInstance(35, 35,  java.awt.Image.SCALE_SMOOTH)); 
		this.settings = new JButton(settingsScale);
		this.settings.setActionCommand("SETTINGS");
		this.settings.setMargin(new Insets(0, 0, 0, 0));

		
		this.settings.setOpaque(false);
		this.settings.setContentAreaFilled(false);
		this.settings.setBorderPainted(false);
		this.settings.setBorder(null);
		
		JPanel settingsPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		settingsPnl.add(this.settings);
		this.add(settingsPnl);
		
		ImageIcon cost = new ImageIcon("assets/cost.png");
		ImageIcon costScale = new ImageIcon(cost.getImage().getScaledInstance(35, 35,  java.awt.Image.SCALE_SMOOTH)); 
		this.costViewer = new JButton(costScale);
		this.costViewer.setActionCommand("COSTS");
		this.costViewer.setMargin(new Insets(0, 0, 0, 0));

		
		this.costViewer.setOpaque(false);
		this.costViewer.setContentAreaFilled(false);
		this.costViewer.setBorderPainted(false);
		this.costViewer.setBorder(null);
		
		settingsPnl.add(this.costViewer);
	
		this.studentFS 		= new StudentFileSelector(font);
		this.add(this.studentFS);
		
		this.supervisorFS 	= new SupervisorFileSelector(font);
		this.add(this.supervisorFS);
		
		this.options		= new AlgorithOptionsGui(font);
		this.add(this.options);
		
		this.studentOutput = new SaveGUI(font, "      Student File");
		this.add(studentOutput);
		
		this.supervisorOutput = new SaveGUI(font, "Supervisor File");
		this.add(supervisorOutput);
		
		this.controller = new Controller(this.parent, this.studentFS,this.supervisorFS,this.options, studentOutput, supervisorOutput);


		this.settings.addActionListener(this.controller);
		this.costViewer.addActionListener(this.controller);
		
		this.btn_start = new JButton("Start");
		this.btn_start.setFont(font);
		this.btn_start.addActionListener(this.controller);
		this.btn_start.setActionCommand("START");
		this.add(btn_start);		
	}
	
	/**
	 * Main entrance to the program
	 * @param args - no args expected
	 */
	public static void main(String [] args) {
		try {
			
			logger.info("Validating config");
			Config.getConfig();//throw if config validation error
			
			logger.info("Setting global fonts");
			//set text of gui elements not controller by program
			UIManager.put("OptionPane.messageFont", DEAFULT_FONT);
			UIManager.put("OptionPane.buttonFont", DEAFULT_FONT);
			
			JFrame f = new JFrame("Chemistry Allocation Project V2.0");
			//add listner to window to close logger properly when program closed
			ChemsitryProjectGUI o = new ChemsitryProjectGUI(f,null);
			
			//set size and show the gui
			f.setContentPane(o);
			f.setSize(1100, 400);
			f.setVisible(true);
			f.addWindowListener(o.controller);
			
		}catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Unknown Error: <"+e.getMessage() + ">",
					"Unknown Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Private controller class to action events of the frame
	 */
	private class Controller implements ActionListener, WindowListener {

		/**
		 * Student file selector to pull the student file location from
		 */
		private StudentFileSelector  studentFS;
		
		/**
		 * Supervisor file location to pull the supervisor file from
		 */
		private SupervisorFileSelector supervisorFS;
		
		/**
		 * Algorithm options gui to pull the variables of the algorithm from
		 */
		private AlgorithOptionsGui options;

		/**
		 * The save gui to allow users to save the student output to 
		 * a given file
		 */
		private SaveGUI studentSave;

		/**
		 * The save gui to allow users to save the supervisor output to 
		 * a given file
		 */
		private SaveGUI supervisorSave;
		
		/**
		 * The JFrame containing the main GUI
		 */
		private JFrame mainPage;
		
		/**
		 * Constructs a new controller with the given panels to pull relevant
		 * data from when performing matchings
		 * @param mainPage - The Parent Jframe, so it can be enabled/deenabled when needed
		 * @param studentFS - The student file selector
		 * @param supervisorFS - The supervisor file selector
		 * @param options - The options gui that holds algorithm options
		 * @param studentSaveGUI - The student save gui to get the location of student
		 * output from
		 * @param supervisorSaveGUI - The supervisor save gui to get the location
		 * of supervisor output from
		 */
		private Controller(JFrame mainPage, StudentFileSelector studentFS,
				SupervisorFileSelector supervisorFS,
				AlgorithOptionsGui options, SaveGUI studentSaveGUI, 
				SaveGUI  supervisorSaveGUI) {
			this.mainPage 		= mainPage;
			this.studentFS 		= studentFS;
			this.supervisorFS 	= supervisorFS;
			this.options 		= options;
			this.studentSave    = studentSaveGUI;
			this.supervisorSave = supervisorSaveGUI;
		}
		
		/**
		 * Shows the settings panel
		 */
		private void showSettings() {
			try {
				Config.getConfig().launchSetter(this.mainPage);
			} catch (JSONException | IOException | InvalidTypeException | ConfigNotValidException
					| CustomValidationException e) {
				logger.severe("Exception occured when launching settings");
				logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						"Error when trying to launch settings: " + e.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		/**
		 * Launch the cost analyser
		 */
		private void showCosts() {
			try {
				new CostAnalyser(this.mainPage, this.studentFS.getStudentFileLocation(),this.supervisorFS.getSupervisorFileLocation());
			} catch (JSONException | IOException | InvalidTypeException | ConfigNotValidException
					| CustomValidationException e) {
				logger.severe("Exception occured when launching cost analysis");
				logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						"Error when trying to launch cost analyser: " + e.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		/**
		 * supported commands:
		 * 	START - Runs allocation
		 *  SETTINGS - Launches settings GUI
		 *  COSTS - Launches Costs GUI
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				switch (arg0.getActionCommand()) {
					case "START":
						logger.info("Start event from gui");
						runAlgorithm();
						break;
					case "SETTINGS":
						logger.info("Settings event from gui");
						showSettings();
						break;
					case "COSTS":
						logger.info("Costs event from gui");
						showCosts();
						break;
					default:
						logger.severe("Unknown action command on main gui form <"+arg0.getActionCommand()+">");
						JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
								"Error: Command not recognised: <" + arg0.getActionCommand() + ">",
								"Internal Error",
								JOptionPane.ERROR_MESSAGE);
					break;	
				}
			}catch(Exception e) {
				e.printStackTrace();
				logger.severe("Unknown error: <"+e.getMessage() + ">");
				JOptionPane.showMessageDialog(null,
						"Unknown Error: <"+e.getMessage() + ">",
						"Unknown Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		/**
		 * Loads the supervisor object into memory
		 * reporting any error messages to the user
		 * Returning null, if the load failed
		 * @param forceload - If true the supervisor date will be reloaded
		 * otherwise just gotten from cache
		 * @return The stupervisor object of null if an error was encountered
		 */
		private Supervisors loadSupervisors(boolean forceload) {
			Supervisors supervisors = null;
			try {
				if (forceload) {
					supervisors = Supervisors.forceLoad();
				}else {
					supervisors = Supervisors.getInstance();
				}
			} catch (FileNotFoundException e) {
				logger.warning("File Not Found exception when trying to intialise superviser output");
				JOptionPane.showMessageDialog(this.supervisorFS.getRootPane(),
						"Error: Supervisor file was not found.",
						"File Not Found",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				logger.warning("IOException when trying to intialise superviser output");
				JOptionPane.showMessageDialog(this.supervisorFS.getRootPane(),
						"Error: Encountered IOException whilst reading Supervisor file",
						"IOException",
						JOptionPane.ERROR_MESSAGE);
			} catch (InvalidTableFormatException e) {
				logger.warning("InvalidTableFormatException when trying to intialise superviser output");
				JOptionPane.showMessageDialog(this.supervisorFS.getRootPane(),
						"Error: Supervisor file is incorrectly formated",
						"Invalid File Format",
						JOptionPane.ERROR_MESSAGE);
			}catch (Exception e) {
				logger.warning("Generic Exception when trying to intialise supervisor output message: <"+e.getMessage()+">");
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						"Error: Supervisor file is invalid, Generic Exception occurred occured." +
						"Error message: <" + e.getMessage() + ">",
						"Invalid File",
						JOptionPane.ERROR_MESSAGE);
			}
			return supervisors;
		}
		
		/**
		 * Loads the student object into memory
		 * reporting any error messages to the user
		 * Returning null, if the load failed
		 * @param forceload - If true the student date will be reloaded
		 * otherwise just gotten from cache
		 * @return The student object of null if an error was encountered
		 */
		private Students loadStudents(boolean forceload) {
			Students students = null;
			try {
				if (forceload) {
					students = Students.forceLoad();
				}else {
					students = Students.getInstance();
				}
			} catch (FileNotFoundException e) {
				logger.warning("File Not Found exception when trying to intialise student output");
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						"Error: Student file was not found.",
						"File Not Found",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				logger.warning("IOException when trying to intialise student output");
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						"Error: Student file coulf not be loaded.",
						"IOException",
						JOptionPane.ERROR_MESSAGE);
			} catch (InvalidTableFormatException e) {
				logger.warning("InvalidTableFormatException when trying to intialise student output");
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						"Error: Student file is invalid, not a valid format.",
						"Format Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (IndexOutOfBoundsException e) {
				logger.warning("IndexOutOfBoundsException when trying to intialise student output");
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						"Error: Student file is invalid, IndexOutOfBoundsException occured." +
						"Error message: <" + e.getMessage() + ">",
						"Invalid File",
						JOptionPane.ERROR_MESSAGE);
			}catch (Exception e) {
				logger.warning("Generic Exception when trying to intialise student output message: <"+e.getMessage()+">");
				logger.warning("Stack trace: " + GetStackTrace.getStackTrace(e));
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						"Error: Student file is invalid, Generic Exception occurred occured." +
						"Error message: <" + e.getMessage() + ">",
						"Invalid File",
						JOptionPane.ERROR_MESSAGE);
			}
			return students;
		}
		
		/**
		 * Attempts to save output of the given matching to the 
		 * student and supervisor output files as found in the 
		 * {@link #studentSave} and {@link #supervisorSave} objects
		 * @param matching - The matching to save
		 */
		private void save(HashMap<String,String> matching) {
			Students students 		= loadStudents(false);
			Supervisors supervisors = loadSupervisors(false);
			
			boolean studentsSaved = false;
			boolean supervisorsSaved = false;
			
			if (studentSave.userWantstoSave() && students != null) {
	
				try {
					logger.info("Saving student output");
					Output.saveStudentOutput(students, supervisors, matching, studentSave.getFile());
					studentsSaved = true;
				} catch (FileNotFoundException e) {
					logger.warning("Couldn't find file to save student output to");
					logger.warning("Stack trace: " + GetStackTrace.getStackTrace(e));
					JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
							"Error: The student output could not be saved, error message:"+e.getMessage()+"\"",
							"File Not Found",
							JOptionPane.ERROR_MESSAGE);
				} catch (ConfigNotValidException e) {
					logger.warning("Encoutered ConfigNotValidException whilst trying to save studfent output");
					logger.warning("Stack trace: " + GetStackTrace.getStackTrace(e));
					JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
							"Error: The student output could not be saved, error message:"+e.getMessage()+"\"",
							"Config Exception",
							JOptionPane.ERROR_MESSAGE);
				} catch (UnexpectedException e) {
					logger.warning("Encoutered UnexpectedException whilst trying to save studfent output");
					logger.warning("Stack trace: " + GetStackTrace.getStackTrace(e));
					JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
							"Error: The student output could not be saved, error message:"+e.getMessage()+"\"",
							"Unexpected Exception",
							JOptionPane.ERROR_MESSAGE);
				} 
			}
			
			if (supervisorSave.userWantstoSave() && supervisors != null) {
				
				try {
					logger.info("Saving supervisor output");
					Output.saveSupervisorOutput(supervisors, students, matching, supervisorSave.getFile());
					supervisorsSaved = true;
				} catch (FileNotFoundException e) {
					logger.warning("Couldn't find file to save supervisor output to");
					JOptionPane.showMessageDialog(this.supervisorFS.getRootPane(),
							"Error: The supervisor output could not be saved, error message:"+e.getMessage()+"\"",
							"File Not Found",
							JOptionPane.ERROR_MESSAGE);
				} catch (UnexpectedException e) {
					logger.warning("Encoutered UnexpectedException whilst trying to save supervisor output");
					logger.warning("Stack trace: " + GetStackTrace.getStackTrace(e));
					JOptionPane.showMessageDialog(this.supervisorFS.getRootPane(),
							"Error: The supervisor output could not be saved, error message:"+e.getMessage()+"\"",
							"Unexpected Exception",
							JOptionPane.ERROR_MESSAGE);
				} 
			}
			
			String message = null;
			
			if (studentsSaved && supervisorsSaved) {
				message = "Student and Supervisor outputs saved";
			}else if (studentsSaved) {
				message = "Student output saved";
			}else if (supervisorsSaved) {
				message = "Supervisor output saved";
			}
			if (message != null) {
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						message,"Output saved",JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		/**
		 * Method to do with configuring the options of an algorithm and
		 * then calling the matcher class to perform the algorithm

		 */
		private void runAlgorithm() {
			String studentFileName 	  = this.studentFS.getStudentFileLocation();
			String supervisorFileName = this.supervisorFS.getSupervisorFileLocation();
		
			Config config = null;
		
			try {
				config = Config.getConfig();
				if (this.studentFS.saveFile()) {
					config.setStringValue(Config.STUDENT_INPUT_FILE, studentFileName);
				}else {
					config.setStringValue(Config.STUDENT_INPUT_FILE, "");
					config.setNonPersistantCache(Config.STUDENT_INPUT_FILE, studentFileName);
				}
				
				if (this.supervisorFS.saveFile()) {
					config.setStringValue(Config.SUPERVISOR_INPUT_FILE, supervisorFileName);
				}else {
					config.setStringValue(Config.SUPERVISOR_INPUT_FILE, "");
					config.setNonPersistantCache(Config.SUPERVISOR_INPUT_FILE, supervisorFileName);
				}
				config.save();
			}catch (IOException e) {
				logger.severe("IOException when saving student/supervisor location. Error message: <"+e.getMessage()+">");
				logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						"Error: Failed to save student/supervisor file locations, error message: "
						+ "<"+e.getMessage()+">",
						"IOException",
						JOptionPane.ERROR_MESSAGE);
			} catch (InvalidTypeException |ConfigNotValidException | CustomValidationException | JSONException e) {
				logger.severe("Exception when saving student/supervisor location. Error message: <"+e.getMessage()+">");
				logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						"Error: Failed to save student/supervisor file locations, error message: "
						+ "<"+e.getMessage()+">",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
			
			if (config == null) {
				return;
			}
			
			Students students = loadStudents(true);//force load flag to true
			if (students == null) {
				return;
			}
			
			Supervisors supervisors = loadSupervisors(true);//force load flag to true
			if (supervisors == null) {
				return;
			}
			
			HashMap<String,String> matching = null;
			
				
			int percentage = this.options.cappedPercentage();
			
			try {
				ArrayList<String> warnings = new ArrayList<String>();
				matching = Matcher.allocate(students, supervisors,percentage, warnings);
				
				if (!warnings.isEmpty()) {
					String [] temp = new String[warnings.size()];
					for (int i=0;i<warnings.size();i++) {
						temp [i] = warnings.get(i);
					}
					JList<String> msg = new JList<String>(temp);
					

					JScrollPane scrollPane = new JScrollPane(msg);
					
					scrollPane.getViewport().add(msg);
					JOptionPane.showMessageDialog(this.supervisorFS.getRootPane(), 
							scrollPane,"Matchinng finished with warnings", JOptionPane.WARNING_MESSAGE);
					
					
				}
			}catch (Exception e) {
				JOptionPane.showMessageDialog(this.supervisorFS.getRootPane(),
						"Matching encountered an error with message <" + e.getMessage() + ">",
						"Generic Error",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			try {
				Config.saveMatching(matching);
			}catch (FileNotFoundException e) {
				logger.severe("FileNotFoundException when saving matching: ,"+e.getMessage()+">");
				JOptionPane.showMessageDialog(this.supervisorFS.getRootPane(),
						"Error: The internal data file was not found"
						+ " Error message: "+e.getMessage()+"\"",
						"File Not Found",
						JOptionPane.ERROR_MESSAGE);
			}

			
			String matchingSummary = null;
			
			try {
				matchingSummary = MatchingUtils.evaulateMatching(matching, students, supervisors, config.getStrListValue(Config.MATCHING_TOPIC_AREAS));
			} catch (UnexpectedException e) {
				logger.severe("UnexpectedException when evaulating matching: ,"+e.getMessage()+">");
				logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
				JOptionPane.showMessageDialog(this.supervisorFS.getRootPane(),
						"Error: The internal error occured"
						+ " Error message: "+e.getMessage()+"\"",
						"Internal Error",
						JOptionPane.ERROR_MESSAGE);
			}
			
			save(matching);
			JOptionPane.showMessageDialog(this.studentFS.getRootPane(),matchingSummary,
					"Matching summary",
					JOptionPane.INFORMATION_MESSAGE);
		}
		
		/**
		 * Attempts to save student and supervisor files
		 * to the outputs pointed to by the user in the 
		 * student and supervisor file gui's
		 */
		private void saveFiles() {
			String studentFileName    = this.studentFS.getStudentFileLocation();
			String supervisorFileName = this.supervisorFS.getSupervisorFileLocation();
			try {
				if (this.studentFS.saveFile()) {
					Config.getConfig().setStringValue(Config.STUDENT_INPUT_FILE, studentFileName);
				}else {
					Config.getConfig().setStringValue(Config.STUDENT_INPUT_FILE, "");
					Config.getConfig().setNonPersistantCache(Config.STUDENT_INPUT_FILE, studentFileName);
				}
				
				if (this.supervisorFS.saveFile()) {
					Config.getConfig().setStringValue(Config.SUPERVISOR_INPUT_FILE, supervisorFileName);
				}else {
					Config.getConfig().setStringValue(Config.SUPERVISOR_INPUT_FILE, "");
					Config.getConfig().setNonPersistantCache(Config.SUPERVISOR_INPUT_FILE, supervisorFileName);
				}
				Config.getConfig().save();
			}catch (IOException e) {
				logger.severe("IOException when saving student/supervisor location. Error message: <"+e.getMessage()+">");
				logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						"Error: Failed to save student/supervisor file locations, error message: "
						+ "<"+e.getMessage()+">",
						"IOException",
						JOptionPane.ERROR_MESSAGE);
			} catch (InvalidTypeException |ConfigNotValidException | CustomValidationException | JSONException e) {
				logger.severe("Exception when saving student/supervisor location. Error message: <"+e.getMessage()+">");
				logger.severe("Stack trace: " + GetStackTrace.getStackTrace(e));
				JOptionPane.showMessageDialog(this.studentFS.getRootPane(),
						"Error: Failed to save student/supervisor file locations, error message: "
						+ "<"+e.getMessage()+">",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		/**
		 * Does nothing
		 */
		@Override
		public void windowActivated(WindowEvent e) {
			/* Do nothing */
		}
		/**
		 * Does nothing
		 */
		@Override
		public void windowClosed(WindowEvent e) {
			/* Do nothing */
		}
		/**
		 * Called when user attempts to close the frame,
		 * Shuts down the logger and the logger thread
		 */
		@Override
		public void windowClosing(WindowEvent e) {
			logger.info("Window closed by user");
			
			saveFiles();
			System.exit(0);
		}
		/**
		 * Does nothing
		 */
		@Override
		public void windowDeactivated(WindowEvent e) {
			/* Do nothing */
			
		}
		/**
		 * Does nothing
		 */
		@Override
		public void windowDeiconified(WindowEvent e) {
			/* Do nothing */
		}
		/**
		 * Does nothing
		 */
		@Override
		public void windowIconified(WindowEvent e) {
			/* Do nothing */
		}
		/**
		 * Does nothing
		 */
		@Override
		public void windowOpened(WindowEvent e) {
			/* Do nothing */
		}
	}
}
