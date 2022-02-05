//MainMenu.java
//Christine Wong
//MainMenu creates the JFrame where user can access
//the main features of the game. By clicking the appropriate button,
//user can start a new run from scratch or cotinue their previous progress.
//User can also access tutorial from this JFrame.

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MainMenu extends JFrame implements ActionListener{
	Timer myTimer; //determines time interval for update
	ButtonPanel menu; //panel that hosts the buttons
	
	//create a new instance of the main menu
    public MainMenu(){
    	//setup basic JFrame info
    	setSize(800,600);
    	myTimer = new Timer(10, this);	
		setResizable(false);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	//add the button panel
		menu = new ButtonPanel(this);
		add(menu);
		pack();
    	setVisible(true);
    }
    
    //start the timer
	public void start(){
		myTimer.start();
	}
	
	//update information on menu panel when triggered
	public void actionPerformed(ActionEvent evt){
		if(menu != null){
			menu.update();
			menu.repaint();
		}
	}
    
    //start the program
    public static void main(String[] args){
    	MainMenu menu = new MainMenu();
    }
}

//hosts the buttons and deals with the interaction of the user's
//mouse and the buttons inside of the panel. Coordinate their state of
//change and when the button's features are triggered.
class ButtonPanel extends JPanel implements MouseListener{
	private Point mouse; //mouse location
	private Image back, buttonUp, buttonDown;
	private Rectangle newGame, continueGame, tutorial; //area of the buttons
	private MainMenu mainFrame; //the frame that hosts this panel
	private final Tutorial tutorialFrame; //avoid multiple setups that can slow the game
	
	//font used on the buttons
	public static final Font displayFont = Utilities.getFont("res/font/Regular.ttf", 32f);
	
	//creates a new instance of the panel
	public ButtonPanel(MainMenu m){
		mainFrame = m;
		//loading the pictures for background and buttons
		back = new ImageIcon("res/graphics/menu.png").getImage();
		buttonUp = new ImageIcon("res/graphics/buttonUp.png").getImage();
		buttonDown = new ImageIcon("res/graphics/buttonDown.png").getImage();
		
		//setup buttons info
		int width = buttonUp.getWidth(null);
		int height = buttonUp.getHeight(null);
		tutorialFrame = new Tutorial(mainFrame);
    	newGame = new Rectangle(260,250,width,height);
    	continueGame = new Rectangle(260,330,width,height);
    	tutorial = new Rectangle(260,410,width,height);
    	
    	mouse = new Point(0,0); //to solve the null exception when startup
    	//setup panel info
		setPreferredSize( new Dimension(800, 600));
        addMouseListener(this);
	}
	
	//for setting up the frame and panel
	public void addNotify() {
        super.addNotify();
        setFocusable(true);
        requestFocus();
        mainFrame.start();
    }
    
    //update mouse location
	public void update(){
		if(mainFrame.isVisible()){//avoid illegalcomponent exception
			mouse = MouseInfo.getPointerInfo().getLocation();
			Point offset = getLocationOnScreen();
			mouse.translate(-offset.x, -offset.y); //adjust mouse location based on screen location
		}
	}
    
    //override original methods
	public void	mouseClicked(MouseEvent e){}
	public void	mouseEntered(MouseEvent e){}
	public void	mouseExited(MouseEvent e){}
	public void	mouseReleased(MouseEvent e){}
	
	//determine user input
	public void	mousePressed(MouseEvent e){
		if(newGame.contains(mouse)){//create new instances for new run of the game
			Game game = new Game();
			mainFrame.setVisible(false);
		}
		else if(continueGame.contains(mouse)){
			if (Utilities.gamePlayed()){//avoid null exception
				Game game = new Game();
				game.loadSave(); //load previous progress
				mainFrame.setVisible(false);
			}
		}
		else if(tutorial.contains(mouse)){//change visibility so tutorial window appears to replace main menu
			tutorialFrame.setVisible(true);
			mainFrame.setVisible(false);
		}		
	}
	
	//draw buttons on the panel based on given position, label, and area
	public void drawButton(Graphics g, Image image, Rectangle area, String word, int x, int y){
		g.drawImage(image, area.x, area.y, area.width, area.height, null);
		//draws text
		g.setColor(Color.white);
		g.setFont(displayFont);
		g.drawString(word, x, y);
    }
    
    //draws the components on the panel
    public void paint(Graphics g){
    	g.drawImage(back, 0, 0, null); //background
    	if(newGame.contains(mouse)){//changes button state so user is aware which button they are pressing
    		drawButton(g, buttonUp, newGame, "New Game", 335, 295);
    	}
    	else{
    		drawButton(g, buttonDown, newGame, "New Game", 335, 295);
    	}
    	
    	if(continueGame.contains(mouse)){//changes button state so user is aware which button they are pressing
    		drawButton(g, buttonUp, continueGame, "Continue Game", 315, 375);
    	}
    	else{
    		drawButton(g, buttonDown, continueGame, "Continue Game", 315, 375);
    	}
    	
    	if(tutorial.contains(mouse)){//changes button state so user is aware which button they are pressing
    		drawButton(g, buttonUp, tutorial, "Tutorial", 345, 455);
    	}
    	else{
    		drawButton(g, buttonDown, tutorial, "Tutorial", 345, 455);
    	}
    }
    
}