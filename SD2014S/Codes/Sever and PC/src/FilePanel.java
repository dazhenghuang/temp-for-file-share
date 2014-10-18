import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane; 
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import security.ClientSecurity;
import config.Config;

//creates the GUI for the user to interact with the program
public class FilePanel extends JPanel{
	
	private String CleanBatPath = "";
	private String DefaultOpen = "";
	private String MasterIndexPath = "";
	private String outputDir = "";
	
	private static final long serialVersionUID = 1L;
	private final int FONTSIZE = 24;
	//sets up two threads for the encrypt and decrypt buttons
	private volatile Thread encryptThread = null, decryptThread = null;
	private boolean encThrDone = true, decThrDone = true;
	private JFrame frame;
	
	//creates the frame for the GUI objects
	private JFrame frame_1 = new JFrame("Enhanced Crypto System");
	private JFrame frame_2 = new JFrame("File Unprotector");
	
	//objects for the second GUI window
	private JLabel pNum = new JLabel("Page#: ");
    private JLabel listLabel = new JLabel("Select a file to unprotect: ");
	
	private JTextField pageNumber = new JTextField(4);
	
	//buttons for the second GUI window
	private JButton encrypt = new JButton("Protect");
	private JButton decrypt = new JButton("Unprotect");
	private JButton reset = new JButton("Reset");
	private JButton open = new JButton("Open");
	private JButton next = new JButton("Next");
	private JButton previous = new JButton("Previous");
	
	private JList<String> deFile = new JList<String>();

	//GUI panels used to organize second window
	private JPanel gui = new JPanel();
	private JPanel decrypter = new JPanel();
	private JPanel dButtons = new JPanel();
	
	//private GridLayout status = new GridLayout(6, 2);
	private GridLayout buttons = new GridLayout(3,1);
	
	private ImagePanel background = new ImagePanel();
	private ImagePanel background2 = new ImagePanel();
	
	private Color trans = new Color(0,0,0,0);
	
	private ClientSecurity sec = new ClientSecurity();
	
	//global variables used by the program
	byte[] key = null;
	
	private String paths[] = null;

   public FilePanel(int frameNum)
   {  
	  new Config();
	  CleanBatPath = Config.cleanBatPath;
	  DefaultOpen = Config.defaultOpen;
	  MasterIndexPath = Config.masterIndexPath;
	  outputDir = Config.outputDir;
	  
	  frame = new JFrame("Enhanced Crypto System");
	  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  
	  setFonts();
	  open.addActionListener(new openListener());
	  
	  decrypter.setPreferredSize(new Dimension(300,175));
	  decrypter.setBackground(trans);
	  decrypter.setOpaque(false);
	 
	 //frame_2.getContentPane().add(background);
	  background2.setLayout(new BorderLayout());

	  
	  deFile.setAlignmentX(Component.TOP_ALIGNMENT);
	  deFile.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	  deFile.setLayoutOrientation(JList.VERTICAL);
	  deFile.setVisibleRowCount(-1);
	  JScrollPane deFileScroll = new JScrollPane(deFile);
	  deFileScroll.setPreferredSize(new Dimension(300,100));
	  decrypter.add(listLabel);
	  decrypter.add(deFileScroll);
	  decrypter.add(pNum);
	  decrypter.add(pageNumber);
	  decrypter.add(open);
	  background2.add(decrypter);
	  background2.add(dButtons);
	  frame_2.add(decrypter);
	  
	  gui.setOpaque(false);
	  gui.setLayout(buttons);
	  gui.setPreferredSize(new Dimension(300, 300));
	  gui.setBackground(trans);
	  
	  background.setLayout(new BorderLayout());
	  background.add(gui);
	  encrypt.setAlignmentX(Component.TOP_ALIGNMENT);
	  gui.add(encrypt);
	  encrypt.addActionListener(new encryptListener());
	  decrypt.setAlignmentX(Component.CENTER_ALIGNMENT);
	  gui.add(decrypt);
	  decrypt.addActionListener(new decryptListener());
	  reset.setAlignmentX(Component.BOTTOM_ALIGNMENT);
	  gui.add(reset);
	  reset.addActionListener(new resetListener());
	  frame.getContentPane().add(background);
	  frame.pack();
	  frame.setVisible(true);
	  frame.setResizable(false);
	  frame.setLocationRelativeTo(null);
   }
   
   //sets the size and color of the fonts
   private void setFonts()
   {
	   Font f = new Font("monospaced", Font.BOLD, FONTSIZE);
		//pin.setFont(f);
		encrypt.setFont(f);
		decrypt.setFont(f);
		reset.setFont(f);
   }
   
   //closes the current GUI window
	public void close()
	{
		frame.setVisible(false);
		frame.dispose();
	}
	
	//resets the program to its initial state
	private class resetListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{			
			//Allow some time for Metamorphosis.java to compile
			try {
				Runtime.getRuntime().exec("cmd /c start " + CleanBatPath);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}	
   
   //the code that is executed by the encrypt thread
   Runnable enc = new Runnable()
   {	   
	   String[] fileNames = null;
	   int numberOfFiles = 0;
	   public void run()
	   {		   
			String fileName = "";
	    	String dir = "";
	    	
	    	//sets default location for file manager to open to
	    	JFileChooser c = new JFileChooser(DefaultOpen);
	    	
	    	//allows for selection of multiple objects
	    	c.setMultiSelectionEnabled(true);
	    	//allows the selection of files and folders
	    	c.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    	
	        // Demonstrate "Open" dialog:
	        int rVal = c.showOpenDialog(FilePanel.this);
	        //creates an array of all objects selected in file manager
	        File files[] = c.getSelectedFiles();
	        
	        
	          for( int i = 0; i < files.length; i++)
	    	  {
	        	  //gets the file name and directory
		          if (rVal == JFileChooser.APPROVE_OPTION) {
		        	fileName = files[i].getName();
		            dir = c.getCurrentDirectory().toString() + "\\";
		          }	          

	        	  //calls the function to encrypt a directory
	        	 System.out.println(files[i]);
	        	 sec.protectDir(files[i]);
	    	}
	          

	   }
   };
   
   //code that is executed by the decrypt thread
   Runnable dec = new Runnable()
   {
	   public void run()
	   {		   
			String indexContents[] = null;
			int numFiles = 0;
			
			try 
			{
				//checks if the index exists
				if(new File(MasterIndexPath).exists())
				{
					//gets the contents of the index file
					indexContents = readFile(MasterIndexPath).split("\\r?\\n");
					
					//gets the number of files in the index
					numFiles = indexContents.length;
				}
			} catch (IOException e1) { e1.printStackTrace();
			}
	        
	        //array for all filenames in the index file
	        String fileNames[] = new String[numFiles];
	        paths = new String[numFiles];

	        //checks if there is anything in the index
	        if(numFiles > 0 && !indexContents[0].equals(""))
	        {
	        	//loops through all contents of the index file
		        for(int i = 0; i < indexContents.length; i++)
		        {
		    	        fileNames[i] = indexContents[i].split(";")[0] + " " + indexContents[i].split(";")[1] + " page(s)";		        	
		    	        paths[i] = indexContents[i].split(";")[2];
		        }
	   		}
	        System.out.println("number of files:"+numFiles);
	       
	        deFile.setListData(fileNames);
			 
			  frame_2.pack();
			  frame_2.setVisible(true);
			  frame_2.setResizable(false);
			  frame_2.setLocationRelativeTo(null);
	   }
   };
   
   //starts the encrypt thread when button is pressed
   private class encryptListener implements ActionListener
   {
		public void actionPerformed(ActionEvent e) {
			if(decThrDone)
			{			
				encryptThread = new Thread(enc);
				encThrDone = false;
				encryptThread.start();
				encThrDone = true;
			}
		}
   }
   
   //starts the decrypt thread when button is pressed
   private class decryptListener implements ActionListener
   {
		public void actionPerformed(ActionEvent e) {
			if(encThrDone)
			{
				decryptThread = new Thread(dec);
				decThrDone = false;
				decryptThread.start();
				decThrDone = true;
			}
		}
   }
   
   //starts the decrypt thread when button is pressed
   private class openListener implements ActionListener
   {
		public void actionPerformed(ActionEvent e) {
			int selectedPage = deFile.getSelectedIndex();
			int pn = Integer.parseInt(pageNumber.getText());
			String file = paths[selectedPage];
			
			byte[] key = sec.getRandomKey();
			
			String filename = file.substring(file.lastIndexOf("\\") + 1, file.length());
			filename = filename.split("\\.")[0] + "_" + pn + ".pdf";
			double start = (double) System.nanoTime();
			sec.unProtectFile(file.split("\\.")[0] + "_" + pn + ".pdf", key);
			double end = (double) System.nanoTime();
			System.out.println((end-start)/1000000);
			
			
			System.out.println("Displaying " + file + "...");
			if (Desktop.isDesktopSupported()) {
			    try {
			        File myFile = new File(outputDir + filename);
			        Desktop.getDesktop().open(myFile);
			        Thread.sleep(1000);
			    } catch (IOException de) {
			        // no application registered for PDFs
			    } catch (InterruptedException de) {
					// TODO Auto-generated catch block
					de.printStackTrace();
				}
			}
			JOptionPane.showMessageDialog(frame_2, "Please click okay after the PDF has been closed.");
			
			sec.protectFile(outputDir + filename, key);
		}
		   
   }

   private class cmdListener implements KeyListener {
	   public void keyTyped(KeyEvent e) {
	    }

	    /** Handle the key-pressed event from the text field. */
	    public void keyPressed(KeyEvent e) {
	    	//frame_1.getInputMap().put(KeyStro)
	    }

	    /** Handle the key-released event from the text field. */
	    public void keyReleased(KeyEvent e) {
	    }
   }
   
   //reads the contents of a file
	private static String readFile(String fileName) throws IOException 
    {
    	String output = "";
    	try{
    		//opens file to be read
    		  FileInputStream fstream = new FileInputStream(fileName);
    		  // Get the object of DataInputStream
    		  DataInputStream in = new DataInputStream(fstream);
    		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		  String strLine;
    		  
    		  //Read File Line By Line
    		  while ((strLine = br.readLine()) != null)   {
	    		  output += strLine;
	    		  output += "\n";
    		  }
    		  
    		  //Close the input stream
    		  in.close();
    	}
    	//Catch exception if any
    	catch (Exception e)
    	{
    		  System.err.println("Error: " + e.getMessage());
    	}
    	
    	//returns contents of file
    	return output;
    }
	

   public JFrame getFrame() {
		return frame_1;
	}
}