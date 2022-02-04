//Game.java
//Christine Wong
//Creates the JFrame where the main game is host at. Deals with the maintenance of
//the progress of the game, including setting up and updating information on the panel the
//user is at, and saving when necessary. Also coordinates the drawing of the panel
//and components.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*; 
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game extends JFrame implements ActionListener{
	private Timer myTimer; //determines how often the game updates
	//the panels the user will be in - different segments of the game
	private Puzzle puzzle; //where there are individual puzzles
	private Escaperoom monster; //where there are monsters (main hall)
	String currentPanel; //determines the panel the user will be in at the moment
	
	JPanel cards;   //stores the 2 game panels
    CardLayout cLayout = new CardLayout(); //shows one panel at a time
	
	//creates a new instance of the game.
    public Game() {
    	//setup basic JFrame information
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		myTimer = new Timer(10, this);	 // trigger every 10 ms
		addKeyListener(new moveListener());
		
		//setup cardLayout and panels
		puzzle = new Puzzle();
		cards = new JPanel(cLayout);
		monster = new Escaperoom();
		cards.add(puzzle, "puzzle");
		cards.add(monster, "monster");
		add(cards);
		
		//default panel will be at Puzzle, where the first puzzle room
		//is located
		currentPanel = "puzzle";
		cLayout.show(cards, "puzzle");
		
		//setup the frame so that contents in the panel maintain their size across computers
		pack();
		start();
		setResizable(false);
		setVisible(true);
    }
    
    //start the timer
    public void start(){
		myTimer.start();
	}
	
	//updates the information in the current panel when it is triggered
	public void actionPerformed(ActionEvent evt){
		if (currentPanel.equals("monster")){
			if(monster.getEnding()!=true){//makes sure the game hasn't ended yet
				monster.update();
				if(monster.getExit()){ //when the panel needs to switch to Puzzle
					setPanel("puzzle");
				}
				if(monster.getSave()){ //when the panel needs saving
					save();
					monster.setSave(false); //avoid saving constantly - slow game down
				}
			}
			monster.repaint(); //update visual information - also needed when displaying ending
		}
		else{//user will not be able to move and access Puzzle if the game ended
			puzzle.repaint();
			if(puzzle.getExit()){
				setPanel("monster");
			}
		}
	}
	
	//save the current user progress
	public void save(){
		//save player information
		ArrayList<String> playerText = new ArrayList<String>();
		playerText.add(monster.getPlayer().toTxt());
		Utilities.writeToFile("player", playerText.toArray(new String[0]));
		//save items that may be changed during game
		ArrayList<String> optionText = puzzle.optionItemsToTxt();
		Utilities.writeToFile("optionItems", optionText.toArray(new String[0]));
		ArrayList<String> keyText = puzzle.keyItemsToTxt();
		Utilities.writeToFile("keyItems", keyText.toArray(new String[0]));
	}
	
	//loading the saved progress
	public void loadSave(){
		loadPlayer();
		//update information on items that may have changed during game
		String[] optionText = Utilities.readFile("optionItems");
		puzzle.optionItemsTxtUpdate(optionText);
		
		String[] keyText = Utilities.readFile("keyItems");
		puzzle.keyItemsTxtUpdate(keyText);
	}
	
	//update user information
	private void loadPlayer(){
		String[] playerText = Utilities.readFile("player");
		currentPanel = "monster"; //reverts back to when the user last saved
		setPanel("monster"); //user cannot access savePoint during Puzzle panel
		Player newUser = monster.getPlayer();
		newUser.txtUpdate(playerText[0]);
		monster.setPlayer(newUser);
	}
	
	//update and send keyboard information when the user has keyboard input
	//implemented here to avoid focus issues
	class moveListener implements KeyListener{
		public void keyTyped(KeyEvent e){
			if(currentPanel == "puzzle"){//only puzzle needs keyTyped events
				puzzle.typedKey(e);
			}
		}
		
		public void keyPressed(KeyEvent e){
			if(currentPanel == "monster" && monster.getEnding() != true){ //makes sure no accidental movements
			//if the game has ended
				monster.pressedKey(e);
			}
			else{ //user can't access Puzzle because ending happens at Escaperoom
				puzzle.pressedKey(e);
			}
		}
		
		public void keyReleased(KeyEvent e){ //gives key input based on what panel needs it right now
			if(currentPanel == "monster"){
				monster.releasedKey(e);
			}
			else{
				puzzle.releasedKey(e);
			}
		}
	}
	
	//setting the currentPanel
	public void setPanel(String panel){
		if (panel.equals("puzzle")){
			currentPanel = "puzzle";
			puzzle.reset(monster.getRoomNum()); //reset the information for level editing
			//and avoid accidental exits
			puzzle.setPlayer(monster.getPlayer()); //update user position & other info
			cLayout.show(cards, currentPanel);
		}
		else if (panel.equals("monster")){
			currentPanel = "monster";
			monster.reset(); //reset information to avoid accidental exits
			monster.setPlayer(puzzle.getPlayer()); //update user position & other info
			cLayout.show(cards, currentPanel);
		}
	}
    
}