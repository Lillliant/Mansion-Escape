//Utilities.java
//Christine Wong
//Includes methods that are shared among other classes, or
//helps the functioning of classes but are not closely related
//with the purpose of the classes. Include methods for drawing
//graphic components, loading graphic, audio, and game object 
//components, and the processing of interactions between objects.
//Also includes methods related to saving progresses in game.

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Utilities {
	
	//MUSIC METHODS
	//loads an audioclip based on filename given.
	//Returns null when file not found.
    public static AudioClip setupAudio(String fName){
    	File audioFile = new File(String.format("res/audio/%s", fName));
    	try{
    		AudioClip sound = Applet.newAudioClip(audioFile.toURL()); //setup sound object
    		return sound;
    	}
    	catch(Exception e){
    		System.out.println(e);
    	}
    	return null;
    }
    
    //FONT METHODS

    //creates a font object based on filename and fontsize given.
    //return null if exceptions occur during font creation.
	public static Font getFont(String fName, float fontSize){
		InputStream is = Decoration.class.getResourceAsStream(fName);
		try{
    		Font newFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(fontSize); //create the objects
    		return newFont;
    	}
    	catch(IOException e){//handling different errors
    		System.out.println(e);
    	}
    	catch(FontFormatException e){
    		System.out.println(e);	
    	}
    	return null;
	}
       
    //LOGIC METHODS
    //converts a String ("true"/"false") into boolean states
    public static boolean convertBoolean(String state){
		return state.equals("true");
	}
	
    //takes a String array of numbers and convert them into an integer array.
    //the objects in the String array must all be numbers.
    public static int[] parseInts(String[] line){
    	int size = line.length;
    	int[] tmp = new int[size];
    	for (int i = 0; i < line.length; i++){
    		tmp[i] = Integer.parseInt(line[i]);
    	}
    	return tmp;
    }
    
    //returns a randomized integer; high-inclusive
    public static int randint(int low, int high){
	    return (int)(Math.random()*(high-low+1)+low);
	}
	
	//converts a string into a string array
   	public static String[] toStringArray(String line){
   		return line.substring(1,line.length()-1).split(", ");
   	}
    
    //finds the index of an object in a list based on the information given
   	public static int findIndex(OptionItem[] items, String name){
   		List<String> tmp = Arrays.asList(toStringArray(Arrays.toString(items)));
   		return tmp.indexOf(name);
   	}
   	
    //I/O METHODS
    
    //converts the data in a file of the given path into a String array
    //return an empty array when file not found.
    public static ArrayList<String> fileToMultiString(String path, String fName){
    	fName = String.format(path+fName+".txt", fName); //setup file path
    	ArrayList<String> texts = new ArrayList<String>();
    	try{
    		Scanner inFile = new Scanner(new BufferedReader(new FileReader(fName)));
    		while (inFile.hasNextLine()){
    			texts.add(inFile.nextLine().strip()); //clean the invisible characters
    		}
    	}
    	catch(IOException e){
    		System.out.println(e);
    	}
    	return texts;
    }
    
    //helps filter out files with specific file names
    static class FileFinder implements FilenameFilter{
    	private String name;
   		
   		//name is the whitelist filter
    	public FileFinder(String n){
    		name = n;
    	}
    	
    	//finds if the file has a certain String in its filename
    	@Override
    	public boolean accept(File path, String fName){
    		return fName.startsWith(name);
    	}
    }
   	
   	//REFERENCE: https://www.tutorialspoint.com/javaexamples/dir_search_file.htm
   	//https://examples.javacodegeeks.com/core-java/io/filenamefilter/java-filenamefilter-example/
   	
   	//finds all the files that starts with a specific filename within a specific directory
   	public static String[] findFiles(String path, String fName){
   		File address = new File(path); //the directory being looked at
   		FileFinder filter = new FileFinder(fName);
   		return address.list(filter); //Creates a string array of filenames within the parameter
   	}
   	
   	//write contents to a .txt file based on filename and string array given.
	public static void writeToFile(String fName, String[] lines){
		fName = String.format("res/savePoint/%s.txt",fName); //format path
		try{
			PrintWriter outFile = new PrintWriter(new BufferedWriter (new FileWriter (fName)));
			for(String line: lines){
				outFile.println(line); //write every object inside array as a new line
			}
			outFile.close();
		}
		catch(IOException e){//handling exception
			System.out.println(e);
		}
	}
	
	//read and returns a string array of the contents of a .txt data file.
	//returns null if no such file found.
	public static String[] readFile(String fName){
		fName = String.format("res/savePoint/%s.txt",fName);
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(fName)));
			ArrayList<String> tmp = new ArrayList<String>();
			while(inFile.hasNextLine()){
				tmp.add(inFile.nextLine().strip());
			}
			return tmp.toArray(new String[0]); //change arraylist to a string array
		}
		catch(IOException e){
			System.out.println(e);
		}
		return null;
	}
	
	//returns if the game has been played previously.
	public static boolean gamePlayed(){
		try{
			Scanner playerFile = new Scanner(new BufferedReader(new FileReader("res/savePoint/player.txt")));
			Scanner optionFile = new Scanner(new BufferedReader(new FileReader("res/savePoint/optionItems.txt")));
			Scanner keyFile = new Scanner(new BufferedReader(new FileReader("res/savePoint/keyItems.txt")));
			//if all files has texts inside, it has been saved with previous progress before.
			return playerFile.hasNextLine() && optionFile.hasNextLine() && keyFile.hasNextLine();
		}
		catch(IOException e){
			System.out.println(e);
		}
		return false;
	}
	 	
   	//IMAGE METHODS
   	
   	//loads an array of Image objects based of one filename given.
   	//searches through a set path for all image files starting
   	//with the same filename
   	//returns an empty array if file not found.
   	public static Image[] loadItemImages(String name){
    	String path = "res/items_graphics/";
    	String[] imageNames = Utilities.findFiles(path, name);
    	Image[] tmp = new Image[imageNames.length];
    	for(int i = 0; i < tmp.length; i++){
    		tmp[i] = new ImageIcon(path+imageNames[i]).getImage();
    	}
    	return tmp;
    }
    
    //returns a BufferedImage object based on filename and path given
    //returns null if file not found.
	public static BufferedImage getImage(String path, String name){
    	BufferedImage tmp = null;
    	try{
    		tmp = ImageIO.read(new File(String.format(path+name+".png"))); //load the object
    	}
    	catch (IOException e) {
			System.out.println(e);
		}
		return tmp;
    }
    
    //returns a cropped image of a larger image based on the user and screen position.
    //returns null if such cropping causes out of bound errors.
    public static BufferedImage subimage(BufferedImage image, Player user, int centreX, int centreY){
    	//left top corner of the screen in image
    	int bx = user.getCX()-centreX;
    	int by = user.getCY()-centreY;
		
		//constrain bx & by to avoid out of bound when cropping
    	if(bx > image.getWidth(null)){
    		bx =  image.getWidth(null);
    	}
    	else if(bx < 0){
    		bx = 0;
    	}
    	//constrain bx & by to avoid out of bound when cropping
    	if(by > image.getHeight(null)){
    		by = image.getHeight(null);
    	}
    	else if (by < 0){
    		by = 0;
    	}
    	
    	//calculate the width and height of the subimage
    	int width = subImageWidth(image, bx, centreX*2);
    	int height = subImageHeight(image, by, centreY*2);
    	
    	if(width > 0 && height > 0){//avoid out of bound
    		return image.getSubimage(bx, by, width, height);
    	}
    	return null;
    }
    
	//finds the width of the subimage for display based of the position
	//info given
	private static int subImageWidth(BufferedImage image, int x, int screenX){
		if(x + screenX >= image.getWidth(null)){//to get the partial image for the right side
			if( (image.getWidth(null)-x) > 0){//avoid out of bound when cropping
				return image.getWidth(null) - x;
			}
			return 0;
		}
		return screenX; //get full subimage that fills the screen's x-axis
	}
	
	//finds the height of the subimage for display based of the position
	//info given
	private static int subImageHeight(BufferedImage image, int y, int screenY){
		if(y + screenY >= image.getHeight(null)){//to get the partial image for the bottom side
			if((image.getHeight(null) - y) > 0){//avoid out of bound when cropping
				return image.getHeight(null) - y;
			}
			return 0; 
		}
		return screenY; //get full subimage that fills the screen's y-axis
	}
	
	//determines if the point in position is not within the mask boundary
	//where user cannot move
	public static boolean clear(BufferedImage mask, int x, int y){
		int WALL = 0xFF000000; //colour.black
		if(x < 0 || x >= mask.getWidth(null) || y < 0 || y >= mask.getHeight(null)){//makes sure mouse position is not out of bound
			return false; //user is not in the map
		}
		int c = mask.getRGB(x, y);
		return c != WALL;
	}
	
	//GAME SETUP METHODS
    
    //returns an array of Decoration objects based on filename given
    //returns null if file not found.
    public static Decoration[] setupDecorations(String room){
    	try{
    		//find file
    		Scanner inFile = new Scanner(new BufferedReader(new FileReader(String.format("res/map/%s/decorations.txt", room))));
    		int n = Integer.parseInt(inFile.nextLine()); //states number of objects in this file; avoid mixup input method
    		Decoration[] tmp = new Decoration[n];
    		for(int i=0; i<n; i++){
    			String[]line = inFile.nextLine().strip().split(" "); //takes and converts the data to properly initialize the object instance
    			int [] vals = Utilities.parseInts(Arrays.copyOfRange(line, 1, 7));
    			tmp[i]= new Decoration(line[0], vals[0], vals[1], vals[2], vals[3], vals[4],vals[5], line[7]);
    		}
    		return tmp;
    	}
    	catch(IOException e){//handling exception
    		System.out.println(e);
    	}
    	return null;
    }
    
    //returns an array of OptionItem objects based on filename given
    //returns null if file not found.
    public static OptionItem[] setupOptionItems(String room){
    	try{
    		Scanner inFile = new Scanner(new BufferedReader(new FileReader(String.format("res/map/%s/optionItems.txt", room))));
    		int n = Integer.parseInt(inFile.nextLine());//states number of objects in this file; avoid mixup input method
    		OptionItem[] tmp = new OptionItem[n];
    		for(int i=0; i<n; i++){
    			 //takes and converts the data to properly initialize the object instance
    			String[]line = inFile.nextLine().strip().split(" ");
    			int [] vals = Utilities.parseInts(Arrays.copyOfRange(line, 1, 7));
    			if(line.length > 12){//handles situation when an arbitrary argument is presents
    				String[] activationName = Arrays.copyOfRange(line, 12, line.length);
    				tmp[i]= new OptionItem(line[0], vals[0], vals[1], vals[2], vals[3], vals[4], 
    				vals[5], line[7], line[8], line[9], Integer.parseInt(line[10]), line[11], activationName);
    			}
    			else{//handles situation when arbitrary argument is not present
    				tmp[i]= new OptionItem(line[0], vals[0], vals[1], vals[2], vals[3], vals[4], 
    				vals[5], line[7], line[8], line[9], Integer.parseInt(line[10]), line[11]);
    			}
    			
    		}
    		return tmp;
    	}
    	catch(IOException e){//handling exception
    		System.out.println(e);
    	}
    	return null;
    }
    
    //returns an array of KeyItem objects based on filename given
    //returns null if file not found.
    public static KeyItem[] setupKeyItems(String room){
    	try{
    		Scanner inFile = new Scanner(new BufferedReader(new FileReader(String.format("res/map/%s/keyItems.txt", room))));
    		int n = Integer.parseInt(inFile.nextLine());//states number of objects in this file; avoid mixup input method
    		KeyItem[] tmp = new KeyItem[n];
    		for(int i=0; i<n; i++){
    			 //takes and converts the data to properly initialize the object instance
    			String[]line = inFile.nextLine().strip().split(" ");
    			int [] vals = Utilities.parseInts(Arrays.copyOfRange(line, 1, 8));
    			tmp[i]= new KeyItem(line[0], vals[0], vals[1], vals[2], vals[3], vals[4], vals[5], vals[6], line[8], line[9]);
    		}
    		return tmp;
    	}
    	catch(IOException e){//handles exception
    		System.out.println(e);
    	}
    	return null;
    }

	//returns an array of Door objects based on filename given
    //returns null if file not found.
    public static Door[] setupDoors(String room){
    	try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(String.format("res/map/%s/doors.txt", room))));
			int n = Integer.parseInt(inFile.nextLine());//states number of objects in this file; avoid mixup input method
			Door[] tmp = new Door[n];
			for (int i = 0; i < n; i++){
				//takes and converts the data to properly initialize the object instance
				String[] doorLine = inFile.nextLine().strip().split(" ");
				int[] doorPos = Utilities.parseInts(Arrays.copyOfRange(doorLine, 0, doorLine.length-1));
				tmp[i] = new Door(doorPos[0], doorPos[1], doorPos[2], doorPos[3], doorPos[4], doorLine[5]);
			}
			return tmp;
		}
		catch (IOException e){//handles exception
			System.out.println(e);
		}
		return null;
    }
    
    //returns an array of SavePoint objects based on filename given
    //returns null if file not found.
    public static SavePoint[] setupSavePoints(String room){
    	try{
    		Scanner inFile = new Scanner(new BufferedReader(new FileReader(String.format("res/map/%s/savePoints.txt", room))));
			int n = Integer.parseInt(inFile.nextLine());//states number of objects in this file; avoid mixup input method
			SavePoint[] tmp = new SavePoint[n];
			for (int i = 0; i<n; i++){
				//takes and converts the data to properly initialize the object instance
				int[] pos = parseInts(inFile.nextLine().strip().split(" "));
				tmp[i] = new SavePoint(pos[0], pos[1]);
			}
			return tmp;
    	}
    	catch(IOException e){//handles exception
    		System.out.println(e);
    	}
    	return null;
    }
	
	//returns an array of BasicEnemy objects based on filename given
    //returns null if file not found.
    public static BasicEnemy[] setupEnemies(String room){
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(String.format("res/map/%s/monster.txt", room))));
			int n = Integer.parseInt(inFile.nextLine());//states number of objects in this file; avoid mixup input method
			BasicEnemy[] tmp = new BasicEnemy[n];
			for (int i = 0; i<n; i++){
				//takes and converts the data to properly initialize the object instance
				int[] pos = parseInts(inFile.nextLine().strip().split(" "));
    			tmp[i] = new BasicEnemy(pos[0], pos[1], pos[2], pos[3]);
			}
			return tmp;
		}
		catch (IOException e){//handles exception
			System.out.println(e);
		}
		return null;
    }
}