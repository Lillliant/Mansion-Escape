//Door.java
//Christine Wong
//Door creates object that helps user to switch between the Puzzle 
//and Escaperoom panel while in game. It requires the user to have 
//a key in the corresponding position (int[]) for the door to be
//able to be used. When it is unlocked and being accessed, the door
//help switch from the panel the user is in to the other panel in game.

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Door {
		
	//information for distinction, text and image display
	private String lockedDescription; //message to display when the door is locked
	private Image image;
	public static final Image msBoxPic = new ImageIcon("res/graphics/message_box.png").getImage(); //the image for the message box; same for every object
	public static final Font displayFont = Utilities.getFont("res/font/Happy Clover.ttf",28f); //font is same for every object
	
	//door information
	private int x,y;
	private int bx, by, ex, ey, height, width; //the interactive area the user can access
	
	private String dir; //the direction the door is facing
	private boolean locked; //if the door is locked and requires a key
	private int destX, destY; //the destination position user will be in
	private int roomNum; //the position of the room the door takes you to
	public static final int KEY = 1; //if the key is available
	
	//determines the message to be displayed in the message box
	private boolean displayText = false;
	private int currentPage;
	private int endPage;
	
	//creates a new instance of Door based on data file input
    public Door(int x, int y, int destX, int destY, int roomNum, String dir) {
    	//setup basic room information
    	this.x = x;
    	this.y = y;
    	this.destX = destX;
    	this.destY = destY;
    	this.dir = dir;
    	this.roomNum = roomNum;
    	
    	//setup display information
    	image = new ImageIcon("res/items_graphics/door_"+dir+".png").getImage();
    	width = image.getWidth(null);
    	height = image.getHeight(null);
    	setupBoundary(); //to make sure the boundary is available to user
    	locked = true;
    	
    	//setup message
    	lockedDescription  = "The door is locked.";
    	currentPage = 0;
    	endPage = 1;
    }
    
    //setup the interactive boundary based on the direction the door is facing
    public void setupBoundary(){
    	if(dir.equals("LEFT")){ //the door is facing left, there is area at the left side
    		bx = x - 30;
    		by = y;
    		ex = width;
    		ey = height;
    	}
    	else if(dir.equals("RIGHT")){ //the door is facing right, there is area at the right side
    		bx = x;
    		by = y;
    		ex = width + 30;
    		ey = height;
    	}
    	else if(dir.equals("UP")){ //the door is facing up, there is area at the top side
    		bx = x;
    		by = y - 30;
    		ex = width;
    		ey = height;
    	}
    	else{//if the door is facing down;  makes sure the door is activated to avoid crashing
    		bx = x;
    		by = y;
    		ex = width;
    		ey = height + 30;
    	}
    }
    
    //returns if user is at the interactive area of the object
    public boolean collide(Player user){
    	return getRect().intersects(user.getInteractRect());
    }
	
	//draws the object when it is at Puzzle panel
	public void draw(Graphics g){
		g.drawImage(image, x, y, null);
		if (displayText){
			displayText(g);
		}
	}
	
	//draws the object when it is at Escaperoom panel
    public void drawWorld(Graphics g, int x_change, int y_change){
    	//keep the object at a secured position relative to the player
		g.drawImage(image, x-x_change, y-y_change, null);
		if(displayText){
			displayText(g);
		}
	}
	
	//display the message assigned
    public void displayText(Graphics g){
		//setup the border
		g.drawImage(msBoxPic,0,406,null);
		//set up the messages
		g.setFont(displayFont);
		g.setColor(Color.white);
		g.drawString(lockedDescription, 20, 450);
	}
    
    //checks if the user has the needed key to unlock the door
    public void checkKey(Player user){
    	if (user.getKeys()[roomNum] == KEY){ //if there is a key at the location
    		unlock();
    	}
    }
    
    //sets the state of message display
    public void setDisplayText(boolean state){
		displayText = state;
		if(displayText == false){
			resetPage(); //avoid skipping the first page/out of bound when next access
		}
	}
	
	//resets index for message display
	public void resetPage(){
		currentPage = 0;
	}
	
	//returns if the message is being displayed
	public boolean getDisplayText(){
		return displayText;
	}
	
	//returns if the message is at the last index of description
	public boolean endPage(){
		return currentPage >= endPage;
	}
	
	//go to the next section for message display
	public void nextPage(){
    	if(currentPage<endPage){ //avoid out of bound
    		currentPage++;
    	}
    }	

	//returns the current section of description the message is in
    public int getCurrentPage(){
    	return currentPage;
    }
    
    //makes the door accessible
	public void unlock(){
    	locked = false;
    }
    
    //gets the state of accessibility
    public boolean getLocked(){
    	return locked;
    }
    
    //returns x position used by other classes while transporting user
    public int getDestX(){
    	return destX;
    }
    
    //returns y position used by other classes while transporting user
    public int getDestY(){
    	return destY;
    }
    
    //returns the room number for other classes to transport user to
    public int getRoomNum(){
    	return roomNum;
    }
    
    //returns the interact area for user
    public Rectangle getRect(){
    	return new Rectangle(bx, by, ex, ey);
    }
}