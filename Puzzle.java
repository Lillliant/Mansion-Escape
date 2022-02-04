//Puzzle.java
//Christine Wong
//Puzzle creates the segment of the game where user solves puzzles
//inside a room. It setup and coordinates the movement and interaction 
//of user and between user and the different objects in the rooms. It
//also display the visual and audio information of the room and when
//user is interacting with the objects. The user can also receive
//information that can progress the game to ending and/or change 
//the layout of the panel the next time user access it.
//The setup of the room will change based on the room information it is
//given at the Game class level.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Arrays;
import java.applet.*;
import java.io.*;
import javax.imageio.*;

public class Puzzle extends JPanel {
	//the game objects in the room
	private Player user;
	private Decoration[] decorations;
	private OptionItem[] optionItems;
	private KeyItem[] keyItems;
	private Door door;
	//total numbers of the room items in game
	private ArrayList<Decoration[]> totDecorations = new ArrayList<Decoration[]>();
	private ArrayList<KeyItem[]> totKeyItems = new ArrayList<KeyItem[]>();
	private ArrayList<OptionItem[]> totOptionItems = new ArrayList<OptionItem[]>();
	
	private boolean exit;//if the panel needs to change to Escaperoom
	public static final int LEFT=0, RIGHT=1, UP=2, DOWN=3, PAUSE = 4;
	private final int roomsNum = 4; //the total number of rooms the game has
	private int room = 0; //current room the panel is set in
	
	//visual and audio files to be used during the game
	private static final AudioClip messageBox = Utilities.setupAudio("messageBox.wav");
	private static final AudioClip openDoor = Utilities.setupAudio("door.wav");
	private Image background;
	
	//the information to locate the current items the user is interacting
	private String currentItem;
	private int indexOfItem;
	private boolean reading;
	
	//ROOM INFO
	//the boundary the user cannot go past
	private int lowX, highX;
	private int lowY, highY;
	
	//creates a new instance of the room based on the original room setup info
	//the class has
    public Puzzle(){
    	setPreferredSize(new Dimension(800, 600));
    	setupMansion(); 
    	setupRoom(room);
    	setupSurrounding(room);
		
		exit = false;
		reading = false;
    }
    
    //SETUP METHODS
    //setup the total room items throughout the game
	public void setupMansion(){
		for(int i = 0; i < roomsNum; i++){ //created based on the number of rooms the game has
			Decoration[] decors = Utilities.setupDecorations("map"+i);
			totDecorations.add(decors);
			
			KeyItem[] kIs = Utilities.setupKeyItems("map"+i);
			totKeyItems.add(kIs);
			
			OptionItem[] oIs = Utilities.setupOptionItems("map"+i);
			totOptionItems.add(oIs);
		}
	}
	
	//setup the basic room information of the room the user is in
    public void setupRoom(int n){
    	try{
    		Scanner inFile = new Scanner(new BufferedReader(new FileReader(String.format("res/map/map%d/map%d.txt", n, n))));
    		//setup boundary of the room
    		int[] constraints = Utilities.parseInts(inFile.nextLine().strip().split(" "));
    		lowX = constraints[0];
			highX = constraints[1];
			lowY = constraints[2];
			highY = constraints[3];
			
			//setup Player's first position
			int[] position = Utilities.parseInts(inFile.nextLine().strip().split(" "));
			user = new Player(position[0],position[1]);
			//setup where the user will go after exiting
			String[] doorLine = inFile.nextLine().strip().split(" ");
			int[] doorPos = Utilities.parseInts(Arrays.copyOfRange(doorLine, 0, doorLine.length-1));
			door = new Door(doorPos[0], doorPos[1], doorPos[2], doorPos[3], doorPos[4], doorLine[5]);
			//setup the background of the room (changes)
			background = new ImageIcon(String.format("res/graphics/map%d.png", n)).getImage();
    	}
    	catch(IOException e){
    		System.out.println(e);
    	}
    }
    
    //reset the room when the user re-enters from another door
    public void reset(int n){
    	setupRoom(n);
    	setupSurrounding(n);
    	room = n;
    	exit = false; //avoid accidental exit
    }
    
    //when the user is ready to leave this panel
    public boolean getExit(){
    	return exit;
    }
    
    //SAVE METHODS
	//converts changeable room items (keyItems) in the game
	public ArrayList<String> keyItemsToTxt(){
		ArrayList<String> tmp = new ArrayList<String>();
		for(int x = 0; x < totKeyItems.size(); x++){//saved in the order of game progress
			KeyItem[] k = totKeyItems.get(x); //the number of this object remains the same regardless of number of runs
			for(int y = 0; y < k.length; y++){
				tmp.add(k[y].toTxt());
			}
		}
		return tmp;
	}
	
	//converts changeable room items (optionItems) in the game
	public ArrayList<String> optionItemsToTxt(){
		ArrayList<String> tmp = new ArrayList<String>();
		for(int x = 0; x < totOptionItems.size(); x++){//saved in the order of game progress
			OptionItem[] opI = totOptionItems.get(x); //the number of this object remains the same regardless of number of runs
			for(int y = 0; y < opI.length; y++){
				tmp.add(opI[y].toTxt());
			}
		}
		return tmp;
	}
	
	//loads changeable room items (optionItems) in the game
	public void optionItemsTxtUpdate(String[] txt){
		int count = 0;
		for(int x = 0; x < totOptionItems.size(); x++){
			OptionItem[] opI = totOptionItems.get(x);
			for(int y = 0;  y < opI.length; y++){
				opI[y].txtUpdate(txt[count]); //loaded in the order they're saved in
				count++; //makes sure the right order is assigned
			}
		}
	}

	//loads changeable room items (keyItems) in the game
	public void keyItemsTxtUpdate(String[] txt){
		int count = 0;
		for(int x = 0; x < totKeyItems.size(); x++){
			KeyItem[] k = totKeyItems.get(x);
			for(int y = 0;  y < k.length; y++){
				k[y].txtUpdate(txt[count]); //loaded in the order they're saved in
				count++; //makes sure the right order is assigned
			}
		}
	}
    
    //setup the items the current room the user is in based on the room number given
    public void setupSurrounding(int n){
    	//gets the same reference point from a database, so all changes are automatically saved.
    	decorations = totDecorations.get(n);
    	optionItems = totOptionItems.get(n);
    	keyItems = totKeyItems.get(n);
    	setupActivation();
    }
    
    //finds the information necessary for object to changed based on the state of other objects
    public void setupActivation(){
    	for(OptionItem item: optionItems){
    		if(item.getOtherActivation()==true){//finds the index of that needed object in the collection
    			int otherIndex = Utilities.findIndex(optionItems,item.getOtherName());
    			item.setOtherIndex(otherIndex);
    		}
    	}
    }
    
    //KEYBOARD INPUT
    public void pressedKey(KeyEvent e){
    	if(e.getKeyCode() == KeyEvent.VK_RIGHT){//for movement
			user.move(RIGHT, decorations, optionItems, keyItems); //get items for clearance to make sure doesn't get stuck
			user.constrainX(lowX, highX);//constain based on boundary of the room
		}
		else if(e.getKeyCode()==KeyEvent.VK_LEFT){//for movement
			user.move(LEFT, decorations, optionItems, keyItems); 
			user.constrainX(lowX, highX);//constain based on boundary of the room
		}
		else if(e.getKeyCode()==KeyEvent.VK_UP){//for movement
			user.move(UP, decorations, optionItems, keyItems);
			user.constrainY(lowY, highY);//constain based on boundary of the room
		}
		else if(e.getKeyCode()==KeyEvent.VK_DOWN){//for movement
			user.move(DOWN, decorations, optionItems, keyItems);
			user.constrainY(lowY, highY);//constain based on boundary of the room
		}
			
		if(e.getKeyCode()==KeyEvent.VK_SPACE){ //for room items
			checkSurrounding();
		}
			
		if (e.getKeyCode()==KeyEvent.VK_Y || e.getKeyCode()==KeyEvent.VK_N){ //for optionItems
			inputOptionItems(e);
		}
			
		if(e.getKeyCode()==KeyEvent.VK_BACK_SPACE){ //for keyItems
			inputDeleteKeyItems(e);
		}
    }
    
    //input for keyItems
    public void typedKey(KeyEvent e){
    	if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE){
    		inputDeleteKeyItems(e); //delete input
    	}
    	else{
    		inputKeyItems(e); //add/submit input to item
    	}
    }
    
    //input for movement animation
    public void releasedKey(KeyEvent e){
    	if(e.getKeyCode() == KeyEvent.VK_RIGHT){//makes sure user is standing still when not moving
			user.move(PAUSE, decorations, optionItems, keyItems);
		}
		else if(e.getKeyCode()==KeyEvent.VK_LEFT){//makes sure user is standing still when not moving
			user.move(PAUSE, decorations, optionItems, keyItems);
		}
		else if(e.getKeyCode()==KeyEvent.VK_UP){//makes sure user is standing still when not moving
			user.move(PAUSE, decorations, optionItems, keyItems);
		}
		else if(e.getKeyCode()==KeyEvent.VK_DOWN){//makes sure user is standing still when not moving
			user.move(PAUSE, decorations, optionItems, keyItems);
		}
    }
    
    //add text to the keyItems currently reading
    public void inputKeyItems(KeyEvent e){
    	if(reading && currentItem.equals("keyItem")){
			keyItems[indexOfItem].addText(e);
		}
    }
    
    //delete text to the keyItems currently reading
	public void inputDeleteKeyItems(KeyEvent e){
		if(reading && currentItem.equals("keyItem")){
			keyItems[indexOfItem].deleteText();
		}
	}
	
	//input information into the optionItem currently reading
    public void inputOptionItems(KeyEvent e){
    	if(e.getKeyCode()==KeyEvent.VK_Y){
    		if(reading && currentItem.equals("optionItem")){//avoid accidental change
    			messageBox.play(); //play sound effect
    			if(optionItems[indexOfItem].getOptionDisplay()){//makes sure the item is activated
    				optionItems[indexOfItem].selectOption(user);
    			}
    		}
    	}
    	else if (e.getKeyCode()==KeyEvent.VK_N){//exit the option page
    		if(reading && currentItem.equals("optionItem")){//avoid accidental change
    			messageBox.play(); //play sound effect
    			optionItems[indexOfItem].setDisplayText(false);
    			user.setPause(false); //user is locked when reading
    			reading = false;
    		}
    	}
    }
    
    //CHECK METHODS - for interactions
    
    //check all the items in the room for interactions
    public void checkSurrounding(){
    	messageBox.play(); //play sound effect
    	checkDecors();
    	checkOptionItems();
    	checkKeyItems();
    	checkDoor();
    }
    
    //check if the user wants to exit the panel
    public void checkDoor(){
    	if(reading && currentItem.equals("door")){
    		//if the door is locked and message is displayed
    		if(door.endPage()){//when user exits message display
    			user.setPause(false);
    			door.setDisplayText(false);
    			reading = false;
    		}
    		else{//gets to exits message display
    			door.nextPage();
    		}		
    	}
    	else if (reading == false){
    		if(door.collide(user)){//looks if there are any items in collision with user
    			door.checkKey(user);
    			if(door.getLocked()){//if locked, display message
    				user.setPause(true); //user can't move when reading
    				door.setDisplayText(true);
    				door.nextPage();
    				reading = true;
    				currentItem = "door"; //gets information for message display in panel
    				indexOfItem = 0;
    			}
    			else{//setup for exiting panel
    				user.updatePos(door.getDestX(), door.getDestY());
    				openDoor.play();
    				exit = true;
    			}		
    		}
    	}
    }
    
    //checks for interaction with keyItems
   	public void checkKeyItems(){
   		if(reading){
   			if(currentItem.equals("keyItem")){//avoid accidental change when not reading the object
   				if(keyItems[indexOfItem].endPage()){//exit message display
   					keyItems[indexOfItem].setDisplayText(false);
    				user.setPause(false);
    				reading = false;
   				}
   				else if (keyItems[indexOfItem].inputPage() == false){//moves on with message display
   					keyItems[indexOfItem].nextPage();
   				}
   				else if (keyItems[indexOfItem].inputPage()){//input interactions
   					keyItems[indexOfItem].checkText(user);
   				}
   			}
   		}
   		else{
   			for(int i = 0; i < keyItems.length; i++){
   				if(keyItems[i].collide(user) && reading == false){//looks if there are any items in collision with user
   					reading = true;
	    			user.setPause(true); //user can't move when reading
	    			keyItems[i].setDisplayText(true);
	    			currentItem = "keyItem"; //gets information for message display in panel
	    			indexOfItem = i;
   				}
   			}
   		}
   	}
    
    //checks for interaction with optionItems
    public void checkOptionItems(){
    	if(reading){
    		if(currentItem.equals("optionItem")){//avoid accidental change
    			optionItems[indexOfItem].setDisplayText(false); //exit message display
    			user.setPause(false);
    			reading = false;
    		}
    	}
    	else{
    		for(int i = 0; i < optionItems.length; i++){
	    		if(optionItems[i].collide(user) && reading == false){//looks if there are any items in collision with user
	    			reading = true;
	    			user.setPause(true); //user can't move when reading
	    			if(optionItems[i].getOtherActivation()){//change the object if its associated object has been activated
	    				optionItems[i].changeOptionDescription(optionItems[optionItems[i].getOtherIndex()].isActivated());
	    			}
	    			optionItems[i].setDisplayText(true);
	    			currentItem = "optionItem"; //gets information for message display in panel
	    			indexOfItem = i;
    			}
    		}
    	}				
    }
    
    //checks for interaction with decorations
    public void checkDecors(){
    	if(reading){
    		if(currentItem.equals("decoration")){//avoid accidental change
    			if (decorations[indexOfItem].endPage()==false){//move on with message display
    			decorations[indexOfItem].nextPage();
	    		}
	    		else{//exit message display
	    			decorations[indexOfItem].setDisplayText(false);
		    		user.setPause(false);
		    		reading = false;
	    		}
    		}
    	}
    	else{
    		for(int i = 0; i < decorations.length; i++){
    			if(decorations[i].collide(user) && reading == false){//looks if there are any items in collision with user
    				reading = true;
    				user.setPause(true);//user can't move when reading
    				decorations[i].setDisplayText(true);
    				decorations[i].nextPage();
    				currentItem = "decoration"; //gets information for message display in panel
    				indexOfItem = i;
    			}
    		}
    	}
    }
    
    //PAINTING METHODS
    @Override
   	public void paintComponent(Graphics g){
   		paintRoom(g);
   		drawItems(g);
   		door.draw(g);
   		user.draw(g);
   		paintMessage(g); //avoid items from blocking the message box
   		
   	}
   	
   	//draws background
   	public void paintRoom(Graphics g){
   		g.drawImage(background, 0, 0, null);
   	}
   	
   	//displays the message of the specific item the user is currently reading
   	public void paintMessage(Graphics g){
   		if(reading){
   			if(currentItem.equals("optionItem")){
   				optionItems[indexOfItem].displayText(g);
   			}
   			else if (currentItem.equals("decoration")){
   				decorations[indexOfItem].displayText(g);
   			}
   			else if (currentItem.equals("keyItem")){
   				keyItems[indexOfItem].displayText(g);
   			}
   			else if (currentItem.equals("door")){
   				door.displayText(g);
   			}
   		}
   	}
   	
   	//draws the item in the current room user is in
   	public void drawItems(Graphics g){
   		for(Decoration item: decorations){
   			item.draw(g);
   		}
   		for(OptionItem item: optionItems){
   			item.draw(g);
   		}
   		for(KeyItem item: keyItems){
   			item.draw(g);
   		}
   	}
   	
   	//gets the current room number
    public int getRoomsNum(){
		return roomsNum;
	}
	
	//gets user for other panel to update
    public Player getPlayer(){
    	return user;
    }
    
    //used to update player object
    public void setPlayer(Player newUser){
    	user = newUser;
    }
}