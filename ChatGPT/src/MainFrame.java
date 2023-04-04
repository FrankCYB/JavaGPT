import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.lilittlecat.chatgpt.offical.ChatGPT;

import okhttp3.OkHttpClient;

import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.ImageIcon;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private ChatGPT chatgpt;
	private JTextArea ChatArea;
	private String GPTConvo;
	private String GPTFullConvo;
	private Boolean first = true;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		
		setTitle("JavaGPT 0.10");		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setBounds(100, 100, 703, 686);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		//scrollPane.setBounds(10, 11, 667, 532);
		
		contentPane.add(scrollPane);
		
		JTextArea DisplayArea = new JTextArea();
		scrollPane.setViewportView(DisplayArea);
		DisplayArea.setEditable(false);
		DisplayArea.setWrapStyleWord(true);
		DisplayArea.setLineWrap(true);	
		
		JButton SubmitButton = new JButton("Submit");
		//SubmitButton.setBounds(10, 554, 89, 23);

		SubmitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SubmitButton.setText("Loading...");
				Thread myThread = new Thread(new Runnable() {				    
				    public void run() {
				    	if(first) {
				    		try {
				    			GPTConvo = chatgpt.ask(ChatArea.getText());
				    			}
				    			catch(Exception e) {
				    			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				    			}	
				    	
				    	GPTFullConvo = ChatArea.getText() + "\n" + GPTConvo + "\n";
				    	first = false;
				    	}else{
				    		try {
				    			GPTConvo = chatgpt.ask(GPTFullConvo + ChatArea.getText());
				    			}
				    			catch(Exception e) {
				    			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				    			}	
				   				    	
				    	GPTFullConvo = GPTFullConvo + ChatArea.getText() + "\n" + GPTConvo + "\n";
				    	}
				    	DisplayArea.setText(DisplayArea.getText() + "<You>\n" + ChatArea.getText() + "\n\n" + "<ChatGPT>\n" + GPTConvo + "\n\n");
				    	
				    	SubmitButton.setText("Submit");
				    }
				});

				myThread.start(); // Start the thread
				
			}
		});
		contentPane.add(SubmitButton);
		
		JButton ResetButton = new JButton("Reset");
		//ResetButton.setBounds(10, 611, 89, 23);
		ResetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GPTConvo = "";
				GPTFullConvo = "";
				DisplayArea.setText("");
				ChatArea.setText("");
				first = true;
			}
		});
		contentPane.add(ResetButton);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		//scrollPane_1.setBounds(119, 554, 558, 85);
		
		contentPane.add(scrollPane_1);
		
		ChatArea = new JTextArea();
		ChatArea.setWrapStyleWord(true);
		scrollPane_1.setViewportView(ChatArea);
		ChatArea.setLineWrap(true);
		
		JButton SaveButton = new JButton("");
		try {
		SaveButton.setIcon(new ImageIcon(MainFrame.class.getResource("FloppyDrive.gif")));
		}catch(Exception e4) {
			JOptionPane.showMessageDialog(null, e4.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		SaveButton.setFont(new Font("Arial Black", Font.BOLD, 6));
		SaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			      // Create a new JFileChooser
				  File defaultDir = new File(".");
			      JFileChooser fileChooser = new JFileChooser(defaultDir);
			      fileChooser.setDialogTitle("Save");
			      // Show the Save dialog
			      int result = fileChooser.showSaveDialog(null);

			      if (result == JFileChooser.APPROVE_OPTION) {
			         // Get the selected file
			         File selectedFile = fileChooser.getSelectedFile();

			         try {
			            // Create FileWriter object
			            FileWriter writer = new FileWriter(selectedFile);

			            // Write text to file
			            writer.write(DisplayArea.getText());

			            // Close the writer
			            writer.close();

			            // Display a success message
			            JOptionPane.showMessageDialog(null, "File saved successfully.");
			         } catch (IOException e1) {			        	 
			            e1.printStackTrace();
			            JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			         }
			      }
			}
		});
		//SaveButton.setBounds(10, 582, 89, 23);
		contentPane.add(SaveButton);
		
		JButton ImportButton = new JButton("");
		ImportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 JFileChooser fileChooser = new JFileChooser();
				 fileChooser.setDialogTitle("Import");
			        int returnVal = fileChooser.showOpenDialog(null);
			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            String filename = fileChooser.getSelectedFile().getAbsolutePath();
			            ChatArea.setText("");
			            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			                String line;
			                while ((line = br.readLine()) != null) {
			                    ChatArea.append(line);
			                    ChatArea.append("\n");
			                }
			            } catch (IOException e2) {
			                e2.printStackTrace();
			                JOptionPane.showMessageDialog(null, e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			            }
			        }
			}
		});
		ImportButton.setIcon(new ImageIcon(MainFrame.class.getResource("upFolder.gif")));
		//ImportButton.setBounds(56, 582, 43, 23);
		contentPane.add(ImportButton);
		
		//Default
		setBounds(100, 100, 703, 686);
		scrollPane.setBounds(10, 11, 667, 532);
		SubmitButton.setBounds(10, 554, 89, 23);
		ResetButton.setBounds(10, 611, 89, 23);
		scrollPane_1.setBounds(119, 554, 558, 85);
		SaveButton.setBounds(10, 582, 43, 23);
		ImportButton.setBounds(56, 582, 43, 23);		
    	
		Properties prop = new Properties();
	    InputStream input = null;
	    try {

	        input = new FileInputStream("config.properties");

	        // load a properties file
	        prop.load(input);

	        // get the property value and print it out
	        if(prop.getProperty("proxyip") != null && prop.getProperty("proxyport") != null && !prop.getProperty("proxyip").isEmpty() && !prop.getProperty("proxyport").isEmpty()) {	        	
	        	chatgpt = new ChatGPT(prop.getProperty("proxyip"), prop.getProperty("proxyip"), Integer.parseInt(prop.getProperty("proxyport")));	
	        }else if(prop.getProperty("OkHttpClient").equals("true")){	        	
	        	OkHttpClient httpclient = new OkHttpClient.Builder()
	        		    .connectTimeout((prop.getProperty("OHC_connectTimeout") != null && prop.getProperty("OHC_connectTimeout").isEmpty()) ? Long.parseLong(prop.getProperty("OHC_connectTimeout")) : 10, TimeUnit.SECONDS)
	        		    .writeTimeout((prop.getProperty("OHC_writeTimeout") != null && prop.getProperty("OHC_writeTimeout").isEmpty()) ? Long.parseLong(prop.getProperty("OHC_writeTimeout")) : 10, TimeUnit.SECONDS)
	        		    .readTimeout((prop.getProperty("OHC_readTimeout") != null && prop.getProperty("OHC_readTimeout").isEmpty()) ? Long.parseLong(prop.getProperty("OHC_readTimeout")) : 30, TimeUnit.SECONDS)
	        		    .build();
	        	chatgpt = new ChatGPT(prop.getProperty("apikey"), httpclient);
	        }else {
	        	chatgpt = new ChatGPT(prop.getProperty("apikey"));	
	        }
	        if(prop.getProperty("WindowSize").equals("small")){
	        	setBounds(100, 100, 491, 568);
	        	scrollPane_1.setBounds(103, 454, 363, 69);
	        	scrollPane.setBounds(10, 11, 456, 432);
	        	SubmitButton.setBounds(10, 454, 89, 23);
	        	SaveButton.setBounds(10, 477, 43, 23);
	        	ImportButton.setBounds(56, 477, 43, 23);
	        	ResetButton.setBounds(10, 500, 89, 23);
	        }else {
	        	
	        }

	    } catch (IOException ex) {	    	
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        
	    } finally {
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	            }
	        }
	    }
	}
}
