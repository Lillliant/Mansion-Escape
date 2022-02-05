//Tutorial.java
//Christine Wong
//The tutorial class creates a window of different JPanels stored
//in a tabbed pane that displays texts read from the tutorial
//data files when user clicks on the tab. User can also return to
//the main menu when clicking the button at the top.

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class Tutorial extends JFrame{
	
	private MainMenu menuFrame; //to avoid making too many replicated when alternating
	private JTabbedPane pane = new JTabbedPane(); //stores the tutorial in sections
	
	//customized fonts
	private final Font titleFont = Utilities.getFont("res/font/Regular.ttf", 24f);
	private final Font textFont = Utilities.getFont("res/font/Happy Clover.ttf", 20f);
	
	//creates new instance of a separate JFrame for user to access game tutorial
    public Tutorial(MainMenu m) {
    	menuFrame = m;
    	
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setSize(800,600);
    	setLayout(new BorderLayout());
    	getContentPane().setBackground(Color.black); //makes sure the space not covered by the Tabbedpane is coloured
    	
    	//customize the font and colour of the tabs
    	pane.setFont(titleFont);
    	pane.setBackground(Color.black);
    	pane.setForeground(Color.white);
    	
    	//for when the user wants to go back to the main menu
    	JButton backBtn = setupButton("Return to main menu");
    	backBtn.addActionListener(new StartMenu());
    	add(backBtn, BorderLayout.PAGE_START);
		
		//creating the contents to be displayed in each tab of the tutorial
		JPanel puzzles = setupTab("puzzles");
        JPanel movement = setupTab("interactions");
		//adding the components to the tabs
    	pane.addTab("Objective", puzzles);
    	pane.addTab("Moving Around", movement);
    	add(pane, BorderLayout.CENTER);
    }
    
    //changes the visibility of the JFrames to make it look like the user
    //has returned to the main menu
    class StartMenu implements ActionListener{
    	@Override
    	public void actionPerformed(ActionEvent evt){
    		menuFrame.setVisible(true);
    		setVisible(false);
    	}
    }
    
    //takes a string and returns a JButton with the string as a label.
    public JButton setupButton(String text){
    	JButton btn = new JButton(text);
    	//changes the font and colour to blend in with other components
    	btn.setFont(textFont);
    	btn.setBackground(Color.black);
    	btn.setForeground(Color.white); //changes text colour
    	btn.setFocusPainted(false); //makes sure the border around the text is not drawn 
    	
    	return btn;
    }
    
    //takes the string of a .txt filename, reads the text in the file and draws them on a JPanel.
    //will return a tab with no text if the filename is invalid.
    public JPanel setupTab(String fName){
    	//setup the JPanel
    	JPanel tab = new JPanel();
    	tab.setLayout(new BorderLayout());
    	tab.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10)); //adds "padding" between the text and the border
    	tab.setBackground(Color.black);
    	
    	//setup the text
    	ArrayList<String> line = Utilities.fileToMultiString("res/tutorial_text/",fName);
    	if(line != null){ //avoid NullExceptions
    		JTextArea tabText = new JTextArea(line.get(0));

    		tabText.setFont(textFont);
	    	tabText.setBackground(Color.black);
	    	tabText.setForeground(Color.white); //changes text colour
	    	
	    	tabText.setLineWrap(true);
	    	tabText.setWrapStyleWord(true); //makes sure the wrapping is done at word level, not character level
	    	
	    	for(int i = 1; i < line.size(); i++){
    			tabText.append("\n"+line.get(i)); //add \n to retain the line breaks
    		}
    		
    		tab.add(tabText, BorderLayout.PAGE_START); //add the text to the JPanel
    	}
    	
    	return tab;
    }
    
}