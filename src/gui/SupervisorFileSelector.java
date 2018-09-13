package gui;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.json.JSONException;

import Exceptions.ConfigNotValidException;
import Exceptions.CustomValidationException;
import Exceptions.InvalidTypeException;
import dataManager.Config;
import utils.GetStackTrace;
/**
 * Supervisor File selector, Creates a GUI to allow users to specify
 * a supervisor file and save that file to file for future uses of the program
 *
 * @author Robert Cobb <br>
 * Bath University<br>
 * Email: rbc31@bath.ac.uk
 *
 */
public class SupervisorFileSelector extends JPanel {

	/**
	 * Logger for this class
	 */
	private static Logger logger = Logger.getLogger(SupervisorFileSelector.class.getName());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The GUi selector that lets users select the file
	 */
	private FileSelector selector;
	
	/**
	 * Check box to allow users to save the file for future use
	 */
	private JCheckBox 	 checkbox;
	
	/**
	 * Default font to be used in form creation if one isn't specified
	 */
	private static final Font DEAFULT_FONT = new Font("TimesRoman", Font.PLAIN, 18);
	
	
	/**
	 * Constructs a new Supervisor File Selector with the given font
	 * @param font - The font to use when constructing the form
	 */
	public SupervisorFileSelector(Font font) {
		super(new FlowLayout());
		
		if (font == null) {
			font = SupervisorFileSelector.DEAFULT_FONT;
		}
		
		construct(font);
	}
	
	/**
	 * Constructs a new Supervisor File Selector with the given font
	 */
	public SupervisorFileSelector() {
		this(null);
	}
	
	/**
	 * Constructs a new Supervisor file selector with the given font
	 * @param font - The font to use when constructing form
	 */
	public void construct(Font font) {
		try {
			this.checkbox = new JCheckBox("Remember File");
			this.checkbox.setFont(font);
			this.selector = new FileSelector("Input Supervisor File : ",
					Config.getConfig().getStringValue(Config.SUPERVISOR_INPUT_FILE),font, "Please load the Supervisor file");
			
			if (!Config.getConfig().getStringValue(Config.SUPERVISOR_INPUT_FILE).equals("")) {
				this.checkbox.setSelected(true);;
			}
			
		}catch (JSONException | InvalidTypeException | CustomValidationException | IOException | ConfigNotValidException e) {
			logger.severe("Exception encountered whilst trying to construct supervisor file selector. Stack trace: " + GetStackTrace.getStackTrace(e));
			this.selector = new FileSelector("Input Supervisor File : ", "",font, "Please load the Supervisor file");
		}	
		this.add(selector);
		this.add(checkbox);
	}
	
	/**
	 * Determines if the user wishes to save the current inputed data to file
	 * @return true if the user wants to save the file location to file, false otherwise
	 */
	public boolean saveFile() {
		return this.checkbox.getModel().isSelected();
	}
	
	/**
	 * Gets the location of the Supervisor file input by the user
	 * @return The file location to load the Supervisor file from
	 */
	public String getSupervisorFileLocation () {
		return this.selector.getFileLocal();
	}
}
