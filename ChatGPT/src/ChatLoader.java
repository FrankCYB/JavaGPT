import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.DefaultListModel;
import java.awt.BorderLayout;

public class ChatLoader extends JFrame {

	private JPanel contentPane;
	private JList<FileListItem> fileList;
	private DefaultListModel<FileListItem> model;
	private JPopupMenu popupMenu;
	private JPopupMenu popupMenu2;
	private String path;
	private int selectedIndex;
	


	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatLoader frame = new ChatLoader();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
	
	class FileListItem {
	    private String displayName;
	    private String filePath;

	    public FileListItem(String displayName, String filePath) {
	        this.displayName = displayName;
	        this.filePath = filePath;
	    }

	    public String getDisplayName() {
	        return displayName;
	    }

	    public String getFilePath() {
	        return filePath;
	    }

	    @Override
	    public String toString() {
	        return displayName;
	    }
	}
	
	/**
	 * Create the frame.
	 */
	public ChatLoader(String path) {
		this.path = path;
		setTitle("Chat History");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 288);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
	        model = new DefaultListModel<>();
	        fileList = new JList<>(model);
	        
	        
	        popupMenu = new JPopupMenu();
	        popupMenu2 = new JPopupMenu();

	        // Add menu items to the popup menu
	        JMenuItem deleteItem = new JMenuItem("Delete");
	        JMenuItem renameItem = new JMenuItem("Rename");	        
	        JMenuItem refreshItems = new JMenuItem("Refresh");
	        JMenuItem sortItems = new JMenuItem("Sort");
	        
	        JMenuItem refreshItems2 = new JMenuItem("Refresh");
	        JMenuItem sortItems2 = new JMenuItem("Sort");
	        
	        popupMenu.add(deleteItem);
	        popupMenu.add(renameItem);	        
	        popupMenu.add(refreshItems);
	        popupMenu.add(sortItems);
	        
	        popupMenu2.add(refreshItems2);
	        popupMenu2.add(sortItems2);
	        
	        deleteItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {          
	            	    	
					    	 File file = new File(fileList.getModel().getElementAt(selectedIndex).filePath); //replace path/to/file with the actual file path    	
					         if(file.exists()) { //checks if the file exists
					             file.delete(); //deletes the file					             
					             model.removeElementAt(selectedIndex);					         
					         } else {
					        	 JOptionPane.showMessageDialog(null, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
					         }
					         
	            }
	        });
	        
	        renameItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {	            		            			    					    	
					    	String title = JOptionPane.showInputDialog(null, "Please enter a title:", "Rename", JOptionPane.PLAIN_MESSAGE);
					    	if (title != null) {
					    	    File file = new File(fileList.getModel().getElementAt(selectedIndex).filePath);
					    	    String path = file.getParent();
					    	    String name = file.getName();
					    	    String ext = name.substring(name.lastIndexOf('.'));
					    	    File newFile = new File(path, title + ext);

					    	    if (newFile.exists()) {
					    	        JOptionPane.showMessageDialog(null, "File already exists", "Error", JOptionPane.ERROR_MESSAGE);
					    	    } else {
					    	        File txtFile = new File(path, title + ".json");
					    	        file.renameTo(newFile);
					    	        new File(path, name.substring(0, name.length() - ext.length()) + ".json").renameTo(txtFile);
					    	        refreshlist();
					    	        JOptionPane.showMessageDialog(null, "File renamed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
					    	    }
					    	}					  	            	

	            }
	        });
	        
	        refreshItems.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	refreshlist();
	            }
	        });
	        
	        refreshItems2.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	refreshlist();
	            }
	        });
	        
	        sortItems.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	MainFrame.isAlpha = !MainFrame.isAlpha;
	            	refreshlist();
	            }
	        });
	        
	        sortItems2.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	MainFrame.isAlpha = !MainFrame.isAlpha;
	            	refreshlist();
	            }
	        });
	        
	        // Attach the popup menu to the JList using a MouseListener
	        fileList.addMouseListener(new MouseAdapter() {
	            public void mousePressed(MouseEvent e) {
	                if (e.isPopupTrigger()) showPopupMenu(e);
	            }
	            public void mouseReleased(MouseEvent e) {
	                if (e.isPopupTrigger()) showPopupMenu(e);
	            }
	            public void mouseClicked(MouseEvent e) {
	                if (e.getClickCount() == 2) {
	                	selectedIndex = fileList.getSelectedIndex();
	                	try {
							MainFrame.loadchat(fileList.getModel().getElementAt(selectedIndex).filePath, fileList.getModel().getElementAt(selectedIndex).displayName);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

	                }
	            }
	        });
	        
	        refreshlist();
	        contentPane.setLayout(new BorderLayout(0, 0));
	        JScrollPane scrollPane = new JScrollPane(fileList);
			scrollPane.setViewportView(fileList);
			contentPane.add(scrollPane);
	}
	
	/*public void refreshlist() {
		File directory = new File(path);
		model.clear();
        for (File file : directory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
            	String displayName = file.getName();
                String filePath = file.getAbsolutePath();
                FileListItem item = new FileListItem(displayName.replaceFirst("[.][^.]+$", ""), filePath);
                model.addElement(item);
            }
        }
	}*/
	
	public void refreshlist() {
	    File directory = new File(path);
	    model.clear();
	    File[] files = directory.listFiles(new FileFilter() {
	        public boolean accept(File file) {
	            return file.isFile() && file.getName().endsWith(".json");
	        }
	    });
	    
	    if(MainFrame.isAlpha) {
	    Arrays.sort(files, new Comparator<File>() {
	        public int compare(File f1, File f2) {
	            long diff = f2.lastModified() - f1.lastModified();
	            return Long.signum(diff);
	        }
	    });
	    }
	    
	    for (File file : files) {
	        String displayName = file.getName();
	        String filePath = file.getAbsolutePath();
	        FileListItem item = new FileListItem(displayName.replaceFirst("[.][^.]+$", ""), filePath);
	        model.addElement(item);
	    }
	}
	
	private void showPopupMenu(MouseEvent e) {
        selectedIndex = fileList.getSelectedIndex();
        if (selectedIndex == -1) {
        	popupMenu2.show(e.getComponent(), e.getX(), e.getY());
        }else {

        popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
