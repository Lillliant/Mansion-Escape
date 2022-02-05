//OptionItems.java
//Christine Wong
//Creates object that the user can interact with.
//Can change image states based on the Y/N option user inputs
//via keyboard, and may give user a key to access other rooms 
//in the Escaperoom or Puzzle panel. May require activation
//from the changing of state from other optionItems before
//being able to take inputs or other manipulations.
 
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class OptionItem{
	//
	private String name;
	private final Font displayFont = Utilities.getFont("res/font/Happy Clover.ttf",28f);
	private ArrayList<String> description;
	
	private boolean displayText = false;
	private int x,y;
	
	//the y location the maximum boundary of which the character can access the item.
	private int width, height;
	
	//the boundary of interaction area - bx, by is the start (top left), ex and ey is the end
	private int bx, by, ex, ey;
	private boolean tangible; //if the player can goes through it
	
	//FOR IMAGE DISPLAY
	private int activationState = UNACTIVATED; //the different image states
	private int statenum; //the number of other states the object have (start at 0)
	private Image[] images; //the physical appearances of the object
	//the specific states the object has
	public static final int UNACTIVATED = 0;
	private int ACTIVATED;
	
	
	//FOR MESSAGE DISPLAY
	private ArrayList<String> optionDescription;
	private ArrayList<String> nonOptionDescription; //when the optionDisplay is not activated
	public static final int OPTIONPAGE = 0; //the page where user can input options
	private String nonKeyMessage; //for when the object no longer has keys
	public static final Image msBoxPic = new ImageIcon("res/graphics/message_box.png").getImage(); //the image for the message box
	private int currentPage, endPage;
	
	//FOR ADDING KEY
	private boolean keyGiveable; //if the user can receive a key by activating the object itself
	private boolean optionDisplay; //if the object current takes option input
	private boolean keyGiven; //if the key has been given to the player
    private int keyNum; //the location of the key that will be given
    
    //OPTIONDISPLAY
	private boolean otherKeyActivation; //if the other object activates its optionDisplay
    private String otherName; //the name of the other object
    private int otherIndex; //the index of the object in Puzzle panel
    
    //key information
    public static final int EMPTY = 0;
    public static final int KEY = 1; //used when in an array of "keys". Means that there is key in the position.
    //the position of the keys corresponds to the location of the room
    public static final String[] keyAsRooms = {"of this room", "to the master bedroom", "to the bathroom", "to the kitchen", "to the front door"};
	
	//add another value for if checking other objects, and if keygiveable
    public OptionItem(String name, int x, int y,
    				  int bx, int by, int ex, int ey,
    				  String tangible, String option,
    				  String key, int akn, String other,
    				  String...objectName){
    	
    	//loads position and image information		  	
    	this.name = name;
    	this.x = x;
    	this.y = y;
		images = Utilities.loadItemImages(name);
		statenum = images.length-1; //avoid out of bound
		width = images[activationState].getWidth(null);
		height = images[activationState].getHeight(null);
		
		//loads interaction area
		this.bx = x+bx;
		this.by = y+by;
		this.ex = width+ex;
		this.ey = height+ey;
		
		//loads interaction states/info
		this.tangible = Utilities.convertBoolean(tangible);
		optionDisplay = Utilities.convertBoolean(option);
		
		//loads key information
		keyGiveable = Utilities.convertBoolean(key);
		keyGiven = false;
		otherKeyActivation = Utilities.convertBoolean(other);
		
		//setup description/message
		description = Utilities.fileToMultiString("res/items_text/",name);
		setupActivation(akn, objectName);
    	currentPage = 0;
    	endPage = description.size();
    }
    
    public void setupActivation(int akn, String[] objectName){
    	if(otherKeyActivation){//if optionDisplay is activated by other objects
			otherName = objectName[0]; //name of that object that can activate this object
			//setup the description at different optionDisplay states
			nonOptionDescription = new ArrayList<String>();
			nonOptionDescription.addAll(description);
			optionDescription = Utilities.fileToMultiString("res/items_text/",name+"_option");
			
			if(keyGiveable){//prepare for change in description when user gets the key
				nonKeyMessage = optionDescription.get(1);
				optionDescription.set(1, String.format("You found the key %s!", keyAsRooms[keyNum]));
				keyNum = akn;
			}
			ACTIVATED = 0; //is already activated, but can't activate others
		}
		else{
			ACTIVATED = akn; //the image state when the object is activated
			keyNum = -1; //has no keys
		}
    }
    
    //sets the index of the object that can activate this object's optionDisplay
    public void setOtherIndex(int i){
    	otherIndex = i;
    }
    
    //gets the index of the object that can activate this object's optionDisplay
    public int getOtherIndex(){
    	return otherIndex;
    }
    
    //gets the name of the object that can activate this object's optionDisplay
    public String getOtherName(){
    	return otherName;
    }
    
    //gets if the object requires another object to activate optionDisplay
    public boolean getOtherActivation(){
    	return otherKeyActivation;
    }
	
	//reverts the image state of the object
	public void resetActivation(){
		activationState = UNACTIVATED;
	}
	
	//determines the change in image state
	public void nextActivationState(){//reverts to UNACTIVATED when exceed total states to avoid out of bound
		activationState = (activationState + 1 > statenum) ? UNACTIVATED : activationState + 1;
	}
	
	//input for options
	public void selectOption(Player user){
		if(optionDisplay && currentPage == OPTIONPAGE){//avoid accidental change
			nextActivationState(); //change image state
			if(keyGiveable){//for giving keys
				user.setKeys(addKey(user.getKeys()));
				keyGiven = true;//avoid giving duplicate keys
			}
		}
		if(endPage()==false){//changes message display
			nextPage();
		}
	}
    
    //gives the key the object has to user
    public int[] addKey(int[] keys){
    	if (keys[keyNum] == EMPTY){//makes sure key has not been given yet
    		keys[keyNum] = KEY;
    	}
    	return keys;
    }
    
    //returns the state of optionDisplay
   	public boolean getOptionDisplay(){
		return optionDisplay;
	}
    
    //changes specific message in the current description
    public void changeDescription(int index, String newMessage){
    	description.set(index, newMessage);
    }
    
    //changes the description of the object based on state of optionDisplay
    public void changeOptionDescription(boolean state){
    	optionDisplay = state;
    	if(optionDisplay==false){
    		description.clear(); //avoid making description longer the intended
    		description.addAll(nonOptionDescription);
    		endPage = description.size();
    	}
    	else{
    		description.clear(); //avoid making description longer the intended
    		description.addAll(optionDescription);
    		if(keyGiveable && keyGiven){//avoid giving false message for giving keys to user
				changeDescription(1, nonKeyMessage);
			}
    		endPage = description.size();
    	}
    }
    
    //returns the name of the object
    public String getName(){
    	return name;
    }
    
    //if the object is activated right now
    public boolean isActivated(){
    	if(ACTIVATED == activationState){
    		return true;
    	}
    	return false;
    }
    
    //sets the state of displaying message
    public void setDisplayText(boolean state){
		displayText = state;
		resetPage(); //avoid skipping option page
	}
	
	//if message is being displayed
	public boolean getDisplayText(){
		return displayText;
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
	
	//draws the object in Puzzle panel
    public void draw(Graphics g){
		g.drawImage(images[activationState], x, y, null);
		if (displayText){
			displayText(g);
		}
    }
	
	//if user collides with the interactive area of the object
	public boolean collide(Player user){
		Rectangle decoration_area = new Rectangle(bx,by, ex, ey);
		Rectangle user_area = user.getInteractRect();
		return decoration_area.intersects(user_area);
	}
	
	//if the area given collides with the image of the object
	public boolean collideItem(Rectangle area){
		if(tangible){//if the object cannot have items go through it
			Rectangle decoration_area = new Rectangle(x,y, width, height);
			return decoration_area.intersects(area);
		}
		return false;
	}
	
	//goes to the next part of the description
	public void nextPage(){
    	if(currentPage < endPage){ //avoid out of bound
    		currentPage++;
    	}
    }
    
    //returns the current index to description the message is on
    public int getPage(){
    	return currentPage;
    }
	
	//reset message for next access
	public void resetPage(){
    	currentPage = 0;
    }
    
	//if the message is at the last index of current description
	public boolean endPage(){
		return currentPage >= (endPage - 1);
	}
    
    //SAVE METHODS
    //load & update the changeable information from datafile to continue the previous progress
    public void txtUpdate(String line){
    	//split information into parts based on purpose
		String[] data = line.split(" ");
		
		//update information in the order they are saved in
    	activationState = Integer.parseInt(data[0]);
    	keyGiven = Utilities.convertBoolean(data[1]);
    	optionDisplay = Utilities.convertBoolean(data[2]);
    	if(otherKeyActivation){//update description based on input from datafile
    		changeOptionDescription(optionDisplay);
    	}
    }
    
    //convert changeable information into text to be saved in datafile
    public String toTxt(){
    	return activationState+" "+keyGiven+" "+optionDisplay+" "; //add " " so it's easier to split
    }
    
    @Override
    public String toString(){
    	return name;
    }
}