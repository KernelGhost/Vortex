// Indexed JLabels are used to represent the game board on the GUI.
import javax.swing.JLabel;

public class GridBox extends JLabel {
	private static final long serialVersionUID = 7197962877418899056L;
	private int integer_id;
	
	public GridBox(int i) {
		this.integer_id = i;
	}
	
	public int GetIndex() {
		return this.integer_id;
	}
}
