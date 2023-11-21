import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;

public class About extends JFrame {
	private static final long serialVersionUID = 7117221682268127618L;
	private JPanel contentPane;
	
	// Called on close to dispose the JFrame
	private void Close() {
		this.dispose();
	}
	
	public About() {
		// Set icon image.
		setIconImage(Toolkit.getDefaultToolkit().getImage(About.class.getResource("/resources/graphics/icon.png")));
		
		// Set window title.
		setTitle("About Vortex");
		
		// Disable resizing.
		setResizable(false);
		
		// Configure JPanel.
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(new Dimension(610, 220));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Set JFrame to self-dispose on closing.
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	Close();
		    }
		});
		
		// Pack and centre the JFrame.
		pack();
		setLocationRelativeTo(null);
		
		// LABELS
		// lblHeading
		JLabel lblHeading = new JLabel("VORTEX");
		lblHeading.setHorizontalAlignment(SwingConstants.LEFT);
		lblHeading.setFont(new Font("Lucida Grande", Font.BOLD, 35));
		lblHeading.setBounds(220, 8, 330, 48);
		contentPane.add(lblHeading);
		
		// lblVersion
		JLabel lblVersion = new JLabel("Version 4.0");
		lblVersion.setHorizontalAlignment(SwingConstants.LEFT);
		lblVersion.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblVersion.setBounds(220, 48, 330, 25);
		contentPane.add(lblVersion);
		
		// lblAuthor
		JLabel lblAuthor = new JLabel("By KernelGhost");
		lblAuthor.setHorizontalAlignment(SwingConstants.LEFT);
		lblAuthor.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblAuthor.setBounds(220, 68, 330, 25);
		contentPane.add(lblAuthor);
		
		// lblVortex
		JLabel lblVortex = new JLabel("<html><body style=\"text-align: justify; text-justify: inter-word;\">Vortex is a simple 'Minimax' Tic-Tac-Toe AI written in Java. Alpha-beta pruning and multithreading are utilised to enhance overall performance.<br><br>Vortex uses FlatLaf, a modern open-source cross-platform Look and Feel for Java Swing desktop applications. You can learn more about it at <a href='https://github.com/JFormDesigner/FlatLaf'>https://github.com/JFormDesigner/FlatLaf</a>.</body></html>");
		lblVortex.setHorizontalAlignment(SwingConstants.LEFT);
		lblVortex.setVerticalAlignment(SwingConstants.TOP);
		lblVortex.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblVortex.setBounds(220, 93, 360, 97);
		contentPane.add(lblVortex);
		
		// IMAGES
		// AppIcon
		try {
			JLabel lblIcon = new JLabel("");
			lblIcon.setBounds(30, 30, 160, 160);
			BufferedImage imgIcon;
			imgIcon = ImageIO.read(getClass().getResource("/resources/graphics/icon.png"));
			Image imgIconScaled = imgIcon.getScaledInstance(lblIcon.getWidth(), lblIcon.getHeight(), Image.SCALE_SMOOTH);
			ImageIcon icoIcon = new ImageIcon(imgIconScaled);
			lblIcon.setIcon(icoIcon);
			contentPane.add(lblIcon);
		} catch (IOException e1) {}
	}
}
