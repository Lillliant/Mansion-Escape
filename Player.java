//Player.java
//Christine Wong
//Player creates object that the user directly controls
//via keyboard input. It can move and interact with other objects
//while in Puzzle and Escaperoom panel, and these panels
//determines what happens when the user interacts with it.
//The object will also change image (animation) when it moves.

import javax.swing.*;
import java.awt.*;
import java.awt.image.*; 
import java.io.*;
import javax.imageio.*;
import java.util.Arrays;

public class Player{
	//the position of the player (top left corner)
	private int x, y;
	private int width, height;
	
	//graphics
	//4 , 4 - number of direction, number of frames per direction
	private Image[][] pics; //for storing the frames of the walking animation
	
	//user information
	private String base = "res/sprite/playersprite/player"; //for loading the graphic
	public static final int UNIT = 5; //how much pixel the player moves for every key press
	private boolean pause = false; //if the player is being paused right now
	
	private int dir, frame, delay;
	//frame - current frame of the animation
	//delay - keeps track of how long the current frame has been displayed
	
	//the directions the player can move in
	public static final int LEFT=0, RIGHT=1, UP=2, DOWN=3, PAUSE = 4, WAIT = 4;
	//pause -  when the user is not moving; wait -  number of frame of the animation before 
	//it moves onto the next frame
	
	//INVENTORY
	private int [] keys; //the keys the user needs to obtain in order to go to different rooms.
	//the position of the key corresponds to different rooms. Obtaining the key in that position grants
	//user the permission to go to that room.
	
	//creates new instance based on position	
	public Player(int x, int y){
		//loading in the position
		this.x = x;
		this.y = y;
		
		//loading in basic user information
		keys = new int[5];
		
		dir = RIGHT; //the default direction and animation frame
		frame = 0;
		delay = 0;
		
		//loads the walking animation frames
		pics = loadWalking(4,4);
	}
	
	//loads the graphics of the walking animation
	public Image[][] loadWalking(int d, int f){
		Image[][] tmp = new Image[d][f]; //d - number of direction, f - number of frames per direction
		for(int px = 0; px < d; px++){
			for(int py = 0; py < f; py++){
				tmp[px][py] = new ImageIcon(base+0+px+(py+1)+".png").getImage();
			}
		}
		return tmp;
	}
	
	//for moving the player object's position in Puzzle panel
	public void move(int direction, Decoration[] decors, OptionItem[] options, KeyItem[] keys){
		if(pause == false){//makes sure the player is able to move right now
			//updates the direction even when not moving for animation
			if(direction==LEFT){
				if (clearItems(decors, options, keys, -UNIT,0)){//makes sure doesn't collide with tangible items
					x-=UNIT;
				}
				dir = LEFT;
			}
			else if(direction==RIGHT){
				if(clearItems(decors, options, keys, UNIT,0)){//makes sure doesn't collide with tangible items
					x+=UNIT;
				}
				dir = RIGHT;
			}
			else if (direction==UP){
				if (clearItems(decors, options, keys, 0, -UNIT)){//makes sure doesn't collide with tangible items
					y-=UNIT;
				}
				dir = UP;
			}
			else if(direction==DOWN){
				if (clearItems(decors, options, keys, 0, UNIT)){//makes sure doesn't collide with tangible items
					y+=UNIT;
				}
				dir = DOWN;
			}
			//update visual information
			updateFrame(direction);
		}
	}
	
	//for moving the player object in the Escaperoom panel
	public void move(int direction){//checking for clearance is done at the panel level
		if(pause == false){//makes sure the user is able to move right now
			if(direction==LEFT){
				x-=UNIT;
				dir = LEFT;
			}
			else if(direction==RIGHT){//uses else-if to make sure user doesn't move diagonally
				x+=UNIT;
				dir = RIGHT;
			}
			else if (direction==UP){
				y-=UNIT;
				dir = UP;
			}
			else if(direction==DOWN){
				y+=UNIT;
				dir = DOWN;
			}	
		}
	}
	
	//check if the user is colliding any of the tangible items before moving
	private boolean clearItems(Decoration[] decors, OptionItem[] options, KeyItem[] kIs, int x_change, int y_change){
		return clearDecorations(decors, x_change, y_change) &&
			   clearOptionItems(options, x_change, y_change) &&
			   clearKeyItems(kIs, x_change, y_change); //user can move only when not colliding with any of the items
	}
	
	//sub-method from clearItems; check if the user is colliding with any of the tangible optionItems
	private boolean clearOptionItems(OptionItem[] options, int x_change, int y_change){
		if(options != null){//avoid null exception
			for(OptionItem item: options){
				if(item.collideItem(this.getInteractRect(x_change, y_change))){//uses the preview position to avoid getting stuck
					return false;
				}
			}
		}
		return true;
	}
	
	//sub-method from clearItems; check if the user is colliding with any of the tangible keyItems
	private boolean clearKeyItems(KeyItem[] kIs, int x_change, int y_change){
		if(kIs!=null){//avoid null exception
			for(KeyItem item: kIs){
				if(item.collideItem(this.getInteractRect(x_change, y_change))){//uses the preview position to avoid getting stuck
					return false;
				}
			}
		}
		return true;
	}
	
	//sub-method from clearItems; check if the user is colliding with any of the tangible decorations
	private boolean clearDecorations(Decoration[] decors, int x_change, int y_change){
		if(decors != null){//avoid null exception
			for(Decoration item: decors){
				if(item.collideItem(this.getInteractRect(x_change, y_change))){//uses the preview position to avoid getting stuck
					return false;
				}
			}
		}
		return true;
	}
	
	//clearance method used in Escaperoom panel
	//makes sure the user is not in collision with any of the items or the wall/boundaries
	public boolean clearEscaperoom(BufferedImage mask, Decoration[] decors){
		if(dir==RIGHT){//check based on what position the user will be in after movement
					   //to avoid getting stuck
			return clearMask(mask, UNIT) && clearDecorations(decors, UNIT, 0);
		}
		else if(dir==LEFT){//check based on what position the user will be in after movement
					   	   //to avoid getting stuck
			return clearMask(mask, UNIT) && clearDecorations(decors, -UNIT, 0);
		}
		else if(dir==UP){//check based on what position the user will be in after movement
					     //to avoid getting stuck
			return clearMask(mask, UNIT) && clearDecorations(decors, 0, -UNIT);
		}
		else if(dir==DOWN){//check based on what position the user will be in after movement
					   //to avoid getting stuck
			return clearMask(mask, UNIT) && clearDecorations(decors, 0, UNIT);
		}
		return true; //the user can move if there is no collision (true)
	}
	
	//used when the user is in Escaperoom panel
	//checks if the bottom half of the user (uses 4 reference points) are in collision of the mask
	private boolean clearMask(BufferedImage mask, int dist){
		if(dir == RIGHT){//uses preview position to avoid getting stuck
			return Utilities.clear(mask, x+dist, getCY()) && Utilities.clear(mask, x+width+dist, getCY())
				&& Utilities.clear(mask, x+dist, y+height) && Utilities.clear(mask, x+width+dist, y+height);
		}
		else if(dir == LEFT){//uses preview position to avoid getting stuck
			return Utilities.clear(mask, x-dist, getCY()) && Utilities.clear(mask, x+width-dist, getCY())
				&& Utilities.clear(mask, x-dist, y+height) && Utilities.clear(mask, x+width-dist, y+height);
		}
		else if(dir == UP){//uses preview position to avoid getting stuck
			return Utilities.clear(mask, x, getCY()-dist) && Utilities.clear(mask, x, y+height-dist)
				&& Utilities.clear(mask, x+width, getCY()-dist) && Utilities.clear(mask, x+width, y+height-dist);
		}
		else if(dir==DOWN){//uses preview position to avoid getting stuck
			return Utilities.clear(mask, x, getCY()+dist) && Utilities.clear(mask, x, y+height+dist)
				&& Utilities.clear(mask, x+width, getCY()+dist) && Utilities.clear(mask, x+width, y+height+dist);
		}
		return true; //the user can move if there is no collision (true)
	}
	
	//determines what frame the animation is in
	public void updateFrame(int direction){
		delay += 1;
		//WAIT here dictates the speed of the object animation
		if(delay % WAIT == 0){
			frame = (frame + 1) % pics[dir].length;
		}
		if (direction==PAUSE){//so the user looks standing still
			frame=0;
		}
	}
	
	//makes sure the user doesn't go out of bound in the panel
	public void constrainX(int low, int high){
		if(x < low){
			x = low;
		}
		else if(x + width > high){ //if the right side of the image goes out of bound
			x= high - width;
		}
	}
	
	//makes sure the user doesn't go out of bound in the panel
	public void constrainY(int low, int high){
		if(y + (height/2) < low){ //if the centre in y-position is out of bound
			y = low - (height/2);
		}
		else if (y + height > high){ //if the bottom side is out of bound
			y = high - height;
		}
	}
	
	//draws the user when in Puzzle panel
	public void draw(Graphics g){
		//update image lengths from the frames 
		width = pics[dir][frame].getWidth(null);
		height = pics[dir][frame].getHeight(null);
		
		g.drawImage(pics[dir][frame], x, y, null);
	}
	
	//draws the user when in Escaperoom panel
	public void drawWorld(Graphics g){
		//update image lengths from the frames 
		width = pics[dir][frame].getWidth(null);
		height = pics[dir][frame].getHeight(null);
		int halfX = width/2;
		int halfY = height/2;
		//makes sure the centre of the user is at the centre of the screen
		g.drawImage(pics[dir][frame], x-(x-400)-halfX, y-(y-300)-halfY, null);
	}
	
	//returns the centre of the user (x-position)
	public int getCX(){
		return x+(width/2);
	}
	
	//returns the centre of the user (y-position)
	public int getCY(){
		return y+(height/2);
	}
	
	//returns the x-position of the user
	public int getX(){
		return x;
	}
	
	//returns the y-position of the user
	public int getY(){
		return y;
	}
	
	//returns the keys and key information of the user
	public int[] getKeys(){
		return keys;
	}	
	
	//update the keys
	public void setKeys(int[] newKeys){
		keys = Arrays.copyOf(newKeys, newKeys.length);
	}
	
	//update user position
	public void updatePos(int x, int y){
		this.x = x;
		this.y = y;
	}	
	
	//determines if the player can move or not (get paused)
	public void setPause(boolean state){
		pause = state;
	}
	
	//get the state of paused for the player object
	public boolean getPause(){
		return pause;
	}
	
	//sets the direction the user is facing/moving
	public void setDir(int direction){
		dir = direction;
	}
	
	//get the area of the player that can interact with the surrounding objects
	//(at the feet of the player image)
	public Rectangle getInteractRect(){
		int size = 2; //the length of that "feet"
		return new Rectangle(getCX() - size, y + height - size, size*2, size);
	}
	
	//gets the area the enemy can collide with the user
	//(at the centre of the player image)
	public Rectangle getEnemyRect(){
		int size = 5; //the length of the area
		return new Rectangle(x + size, getCY() - size, width - size*2, size*2);
	}
	
	//overload method of getInteractRect(), for when need to predict the location of the
	//interacting area after movement
	public Rectangle getInteractRect(int x_change, int y_change){
		int size = 2; //the height of that "feet"
		return new Rectangle(getCX()+x_change - size, y+height-size+y_change,size*2,size);
	}
	
	//SAVING METHODS
	
	//convert changeable user information into text to be saved in datafile
	public String toTxt(){
		String keyString = Arrays.toString(keys).substring(1, Arrays.toString(keys).length()-1).replaceAll(",",""); //clean the key information
		//so it is easier to load
		
		return x+" "+y+" "+dir+" "+keyString;
	}
	
	//load & update the changeable user information from datafile to continue the previous progress
	public void txtUpdate(String line){
		//split information into parts based on purpose
		String[] data = line.split(" ");
		int[] val = Utilities.parseInts(Arrays.copyOfRange(data, 0, data.length));
		
		//update information in the order they are saved in
		x = val[0];
		y = val[1];
		dir = val[2];
		keys = Arrays.copyOfRange(val, 3, val.length);
	}
}