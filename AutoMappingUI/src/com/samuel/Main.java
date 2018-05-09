package com.samuel;

import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuadc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.action.HvlAction0;
import com.osreboot.ridhvl.action.HvlAction1;
import com.osreboot.ridhvl.display.collection.HvlDisplayModeDefault;
import com.osreboot.ridhvl.menu.HvlComponentDefault;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlButton;
import com.osreboot.ridhvl.menu.component.HvlComponentDrawable;
import com.osreboot.ridhvl.menu.component.HvlSpacer;
import com.osreboot.ridhvl.menu.component.HvlTextBox;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox.ArrangementStyle;
import com.osreboot.ridhvl.menu.component.collection.HvlLabeledButton;
import com.osreboot.ridhvl.painter.HvlCamera2D;
import com.osreboot.ridhvl.painter.HvlCursor;
import com.osreboot.ridhvl.painter.painter2d.HvlFontPainter2D;
import com.osreboot.ridhvl.painter.painter2d.HvlPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;

public class Main extends HvlTemplateInteg2D{

	public static void main(String [] args){
		new Main();
	}
	public Main(){
		super(60, 1440, 720, "Auto Mapping Client v2", new HvlDisplayModeDefault());
	}
	//Method for drawing text with an outline. Much more visually appealing
	public static void textOutline(String text, Color textColor, Color outlineColor, float x, float y, float size) {
		gameFont.drawWord(text, x+1, y, outlineColor, size);
		gameFont.drawWord(text, x, y+1, outlineColor, size);
		gameFont.drawWord(text, x, y-1, outlineColor, size);
		gameFont.drawWord(text, x-1, y, outlineColor, size);
		gameFont.drawWord(text, x, y, textColor, size);
	}
	//Method for saving profiles into text files
	public void profileSaver() {
		fileName = UI.getChildOfType(HvlArrangerBox.class, 1).getFirstOfType(HvlTextBox.class).getText(); //gets text box input; saves as variable
		 
		String userHomeFolder = System.getProperty("user.home")+"/Documents"; 
		File profile = new File(userHomeFolder, fileName+".txt");  
		File loader = new File(userHomeFolder, fileName+"Loader.BOND");//creates new txt file with inputed name
		BufferedWriter output = null;
		BufferedWriter loaderOut = null;
		try {
			
			output = new BufferedWriter(new FileWriter(profile));  
			loaderOut = new BufferedWriter(new FileWriter(loader));
			//output.write((waypoints.get(0).x+83)+" "+(waypoints.get(0).y-197)+waypoints.get(0).size + " " + waypoints.get(0).color + " "+waypoints.get(0).type + " "   );//writes starting coords
			//output.newLine();
			for(int i = 0; i < waypoints.size(); i++) {
				loaderOut.write((waypoints.get(i).x+83)+" "+(waypoints.get(i).y-197)+" "+waypoints.get(i).size + " " + "Color"+ " "+waypoints.get(i).type + " " +waypoints.get(i).action + " "+ waypoints.get(i).distance+" "+waypoints.get(i).angleOffset+" "+waypoints.get(i).origAngle+" "+waypoints.get(i).sizeX+ " "+waypoints.get(i).sizeY);//writes distance, angle, type, and action to text file
				loaderOut.newLine();
			}
			output.write((waypoints.get(0).x+83)+" "+(waypoints.get(0).y-197)+ " start null" );//writes starting coords
			output.newLine();
			for(int i = 1; i < waypoints.size(); i++) {
				output.write(waypoints.get(i).distance+" "+waypoints.get(i).angleOffset+ " " +waypoints.get(i).type + " " +waypoints.get(i).action);//writes distance, angle, type, and action to text file
				output.newLine();
			}
			output.write("END END END END");
					//MORE WRITING HERE
		
			output.close();
			loaderOut.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	static HvlMenu UI;
	
	HvlCamera2D zoomer;
	float picSizeX;
	float picSizeY;
	float mouseX;
	float mouseY;
	float fineSpeed;
	static HvlFontPainter2D gameFont;
	
	int mouseDerivative;
	float yPos;
	float xPos;
	float zoom;
	float mouseX1;
	float mouseY1;
	static float deleteCounter;
	float distanceBet;
	float xOffsetBet;
	float yOffsetBet;
	float xCoord;
	float yCoord;

	
	double origAngle;
	double prevAngle;
	double angleOff;
	int numPoints;
	boolean ran;
	static float textY;

	
	String fileName;
	String path = System.getProperty("user.home") + "/Desktop";
	
	float screenOffset;
	
	boolean clicked;
	
	String direct;
	
	static ArrayList<Waypoint>waypoints;

	
	@Override
	public void initialize() {
		getTextureLoader().loadResource("field2018");//0 //
		getTextureLoader().loadResource("osFont");//1					//TEXTURES
		getTextureLoader().loadResource("robotFrame2");//2
		zoom = 1;
		gameFont =  new HvlFontPainter2D(getTexture(1), HvlFontPainter2D.Preset.FP_INOFFICIAL,.5f,8f,0); //font definition
		zoomer = new HvlCamera2D(540, 360, 0, zoom, HvlCamera2D.ALIGNMENT_CENTER); //Camera definition
		numPoints = 0;
		picSizeX = 1080;
		picSizeY = 720;
		xPos = 540;
		yPos = 360;
		screenOffset = 720;
		deleteCounter = 0;
		fineSpeed = (float) 0.125;
		clicked = false;
		ran = false;
		waypoints = new ArrayList<Waypoint>();
		
		textY = 40;

		origAngle = 0;
	
		xOffsetBet = 0;
		yOffsetBet = 0;
		//DEFAULT BUTTON CONFIG
		HvlComponentDefault.setDefault(HvlLabeledButton.class, new HvlLabeledButton.Builder().setWidth(100).setHeight(50).setFont(gameFont).setTextColor(Color.white).setTextScale(0.25f).setOnDrawable(new HvlComponentDrawable() {
			
			@Override
			public void draw(float delta, float x, float y, float width, float height) {
				hvlDrawQuad(x,y,width,height,Color.lightGray);
				
			}
		}).setOffDrawable(new HvlComponentDrawable() {
			
			@Override
			public void draw(float delta, float x, float y, float width, float height) {
				hvlDrawQuad(x,y,width,height,Color.darkGray);
				
			}
		}).setHoverDrawable(new HvlComponentDrawable() {
			
			@Override
			public void draw(float delta, float x, float y, float width, float height) {
				hvlDrawQuad(x,y,width,height,Color.gray);
				
			}
		}).build());
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		//Menu initialization
		RobotGeometry.initMenu();
		Instructions.initInstructions();
		Coordinates.initCoords();
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//Main screen user will see
		UI = new HvlMenu() {
			public void draw(float delta) {
				//start and forwards
				if(mouseX < 1095 || mouseX > 1435 && mouseY > 75 || mouseY < 25) {
					if(Mouse.isButtonDown(0) && clicked == false && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_U) || Keyboard.isKeyDown(Keyboard.KEY_I)|| Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_N))) {
						if(waypoints.size() >= 1) {
							Waypoint point = new Waypoint((mouseX / zoomer.getZoom() + (zoomer.getX() - 720)/zoomer.getZoom()), (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 10, Color.green, "forward","drive",0, 0, 0, RobotGeometry.robotW, RobotGeometry.robotL);
							waypoints.add(point);
							//Adds waypoints to an arraylist when the user clicks
							numPoints++;
							clicked = true;
							ran = false;
						}
						if(waypoints.size() ==0 && mouseX <= 540 && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
							
							Waypoint point = new Waypoint((float) (157-240+((RobotGeometry.robotL*.4646*2.56)/2))-5, (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 20, Color.orange,"forward", "start",0,0 ,0,RobotGeometry.robotW, RobotGeometry.robotL);
							waypoints.add(point);

							numPoints++;
							clicked = true;
							ran = false;
						}	
						if(waypoints.size() == 0 && mouseX > 540 && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
	
							Waypoint point = new Waypoint((float) (930-240-((RobotGeometry.robotL*.4646*2.56)/2))+5, (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 20, Color.orange, "forward","start", 0,0 ,0,RobotGeometry.robotW, RobotGeometry.robotL);
							waypoints.add(point);

							numPoints++;
							clicked = true;
							ran = false;
						}	
						
					}
					//backwards start
					if(Mouse.isButtonDown(0) && clicked == false && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_U) || Keyboard.isKeyDown(Keyboard.KEY_I)|| Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_N))) {
						if(waypoints.size() ==0 && mouseX <= 540) {
							
							Waypoint point = new Waypoint((float) (157-240+((RobotGeometry.robotL*.4646*2.56)/2))-5, (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 20, Color.red, "backwards","start",0,0 ,0,RobotGeometry.robotW, RobotGeometry.robotL);
							waypoints.add(point);

							numPoints++;
							clicked = true;
							ran = false;
						}	
						if(waypoints.size() == 0 && mouseX > 540) {
	
							Waypoint point = new Waypoint((float) (930-240-((RobotGeometry.robotL*.4646*2.56)/2))+5, (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 20, Color.red, "backwards","start", 0,0 ,0,RobotGeometry.robotW,RobotGeometry. robotL);
							waypoints.add(point);

							numPoints++;
							clicked = true;
							ran = false;
						}	
					}
					//backwards forward
					if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_U) || Keyboard.isKeyDown(Keyboard.KEY_I)|| Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_N)) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)&& Mouse.isButtonDown(0) && clicked == false) {
						if(waypoints.size() >= 1) {
							Waypoint point = new Waypoint((mouseX / zoomer.getZoom() + (zoomer.getX() - screenOffset)/zoomer.getZoom()), (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 10, Color.red, "backwards","drive",0,0, 0,RobotGeometry.robotW, RobotGeometry.robotL);
							waypoints.add(point);

							numPoints++;
							clicked = true;
						}
					}
					if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Mouse.isButtonDown(0) && clicked == false && (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_U) || Keyboard.isKeyDown(Keyboard.KEY_I)|| Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_N))) {
						String action = "drive";
						Color color = Color.blue;
						String type = "forward";

						if(waypoints.size() >= 1) {
							if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
								action = "shoot";
								color = color.yellow;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_U)) {     //DEPENDING ON KEY PRESSED, THE TYPE WILL CHANGE, ALONG WITH COLOR	
								action = "up";
								color = color.pink;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_I)) {
								action = "intake";
								color = color.magenta;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
								action = "down";
								color = color.blue;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_N)) {
								color = color.transparent;
								type = "forwardnoAngle";
							}
							Waypoint point = new Waypoint((mouseX / zoomer.getZoom() + (zoomer.getX() - screenOffset)/zoomer.getZoom()), (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 15, color, type,action,0,0,0,RobotGeometry.robotW,RobotGeometry.robotL);
							waypoints.add(point);

							numPoints++;
							
							clicked = true;
						}
					}
					if( Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Mouse.isButtonDown(0) && clicked == false & (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_U) || Keyboard.isKeyDown(Keyboard.KEY_I)|| Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_N))) {
						String action = "drive";
						Color color = Color.blue;
						String type = "backwards";
						if(waypoints.size() >= 1) {
							if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
								action = "shoot";
								color = color.yellow;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_U)) {
								action = "up";									//SAME FOR BACKWARDS
								color = color.pink;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_I)) {
								action = "intake";
								color = color.magenta;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
								action = "down";
								color = color.blue;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_N)) {
								color = color.transparent;
								type = "backwardsnoAngle";
							}
							Waypoint point = new Waypoint((mouseX / zoomer.getZoom() + (zoomer.getX() - screenOffset)/zoomer.getZoom()), (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 15, color, type,action,0,0, 0,RobotGeometry.robotW, RobotGeometry.robotL);
							waypoints.add(point);

							numPoints++;
							
							clicked = true;
						}
					}
					if(!Mouse.isButtonDown(0)) {
						clicked = false;
					}
				}

	 
				//System.out.println("zoomer X: " + (zoomer.getX() - (540/zoomer.getZoom())) + "\t     mouseX: "+ mouseX);
				
				mouseDerivative = Mouse.getDWheel();
				mouseX = HvlCursor.getCursorX();
				mouseY = HvlCursor.getCursorY();
				//gameFont.drawWordc("MD: "+mouseDerivative, 100, 850, Color.white);
				
				//ZOOMING AND MOVING CODE
				if(mouseDerivative > 0) {
					zoom+=0.05;
				}else if(mouseDerivative < 0) {
					zoom -= 0.05;
				}
				
				if(zoom <= 1) {
					zoom = 1;
				}

				if(Mouse.isButtonDown(1)) {
			
					if(mouseX1 < mouseX){
						zoomer.setX(zoomer.getX() - (mouseX-mouseX1));
					}
					if(mouseX1 > mouseX){
						zoomer.setX(zoomer.getX() +(mouseX1-mouseX));
					}
					if(mouseY1 < mouseY){
						zoomer.setY(zoomer.getY() - (mouseY-mouseY1));
					}
					if(mouseY1 > mouseY){
						zoomer.setY(zoomer.getY() +(mouseY1-mouseY));
					}				
				}
				
				if(zoomer.getX() >= 1080) {
					zoomer.setX(1080);
				}
				if(zoomer.getX()<= 0) {
					zoomer.setX(0);
				}
				if(zoomer.getY() >= 720) {
					zoomer.setY(720);
				}
				if(zoomer.getY() <= 0) {
					zoomer.setY(0);
				}
			
				for(int i = 0; i < waypoints.size(); i++) {
					textOutline((i+1)+". X: "+Math.round((((waypoints.get(i).x)-157+240)/0.4646)/2.56)+" In. Y: "+Math.round((((waypoints.get(i).y)-135)/0.4646)/2.56)+" In.",Color.cyan, Color.darkGray, 1030, (25*(i-1))+textY, 0.25f);

					if(i > 0) {
						//Simple distance formula : ((y2-y1)^2 + (x2-x1)^2)^0.5
						distanceBet = Math.round(Math.sqrt(((waypoints.get(i).y-waypoints.get(i-1).y)*(waypoints.get(i).y-waypoints.get(i-1).y))+ ((waypoints.get(i).x-waypoints.get(i-1).x)*(waypoints.get(i).x-waypoints.get(i-1).x)))/0.4646/2.56);
						
	
						xOffsetBet = Math.round(waypoints.get(i).x - waypoints.get(i-1).x);
						yOffsetBet = Math.round(waypoints.get(i).y - waypoints.get(i-1).y);
						origAngle = Math.round(Math.toDegrees(Math.atan2(yOffsetBet, xOffsetBet))); //Calculating angle offset 
						angleOff = origAngle-waypoints.get(i-1).origAngle;
						
						//TRANSFORMATIONS BASED ON CERTAIN SCENARIOS	
						if(waypoints.get(i).type.contains("backwards")) {
							angleOff -= 180;
							distanceBet *= -1;
						}
						if(waypoints.get(i-1).type.contains("backwards")) {
							angleOff+=180;
						}
						if(waypoints.get(i-1).type.contains("backwards") && waypoints.get(i).type.contains("backwards")) {
							//angleOff -= 360;
						}
						
						//CANT GO ABOVE 360	
						if(angleOff <= -360) {
							angleOff+=360;
						}
						if(angleOff >= 360) {
							angleOff-=360;
						}
			
						waypoints.get(i).setDistance(distanceBet);  //Assigning each waypoint its properties
						waypoints.get(i).setOrig(origAngle);
						waypoints.get(i).setAngle(angleOff);

						
						textOutline("Angle from last: "+ waypoints.get(i).angleOffset, Color.cyan, Color.darkGray, 1265, (25*(i-1))+(textY+5), 0.20f);
					}
					
			}
				
				if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_LEFT) ||  
						Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {  //FINE ADJUSTMENT FOR WAYPOINTS
					if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && waypoints.size()>1) {
						waypoints.get(waypoints.size()-1).x += fineSpeed;
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_LEFT) && waypoints.size()>1) {
						waypoints.get(waypoints.size()-1).x += -fineSpeed;
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
						waypoints.get(waypoints.size()-1).y += -fineSpeed;
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
						waypoints.get(waypoints.size()-1).y += fineSpeed;
					}
			
				}else {
					if(waypoints.size()>0) {
						waypoints.get(waypoints.size()-1).x += 0;
						waypoints.get(waypoints.size()-1).y += 0;
					}
			
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_A)) {    //SCROLLING
					textY -= 1;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_Z)) {
					textY+=1;
				}
				
				mouseX1 = mouseX;
				mouseY1 = mouseY;
				//System.out.println(Mouse.getX()+"     "+Mouse.getY());
				zoomer.setZoom(zoom);
				zoomer.doTransform(new HvlAction0() { //THIS THING ALLOWS THE ZOOM TO WORK	

					@Override
					public void run() {
						
						hvlDrawQuadc(300, 360, picSizeX, picSizeY, getTexture(0));
						for(Waypoint allPoints : waypoints) {   //DISPLAYING ALL WAYPOINTS
								allPoints.display();		
						}
		
						for(int i = 0; i < waypoints.size(); i++) {
							if(i>0) {
								HvlPainter2D.hvlDrawLine(waypoints.get(i-1).x, waypoints.get(i-1).y, waypoints.get(i).x, waypoints.get(i).y, Color.green);
							}
							if(waypoints.get(i).type.contains("backwards")&& i>0) {
								HvlPainter2D.hvlDrawLine(waypoints.get(i-1).x, waypoints.get(i-1).y, waypoints.get(i).x, waypoints.get(i).y, Color.red);

							}
							
						}
					}
				});
				textOutline("Press Q to see controls", Color.black, Color.white, 50, 50, 0.4f);

				
				super.draw(delta);
			}
		};
		
		//THESE ARE UI ELEMENTS. AN ARRANGERBOX CONTAINS ALL TEXT BOXES AND BUTTONS
		UI.add(new HvlArrangerBox.Builder().setStyle(ArrangementStyle.HORIZONTAL).setWidth(250).setHeight(100).setX(Display.getWidth() - 235).setY(Display.getHeight()-180).build());
		UI.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("Delete").setClickedCommand(new HvlAction1<HvlButton>() {
			
			@Override
			public void run(HvlButton a) {
				if(waypoints.size() > 0) {
					waypoints.remove(waypoints.size()-1);
				}
			}
		}).build());
		UI.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("Clear").setClickedCommand(new HvlAction1<HvlButton>() {
			
			@Override
			public void run(HvlButton a) {
				waypoints.clear();
			}
		}).build());
		UI.add(new HvlArrangerBox.Builder().setStyle(ArrangementStyle.HORIZONTAL).setWidth(250).setHeight(100).setX(Display.getWidth() - 300).setY(Display.getHeight()-100).build());

		UI.getChildOfType(HvlArrangerBox.class, 1).add(new HvlLabeledButton.Builder().setText("Save").setClickedCommand(new HvlAction1<HvlButton>() {
			
			@Override
			public void run(HvlButton a) {
				profileSaver();
			}
		}).build());
		UI.getChildOfType(HvlArrangerBox.class, 1).add(new HvlSpacer(30, 30));
		UI.getChildOfType(HvlArrangerBox.class, 1).add(new HvlTextBox.Builder().setWidth(200).setHeight(50).setFont(gameFont).setTextColor(Color.darkGray).setTextScale(0.25f).setOffsetY(20).setOffsetX(20).setText("").setFocusedDrawable(new HvlComponentDrawable() {	
			@Override
			public void draw(float delta, float x, float y, float width, float height) {
				hvlDrawQuad(x,y,width,height, Color.lightGray);	
			}
		}).setUnfocusedDrawable(new HvlComponentDrawable() {
			
			@Override
			public void draw(float delta, float x, float y, float width, float height) {
				hvlDrawQuad(x,y,width,height, Color.green);	
			}
		}).build());
		//DISPLAYS THE INFORMATION ABOUT EACH COORD

		
		HvlMenu.setCurrent(RobotGeometry.Geo);
	}
	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		HvlMenu.updateMenus(delta);
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			exit();
		}
		if(mouseX < 1095 || mouseX > 1435 && mouseY > 75 || mouseY < 25) {
			if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
				HvlMenu.setCurrent(Coordinates.Coords);
				textY = 40;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				HvlMenu.setCurrent(Instructions.Controls);
			}

			if(Keyboard.isKeyDown(Keyboard.KEY_L)) {
				waypoints.clear();
				RobotGeometry.Geo.getChildOfType(HvlArrangerBox.class,1).getChildOfType(HvlTextBox.class,0).setText("");
				RobotGeometry.Geo.getChildOfType(HvlArrangerBox.class,0).getChildOfType(HvlTextBox.class,0).setText("");
				RobotGeometry.Geo.getChildOfType(HvlArrangerBox.class,0).getChildOfType(HvlTextBox.class,1).setText("");
				HvlMenu.setCurrent(RobotGeometry.Geo);
			}
		}
	}
	
}
