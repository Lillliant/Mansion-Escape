//Decoration.java
//Christine Wong
//Decoration creates decorative visual in-game objects that doesn't change other
//objects when user provides keyboard input. It can display messages
//and images when the user presses the space bar when in certain distance, but it doesn't
//change state or provide keys or other functions.

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.util.Scanner;
import java.util.ArrayList;

class Decoration{
	//information for distinction, text and image display
	private String name;
	private ArrayList<String> description; //what the message box will say when interacting with the object
	private Image image; //the physical appearance of the object
	public static final Image msBoxPic = new ImageIcon("res/graphics/message_box.png").getImage(); //the image for the message box; same for every object
	public static final Font displayFont = Utilities.getFont("res/font/Happy Clover.ttf",28f); //font is the same for every object
	
	private int x, y;
	private int width, height; //the location of the left top point of the item.
	
	//the boundary where the user can access the object
	private int bx, by, ex, ey;	//bx, by - top left corner; ex, ey - bottom right
	private boolean tangible; //if the player can go through the object
	
	//determines the message to be displayed in the message box
	private boolean displayText = false;
	private int currentPage;
	private int endPage;
	
	//creates a new instance of the Decoration object based on data file input
	public Decoration(String name, int x, int y, int bx, int by, int ex, int ey, String tangible){
		this.name = name;
		//finds the right file based on the name given
		description = Utilities.fileToMultiString("res/items_text/",name);
		image = new ImageIcon("res/items_graphics/"+name+".png").getImage();
		
		this.x = x;
		this.y = y;
		
		//gets the interact boundary by making
		//a rectangle around the x,y position
		this.bx = x+bx;
		this.by = y+by;
		width = image.getWidth(null);
		height = image.getHeight(null);
		this.ex = width+ex;
		this.ey = height+ey;
		
		this.tangible = Utilities.convertBoolean(tangible);
		currentPage = -1;
		endPage = description.size() - 1; //avoid out of bound
	}
	
	//draws the object when it is in the Puzzle panel
	public void draw(Graphics g){
		g.drawImage(image, x, y, null);
		if (displayText){
			displayText(g);
		}
	}
	
	//draws the object when it is in the Escaperoom panel
	public void drawWorld(Graphics g, int x_change, int y_change){
		//keep the object at a position relative to the player
		g.drawImage(image, x - x_change, y - y_change, null);
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
	
	//returns if the user collides with the interaction area of the object
	public boolean collide(Player user){
		Rectangle decoration_area = new Rectangle(bx,by, ex, ey);
		Rectangle user_area = user.getInteractRect();
		return decoration_area.intersects(user_area);
	}
	
	//returns if the item's physical image collides with the user
	//used when determined if the player can move to this location
	public boolean collideItem(Rectangle user_area){
		if(tangible){ //if the object's image cannot collide with the player
			Rectangle decoration_area = new Rectangle(x,y, width, height);
			return decoration_area.intersects(user_area);
		}
		return false;
	}
	
	//changes the state of message display
	public void setDisplayText(boolean state){
		displayText = state;
		if(displayText == false){
			resetPage(); //avoid skipping the first page when accessing next time
		}
	}
	
	//returns the state of message display
	public boolean getDisplayText(){
		return displayText;
	}	

	//reset the index of the description the message displays
	public void resetPage(){
		currentPage = -1;
	}

	//goes to the next part of the description
	public void nextPage(){
    	if(currentPage < endPage){ //avoid out of bound
    		currentPage++;
    	}
    }
    	
	//if the message is at the last index of the description Arraylist
	public boolean endPage(){
		return currentPage > (endPage - 1);
	}
}