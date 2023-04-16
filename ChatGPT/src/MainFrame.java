import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.google.gson.Gson;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;


import javax.swing.JTextArea;
//import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

//import java.awt.Frame; //Uncomment this when editing design
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JEditorPane;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

public class MainFrame extends JFrame {

	private JPanel contentPane;

	private OpenAiService service;
	private final static ArrayList<ChatMessage> messages = new ArrayList<>();
	private static JTextArea ChatArea;
    private JButton SubmitButton;
	private static JEditorPane DisplayArea;
	private static StyledDocument doc;
	private JMenuBar menuBar;
	private static String GPTConvo;

	private File FGPTConvo;
	
	public static Properties prop;
	public static String version = "1.0.5";
	private Boolean first = true;
	private Boolean autosave = true;
	private Boolean autotitle = true;
	private Boolean cloaderopen = false;
	private Boolean aframeopen = false;
	public static int seltheme = 0;
	private ChatLoader cloader;
	private String chatDir;
	private static Style YouStyle;
	private static Style GPTStyle;
	private static Style ChatStyle;
	private static MainFrame INSTANCE = null;
	

    public static void loadchat(String fullfilepath, String filename) throws BadLocationException {

    	INSTANCE.setTitle("JavaGPT - " + filename);
		try {		
		
			DisplayArea.setText("");

			messages.clear();
			readMessagesFromFile(fullfilepath);
			
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		INSTANCE.FGPTConvo = new File(fullfilepath);

		INSTANCE.first = false;
		
    }
    
    public void writeMessagesToFile(String filename) throws IOException {
    	try (PrintWriter writer = new PrintWriter(filename)) {
            Gson gson = new Gson();
            for (ChatMessage message : messages) {
                String json = gson.toJson(message);
                writer.println(json);
            }
        }
    }

    public static void readMessagesFromFile(String filename) throws IOException {
    	try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            Gson gson = new Gson();
            while ((line = reader.readLine()) != null) {
                ChatMessage message = gson.fromJson(line, ChatMessage.class);
                if(message.getRole().equals("user")) {
                	try {
		    		    doc.insertString(doc.getLength(), "You\n", YouStyle);
		    		    doc.insertString(doc.getLength(), message.getContent() + "\n\n", ChatStyle);
		    		} catch (BadLocationException e) {
		    		    e.printStackTrace();
		    		}	
                }else{
                	try {
		    		    doc.insertString(doc.getLength(), "ChatGPT\n", GPTStyle);
		    		    doc.insertString(doc.getLength(), message.getContent() + "\n\n", ChatStyle);
		    		} catch (BadLocationException e) {
		    		    e.printStackTrace();
		    		}	
                }
                messages.add(message);
            }
        }
    }
    
    public static String getRandomString() {
        String letters = "abcdefghijklmnopqrstuvwxyz1234567890";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < 3; i++) {
            int index = rand.nextInt(letters.length());
            sb.append(letters.charAt(index));
        }
        
        return sb.toString();
    }
    
    
    public void newFile() {
    	String randfilename = getRandomString();
		FGPTConvo = new File(chatDir + "\\Chat_" + randfilename  + ".json");
		while(FGPTConvo.exists()) {
			randfilename = getRandomString();
			FGPTConvo = new File(chatDir + "\\Chat_" + randfilename + ".json");		
		}
		setTitle("JavaGPT - Chat_" + randfilename);	
    }
    
    public void Reset() { 
    	messages.clear();
    	FGPTConvo = null;
		GPTConvo = "";
		DisplayArea.setText("");
		ChatArea.setText("");
		setTitle("JavaGPT");
		first = true;
    }

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				   //Loads properties------------------------
					prop = new Properties();
				    InputStream input = null;
				    
				    try {
						input = new FileInputStream("config.properties");
						prop.load(input);
					} catch (FileNotFoundException e1) {
						int choice = JOptionPane.showConfirmDialog(null,
				                "No config file found. Would you like to create one?",
				                "Create Config File", JOptionPane.YES_NO_OPTION);
				            
				            if(choice == JOptionPane.YES_OPTION) {
				                String apikey = JOptionPane.showInputDialog(
				                    null, "Please enter your API key:");				    			         
				                    
				                prop.setProperty("apikey", apikey);
				                prop.setProperty("model", "gpt-3.5-turbo");
				                prop.setProperty("maxTokens", "1024");
				                prop.setProperty("timeout", "30");
				                //prop.setProperty("proxyip", ""); // WIP Support will be added back
				                //prop.setProperty("proxyport", ""); // WIP Support will be added back
				                prop.setProperty("autosave", "true");
				                prop.setProperty("autotitle", "true");
				                prop.setProperty("chat_location_override", "");
				                prop.setProperty("WindowSize", "");
				                prop.setProperty("Theme", "dark");
				                
				                try {
				                    FileOutputStream out = new FileOutputStream("config.properties");
				                    prop.store(out, "Generated config file");
				                    out.close();
				                    
				                    JOptionPane.showMessageDialog(null, "Config file created successfully!");
				                } catch (IOException ex) {
				                    ex.printStackTrace();
				                }
				            }
						e1.printStackTrace();
					}
				       catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
				    //----------------------------------------
				   
				    
				    //Sets selected JTattoo theme-------------
				        try {
				        	if(!prop.getProperty("Theme").isEmpty()) {
					        if(prop.getProperty("Theme").equals("dark")) {
					        	Properties p = new Properties();
								p.put("windowTitleFont", "Ebrima PLAIN 15");
								p.put("backgroundPattern", "off");
								p.put("logoString", "");
								HiFiLookAndFeel.setCurrentTheme(p);
								UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
								seltheme = 1;
					        }
					       }
				         } catch (Exception e) {
							e.printStackTrace();
					     }
				      //----------------------------------------
				        	
					MainFrame frame = new MainFrame(); //Loads main JFrame
					 
				//Scales JFrame based on "WindowSize" prop	
				if(prop.getProperty("WindowSize").equals("small")){
					
					frame.getContentPane().setPreferredSize(new Dimension(475, 532));
					
				}else if(prop.getProperty("WindowSize").equals("large")){
					
					frame.getContentPane().setPreferredSize(new Dimension(1370, 960));
					
				}else {
					
					frame.getContentPane().setPreferredSize(new Dimension(686, 647));
					
				}
				frame.pack();
				frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.png")));
				//----------------------------------------
				frame.setVisible(true);
				
			}
		});
	}

	/**
	 * Create the frame.
	 * @param GPTStyle 
	 * @param ChatStyle 
	 */
	public MainFrame() {
		setResizable(false);
		INSTANCE = this;
		
		setTitle("JavaGPT");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		service = new OpenAiService(prop.getProperty("apikey"),(prop.getProperty("timeout") == null && prop.getProperty("timeout").isEmpty()) ? Duration.ZERO : Duration.ofSeconds(Long.parseLong(prop.getProperty("timeout"))));
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu OptionMenu = new JMenu("Options");
		menuBar.add(OptionMenu);
		
		JMenu mnNewMenu = new JMenu("Rename");
		OptionMenu.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Auto");
		mnNewMenu.add(mntmNewMenuItem_3);
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	if(FGPTConvo != null) {
		    		AutoTitle();
		    	}else {
		    		JOptionPane.showMessageDialog(null, "No file to rename", "Error", JOptionPane.ERROR_MESSAGE);
		    	}
		
		    	
		    }
		});
		
		JMenuItem mntmNewMenuItem_4 = new JMenuItem("Manual");
		mnNewMenu.add(mntmNewMenuItem_4);
		
		mntmNewMenuItem_4.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {		    			    	       	            
		    	if(FGPTConvo != null) {
		    	String title = JOptionPane.showInputDialog(null, "Please enter a title:", "Rename", JOptionPane.PLAIN_MESSAGE);
		    	if(title != null) {		    	
				File file = new File(FGPTConvo.getParentFile(), title + ".json");
				if(file.exists()) {
					JOptionPane.showMessageDialog(null, "File already exists", "Error", JOptionPane.ERROR_MESSAGE);
				}else {
					FGPTConvo.renameTo(file);
					FGPTConvo = file;
					JOptionPane.showMessageDialog(null, "File renamed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
					INSTANCE.setTitle("JavaGPT - " + title);
				}
		      }
		    	}else {
		    		JOptionPane.showMessageDialog(null, "No file to rename", "Error", JOptionPane.ERROR_MESSAGE);
		    	}
		    	
		    }
		});
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Delete");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	if(FGPTConvo != null && FGPTConvo.exists()) { //checks if the file exists		    		
			        FGPTConvo.delete(); //deletes the file
			        Reset();	             
		         } else {
		        	 JOptionPane.showMessageDialog(null, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
		         }
		         
		    }
		});
		OptionMenu.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("About");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {	    	
		    	if(aframeopen != true) {
					AboutFrame aframe = new AboutFrame();
					aframe.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.png")));
					aframe.setVisible(true);
					aframeopen = true;
					aframe.addWindowListener(new java.awt.event.WindowAdapter() {
			            @Override
			            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
			            	aframeopen = false;
			            }
			        });
					}
		         
		    }
		});
		OptionMenu.add(mntmNewMenuItem_2);
		
		JMenu LoadChatButton = new JMenu("Load Chat");
		LoadChatButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(cloaderopen != true) {
				cloader = new ChatLoader(chatDir);
				cloader.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.png")));
				cloader.setVisible(true);
				cloaderopen = true;
				cloader.addWindowListener(new java.awt.event.WindowAdapter() {
		            @Override
		            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		            	cloaderopen = false;
		            }
		        });
				}
			}
		});
		
		menuBar.add(LoadChatButton);
		
			
		JMenu AutoSaveButton = new JMenu("Auto Save (Null)");
		AutoSaveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(autosave) {
					autosave = false;
					AutoSaveButton.setText("Auto Save (Off)");
				}else {
					autosave = true;
					AutoSaveButton.setText("Auto Save (On)");
				}
			}
		});
		menuBar.add(AutoSaveButton);
				
		contentPane = new JPanel();
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);			
		
		JScrollPane scrollPane = new JScrollPane();
		
		contentPane.add(scrollPane);


		DisplayArea = new JEditorPane();
		scrollPane.setViewportView(DisplayArea);
		DisplayArea.setEditable(false);
		DisplayArea.setContentType("text/rtf");

		// Define a style for bold text
		
		
		StyleContext sc = StyleContext.getDefaultStyleContext();
		
		YouStyle = sc.addStyle("bold", null);
		StyleConstants.setFontFamily(YouStyle, "Tahoma");
		StyleConstants.setBold(YouStyle, true);
		if(seltheme == 1) {
			StyleConstants.setForeground(YouStyle, Color.ORANGE); //getHSBColor(30f/360, 0.8f, 1f)
		}else {
			StyleConstants.setForeground(YouStyle, Color.BLUE);
		}
		
		
		GPTStyle = sc.addStyle("bold", null);
		StyleConstants.setFontFamily(GPTStyle, "Tahoma");
		StyleConstants.setBold(GPTStyle, true);
		StyleConstants.setForeground(GPTStyle, Color.RED); //getHSBColor(0, 0.8f, 0.8f)
		
		ChatStyle = sc.addStyle("black", null);
		StyleConstants.setFontFamily(ChatStyle, "Tahoma");
		if(seltheme == 1) {
			StyleConstants.setForeground(ChatStyle, Color.WHITE); //Color.getHSBColor(0f, 0f, 0.8f)
		}else {
			StyleConstants.setForeground(ChatStyle, Color.BLACK);
		}
		
		Style ErrorStyle = sc.addStyle("ErrorStyle", null);
		StyleConstants.setItalic(ErrorStyle, true);
		StyleConstants.setFontFamily(ErrorStyle, "Tahoma");
	
		doc = (StyledDocument) DisplayArea.getDocument();
		//
		SimpleAttributeSet codeBlockAttributes = new SimpleAttributeSet();
		StyleConstants.setFontFamily(codeBlockAttributes, "Courier New");
		StyleConstants.setFontSize(codeBlockAttributes, 10);
		StyleConstants.setForeground(codeBlockAttributes, new Color(0, 128, 0));
		StyleConstants.setSpaceBelow(codeBlockAttributes, 8);
		StyleConstants.setSpaceAbove(codeBlockAttributes, 8);
		StyleConstants.setAlignment(codeBlockAttributes, StyleConstants.ALIGN_LEFT);
		StyleConstants.setLeftIndent(codeBlockAttributes, 20);
		StyleConstants.setRightIndent(codeBlockAttributes, 20);
		StyleConstants.setFirstLineIndent(codeBlockAttributes, -20);
		//
		
		SubmitButton = new JButton("Submit");

		SubmitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Thread myThread = new Thread(new Runnable() {				    
				    public void run() {	
				    	
				    	SubmitButton.setText("Loading...");				    	
				    	Boolean success = false;
				    					    
				    	try {
			    		    doc.insertString(doc.getLength(), "You\n", YouStyle);
			    		    doc.insertString(doc.getLength(), ChatArea.getText() + "\n\n", ChatStyle);
			    		    doc.insertString(doc.getLength(), "ChatGPT\n", GPTStyle);
			    		} catch (BadLocationException e2) {
			    		    e2.printStackTrace();
			    		}	
				    	
				    	
				    		try {
								
						    	StringBuilder GPTConvoBuilder = new StringBuilder();						    	
						    							    	
					            final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), ChatArea.getText());
					            messages.add(userMessage);
					            
					            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
					                    .builder()
					                    .model(prop.getProperty("model"))
					                    .messages(messages)
					                    .n(1)
					                    .maxTokens(Integer.parseInt(prop.getProperty("maxTokens")))
					                    .logitBias(new HashMap<>())
					                    .build();
					            
					            service.streamChatCompletion(chatCompletionRequest)
					                    .doOnError(Throwable::printStackTrace)
					                    .blockingForEach(chunk -> {					                    	
					                        for (ChatCompletionChoice choice : chunk.getChoices()) {
					                        	if(choice.getMessage().getContent() != null) {
					                        	GPTConvoBuilder.append(choice.getMessage().getContent());
					                        	}
									    		try {								    			
									    			//String messageContent = new String(choice.getMessage().getContent().getBytes("UTF-8"), "UTF-8");
									    			//doc.putProperty("console.encoding", "UTF-8");
	
									    		    doc.insertString(doc.getLength(), choice.getMessage().getContent(), ChatStyle);
									    			
									    		} catch (BadLocationException e2) {
									    		    e2.printStackTrace();
									    		}	
					                           //System.out.println(choice.getMessage());
					                        }//
					                    });
					            try {
					    		    doc.insertString(doc.getLength(), "\n\n", ChatStyle);		
					    		} catch (BadLocationException e2) {
					    		    e2.printStackTrace();
					    		}
					            //service.shutdownExecutor();
					            
					            GPTConvo = GPTConvoBuilder.toString();
					            final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), GPTConvo);
					            messages.add(systemMessage);
								if(first) {	
								newFile();	
				    			if(!autotitle) {					    				
				    			first = false;			    			
				    			}
								}
								
								if(autosave) {				    		
						    		try {
										writeMessagesToFile(FGPTConvo.getPath());
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
						    	}
						    	//Runs when autotitle is true and first is true
						    	if(first) {
						    		AutoTitle();
						    		first = false;
						    	}
				    			
				    		}catch(Exception e) {
				    			//JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				    			try {
					    		    doc.insertString(doc.getLength(), "Error: " + e.getMessage() + "\n\n", ErrorStyle);
					    		} catch (BadLocationException e2) {
					    		    e2.printStackTrace();
					    		}
				    			}					    						    	
				    	
				    	SubmitButton.setText("Submit");
				    }
				});
				myThread.start(); // Start the thread				
			}
		});
		contentPane.add(SubmitButton);
				
			
		JButton ResetButton = new JButton("Reset");
		ResetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Reset();
			}
		});
		contentPane.add(ResetButton);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
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
			      fileChooser.setDialogTitle("Save chat");
			      // Show the Save dialog
			      int result = fileChooser.showSaveDialog(null);

			      if (result == JFileChooser.APPROVE_OPTION) {
			         // Get the selected file
			         File selectedFile = fileChooser.getSelectedFile();

			         try {
			            // Create FileWriter object
			            FileWriter writer = new FileWriter(selectedFile);
			            
			            //Convert rtf doc to plain txt
			            String plaintext = DisplayArea.getDocument().getText(0, DisplayArea.getDocument().getLength());
			            
			            // Write text to file
			            writer.write(plaintext);

			            // Close the writer
			            writer.close();

			            // Display a success message
			            JOptionPane.showMessageDialog(null, "File saved successfully.");
			         } catch (IOException e1) {			        	 
			            e1.printStackTrace();
			            JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			         } catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			      }
			}
		});

		contentPane.add(SaveButton);
		
		JButton ImportButton = new JButton("");
		ImportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 JFileChooser fileChooser = new JFileChooser();
				 fileChooser.setDialogTitle("Import prompt");
			        int returnVal = fileChooser.showOpenDialog(null);
			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            String filename = fileChooser.getSelectedFile().getAbsolutePath();
			            try {
							ChatArea.setText(new String(Files.readAllBytes(Paths.get(filename))));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
			        }
			}
		});
		ImportButton.setIcon(new ImageIcon(MainFrame.class.getResource("upFolder.gif")));
		contentPane.add(ImportButton);
		
		DisplayArea.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            showPopupMenu(e.getX(), e.getY());
		        }
		    }

		    @Override
		    public void mouseReleased(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            showPopupMenu(e.getX(), e.getY());
		        }
		    }
		});

		ChatArea.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            showPopupMenu(e.getX(), e.getY());
		        }
		    }

		    @Override
		    public void mouseReleased(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            showPopupMenu2(e.getX(), e.getY());
		        }
		    }
		});

		    
		//Default
		setBounds(100, 100, 702, 707); //Uncomment this when editing design
		SubmitButton.setBounds(10, 554, 89, 23);
		ResetButton.setBounds(10, 616, 89, 23);
		scrollPane.setBounds(10, 11, 667, 532);
		scrollPane_1.setBounds(109, 554, 568, 85);
		SaveButton.setBounds(10, 585, 43, 23);
		ImportButton.setBounds(56, 585, 43, 23);
    	
		
		//Bulk property setting-------------------
	    try {
	        if(prop.getProperty("proxyip") != null && prop.getProperty("proxyport") != null && !prop.getProperty("proxyip").isEmpty() && !prop.getProperty("proxyport").isEmpty()) {	        	
	        	
	        	
	        }else {
	        	
	        }
	        if(prop.getProperty("autosave").equals("true")){
	        	autosave = true;
	        	AutoSaveButton.setText("Auto Save (On)");
	        }else{
	        	autosave = false;
	        	AutoSaveButton.setText("Auto Save (Off)");
	        }
	        
	        if(prop.getProperty("autotitle").equals("true")){
	        	autotitle = true;
	        }else{
	        	autotitle = false;
	        }
	        
	        if(prop.getProperty("chat_location_override") != null && !prop.getProperty("chat_location_override").isEmpty()){
	        	chatDir = prop.getProperty("chat_location_override");
	        }else {
	        	try {	        		
	    			chatDir = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
	    			chatDir = chatDir + "\\chat_history";
	    			 File directory = new File(chatDir);
	    		        if (!directory.exists()) {
	    		            directory.mkdirs();
	    		        }	    		        
	    		} catch (URISyntaxException e1) {
	    			JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    		}	        	
	        }
	        
	        if(prop.getProperty("WindowSize").equals("small")){
	        	//setBounds(100, 100, 481, 584); //Uncomment this when editing design
	        	scrollPane_1.setBounds(103, 454, 363, 69);
	        	scrollPane.setBounds(10, 11, 456, 432);
	        	SubmitButton.setBounds(10, 454, 89, 23);
	        	SaveButton.setBounds(10, 477, 43, 23);
	        	ImportButton.setBounds(56, 477, 43, 23);
	        	ResetButton.setBounds(10, 500, 89, 23);	        	
	        }else if(prop.getProperty("WindowSize").equals("large")) {
	        	//setBounds(100, 100, 702, 707);
	    		SubmitButton.setBounds(13, 831, 148, 36);
	        	ResetButton.setBounds(13, 914, 148, 36);
	        	scrollPane.setBounds(13, 15, 1344, 802);
	        	scrollPane_1.setBounds(171, 831, 1186, 118);
	        	SaveButton.setBounds(13, 873, 73, 36);
	        	ImportButton.setBounds(88, 873, 73, 36);
	        }else {	     	    		
	        	SubmitButton.setBounds(10, 554, 89, 23);
	    		ResetButton.setBounds(10, 616, 89, 23);
	    		scrollPane.setBounds(10, 11, 667, 532);
	    		scrollPane_1.setBounds(109, 554, 568, 85);
	    		SaveButton.setBounds(10, 585, 43, 23);
	    		ImportButton.setBounds(56, 585, 43, 23);
	        }
	    //----------------------------------------
	    } catch (Exception ex) {	    	
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        
	    } 
	}
private void showPopupMenu(int x, int y) {
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem copyMenuItem = new JMenuItem("Copy");
    copyMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedText = DisplayArea.getSelectedText();
            if (selectedText != null) {
                StringSelection selection = new StringSelection(selectedText);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
            }
        }
    });
    popupMenu.add(copyMenuItem);
    popupMenu.show(DisplayArea, x, y);
}

private void showPopupMenu2(int x, int y) {
    JPopupMenu popupMenu = new JPopupMenu();
    
    JMenuItem copyMenuItem = new JMenuItem("Copy");
    copyMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedText = ChatArea.getSelectedText();
            if (selectedText != null) {
                StringSelection selection = new StringSelection(selectedText);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
            }
        }
    });
    popupMenu.add(copyMenuItem);

    JMenuItem pasteMenuItem = new JMenuItem("Paste");
    pasteMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    String text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    int caretPos = ChatArea.getCaretPosition();
                    ChatArea.insert(text, caretPos);
                } catch (UnsupportedFlavorException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    });
    popupMenu.add(pasteMenuItem);
      
    JMenuItem clearMenuItem = new JMenuItem("Clear");
    clearMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            ChatArea.setText("");
        }
    });
    popupMenu.add(clearMenuItem); 
    
    popupMenu.show(ChatArea, x, y);
}
	public void AutoTitle() {
		Thread myThread = new Thread(new Runnable() {
			public void run() {
				setTitle("JavaGPT *** ChatGPT is generating a title. Please wait...");
				SubmitButton.setText("Loading...");	
				StringBuilder TitleBuilder = new StringBuilder();
				try {				
		        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "Create a short title that summarizes this conversation. Provide title only.");
		        messages.add(systemMessage);
		        
		        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
		                .builder()
		                .model(prop.getProperty("model"))
		                .messages(messages)
		                .n(1)
		                .maxTokens(25)
		                .logitBias(new HashMap<>())
		                .build();
		        service.streamChatCompletion(chatCompletionRequest)
                .doOnError(Throwable::printStackTrace)
                .blockingForEach(chunk -> {
                    for (ChatCompletionChoice choice : chunk.getChoices()) {
                    	if(choice.getMessage().getContent() != null) {
                    	TitleBuilder.append(choice.getMessage().getContent());	
                    	}
                        //System.out.println(choice.getMessage().getContent());
                    }//
                });		    		        
		        messages.remove(messages.size() - 1);
		        
		        String title = TitleBuilder.toString();
		        
		        //System.out.println(title);
		        
				title = title.replaceAll("[\\\\/:*?\"<>|]", "");
				if(title.substring(title.length() - 1).equals(".")) {
					title = title.substring(0, title.length() - 1);
				}
		    	SubmitButton.setText("Submit");
		    	if(title != null) {		    	
					File file = new File(FGPTConvo.getParentFile(), title + ".json");
					if(file.exists()) {
						JOptionPane.showMessageDialog(null, "File already exists", "Error", JOptionPane.ERROR_MESSAGE);
						setTitle("JavaGPT - " + FGPTConvo.getName().substring(0, FGPTConvo.getName().length()-5));
					}else {
						FGPTConvo.renameTo(file);
						FGPTConvo = file;
						INSTANCE.setTitle("JavaGPT - " + title);
					}
			      }
				
				}catch(Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					SubmitButton.setText("Submit");
					setTitle("JavaGPT - " + FGPTConvo.getName().substring(0, FGPTConvo.getName().length()-5));
				}
		       
			}
			});
			myThread.start();	
	}
	
}
