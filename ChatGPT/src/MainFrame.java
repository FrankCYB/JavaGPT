import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
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

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MainFrame extends JFrame {
	
	private static MainFrame frame;
	private JPanel contentPane;

	private OpenAiService service;
	private final static ArrayList<ChatMessage> messages = new ArrayList<>();
	private static JTextArea ChatArea;
    private JButton SubmitButton;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane_1;
	private JButton SaveButton;
	private JButton ImportButton;
	private JButton ResetButton;
	
	private static JEditorPane DisplayArea;
	private static JEditorPane HTMLArea;
	private static StyledDocument doc;
	private JMenuBar menuBar;
	private static String GPTConvo;

	private File FGPTConvo;
	
	public static Properties prop;
	public static String version = "1.2.7";
	private Boolean first = true;
	private Boolean autosave = true;
	private Boolean autotitle = true;
	private Boolean cloaderopen = false;
	private Boolean aframeopen = false;
	private static Boolean isHTMLView = false;
	private static Parser parser;
	private static HtmlRenderer renderer;
	public static Boolean isAlpha = true;
	private Boolean isStreamRunning = false;
	private static int FormSize;
	public static int seltheme = 0;
	private ChatLoader cloader;
	private String chatDir;

	
	private static Style YouStyle;
	private static Style InvisibleStyle;
	private static Style GPTStyle;
	private static Style ChatStyle;
	private static MainFrame INSTANCE = null;
	

    public static void loadchat(String fullfilepath, String filename) throws BadLocationException {

    	INSTANCE.setTitle("JavaGPT - " + filename);
		try {		
		
			DisplayArea.setText("");

			messages.clear();
			readMessagesFromFile(fullfilepath);
			if(isHTMLView) {
				resetHTMLAreaStyle();
				Node document = parser.parse(DisplayArea.getDocument().getText(0, DisplayArea.getDocument().getLength()));
				//System.out.println(renderer.render(document));
				HTMLArea.setText(renderer.render(document));
			}
			
			
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
		    		    doc.insertString(doc.getLength(), "You", YouStyle);
		    		    doc.insertString(doc.getLength(), ":\n", InvisibleStyle);
		    		    doc.insertString(doc.getLength(), message.getContent() + "\n\n", ChatStyle);
		    		} catch (BadLocationException e) {
		    		    e.printStackTrace();
		    		}	
                }else{
                	try {
		    		    doc.insertString(doc.getLength(), "ChatGPT", GPTStyle);
		    		    doc.insertString(doc.getLength(), ":\n", InvisibleStyle);
		    		    doc.insertString(doc.getLength(), message.getContent() + "\n\n", ChatStyle);
		    		} catch (BadLocationException e) {
		    		    e.printStackTrace();
		    		}	
                }
                messages.add(message);
            }
        }
    }
    
    public void refreshMessages() {
    	DisplayArea.setText("");
    	for (ChatMessage message : messages) {
    		 if(message.getRole().equals("user")) {
             	try {
		    		    doc.insertString(doc.getLength(), "You", YouStyle);
		    		    doc.insertString(doc.getLength(), ":\n", InvisibleStyle);
		    		    doc.insertString(doc.getLength(), message.getContent() + "\n\n", ChatStyle);
		    		} catch (BadLocationException e) {
		    		    e.printStackTrace();
		    		}	
             }else{
             	try {
		    		    doc.insertString(doc.getLength(), "ChatGPT", GPTStyle);
		    		    doc.insertString(doc.getLength(), ":\n", InvisibleStyle);
		    		    doc.insertString(doc.getLength(), message.getContent() + "\n\n", ChatStyle);
		    		} catch (BadLocationException e) {
		    		    e.printStackTrace();
		    		}	
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
		HTMLArea.setText("");
		resetHTMLAreaStyle();
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
				                prop.setProperty("autoscroll", "true");
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
				        	
					frame = new MainFrame(); //Loads main JFrame
					 
				//Scales JFrame based on "WindowSize" prop	
				if(prop.getProperty("WindowSize").equals("small")){
					
					frame.getContentPane().setPreferredSize(new Dimension(475, 532));
					FormSize=1;
				}else if(prop.getProperty("WindowSize").equals("large")){
					frame.getContentPane().setPreferredSize(new Dimension(1370, 960));
					FormSize=2;
					
				}else {
					
					frame.getContentPane().setPreferredSize(new Dimension(686, 647));
					FormSize=3;
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
		
		//Renderer and Parser for HTMLView
		parser = Parser.builder().build();
		renderer = HtmlRenderer.builder().build();
		//
		
		JMenuItem HTMLViewMenuItem = new JMenuItem("HTML View");
		HTMLViewMenuItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	if(isHTMLView) {										
					try {
						scrollPane.setViewportView(DisplayArea);						
						HTMLViewMenuItem.setText("HTML View");
						isHTMLView=false;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else {
					try {
						scrollPane.setViewportView(HTMLArea);						
						resetHTMLAreaStyle();
	    				Node document = parser.parse(DisplayArea.getDocument().getText(0, DisplayArea.getDocument().getLength()));	    	
	    				HTMLArea.setText(renderer.render(document));
						HTMLViewMenuItem.setText("Normal View");
						isHTMLView=true;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}

		    }
		});
			
		OptionMenu.add(HTMLViewMenuItem);
		
		JMenu FormSizeMenu = new JMenu("Form Size");
		OptionMenu.add(FormSizeMenu);
		
		JMenuItem LargeMenuItem = new JMenuItem("Large");
		FormSizeMenu.add(LargeMenuItem);
		LargeMenuItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	if(FormSize != 2) {
		    	FormSize = 2;
		    	setFormSize(2);	
		    	}
		    }
		});
		
		JMenuItem MediumMenuItem = new JMenuItem("Medium");
		FormSizeMenu.add(MediumMenuItem);
		MediumMenuItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	if(FormSize != 3) {
		    	FormSize = 3;
		    	setFormSize(3);	
		    	}
		    }
		});
		
		JMenuItem SmallMenuItem = new JMenuItem("Small");
		FormSizeMenu.add(SmallMenuItem);
		SmallMenuItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	if(FormSize != 1) {
		    	FormSize = 1;
		    	setFormSize(1);	
		    	}
		    }
		});
		
		JMenu RenameMenu = new JMenu("Rename");
		OptionMenu.add(RenameMenu);
		
		JMenuItem AutoMenuItem = new JMenuItem("Auto");
		RenameMenu.add(AutoMenuItem);
		AutoMenuItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	if(FGPTConvo != null) {
		    		AutoTitle();
		    	}else {
		    		JOptionPane.showMessageDialog(null, "No chat file loaded", "Error", JOptionPane.ERROR_MESSAGE);
		    	}
		
		    	
		    }
		});
		
		JMenuItem ManualMenuItem = new JMenuItem("Manual");
		RenameMenu.add(ManualMenuItem);
		
		ManualMenuItem.addActionListener(new ActionListener() {
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
		    		JOptionPane.showMessageDialog(null, "No chat file loaded", "Error", JOptionPane.ERROR_MESSAGE);
		    	}
		    	
		    }
		});
		
		JMenuItem DeleteMenuItem = new JMenuItem("Delete");
		DeleteMenuItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	if(FGPTConvo != null && FGPTConvo.exists()) { //checks if the file exists		    		
			        FGPTConvo.delete(); //deletes the file
			        Reset();	             
		         } else {
		        	 JOptionPane.showMessageDialog(null, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
		         }
		         
		    }
		});
		
		JMenuItem RevertMenuItem = new JMenuItem("Revert");
		RevertMenuItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	if(messages.size() >= 4) { //checks if the file exists
		    		messages.remove(messages.size() - 1);
		    		messages.remove(messages.size() - 1);
		    		refreshMessages();
		         } else {
		        	 if(messages.isEmpty()) {
		        		 JOptionPane.showMessageDialog(null, "No chat loaded", "Error", JOptionPane.ERROR_MESSAGE);	  		        	 
		        	 }else {
		        		 JOptionPane.showMessageDialog(null, "Can't revert first prompt", "Error", JOptionPane.ERROR_MESSAGE);
		        	 }
		         }
		         
		    }
		});
		OptionMenu.add(RevertMenuItem);
		OptionMenu.add(DeleteMenuItem);
		
		JMenuItem AboutMenuItem = new JMenuItem("About");
		AboutMenuItem.addActionListener(new ActionListener() {
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
		OptionMenu.add(AboutMenuItem);
		
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
		
		scrollPane = new JScrollPane();
		
		contentPane.add(scrollPane);


		DisplayArea = new JEditorPane();
		scrollPane.setViewportView(DisplayArea);
		DisplayArea.setEditable(false);
		DisplayArea.setContentType("text/rtf");		
		
		HTMLArea = new JEditorPane();
		HTMLArea.setEditable(false);
		HTMLArea.setBackground(Color.white);
		HTMLArea.setContentType("text/html");
		//DisplayArea.setCharacterAttributes(attributeSet, true);
		// Define a style for bold text
		
		StyleContext sc = StyleContext.getDefaultStyleContext();
		
		YouStyle = sc.addStyle("bold", null);
		StyleConstants.setFontFamily(YouStyle, "Tahoma");
		StyleConstants.setBold(YouStyle, true);
								
		GPTStyle = sc.addStyle("bold", null);
		StyleConstants.setFontFamily(GPTStyle, "Tahoma");
		StyleConstants.setBold(GPTStyle, true);
		StyleConstants.setForeground(GPTStyle, Color.RED); //getHSBColor(0, 0.8f, 0.8f)
		
		InvisibleStyle = sc.addStyle("bold", null);
		//StyleConstants.setFontFamily(InvisibleStyle, "Tahoma");
		//StyleConstants.setBold(InvisibleStyle, true);
		StyleConstants.setForeground(InvisibleStyle, DisplayArea.getBackground());		
		
		ChatStyle = sc.addStyle("black", null);
		StyleConstants.setFontFamily(ChatStyle, "Tahoma");

		Style ErrorStyle = sc.addStyle("ErrorStyle", null);
		StyleConstants.setItalic(ErrorStyle, true);
		StyleConstants.setFontFamily(ErrorStyle, "Tahoma");
		
		if(seltheme == 1) {
			StyleConstants.setForeground(YouStyle, Color.ORANGE); //getHSBColor(30f/360, 0.8f, 1f)
			StyleConstants.setForeground(ChatStyle, Color.WHITE); //Color.getHSBColor(0f, 0f, 0.8f)
			StyleConstants.setForeground(ErrorStyle, Color.WHITE); //Color.getHSBColor(0f, 0f, 0.8f)
		}else {
			StyleConstants.setForeground(YouStyle, Color.BLUE);
			StyleConstants.setForeground(ChatStyle, Color.BLACK);
			StyleConstants.setForeground(ErrorStyle, Color.BLACK);					
		}
		
		doc = (StyledDocument) DisplayArea.getDocument();
		
		SubmitButton = new JButton("Submit");

		SubmitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isStreamRunning) {
					isStreamRunning = false;
					SubmitButton.setText("Submit");
					return;
				}
				Thread myThread = new Thread(new Runnable() {				    
				    public void run() {	
				    	
				    	SubmitButton.setText("Cancel Req");				    	
				    	//Boolean success = false;
				    					    
				    	try {
			    		    doc.insertString(doc.getLength(), "You", YouStyle);
			    		    doc.insertString(doc.getLength(), ":\n", InvisibleStyle);
			    		    doc.insertString(doc.getLength(), ChatArea.getText() + "\n\n", ChatStyle);
			    		    doc.insertString(doc.getLength(), "ChatGPT", GPTStyle);
			    		    doc.insertString(doc.getLength(), ":\n", InvisibleStyle);
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
					            
					            isStreamRunning = true;
					            service.streamChatCompletion(chatCompletionRequest)
					                    .doOnError(Throwable::printStackTrace)
					                    .takeWhile(resultsBatch -> isStreamRunning)
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
					                        }
					                    });
					            try {
					            	doc.insertString(doc.getLength(), "\n\n", ChatStyle);
					            	if(isHTMLView) {
					            		resetHTMLAreaStyle();
					    				Node document = parser.parse(DisplayArea.getDocument().getText(0, DisplayArea.getDocument().getLength()));
					    				HTMLArea.setText(renderer.render(document));
					            	}
					            						    		  
					    		} catch (BadLocationException e2) {
					    		    e2.printStackTrace();
					    		}
					            //service.shutdownExecutor(); 
								
								if(isStreamRunning) {
									
									GPTConvo = GPTConvoBuilder.toString();
						            final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), GPTConvo);
						            messages.add(systemMessage);
						            
								if(autosave) {
									
									if(first) {	
										
										newFile();							    			
									}
									
						    		try {
										writeMessagesToFile(FGPTConvo.getPath());
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
						    		if(first && autotitle){					    				
						    			AutoTitle();
						    			first = false;
						    		}
						    	}
								}else {
									messages.remove(messages.size() - 1);
									doc.insertString(doc.getLength(), "Note: The previous prompt and response did not save as it was canceled" + "\n\n", ErrorStyle);
								}
						  				    			
				    		}catch(Exception e) {
				    			//JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				    			try {
					    		    doc.insertString(doc.getLength(), "Error: " + e.getMessage() + "\n\n", ErrorStyle);
					    		} catch (BadLocationException e2) {
					    		    e2.printStackTrace();
					    		}
				    		}
				    		
				    	isStreamRunning = false;
				    	SubmitButton.setText("Submit");
				    }
				});
				myThread.start(); // Start the thread				
			}
		});
		contentPane.add(SubmitButton);
				
			
		ResetButton = new JButton("New Chat");
		ResetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Reset();
			}
		});
		contentPane.add(ResetButton);
		
		scrollPane_1 = new JScrollPane();
		
		contentPane.add(scrollPane_1);
		
		ChatArea = new JTextArea();
		ChatArea.setWrapStyleWord(true);
		scrollPane_1.setViewportView(ChatArea);
		ChatArea.setLineWrap(true);
		
		SaveButton = new JButton("");
		try {
		SaveButton.setIcon(new ImageIcon(MainFrame.class.getResource("FloppyDrive.gif")));
		}catch(Exception e4) {
			JOptionPane.showMessageDialog(null, e4.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		SaveButton.setFont(new Font("Arial Black", Font.BOLD, 6));
		SaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				  File defaultDir = new File(".");
			      JFileChooser fileChooser = new JFileChooser(defaultDir);
			      fileChooser.setDialogTitle("Save chat");

			      int result = fileChooser.showSaveDialog(null);

			      if (result == JFileChooser.APPROVE_OPTION) {

			         File selectedFile = fileChooser.getSelectedFile();

			         try {
			        	 
			            FileWriter writer = new FileWriter(selectedFile);		            
			            String plaintext = DisplayArea.getDocument().getText(0, DisplayArea.getDocument().getLength());			            			 
			            writer.write(plaintext);			 
			            writer.close();		 
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
		
		ImportButton = new JButton("");
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
		            showDisplayMenu(e.getX(), e.getY());
		        }
		    }

		    @Override
		    public void mouseReleased(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            showDisplayMenu(e.getX(), e.getY());
		        }
		    }
		});
		
		HTMLArea.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            showHTMLMenu(e.getX(), e.getY());
		        }
		    }

		    @Override
		    public void mouseReleased(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            showHTMLMenu(e.getX(), e.getY());
		        }
		    }
		});

		ChatArea.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            showChatMenu(e.getX(), e.getY());
		        }
		    }

		    @Override
		    public void mouseReleased(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            showChatMenu(e.getX(), e.getY());
		        }
		    }
		});

		HTMLArea.addHyperlinkListener(new HyperlinkListener() {
		    public void hyperlinkUpdate(HyperlinkEvent e) {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		        	try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException | URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        }
		    }
		});   
		//Default
		/*setBounds(100, 100, 702, 707); //Uncomment this when editing design
		SubmitButton.setBounds(10, 554, 89, 23);
		ResetButton.setBounds(10, 616, 89, 23);
		scrollPane.setBounds(10, 11, 667, 532);
		scrollPane_1.setBounds(109, 554, 568, 85);
		SaveButton.setBounds(10, 585, 43, 23);
		ImportButton.setBounds(56, 585, 43, 23);*/
    	
		
		//Bulk property setting-------------------
	    try {
	        if(prop.getProperty("autoscroll") != null && !prop.getProperty("autoscroll").isEmpty()) {	        	
	        	if(prop.getProperty("autoscroll").equals("true")) {
	        		DefaultCaret caret = (DefaultCaret)DisplayArea.getCaret();
	        		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	        	}        	
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
private void showDisplayMenu(int x, int y) {
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

private void showHTMLMenu(int x, int y) {
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem copyMenuItem = new JMenuItem("Copy");
    copyMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedText = HTMLArea.getSelectedText();
            if (selectedText != null) {
                StringSelection selection = new StringSelection(selectedText);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
            }
        }
    });
    popupMenu.add(copyMenuItem);
    popupMenu.show(HTMLArea, x, y);
}

private void showChatMenu(int x, int y) {
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
        	 String selectedText = ChatArea.getSelectedText();
        	    if (selectedText != null && !selectedText.isEmpty()) {
        	        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	        Transferable contents = clipboard.getContents(null);
        	        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        	            try {
        	                String clipboardText = (String) contents.getTransferData(DataFlavor.stringFlavor);
        	                ChatArea.replaceSelection(clipboardText);
        	            } catch (UnsupportedFlavorException | IOException ex) {
        	                ex.printStackTrace();
        	            }
        	        }
        	    } else {
        	        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	        Transferable contents = clipboard.getContents(null);
        	        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        	            try {
        	                String clipboardText = (String) contents.getTransferData(DataFlavor.stringFlavor);
        	                int caretPos = ChatArea.getCaretPosition();
        	                ChatArea.insert(clipboardText, caretPos);
        	            } catch (UnsupportedFlavorException | IOException ex) {
        	                ex.printStackTrace();
        	            }
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
                    }
                });		    		        
		        messages.remove(messages.size() - 1);
		        
		        String title = TitleBuilder.toString();		        
		        
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
	
	public static void resetHTMLAreaStyle() {
		HTMLArea.setContentType("text/plain");
		HTMLArea.setContentType("text/html");
	}
	
	public void setFormSize(int size){
		if(size==1){
        	//setBounds(100, 100, 481, 584); //Uncomment this when editing design
			frame.getContentPane().setPreferredSize(new Dimension(475, 532));
			frame.pack();
        	scrollPane_1.setBounds(103, 454, 363, 69);
        	scrollPane.setBounds(10, 11, 456, 432);
        	SubmitButton.setBounds(10, 454, 89, 23);
        	SaveButton.setBounds(10, 477, 43, 23);
        	ImportButton.setBounds(56, 477, 43, 23);
        	ResetButton.setBounds(10, 500, 89, 23);
        	
        }else if(size==2) {
        	//setBounds(100, 100, 702, 707);
        	frame.getContentPane().setPreferredSize(new Dimension(1370, 960));
        	frame.pack();
    		SubmitButton.setBounds(13, 831, 148, 36);
        	ResetButton.setBounds(13, 914, 148, 36);
        	scrollPane.setBounds(13, 15, 1344, 802);
        	scrollPane_1.setBounds(171, 831, 1186, 118);
        	SaveButton.setBounds(13, 873, 73, 36);
        	ImportButton.setBounds(88, 873, 73, 36);
        }else {	
        	frame.getContentPane().setPreferredSize(new Dimension(686, 647));
			frame.pack();
        	SubmitButton.setBounds(10, 554, 89, 23);
    		ResetButton.setBounds(10, 616, 89, 23);
    		scrollPane.setBounds(10, 11, 667, 532);
    		scrollPane_1.setBounds(109, 554, 568, 85);
    		SaveButton.setBounds(10, 585, 43, 23);
    		ImportButton.setBounds(56, 585, 43, 23);
        }
	}
}
