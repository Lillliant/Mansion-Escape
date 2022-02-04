//BasicEnemy.java
//Christine Wong
//BasicEnemy creates objects that stops the player from further playing the game
//when they collide with each other by reaching to a bad ending.
//In the game, BasicEnemy objects will move independently from the user's movement.
//When these object collide at a certain area, the user is "killed".

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;
import java.util.Arrays;

public class BasicEnemy{
	
	//POSITION & MOVEMENT INFORMATION
	private int x, y; //top left corner
	private int dir; //direction the object is moving at the moment
	private int[] startBound; //the position the object started at
	private int[] endBound; // the position the object is aiming to go
	public static final int X = 0, Y=1, UNIT = 1; //UNIT - the displacement at a time in pixel
	
	public static final int LEFT=0, RIGHT=1, UP=2, DOWN=3, PAUSE = 4, WAIT = 8;
	//PAUSE - used in direction when switching startBound and endBound
	//WAIT - how long a frame displays before moving on to the next
	
	public static final String name = "res/sprite/skeletonsprite/skeleton"; //same for every object
	private Image[][] pics;
	private int frame, delay; //delay - the number of time the frame has been displayed
	
	//creates a new instance of the object based on positions given
	public BasicEnemy(int sx, int sy, int ex, int ey){
		x = sx;
		y = sy;
		startBound = new int[]{sx, sy};
		endBound = new int[]{ex, ey};
		
		//determines the starting direction automatically
		dir = startDir();
		
		//setup image display information
		frame = 0;
		delay = 0;
		//load image/frames
		pics = new Image[4][9];
		for(int px=0; px<4; px++){
			for(int py=0; py<9; py++){
				pics[px][py] = new ImageIcon(name+0+px+(py+1)+".png").getImage();
			}
		}
	}
	
	//determines the starting direction of each loop of movement
	public int startDir(){
		int d;
		
		//check if the object is moving along the x-position of endBound
		if(startBound[X] > endBound[X]){ //if not, make the object move towards it
			d = LEFT;
		}
		else if (startBound[X] < endBound[X]){
			d = RIGHT;
		}
		else{ //check if the object is moving along the y-position of endBound
			if(startBound[Y] > endBound[Y]){ //if not, make the object move towards it
				d = UP;
			}
			else if (startBound[Y] < endBound[Y]){
				d = DOWN;
			}
			else{
				d = PAUSE; //makes sure the 
				//object doesn't move in a loop when its path is linear
			}
		}
		
		return d;		
	}
	
		
	//checks the direction whilst moving to see if it needs changing
	public int checkDir(){
		if(x == endBound[X] && y == endBound[Y]){ //the end of the loop of movement
			//resets the position the object moves towards
			//the object will continue in a loop, moving in
			//a rectangular/linear path
			int [] tmp = endBound.clone();
			endBound = startBound.clone();
			startBound = tmp.clone();
			//redetermine starting direction
			return startDir();
		}
		else{ //if the object has not reached its end position yet
			if(y == endBound[Y]){ //if the user hasn't reach the x-position yet but has with y-position
				if(x < endBound[X]){ //make moving to the x-position a priority
					return RIGHT;
				}
				else{
					return LEFT;
				}
			}
			else{ //if the user hasn't reach the y-position yet but has with x-position
				if(y < endBound[Y]){ //make moving to the y-position a priority
					return DOWN;
				}
				else{
					return UP;
				}
			}
		}
	}
	
	//move the object
	public void move(){
		dir = checkDir(); //makes sure the object is moving at its intended path
		//makes sure that the enemy doesn't move diagonally
		if (dir==LEFT){
			x-=UNIT;
		}
		else if (dir==RIGHT){
			x+=UNIT;
		}
		else if (dir==DOWN){
			y+=UNIT;
		}
		else if (dir==UP){
			y-=UNIT;
		}
		constrainPos(); //makes sure the object doesn't go out of bound
		updateFrame(); //update visual information
	
	}
	
	//makes sure the object doesn't move aways from its intended path
	//because of difference between UNIT and rate of movement (UNIT)
	public void constrainPos(){
		//y shouldn't go lower than the lowest or higher than the highest of the positions
		//(path/linear)
		if(x < Math.min(startBound[X], endBound[X])){
			x = Math.min(startBound[X], endBound[X]);
		}
		if (x > Math.max(startBound[X], endBound[X])){
			x = Math.max(startBound[X], endBound[X]);
		}
		
		//y shouldn't go lower than the lowest or higher than the highest of the positions
		//(path/linear)
		if(y < Math.min(startBound[Y], endBound[Y])){
			y = Math.min(startBound[Y], endBound[Y]);
		}
		if (y > Math.max(startBound[Y], endBound[Y])){
			y = Math.max(startBound[Y], endBound[Y]);
		}
	}
	
	//checks the frame the sprite needs to be displayed as
	public void updateFrame(){
		delay += 1;
		//the frame need to be displayed at a certain rate 
		//to make it not too fast for user
		if(delay % WAIT == 0){
			frame = (frame + 1) % pics[dir].length;
		}
	}
	
	//draws the object when it is in Escaperoom panel
	public void drawWorld(Graphics g, int x_change, int y_change){
		//makes sure the object is at a secured position relative to the user
		g.drawImage(pics[dir][frame], x-x_change, y-y_change, null);
	}
	
	//checks to see if the user can touch the object
	public boolean collide(Player user){
		//create area of the object based on the instant
		int width = pics[dir][frame].getWidth(null);
		int height = pics[dir][frame].getHeight(null);
		Rectangle self_area = new Rectangle(x,y, width, height);
		
		return self_area.intersects(user.getEnemyRect());
	}
	
	//checks to see if the given area is within the interacting area of the object
	public boolean collide(Rectangle area){
		//create area of the object based on the instant
		int width = pics[dir][frame].getWidth(null);
		int height = pics[dir][frame].getHeight(null);
		Rectangle self_area = new Rectangle(x,y, width, height);
		
		return self_area.intersects(area);
	}
	
	//returns current y position of the object
	public int getY(){
		return y;
	}
	
	//returns current direction the object is moving towards
	public int getDir(){
		return dir;
	}
	
}