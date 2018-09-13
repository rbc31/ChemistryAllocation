package gui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Class describes a GUI to allow users to select parameters in 
 * the algorithm
 * 
 * 
 * @author Robert Cobb <br>
 * Bath University<br>
 * Email: rbc31@bath.ac.uk
 *
 */
public class AlgorithOptionsGui extends JPanel {

	/**
	 * Logger for this class
	 */
	private static Logger logger = Logger.getLogger(AlgorithOptionsGui.class.getName());
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Label to describe the capped percentage picker
	 */
	private JLabel lbl_capped;
	
	
	/**
	 * Combo box to allow users to cap the algorithm 
	 * parameter at a certain level
	 * <br>
	 * Capping the total percentage of students that can be 
	 * assigned to a topic area
	 */
	private JComboBox<Integer>   cbo_percentage;

	/**
	 * Font to use on the form
	 */
	private Font font;
	
	/**
	 * Default font to be used in form creation if one isn't specified
	 */
	private static final Font DEAFULT_FONT = new Font("TimesRoman", Font.PLAIN, 18);
	
	
	/**
	 * Constructs a new GUI to select algorithm options on
	 * @param font - The font to be used in constructing the form,
	 * is null a default font will be used
	 */
	public AlgorithOptionsGui(Font font) {
		if (font == null) {
			this.font = AlgorithOptionsGui.DEAFULT_FONT;
		}else {
			this.font = font;
		}
		
		construct(this.font);
	}
	
	/**
	 * Construct the form
	 * @param font - The font to use in construction
	 */
	private void construct(Font font) {	
		logger.info("Constructing a new " + this.getClass().getName() + " gui");
		//panel to allow users to select a percentage to cap 
		JPanel pnl_percentage = new JPanel(new FlowLayout());
		
		//add label
		this.lbl_capped = new JLabel("Capped percentage: ");
		this.lbl_capped.setFont(font);
		this.lbl_capped.setEnabled(false);
		pnl_percentage.add(lbl_capped);
		
		Integer [] options = {25,30,35,40,45,50,55,60,65,70,75,80,85,90,95,100};
		this.cbo_percentage = new JComboBox<Integer>(options);
		this.cbo_percentage.setFont(font);
		pnl_percentage.add(cbo_percentage);
		
		
		this.add(pnl_percentage);	
		
	}
	
	/**
	 * Returns the capped percentage the user wishes to use in the algorithm
	 * @return The capped percentage the user wishes to use in the algorithm
	 */
	public int cappedPercentage() {
		Object temp = this.cbo_percentage.getModel().getSelectedItem();
		if (temp instanceof Integer) {
			return (Integer) temp;
		}else {
			logger.warning("Returning capped percentae of -1, no percentage selcted");
			return -1;
		}
		
	}
}
