//SavePoint.java
//Christine Wong
//SavePoint creates objects that is the representation of locations where
//user can save their progress. When the user interacts with a SavePoint
//object while the Escaperoom panel is showing, then the game will save
//data to datafiles.

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class SavePoint {
	
	//IMAGE & TEXT INFORMATION
	//these variables are the same for every SavePoint objects and will not change
	//during the course of the game.
	public static final String name = "savepoint";
	public static final Image image = new ImageIcon("res/items_graphics/savePoint.png").getImage();;
	public static final Image msBoxPic = new ImageIcon("res/graphics/message_box.png").getImage();
	public static final ArrayList<String> description = Utilities.fileToMultiString("res/items_text/", "savePoint");
	public static final Font displayFont = Utilities.getFont("res/font/Happy Clover.ttf",28f);
	
	public static final int OPTIONPAGE = 0; //the page number where the user can make
	//options to save
	public static final int INTERACTUNIT = 15; //how many pixels around the object's image
	//can the user start accessing it
	
	private int x, y, width, height;
	private int currentPage, endPage; //what part of the description the object's at
	private boolean displayText = false; //used to determine when to draw messages
	
	//creates a new instance of SavePoint based on the position given
    public SavePoint(int x, int y) {
    	this.x = x;
    	this.y = y;
    	this.width = image.getWidth(null);
    	this.height = image.getHeight(null);
    	
    	currentPage = 0;
    	endPage = description.size() - 1; //avoid out of bound
    }
    
    //goes to the next part of the description
    public void nextPage(){
    	if(currentPage < endPage){ //avoid out of bound
    		currentPage++;
    	}
    }
	
	//returns if the object is at the page where
	//user can make an option
    public boolean isOptionPage(){
    	return currentPage==OPTIONPAGE;
    }
    
    //returns the part (index) of description the object is displaying
    public int getPage(){
    	return currentPage;
    }
    
    //if the message is at the last index of the description Arraylist
    public boolean endPage(){
		return currentPage + 1 >= endPage;
	}
	
	//reset the index of the description the message displays
    public void resetPage(){
    	currentPage=0;
    }
	
	//draws the SavePoint object when it is in the Escaperoom panel
    public void drawWorld(Graphics g, int x_change, int y_change){
		g.drawImage(image, x - x_change, y - y_change, null); //keep the object at a position
		//relative to the player
		if (displayText){
			displayText(g);
		}
    }
    
    //draws the message of the object
    public void displayText(Graphics g){
		//setup the border
		g.drawImage(msBoxPic,0,406,null);
		//set up the messages
		g.setFont(displayFont);
		g.setColor(Color.white);
		g.drawString(description.get(currentPage), 20, 450);
	}    
	
	//checks if the user has reached the area where user can access the object
	public boolean collide(Player user){
		//increase area with INTERACTUNIT to make accessing more convenient
		Rectangle self_area = new Rectangle(x - INTERACTUNIT, y - INTERACTUNIT,width + 2*INTERACTUNIT, height + 2*INTERACTUNIT);
		Rectangle user_area = user.getInteractRect();
		
		return self_area.intersects(user_area);
	}
	
	//returns the state of message display
	public boolean getDisplayText(){
		return displayText;
	}
	
	//changes the state of message display
	public void setDisplayText(boolean state){
		displayText = state;
		if(displayText == false){
			resetPage(); //avoid skipping the first page when accessing next time
		}
	}
}