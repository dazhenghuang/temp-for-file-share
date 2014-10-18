import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

import radial.*;
import security.KeyGrouping;

public class GUI extends JFrame implements UI, ActionListener {
    // Graphics Constants
    public static final int SIZE = 350;
    public static final int RINGS = 4;
    public static final int SECTIONS_PER_RING = 4;

    // Graphical Components
    private JLayeredPane layeredPane;
    private Background background;
    private CenterButton centerButton;
    private JPanel inputPanel;
    private JPanel androidPanel;
    private JLabel usernameLabel;
    private JTextField usernameField;
    private JLabel passwordLabel;
    private JTextField passwordField;
    private JButton resetButton;
    private JLabel serverAddressLabel;
    private JTextField serverAddressField;
    private JLabel clientAddressLabel;
    private JTextField clientAddressField;
    private JButton submitAddressButton;
    private JLabel androidKeyLabel;
    
    private double start = 0;
    private double end = 0;

    // Client information
    private static Client client;
    private String serverAddress;
    private String clientAddress;
    private boolean authenticated = false;

    // Character and Key information

    private static ArrayList<KeyGrouping> groupingTable;

    // Timer information
    private int MIN_SCRAMBLE_TIME = 4000;
    private int MAX_SCRAMBLE_TIME = 6000;
    private boolean TIMER_STARTED = false;

    // Timer object
    Timer t = new Timer(MIN_SCRAMBLE_TIME, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            scrambleCharacters();
            updateTimer(MIN_SCRAMBLE_TIME, MAX_SCRAMBLE_TIME);
        }
    });

    public static void main(String[] args) {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
		{
			//sets the way the program looks
		    if ("Nimbus".equals(info.getName())) 
		    {
		        try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        break;
		    }
		}
    	
    	GUI gui = new GUI();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.pack();
        gui.setVisible(true);

    }

    public GUI() {
        buildIPScreen();
    }

    public void buildIPScreen() {
        inputPanel = new JPanel();
        serverAddressLabel = new JLabel("Server IP: ");
        inputPanel.add(serverAddressLabel);
        serverAddressField = new JTextField("192.168.43.106", 16);
        inputPanel.add(serverAddressField);
        clientAddressLabel = new JLabel("Client IP: ");
        inputPanel.add(clientAddressLabel);
        clientAddressField = new JTextField("192.168.43.188", 16);
        inputPanel.add(clientAddressField);
        submitAddressButton = new JButton("Submit");
        submitAddressButton.addActionListener(this);
        submitAddressButton.setActionCommand("submit address");
        inputPanel.add(submitAddressButton);
        inputPanel.setPreferredSize(new Dimension(300, 100));
        add(inputPanel, BorderLayout.NORTH);
    }

    public void buildPasswordScreen() {
        remove(inputPanel);
//        setPreferredSize(new Dimension(350, 450));
        layeredPane = new JLayeredPane();
        client = new Client(serverAddress, clientAddress, true);
        groupingTable = client.getKeyGroupings();
        // TODO: wait on the Android client to connect.

        // Initialize the graphical components for the keyboard screen
        createBackground();
        createSubmitButton();
        createPasswordButtons();

        add(layeredPane);

        // Add input panel (for username input and password display)
        inputPanel = new JPanel();
        usernameLabel = new JLabel("Username: ");
        inputPanel.add(usernameLabel);
        usernameField = new JTextField(20);
        inputPanel.add(usernameField);
        passwordLabel = new JLabel("Password: ");
        inputPanel.add(passwordLabel);
        passwordField = new JTextField(20);
        passwordField.setEditable(false);
        inputPanel.add(passwordField);
        resetButton = new JButton("Clear Password");
        resetButton.addActionListener(this);
        resetButton.setActionCommand("clear");
        inputPanel.add(resetButton);
        inputPanel.setPreferredSize(new Dimension(300, 100));
        add(inputPanel, BorderLayout.NORTH);
        
        androidPanel = new JPanel();
        for(KeyGrouping grouping : groupingTable) {
            androidKeyLabel = new JLabel(grouping.getKeys().substring(0, 1) + " = " + grouping.getKeys());
            androidPanel.add(androidKeyLabel);
        }
        androidPanel.setPreferredSize(new Dimension(300, 100));
        add(androidPanel, BorderLayout.SOUTH);
        setSize(375, 600);
        revalidate();
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("submit")) {
            // Tell the client that the user has finished entering the password
            // on this device.
            authenticated = client.finishLogin(usernameField.getText());
        	//authenticated = true;
        	
        	if (authenticated) {
                end = System.nanoTime();
                System.out.println("Login time: " + ((end - start)/1000000000));
                
            	setVisible(false);
            	add(new FilePanel(1).getFrame());
            	revalidate();
            	repaint();
            }
        	// TODO: go to the encrypt/decrypt page
        }
        else if(e.getActionCommand().equals("clear")) {
            // Clear the password field
            passwordField.setText("");
            client.clearFields();
        }
        else if(e.getActionCommand().equals("submit address")) {
            serverAddress = serverAddressField.getText();
            clientAddress = clientAddressField.getText();
            buildPasswordScreen();
        }
        else {
            // Add a character to the password
            client.sendPasswordGrouping(Integer.parseInt(e.getActionCommand()));
            passwordField.setText(passwordField.getText() + "*");

            // Start the timer if this is the first character, otherwise reset
            // the scramble timer.
            if(!TIMER_STARTED) {
            	start = System.nanoTime();
                t.start();
                TIMER_STARTED = true;
            }

            scrambleCharacters();
        }
    }

    private void updateTimer(int min, int max) {
        int delay = min + (int) (Math.random() * (max - min));
        t.setDelay(delay);
    }

    private void createBackground() {
        // Create background
        setBackground(new Color(229, 229, 229));
        background = new Background(RINGS, SECTIONS_PER_RING);
        background.setSize(new Dimension(SIZE, SIZE));
        background.setBounds(0, 0, SIZE, SIZE);
        layeredPane.add(background, new Integer(0));
    }

    private void createSubmitButton() {
        // Create submit button
        centerButton = new CenterButton("Submit");
        centerButton.setDiameter((SIZE - 2 * background.BORDER) / (RINGS + 1));
        centerButton.setBounds(0, 0, SIZE, SIZE);
        centerButton.addActionListener(this);
        centerButton.setActionCommand("submit");
        layeredPane.add(centerButton, new Integer(1));
    }

    private void createPasswordButtons() {
        // Create password input buttons
        double radius = SIZE / 2.0 - background.BORDER;
        double ringWidth = radius / (RINGS + 1);
        double sectionRadians = Math.PI * 2 / SECTIONS_PER_RING;
        for(int i = 0; i < SECTIONS_PER_RING; ++i) {
            for(int j = 0; j < RINGS; ++j) {
                InputButton inputter;
                int stringsIndex = i * RINGS + j;
                inputter = new InputButton(groupingTable.get(stringsIndex));
                inputter.addActionListener(this);
                inputter.setActionCommand("" + stringsIndex);
                double subRadius = radius - ringWidth * j;
                inputter.setShape(subRadius - ringWidth, subRadius,
                        sectionRadians, sectionRadians * i);
                inputter.setBounds(0, 0, SIZE, SIZE);
                layeredPane.add(inputter, new Integer(2));
            }
        }
    }

    private void scrambleCharacters() {
        Collections.shuffle(groupingTable);
        layeredPane.removeAll();
        createBackground();
        createSubmitButton();
        createPasswordButtons();
        layeredPane.revalidate();
        layeredPane.repaint();
    }

}
