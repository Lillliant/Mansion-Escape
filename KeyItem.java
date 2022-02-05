//KeyItem.java
//Christine Wong
//Creates objects that the user can input characters or numbers
//into to interact with it. May change image states based on input or
//give key when input matches the password that "locks" the object.
//User needs to interact with it first via spacebar (keyboard) before
//being able to input characters.

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class KeyItem {
	
	//basic object information for datafiles or display
	private String name;
	public static Image msBoxPic = new ImageIcon("res/graphics/message_box.png").getImage(); //the image for the message box
	public static final Font displayFont = Utilities.getFont("res/font/Happy Clover.ttf",28f);
	private int x, y, width, height;
	
	//the interaction area boundary - bx, by is the start (top left), ex and ey is the end
	private int bx, by, ex, ey;
	//if the player can goes through it
	private boolean tangible;
	
	//FOR MESSAGE DISPLAY
	private int currentPage;
	private int endPage; //the end of the description to exit message display
	private ArrayList<String> description;
	private boolean displayText = false;
	private final int KEYINPUTPAGE; //the page when the user inputs characters
    private final int RIGHTPAGE; //the message displayed when user inputs correctly
    private final int WRONGPAGE; //the message displayed when user inputs wrong
	
	//FOR IMAGE DISPLAY
	private int activationState = UNACTIVATED; //the different activationState between states
	private int statenum; //the number of other states the object have (start at 0)
	private Image[] images; //the physical appearances of the object
	public static final int UNACTIVATED = 0; //the original state
    
	//FOR KEY INPUT
	private String line; //user input of password
	private String answer; //the correct password
	//FOR ADDING KEY
	private String antiKeyMessage = "It's empty."; //when the object no longer has keys
	private boolean keyGiven; //if the object has given the key to user
    private int keyNum; //the position of the room the key leads to
    private boolean lockState = true; //if the object is locked
	
	//key information
    public static final int EMPTY = 0;
    public static final int KEY = 1; //used when in an array of "keys". Means that there is key in the position.
    public static final String[] keyAsRooms = {"of this room", "to the master bedroom", "to the bathroom", "to the kitchen", "to the front door"};
	
	//creates new instances of object based on datafile input
    public KeyItem(String name, int x, int y, int bx, int by, int ex, int ey, int kn, String tangible, String answer) {
    	//laod basic information
    	this.name = name;
    	this.x = x;
    	this.y = y;
    	this.bx = x+bx;
		this.by = y+by;
		this.answer = answer;
		this.tangible = Utilities.convertBoolean(tangible);
		description = Utilities.fileToMultiString("res/items_text/",name);
		
		//loads images and creates the interaction area
		images = Utilities.loadItemImages(name);
		statenum = images.length-1; //avoid out of bound
		width = images[activationState].getWidth(null);
		height = images[activationState].getHeight(null);
		this.ex = width+ex;
		this.ey = height+ey;
	
		//loads key information - all keyItems has key inside them
		keyNum = kn;
		keyGiven = false;
		
		//set up variables for correct display of messages when interacting
		KEYINPUTPAGE = description.size()-1;
		RIGHTPAGE = KEYINPUTPAGE + 1;
		WRONGPAGE = KEYINPUTPAGE + 2;
    	currentPage = 0;
    	//add on to the original description for key messages
    	description.add(String.format("You obtained the key %s!", keyAsRooms[keyNum]));
    	description.add("It didn't open.");
    	endPage = description.size();
    }
    
    //SAVE METHODS
    //load & update the changeable information from datafile to continue the previous progress
    public void txtUpdate(String line){
    	//split information into parts based on purpose
    	String[] data = line.split(" ");
    	//update information in the order they are saved in
    	keyGiven = Utilities.convertBoolean(data[0]);
    	activationState = Integer.parseInt(data[1]);
    	if(keyGiven){//update description based on input from datafile
    		changeDescription();
    	}
    }
    
    //convert changeable information into text to be saved in datafile
    public String toTxt(){
    	return ""+keyGiven+" "+activationState; //add " " so it's easier to split
    }
    
    //revets image state to its original
    public void resetActivation(){
		activationState = UNACTIVATED;
	}
	
	//determine next image state
	public void nextActivationState(){//avoid out of bound when exceed total number of image states
		activationState = (activationState + 1 > statenum) ? UNACTIVATED : activationState + 1;
	}
    
    //returns the name of the object
    public String getName(){
    	return name;
    }
    
    //adds key input from user
    //resource: https://stackoverflow.com/questions/15991822/java-converting-keycode-to-string-or-char
    public void addText(KeyEvent e){
    	if(Character.isLetter(e.getKeyChar()) || Character.isDigit(e.getKeyChar())){//if the character can be registered
    		if(currentPage == KEYINPUTPAGE && keyGiven == false && line.length()<15){//avoid message go out of box or accidental change
    			line+=e.getKeyChar();
    		}
    	}
    }
    
    //delete key input from user
   	public void deleteText(){
    	if(line.length()>0 && currentPage == KEYINPUTPAGE && keyGiven == false){//avoid out of bound or accidental change
    		line = line.substring(0,line.length()-1);
    	}
    }
    
    //checks if the user key input can unlock the object
    public void checkText(Player user){
    	if(line.equals(answer) && currentPage == KEYINPUTPAGE && keyGiven == false){//avoid accidental change
    		nextActivationState();
    		user.setKeys(addKey(user.getKeys())); //give user the object's keys
			keyGiven = true; //avoid duplicate act of giving
			setPage(RIGHTPAGE);
			endPage = RIGHTPAGE; //avoid going to the wrong page
    	}
    	else if (!(line.equals(answer))){//when the input is wrong
    		setPage(WRONGPAGE);
    	}
    }
    
    //update user input for next access
	public void resetLine(){
		line = "";
	}
	
	//gives user the key of the object
    public int[] addKey(int[] keys){
    	if (keys[keyNum] == EMPTY){
    		keys[keyNum] = KEY;
    	}
    	return keys;
    }
    
    //setup message for next access
    public void resetPage(){
    	currentPage=0;
    }
    
    //goes to the next part of the description
	public void nextPage(){
    	if(currentPage < endPage){ //avoid out of bound
    		currentPage++;
    	}
    }
    
    //returns the current index of description in message display
    public int getPage(){
    	return currentPage;
    }
    
    //if the index message display is on allows key input
    public boolean inputPage(){
    	if(keyGiven == true){
    		return false; //when key is given, object is unlocked and no longer needs password
    	}
    	return currentPage == KEYINPUTPAGE;
    }
    
    //set the current page of message display
	public void setPage(int num){
		currentPage = num;
	}
	
	//if the current page is at the end index of total messages
	public boolean endPage(){
		return currentPage >= (endPage - 1);
	}
	
	//draws the object when in Puzzle panel
    public void draw(Graphics g){
		g.drawImage(images[activationState], x, y, null);
		if (displayText){
			displayText(g);
		}
    }
	  
	//displays current message
    public void displayText(Graphics g){
		//setup the border
		g.drawImage(msBoxPic,0,406,null);
		//set up the messages
		g.setFont(displayFont);
		g.setColor(Color.white);
		if(currentPage == KEYINPUTPAGE){//makes sure user can see their input
			g.drawString(description.get(currentPage)+" "+line, 20, 450);
		}
		else{
			g.drawString(description.get(currentPage), 20, 450);
		}
	}
	
	//changes the state of message display
	public void setDisplayText(boolean state){
		displayText = state;
		//setup for next access
		resetPage();
		resetLine();
		if(keyGiven){//makes sure user doesn't get false key-giving message
			changeDescription();
		}
	}
	
	//changes the description of the object after key is given
	public void changeDescription(){
		description.clear();
		description.add(antiKeyMessage);
		endPage = description.size();
	}
	
	//returns the current state of message display
	public boolean getDisplayText(){
		return displayText;
	}
	
	//checks if user collides with the object's interaction area
	public boolean collide(Player user){
		Rectangle self_area = new Rectangle(bx,by, ex, ey);
		Rectangle user_area = user.getInteractRect();
		return self_area.intersects(user_area);
	}
	
	//checks if the given area collides with the current object
	public boolean collideItem(Rectangle area){
		if(tangible){//if the object cannot have other areas inside of its area
			Rectangle self_area = new Rectangle(x,y, width, height);
			return self_area.intersects(area);
		}
		return false;
	}
	
	@Override
	public String toString(){
		return name;
	}
}