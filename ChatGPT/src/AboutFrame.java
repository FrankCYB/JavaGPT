import java.awt.Cursor;
import java.awt.Desktop;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class AboutFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public AboutFrame() {
		setTitle("About");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 401, 271);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(AboutFrame.class.getResource("logo.png")));
		lblNewLabel.setBounds(160, 22, 84, 83);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("JavaGPT " + MainFrame.version);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 23));
		lblNewLabel_1.setBounds(118, 105, 169, 51);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("(April 19 2023)");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel_2.setBounds(142, 145, 114, 28);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Source code:");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_3.setBounds(24, 200, 84, 28);
		contentPane.add(lblNewLabel_3);
		JLabel lblHttpsgithubcomfrankcybjavagpt;
		if(MainFrame.seltheme != 1) {
		lblHttpsgithubcomfrankcybjavagpt = new JLabel("<html><a href=\\\"https://github.com/FrankCYB/JavaGPT\\\">https://github.com/FrankCYB/JavaGPT</a></html>");
		}else {
		lblHttpsgithubcomfrankcybjavagpt = new JLabel("<html><a style='color: yellow;' href='https://github.com/FrankCYB/JavaGPT'>https://github.com/FrankCYB/JavaGPT</a></html>");
		}
		lblHttpsgithubcomfrankcybjavagpt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				 try {
	                    // Get the URI of the hyperlink
	                    URI uri = new URI("https://github.com/FrankCYB/JavaGPT");

	                    // Open the URI in a browser
	                    Desktop.getDesktop().browse(uri);

	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                }
				 				 
			}
			public void mouseEntered(MouseEvent e) {
                // Change the cursor to a hand when the mouse enters the label
				lblHttpsgithubcomfrankcybjavagpt.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Change the cursor back to the default when the mouse leaves the label
            	lblHttpsgithubcomfrankcybjavagpt.setCursor(Cursor.getDefaultCursor());
            }
		});
				
		lblHttpsgithubcomfrankcybjavagpt.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblHttpsgithubcomfrankcybjavagpt.setBounds(115, 200, 270, 28);
		contentPane.add(lblHttpsgithubcomfrankcybjavagpt);
		
		Label label = new Label(MainFrame.prop.getProperty("model"));
		label.setAlignment(Label.CENTER);
		label.setBounds(140, 172, 114, 22);
		contentPane.add(label);
	}
}
