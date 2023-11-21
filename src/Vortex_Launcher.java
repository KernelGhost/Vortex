import java.awt.EventQueue;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;

// Launch the application.
public class Vortex_Launcher {
	public static Vortex vortex_window;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Set 'FlatLaf' look and feel.
				try {
				    UIManager.setLookAndFeel(new FlatCarbonIJTheme());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Could not start the application with the preferred look and feel.\nUsing system defaults.", "Vortex", JOptionPane.ERROR_MESSAGE, null);
				}
				
				try {
					// Launch Vortex.
					vortex_window = new Vortex();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Could not start the application. Exiting.", "Vortex", JOptionPane.ERROR_MESSAGE, null);
					System.exit(1);
				}
			}
		});
	}
}
