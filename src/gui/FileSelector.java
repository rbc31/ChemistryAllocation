package gui;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Class describes a file selector class.<br>
 * <br>
 * Allows a GUI to be created to open and select a file. <br>
 * <br>
 * GUI created consist of a description label, text box to enter file 
 * path and button to open a file dialog that will edit the pre-mentioned text box
 * 
 * @author Robert Cobb <br>
 * Bath University<br>
 * Email: rbc31@bath.ac.uk
 *
 */
public class FileSelector extends JPanel {

	/**
	 * Logger for this class
	 */
	private static Logger logger = Logger.getLogger(FileSelector.class.getName());
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Label holds the text to display to user next to the file selector
	 */
	private JLabel label;
	
	/**
	 * Text box that holds the text to the file location
	 */
	private JTextField textbox;
	
	/**
	 * Browse button opens file dialog to select a file
	 */
	private JButton browse;
	
	/**
	 * Private controller class to control the components on the gui
	 */
	private Controller controller;
	
	/**
	 * Default title used in the file dialog if one is not specified
	 */
	private static final String DEAFULT_FILE_SEL_TITLE = "Please select a file to import";
	
	/**
	 * Default font to be used in form creation if one isn't specified
	 */
	private static final Font DEAFULT_FONT = new Font("TimesRoman", Font.PLAIN, 18);
	
	/**
	 * Constructs a new FileSelector to allow a user to select a file
	 * @param label - The text that will be displayed on the left of the file input,
	 * text should be description of the file you wish to be imported <br>
	 * e.g. "Student file"
	 * @param file - the default file to be loaded into the file loader, use "" if no
	 * file is required
	 * @param font - The font to use when creating all the components
	 * @param promptText - The title that will be shown to the user when they open a file dialog
	 * <br>
	 * Should be a description of the file to select e.g. "Please open the student file"
	 */
	public FileSelector (String label,String file,Font font, String promptText) {
		super(new FlowLayout());
		
		if (font == null) {//set to default font
			font = DEAFULT_FONT;
		}
		
		construct(label,file,font,promptText);
	}
	
	/**
	 * Constructs a new FileSelector to allow a user to select a file <br>
	 * 
	 * This constructor will construct a fileSelector with an empty file and default title text
	 * (when user opens a file dialog) of {@value #DEAFULT_FILE_SEL_TITLE} <br>
	 * @param label - The text that will be displayed on the left of the file input,
	 * text should be description of the file you wish to be imported <br>
	 * e.g. "Student file"
	 * @param font - The font to use when creating all the components
	 */
	public FileSelector (String label,Font font) {
		this(label,"",font,DEAFULT_FILE_SEL_TITLE);
	}
	
	/**
	 * Constructs a new FileSelector to allow a user to select a file <br>
	 * 
	 * This constructor will construct a fileSelector with an empty file and default title text
	 * (when user opens a file dialog) of {@value #DEAFULT_FILE_SEL_TITLE} <br>
	 * <br>
	 * @param label - The text that will be displayed on the left of the file input,
	 * text should be description of the file you wish to be imported <br>
	 * e.g. "Student file"
	 */
	public FileSelector (String label) {
		this(label,"",DEAFULT_FONT,DEAFULT_FILE_SEL_TITLE);
	}
	
	/**
	 * Constructs the GUI
	 * @param label - The label to set the text description to
	 * @param file - The file to pre-load into the selector
	 * @param font - The font to use in creating component 
	 * @param promptText - The text to display to users when they open a file dialog
	 */
	private void construct(String label,String file,Font font,String promptText) {
		//description label
		this.label = new JLabel(label);
		this.label.setFont(font);
		this.add(this.label);
		
		//text box for manual specifying of a file path
		this.textbox = new JTextField();
		this.textbox.setText(file);
		this.textbox.setFont(font);
		this.textbox.setColumns(25);
		this.add(this.textbox);
		
		//controller of the class
		this.controller = new Controller(promptText,this.textbox);
		
		//Browse button to open a file dialog to specify a file
		this.browse = new JButton("Browse");
		this.browse.setFont(font);
		this.browse.setActionCommand("BROWSE_BTN");
		this.browse.addActionListener(this.controller);
		this.add(this.browse);
	}
	
	/**
	 * Private Controller class 
	 * 
	 */
	private class Controller implements ActionListener {
		
		/**
		 * Prompt text to give the user when a file dialog is opened
		 */
		private String promptText;
		
		/**
		 * Text field that holds the file extension to file to be loaded.
		 * The controller will update this text when a file is selected via FileDialog
		 */
		private JTextField textField;
		
		/**
		 * Constructs the controller to handle browse button events
		 * @param prompText - Text to be shown to the user in the file dialog
		 * @param textField - The textField to update when a new file is selcted
		 */
		private Controller(String prompText,JTextField textField) {
			this.promptText = prompText;
			this.textField = textField;
		}
		
		/**
		 * Handles browse events
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			
			//opens a file dialog 
			case "BROWSE_BTN":
				FileDialog fd = new FileDialog((Frame) ((JButton)e.getSource()).getTopLevelAncestor(), promptText, FileDialog.LOAD);
				fd.setVisible(true);
				
				if (fd.getDirectory() != null && fd.getFile() != null) {
					String file = fd.getDirectory() + fd.getFile();
					this.textField.setText(file);
				}
				break;
			default:
				logger.severe("Error: unknown action command on File Selector form \""+e.getActionCommand()+"\"");
				JOptionPane.showMessageDialog(this.textField.getRootPane(),
						"Error: Command not recognised: \"" + e.getActionCommand() + "\"",
						"Internal Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	
	/**
	 * Gets the file location input by the user
	 * @return The file location input by the user
	 */
	public String getFileLocal() {
		return this.textbox.getText();
	}
}
