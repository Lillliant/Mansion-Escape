//Escaperoom.java
//Christine Wong
//Creates the segment of the game where user can explore the different areas to access
//different rooms of the mansion. Also coordinates and draws the interaction between user and
//the Door, Decoration, and BasicEnemy objects. When user collides with these in-game objects,
//Escaperoom determines the appropriate visual, audio, and other responses based on the
//the interactions. This is also where the user can save their progress via their interaction
//with SavePoint objects.

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Escaperoom extends JPanel{
	//in-game objects
	private Player user = new Player(600,500);
	private BasicEnemy[] enemies;
	private Door[] doors;
	private SavePoint[] savePoints;
	private Decoration[] decorations;
	
	//images and other display files
	private BufferedImage background = Utilities.getImage("res/graphics/", "back");
	private BufferedImage mask = Utilities.getImage("res/graphics/", "mask");
	private Image filter = new ImageIcon("res/graphics/filter.png").getImage();
	//sound effects
	private static final AudioClip openDoor = Utilities.setupAudio("door.wav");
	private static final AudioClip killedSound = Utilities.setupAudio("hit.wav");
	//for endings
	private final Image killedEnding = new ImageIcon("res/graphics/killedEnding.png").getImage(); //when user gets killed by BasicEnemy objects
	private final Image escapedEnding = new ImageIcon("res/graphics/escapedEnding.png").getImage(); //when user collects all 5 keys and escapes
	//via front door
	
	//ending object
	private Rectangle frontDoor = new Rectangle(1195, 2054, 30, 36);
	private Image frontDoorPic = new ImageIcon("res/items_graphics/door_LEFT.png").getImage();
	
	//room info
	//centre of the screen
	private int centreX = 400;
	private int centreY = 300;
	private int roomNum; //the room in Puzzle panel user will be taken to
	
	private boolean exit; //if the user is ready to leave this panel
	private boolean save; //if the user wants to save progress
	
	private boolean ending; //if the game ended
	private boolean killed; //if user has collided with BasicEnemy object
	
	//message display info - for when the user is interacting with an object
	private String currentItem;
	private int indexOfItem;
	private boolean reading;

	private boolean []keys; //store keycodes
	private Game mainFrame; //hosts the content panel
	
	public static final int LEFT=0, RIGHT=1, UP=2, DOWN=3, PAUSE = 4;
	
	//Creates a new instance of the panel
    public Escaperoom(){
    	keys = new boolean[KeyEvent.KEY_LAST+1];
    	setPreferredSize(new Dimension(centreX*2, centreY*2));
    	setupRoom();
    	exit = false;
    	killed = false;
    	ending = false;
    }
    
    //setup the in-game objects in the panel
    public void setupRoom(){
   		enemies = Utilities.setupEnemies("escaperoom");
   		doors = Utilities.setupDoors("escaperoom");
   		savePoints = Utilities.setupSavePoints("escaperoom");
   		decorations = Utilities.setupDecorations("escaperoom");
   	}
    
    //returns if the user is leaving this panel
    public boolean getExit(){
    	return exit;
    }
    
    //set the state of if user is leaving the panel
    public void setExit(boolean state){
    	exit = state;
    }
    
    //access state of needing to save
    public boolean getSave(){
    	return save;
    }
    
    //set state of needing to save
    public void setSave(boolean state){
    	save = state;
    }
    
    //access current player object by other panels to update
    public Player getPlayer(){
    	return user;
    }
    
    //update current player object from other panels
    public void setPlayer(Player newUser){
    	user = newUser;
    }
    
    //update panel when user re-enters
    public void reset(){
    	exit = false;
    }
    
    //check if user has escaped
   	public void checkEnding(){
   		int KEY = 1;
   		//makes sure all condition for escape are satisfied
   		if(frontDoor.contains(user.getInteractRect()) && user.getKeys()[doors.length] == KEY){
   			ending = true;
   			killed = false;
   		}
   	}
   	
   	//returns if the game has finished (by getting killed by BasicEnemy objects/escape)
   	public boolean getEnding(){
   		return ending;
   	}
    
    //PAINT METHODS
    
    //draws the components in the panel
    @Override
   	public void paintComponent(Graphics g){
   		if(ending==false){//if the player still needs movements and other interactions
   			drawBackground(g);
	   		drawItems(g);
	   		drawMoving(g);
	   		g.drawImage(filter, 0, 0, null);
	   		drawMessage(g); //avoid player and other items from blocking the message box
   		}
   		else{//draws the ending the user gets when game finishes
   			drawEnding(g);
   		}
   	}
   	
   	//draws the ending based on user progress
   	public void drawEnding(Graphics g){
   		if(killed == false){//player escapes when not gotten killed
   			g.drawImage(escapedEnding, 0, 0, null);
   		}
   		else{
   			g.drawImage(killedEnding, 0, 0, null);
   		}
   	}
   	
   	//draws the message of the object user is currently reading
   	public void drawMessage(Graphics g){
   		if(reading){//avoid accidental messages
   			if(currentItem.equals("door")){
   				doors[indexOfItem].displayText(g);
   			}
   			else if (currentItem.equals("savePoint")){
   				savePoints[indexOfItem].displayText(g);
   			}
   			else if(currentItem.equals("decoration")){
   				decorations[indexOfItem].displayText(g);
   			}
   		}
   	}
   	
   	//draws all in-game stationary items
   	public void drawItems(Graphics g){
   		//makes sure objects has been properly offsetted from world coordinates
   		int x_change = user.getCX()-400;
   		int y_change = user.getCY()-300;
   		
   		for(SavePoint sp: savePoints){
   			sp.drawWorld(g, x_change, y_change);
   		}
   		for(Door door: doors){
   			door.drawWorld(g, x_change, y_change);
   		}
   		for(Decoration item: decorations){
   			item.drawWorld(g, x_change, y_change);
   		}
   		//draws the front door
   		g.drawImage(frontDoorPic, 1225 - x_change, 2054 - y_change, null);
   	}
   	
   	//draws all moving objects
   	public void drawMoving(Graphics g){
   		//makes sure objects has been properly offsetted from world coordinates
   		int x_change = user.getCX()-centreX;
   		int y_change = user.getCY()-centreY;
   		user.drawWorld(g);
   		for(BasicEnemy enemy: enemies){
   			enemy.drawWorld(g, x_change, y_change);
   			if(user.getCY()>enemy.getY()){//for layering effect & depth when interacting
   				user.drawWorld(g);
   			}
   		}
   	}
   	
   	//finds and draws the proper subimage based on user position from background
   	//that is too large for the screen
   	public void drawBackground(Graphics g){
   		//top left corner of the subimage
   		int px = 0;
   		int py = 0;
   		//display left and up partial image - for when player reaches the border
   		if(user.getCX() - centreX < 0){ //when user reaches the edge of the image
   			px = Math.abs(user.getCX() - centreX);
   		}
   		if(user.getCY() - centreY < 0){ //when user reaches the edge of the image
   			py = Math.abs(user.getCY() - centreY);
   		}
   		
   		//fills area not covered by the background image
   		g.setColor(Color.black);
   		g.fillRect(0,0,centreX*2,centreY*2);
   		
   		if(Utilities.subimage(background, user, centreX, centreY)!=null){//if user hasn't passed away from
   															//position of background in world coordinates
   			g.drawImage(Utilities.subimage(background, user, centreX, centreY),px ,py ,null);
   		}
   	}
   	
   	//move all the objects not controlled by user
   	//and check ending status
   	public void update(){
   		moveEnemies();
   		checkEnemyCollision();
   		checkEnding();
   	}
   	
   	//move the BasicEnemy objects in the panel
   	public void moveEnemies(){
		for(int i = 0; i < enemies.length; i++){
			enemies[i].move();
		}
	}
	
	//check if user has gotten killed by BasicEnemy objects
   	public void checkEnemyCollision(){
   		for(BasicEnemy enemy: enemies){
   			if(enemy.collide(user)){//if collision happens, object attacks user
   				killedSound.play(); //sound effect
   				ending = true;
   				killed = true;
   			}
   		}
   	}
	
	//INPUT METHODS
   	public void pressedKey(KeyEvent e){
   		if(e.getKeyCode() == KeyEvent.VK_RIGHT){//for movement
   			//set direction and frame even when not moving for animation effects
   			user.updateFrame(RIGHT);
   			user.setDir(RIGHT);
   			if(user.clearEscaperoom(mask, decorations)){//avoid getting stuck
   				user.move(RIGHT);
   			}
		}
		else if(e.getKeyCode()==KeyEvent.VK_LEFT){//for movement
			//set direction and frame even when not moving for animation effects
			user.updateFrame(LEFT);
   			user.setDir(LEFT);
			if(user.clearEscaperoom(mask, decorations)){//avoid getting stuck
   				user.move(LEFT);
   			}
		}
		else if(e.getKeyCode()==KeyEvent.VK_UP){//for movement
			//set direction and frame even when not moving for animation effects
			user.updateFrame(UP);
   			user.setDir(UP);
   			if(user.clearEscaperoom(mask, decorations)){//avoid getting stuck
   				user.move(UP);
   			}
		}
		else if(e.getKeyCode()==KeyEvent.VK_DOWN){//for movement
			//set direction and frame even when not moving for animation effects
			user.updateFrame(DOWN);
			user.setDir(DOWN);
			if(user.clearEscaperoom(mask, decorations)){//avoid getting stuck
   				user.move(DOWN);
   			}
		}
			
    	if(e.getKeyCode()==KeyEvent.VK_SPACE){//for interactions
    		checkSurrounding(); //look for objects to interact
    	}
    		
    	if (e.getKeyCode()==KeyEvent.VK_Y || e.getKeyCode()==KeyEvent.VK_N){//for saving
    		if(e.getKeyCode()==KeyEvent.VK_Y){
    			if(reading && currentItem.equals("savePoint") && savePoints[indexOfItem].isOptionPage()){//avoid accidental saving
	    			save = true;
	    			savePoints[indexOfItem].nextPage();
	    		}
    		}
    		else if (e.getKeyCode()==KeyEvent.VK_N){//exit message display
    			savePoints[indexOfItem].setDisplayText(false);
    			user.setPause(false);
    			reading = false;
    		}
    	}
   	}
   	
   	public void releasedKey(KeyEvent e){
    	if(e.getKeyCode() == KeyEvent.VK_RIGHT){
    		//make user look standing still when not moving
				user.move(PAUSE);
				user.updateFrame(PAUSE);
			}
			else if(e.getKeyCode()==KeyEvent.VK_LEFT){
				//make user look standing still when not moving
				user.move(PAUSE);
				user.updateFrame(PAUSE);
			}
			else if(e.getKeyCode()==KeyEvent.VK_UP){
				//make user look standing still when not moving
				user.move(PAUSE);
				user.updateFrame(PAUSE);
			}
			else if(e.getKeyCode()==KeyEvent.VK_DOWN){
				//make user look standing still when not moving
				user.move(PAUSE);
				user.updateFrame(PAUSE);
			}
    }
    
   	//check all stationary in-game objects
   	public void checkSurrounding(){
    	checkDecors();
    	checkDoor();
    	checkSavePoints();
    }
	
	//check for interactions with Decoration objects
    public void checkDecors(){
    	if(reading){
    		if(currentItem.equals("decoration")){//avoid accidental message display
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
    			if(decorations[i].collide(user) && reading == false){//look for interactable objects via collision
    				reading = true;
    				user.setPause(true);//user can't move during interaction to avoid complication of interactions with other objects
    				decorations[i].setDisplayText(true);
    				currentItem = "decoration";//get info for panel message display
    				indexOfItem = i;
	    			decorations[i].nextPage();
    			}
    		}
    	}
    }
    
    //check for interactions with SavePoint objects
    public void checkSavePoints(){
    	if(reading && currentItem.equals("savePoint")){//avoid accidental changes
    		if(savePoints[indexOfItem].endPage()){//exit message display
    			user.setPause(false);
    			savePoints[indexOfItem].setDisplayText(false);
    			reading = false;
    		}
    	}
    	else if (reading == false){
    		for(int i = 0; i < savePoints.length; i++){
    			if(savePoints[i].collide(user) && reading == false){//look for interactable objects via collision
	    			user.setPause(true);//user can't move during interaction to avoid complication of interactions with other objects
	    			savePoints[i].setDisplayText(true);
	    			reading = true;
	    			currentItem = "savePoint"; //get info for message display
	    			indexOfItem = i;
    			}
    		}
    	}
    }
    
    //check for interaction with Door objects
    public void checkDoor(){
    	if(reading && currentItem.equals("door")){//avoid accidental change
    		if(doors[indexOfItem].endPage()){//exit message display
    			user.setPause(false);
    			doors[indexOfItem].setDisplayText(false);
    			reading = false;
    		}		
    	}
    	else if (reading == false){
    		for(int i = 0; i<doors.length; i++){
    			if(doors[i].collide(user) && reading == false){//look for interactable objects via collision
    				doors[i].checkKey(user);
	    			if(doors[i].getLocked()){//display locked message
	    				user.setPause(true);
	    				doors[i].setDisplayText(true);
	    				doors[i].nextPage();
	    				reading = true;
	    				currentItem = "door";
	    				indexOfItem = i;
	    			}
	    			else{//setup for leaving Escaperoom panel
	    				user.updatePos(doors[i].getDestX(), doors[i].getDestY());
	    				roomNum = doors[i].getRoomNum(); //setup for levelediting in Puzzle panel
	    				openDoor.play(); //sound effect
	    				exit = true;
	    			}		
    			}
    		}
    	}
    }
    
    //get the destination room number user will go in Puzzle panel
    public int getRoomNum(){
    	return roomNum;
    }
}