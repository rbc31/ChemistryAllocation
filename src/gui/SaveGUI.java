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
 * Class to allow users to select locations to save outputs too
 * 
 * @author Robert Cobb <br>
 * Bath University<br>
 * Email: rbc31@bath.ac.uk
 */
public class SaveGUI extends JPanel {

	/**
	 * Logger for this class
	 */
	private static Logger logger = Logger.getLogger(SaveGUI.class.getName());
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Font to use to use to build the form
	 */
	private Font font;
	
	/**
	 * Default font to be used in form creation if one isn't specified
	 */
	private static final Font DEFAULT_FONT = new Font("TimesRoman", Font.PLAIN, 18);
	
	/**
	 * Private description label - tells the user what file to select
	 */
	private JLabel lbl_description;
	
	/**
	 * browse button to open a file dialog
	 */
	private JButton btn_browse;
	
	/**
	 * Text field to enter the file manually
	 */
	private JTextField txt_fileLocation;
	
	/**
	 * Prompt text shown when user opens a dialog to select a file to save
	 */
	private final String PROMPT_TEXT = "Please choose where to save output too";
	
	/**
	 * Constructs a new Save Gui with the given font and label
	 * Gives users an gui to select a folder to save files to
	 * @param font - The font to use in constructing the form
	 * if null, deafult font will be used
	 * @param label - The string text to display to the user in the label 
	 * on thsi gui
	 */
	public SaveGUI(Font font,String label) {
		
		if (font != null) {
			this.font = font; 
		}else {
			this.font = DEFAULT_FONT;
		}
		
		construct(this.font,label);
	}
	
	/**
	 * Constructs the save gui
	 * @param font - The font to use in constructing the form
	 * @param label - the label to give the jlabel on the gui
	 */
	private void construct(Font font,String label) {
		this.setLayout(new FlowLayout());
		
		this.lbl_description = new JLabel(label);
		this.lbl_description.setFont(font);
		this.add(lbl_description);
		
		this.txt_fileLocation = new JTextField("");
		this.txt_fileLocation.setColumns(25);
		this.txt_fileLocation.setFont(font);
		this.add(txt_fileLocation);
		
		this.btn_browse = new JButton("Browse");
		this.btn_browse.setFont(font);
		this.btn_browse.addActionListener(new Controller(txt_fileLocation));
		this.btn_browse.setActionCommand("BROWSE");
		this.add(btn_browse);
	}
	
	/**
	 * Returns true if the user wants to save, false otherwise
	 * @return true if the wants to save, false otherwise
	 */
	public boolean userWantstoSave() {
		//if there is text in the text box then user wants to save
		return !this.txt_fileLocation.getText().equals("");
	}
	
	/**
	 * Returns the file location of where the user wishes to save 
	 * output to
	 * @return The file location of where the user wishes to save 
	 * output to
	 */
	public String getFile() {
		return this.txt_fileLocation.getText();
	}
	
	/**
	 * Private controller to control the save gui
	 * 
	 * @author Robert Cobb <br>
	 * Bath University<br>
	 * Email: rbc31@bath.ac.uk
	 */
	private class Controller implements ActionListener {

		/**
		 * Text field to update the text on
		 * Text field displays where the output is to be save too
		 */
		private JTextField txt_field;
		
		/**
		 * Constructs a new controller to control the elements
		 * on the gui
		 * @param txt_field - The text field to update when a user selects
		 * a new folder to save output to
		 */
		private Controller(JTextField txt_field) {
			this.txt_field = txt_field;
		}
		
		/**
		 * Handles browse events
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			switch (arg0.getActionCommand()) {
			case "BROWSE":
				FileDialog fd = new FileDialog((Frame) ((JButton)arg0.getSource()).getTopLevelAncestor(), PROMPT_TEXT, FileDialog.SAVE);
				fd.setFile("*.csv");
				fd.setVisible(true);
				
				if (fd.getDirectory() != null && fd.getFile() != null) {
					String file = fd.getDirectory() + fd.getFile();
					if (!file.endsWith(".csv")) {
						file += ".csv";
					}
					
					this.txt_field.setText(file);
				}
				break;
			default:
				logger.info("Error: unknown action command on save GUI form \""+arg0.getActionCommand()+"\"");
				JOptionPane.showMessageDialog(this.txt_field.getRootPane(),
						"Error: Command not recognised: \"" + arg0.getActionCommand() + "\"",
						"Internal Error",
						JOptionPane.ERROR_MESSAGE);
			}
			
		}
		
	}
}
