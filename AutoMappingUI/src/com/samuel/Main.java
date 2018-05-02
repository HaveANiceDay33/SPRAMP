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
		super(60, 1440, 720, "Line following v1", new HvlDisplayModeDefault());
	}
	
	public void textOutline(String text, Color textColor, Color outlineColor, float x, float y, float size) {
		gameFont.drawWord(text, x+2, y, outlineColor, size);
		gameFont.drawWord(text, x, y+2, outlineColor, size);
		gameFont.drawWord(text, x, y-2, outlineColor, size);
		gameFont.drawWord(text, x-2, y, outlineColor, size);
		gameFont.drawWord(text, x, y, textColor, size);
	}
	
	HvlMenu UI;
	HvlMenu Coords;
	HvlMenu Controls;
	HvlMenu Geo;
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
	float deleteCounter;
	float distanceBet;
	float xOffsetBet;
	float yOffsetBet;
	float xCoord;
	float yCoord;
	
	float robotW;
	float robotL;
	
	double origAngle;
	double prevAngle;
	double angleOff;
	int numPoints;
	boolean ran;
	
	String fileName;
	String path = System.getProperty("user.home") + "/Desktop";
	
	float screenOffset;
	
	boolean clicked;
	String instructions;
	String direct;
	
	ArrayList<Waypoint>waypoints;

	
	@Override
	public void initialize() {
		getTextureLoader().loadResource("filed2018");//0
		getTextureLoader().loadResource("osFont");//1
		getTextureLoader().loadResource("robotFrame2");//2
		zoom = 1;
		gameFont =  new HvlFontPainter2D(getTexture(1), HvlFontPainter2D.Preset.FP_INOFFICIAL,.5f,8f,0);
		zoomer = new HvlCamera2D(540, 360, 0, zoom, HvlCamera2D.ALIGNMENT_CENTER);
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

		origAngle = 0;
		instructions = "C : Erase all (Only in coords) \nD : Delete last (Only in coords) \nW : Coords\nScroll : Zoom in/out\nRight Click : Drag Map\nESC : exit\nLeft Click : Forward drive\nLeft Click+L-Shift : Backwards drive\n"
				+ "Left Click+KEYCODE : Forward Action\nLeft Click+L Shift+KEYCODE : Backwards Action\nArrow Keys: Adjust LAST point placed\nL : Adjust Robot Width and Length (Clears all points)\n\nU = up, I = intake, D = down, S = shoot";
		direct = "    Press Q to see all controls";
		xOffsetBet = 0;
		yOffsetBet = 0;
		
		HvlComponentDefault.setDefault(HvlLabeledButton.class, new HvlLabeledButton.Builder().setWidth(100).setHeight(50).setFont(gameFont).setTextColor(Color.white).setTextScale(0.25f).setOnDrawable(new HvlComponentDrawable() {
			
			@Override
			public void draw(float delta, float x, float y, float width, float height) {
				hvlDrawQuad(x,y,width,height,Color.lightGray);
				
			}
		}).setOffDrawable(new HvlComponentDrawable() {
			
			@Override
			public void draw(float delta, float x, float y, float width, float height) {
				hvlDrawQuad(x,y,width,height,Color.black);
				
			}
		}).setHoverDrawable(new HvlComponentDrawable() {
			
			@Override
			public void draw(float delta, float x, float y, float width, float height) {
				hvlDrawQuad(x,y,width,height,Color.darkGray);
				
			}
		}).build());
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		
		
		
		// TODO Auto-generated method stub
		Geo = new HvlMenu() {
			public void draw(float delta) {
				textOutline("Set robot width and length",Color.black, Color.white,500,30, 0.4f);
				textOutline("Width : ",Color.black, Color.white,470,300, 0.4f);
				textOutline("Length : ",Color.black, Color.white,470,370, 0.4f);
				super.draw(delta);
			}
		};
		Geo.add(new HvlArrangerBox.Builder().setStyle(ArrangementStyle.VERTICAL).setWidth(250).setHeight(400).setX((Display.getWidth()/2)-125).setY((Display.getHeight()/2)-200).build());
		
		Geo.getFirstArrangerBox().add(new HvlSpacer(30, 30));
		
		Geo.getFirstArrangerBox().add(new HvlTextBox.Builder().setWidth(200).setHeight(50).setFont(gameFont).setTextColor(Color.darkGray).setTextScale(0.25f).setOffsetY(20).setOffsetX(20).setText("").setFocusedDrawable(new HvlComponentDrawable() {	
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
		
		Geo.getFirstArrangerBox().add(new HvlSpacer(30, 30));
		
		Geo.getFirstArrangerBox().add(new HvlTextBox.Builder().setWidth(200).setHeight(50).setFont(gameFont).setTextColor(Color.darkGray).setTextScale(0.25f).setOffsetY(20).setOffsetX(20).setText("").setFocusedDrawable(new HvlComponentDrawable() {	
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
		Geo.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("Set W/H").setClickedCommand(new HvlAction1<HvlButton>() {
			
			@Override
			public void run(HvlButton a) {
				//fileName = UI.getFirstArrangerBox().getFirstOfType(HvlTextBox.class).getText();
				robotW = Float.parseFloat(Geo.getFirstArrangerBox().getFirstOfType(HvlTextBox.class).getText());
				robotL = Float.parseFloat(Geo.getFirstArrangerBox().getChildOfType(HvlTextBox.class, 1).getText());
				
				HvlMenu.setCurrent(UI);
			}
		}).build());
		
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		UI = new HvlMenu() {
			public void draw(float delta) {
				
				if(mouseX < 1095 || mouseX > 1435 && mouseY > 75 || mouseY < 25) {
					if(Mouse.isButtonDown(0) && clicked == false && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_U) || Keyboard.isKeyDown(Keyboard.KEY_I)|| Keyboard.isKeyDown(Keyboard.KEY_D))) {
						if(waypoints.size() >= 1) {
							Waypoint point = new Waypoint((mouseX / zoomer.getZoom() + (zoomer.getX() - 720)/zoomer.getZoom()), (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 10, Color.green, "forward","drive",0, 0, 0, robotW, robotL);
							waypoints.add(point);

							numPoints++;
							clicked = true;
							ran = false;
						}
						if(waypoints.size() ==0 && mouseX <= 540) {
							Waypoint point = new Waypoint((float) (157-240+((robotL*.4646*2.56)/2))-5, (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 20, Color.orange, "start",null,0,0 ,0,robotW, robotL);
							waypoints.add(point);

							numPoints++;
							clicked = true;
							ran = false;
						}	
						if(waypoints.size() == 0 && mouseX > 540) {
							Waypoint point = new Waypoint((float) (930-240-((robotL*.4646*2.56)/2))+5, (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 20, Color.orange, "start",null, 0,0 ,0,robotW, robotL);
							waypoints.add(point);

							numPoints++;
							clicked = true;
							ran = false;
						}	
						
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_U) || Keyboard.isKeyDown(Keyboard.KEY_I)|| Keyboard.isKeyDown(Keyboard.KEY_D)) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)&& Mouse.isButtonDown(0) && clicked == false) {
						if(waypoints.size() >= 1) {
							Waypoint point = new Waypoint((mouseX / zoomer.getZoom() + (zoomer.getX() - screenOffset)/zoomer.getZoom()), (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 10, Color.red, "backwards","drive",0,0, 0,robotW, robotL);
							waypoints.add(point);

							numPoints++;
							clicked = true;
						}
					}
					if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Mouse.isButtonDown(0) && clicked == false && (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_U) || Keyboard.isKeyDown(Keyboard.KEY_I)|| Keyboard.isKeyDown(Keyboard.KEY_D))) {
						String action = "drive";
						Color color = Color.blue;
						if(waypoints.size() >= 1) {
							if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
								action = "shoot";
								color = color.yellow;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_U)) {
								action = "up";
								color = color.pink;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_I)) {
								action = "intake";
								color = color.magenta;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
								action = "down";
								color = color.blue;
							}
							Waypoint point = new Waypoint((mouseX / zoomer.getZoom() + (zoomer.getX() - screenOffset)/zoomer.getZoom()), (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 15, color, "forward",action,0,0,0,robotW, robotL);
							waypoints.add(point);

							numPoints++;
							
							clicked = true;
						}
					}
					if( Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Mouse.isButtonDown(0) && clicked == false & (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_U) || Keyboard.isKeyDown(Keyboard.KEY_I)|| Keyboard.isKeyDown(Keyboard.KEY_D))) {
						String action = "drive";
						Color color = Color.blue;
						if(waypoints.size() >= 1) {
							if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
								action = "shoot";
								color = color.yellow;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_U)) {
								action = "up";
								color = color.pink;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_I)) {
								action = "intake";
								color = color.magenta;
							}else if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
								action = "down";
								color = color.blue;
							}
							Waypoint point = new Waypoint((mouseX / zoomer.getZoom() + (zoomer.getX() - screenOffset)/zoomer.getZoom()), (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 15, color, "backwards",action,0,0, 0,robotW, robotL);
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
					textOutline((i+1)+". X: "+Math.round((((waypoints.get(i).x)-157+240)/0.4646)/2.56)+" In. Y: "+Math.round((((waypoints.get(i).y)-135)/0.4646)/2.56)+" In.",Color.cyan, Color.darkGray, 1030, (25*(i-1))+40, 0.25f);

					if(i > 0) {
						distanceBet = Math.round(Math.sqrt(((waypoints.get(i).y-waypoints.get(i-1).y)*(waypoints.get(i).y-waypoints.get(i-1).y))+ ((waypoints.get(i).x-waypoints.get(i-1).x)*(waypoints.get(i).x-waypoints.get(i-1).x)))/0.4646/2.56);
						
	
						xOffsetBet = Math.round(waypoints.get(i).x - waypoints.get(i-1).x);
						yOffsetBet = Math.round(waypoints.get(i).y - waypoints.get(i-1).y);
						origAngle = Math.round(Math.toDegrees(Math.atan2(yOffsetBet, xOffsetBet)));
						angleOff = origAngle-waypoints.get(i-1).origAngle;
						
				
						if(waypoints.get(i).type.equals("backwards")) {
							angleOff -= 180;
							distanceBet *= -1;
						}
						if(waypoints.get(i-1).type.equals("backwards")) {
							angleOff+=180;
						}
						if(waypoints.get(i-1).type.equals("backwards") && waypoints.get(i).type.equals("backwards")) {
							angleOff -= 360;
						}
						if(angleOff <= -360) {
							angleOff+=360;
						}
						if(angleOff >= 360) {
							angleOff-=360;
						}
						
						waypoints.get(i).setDistance(distanceBet);
						waypoints.get(i).setOrig(origAngle);
						waypoints.get(i).setAngle(angleOff);
						
						textOutline("Angle from last: "+ waypoints.get(i).angleOffset, Color.cyan, Color.darkGray, 1265, (25*(i-1))+44, 0.20f);
					}
					
			}
				
				if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_LEFT) ||  
						Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
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
				mouseX1 = mouseX;
				mouseY1 = mouseY;
				//System.out.println(Mouse.getX()+"     "+Mouse.getY());
				zoomer.setZoom(zoom);
				zoomer.doTransform(new HvlAction0() {

					@Override
					public void run() {
						
						hvlDrawQuadc(300, 360, picSizeX, picSizeY, getTexture(0));
						for(Waypoint allPoints : waypoints) {
								allPoints.display();		
						}
		
						for(int i = 0; i < waypoints.size(); i++) {


							if(i>0) {
								HvlPainter2D.hvlDrawLine(waypoints.get(i-1).x, waypoints.get(i-1).y, waypoints.get(i).x, waypoints.get(i).y, Color.green);
							}
							if(waypoints.get(i).type.equals("backwards")) {
								HvlPainter2D.hvlDrawLine(waypoints.get(i-1).x, waypoints.get(i-1).y, waypoints.get(i).x, waypoints.get(i).y, Color.red);

							}
							
						}
					}
				});
				textOutline("Press Q to see controls", Color.black, Color.white, 50, 50, 0.4f);

				
				super.draw(delta);
			}
		};
		UI.add(new HvlArrangerBox.Builder().setStyle(ArrangementStyle.HORIZONTAL).setWidth(250).setHeight(100).setX(Display.getWidth() - 300).setY(Display.getHeight()-100).build());

		UI.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("Save").setClickedCommand(new HvlAction1<HvlButton>() {
			
			@Override
			public void run(HvlButton a) {
				fileName = UI.getFirstArrangerBox().getFirstOfType(HvlTextBox.class).getText();
				
				String userHomeFolder = System.getProperty("user.home")+"/Documents";
				File profile = new File(userHomeFolder, fileName+".txt");
				BufferedWriter output = null;
				try {
					output = new BufferedWriter(new FileWriter(profile));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
				    try {
				    	output.write((waypoints.get(0).x+83)+" "+(waypoints.get(0).y-197)+" start null");
				    	output.newLine();
						for(int i = 1; i < waypoints.size(); i++) {
							output.write(waypoints.get(i).distance+" "+waypoints.get(i).angleOffset+" "+waypoints.get(i).type+" "+waypoints.get(i).action);
							output.newLine();
						}
						//MORE WRITING HERE
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} finally {
				   try {
					output.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
		}).build());
		UI.getFirstArrangerBox().add(new HvlSpacer(30, 30));
		UI.getFirstArrangerBox().add(new HvlTextBox.Builder().setWidth(200).setHeight(50).setFont(gameFont).setTextColor(Color.darkGray).setTextScale(0.25f).setOffsetY(20).setOffsetX(20).setText("").setFocusedDrawable(new HvlComponentDrawable() {	
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

		Coords = new HvlMenu() {
	
			public void draw(float delta) {
				if(waypoints.size() > 0) {
					if(Keyboard.isKeyDown(Keyboard.KEY_D) && deleteCounter <= 0){
						waypoints.remove(waypoints.size()-1);
						
						deleteCounter = 50;
					}
					deleteCounter --;
					if(deleteCounter == 0) {
						deleteCounter = 0;
					}
				}
					for(int i = 0; i < waypoints.size(); i++) {
	//						xCoord = Math.round((((waypoints.get(i).x)-157+240)/0.4646)/2.56);
	//						yCoord = Math.round((((waypoints.get(i).y)-135)/0.4646)/2.56);
	//						waypoints.get(i).setX(xCoord);
	//						waypoints.get(i).setY(yCoord);
						if(Keyboard.isKeyDown(Keyboard.KEY_C)) {
							waypoints.clear();
						}

						if(waypoints.size()!=0) {
							textOutline("Coord "+(i+1)+": "+Math.round((((waypoints.get(i).x)-157+240)/0.4646)/2.56)+" In.      "+Math.round((((waypoints.get(i).y)-135)/0.4646)/2.56)+" In.", Color.cyan, Color.darkGray, 40, (40 *i) + 20, 0.3f);

						}
							
							if(i > 0) {
								distanceBet = Math.round(Math.sqrt(((waypoints.get(i).y-waypoints.get(i-1).y)*(waypoints.get(i).y-waypoints.get(i-1).y))+ ((waypoints.get(i).x-waypoints.get(i-1).x)*(waypoints.get(i).x-waypoints.get(i-1).x)))/0.4646/2.56);
								
			
								xOffsetBet = Math.round(waypoints.get(i).x - waypoints.get(i-1).x);
								yOffsetBet = Math.round(waypoints.get(i).y - waypoints.get(i-1).y);
								origAngle = Math.round(Math.toDegrees(Math.atan2(yOffsetBet, xOffsetBet)));
								angleOff = origAngle-waypoints.get(i-1).origAngle;
								
						
								if(waypoints.get(i).type.equals("backwards")) {
									angleOff -= 180;
									distanceBet *= -1;
								}
								if(waypoints.get(i-1).type.equals("backwards")) {
									angleOff+=180;
								}
								if(waypoints.get(i-1).type.equals("backwards") && waypoints.get(i).type.equals("backwards")) {
									angleOff -= 360;
								}
								if(angleOff <= -360) {
									angleOff+=360;
								}
								if(angleOff >= 360) {
									angleOff-=360;
								}
								
								waypoints.get(i).setDistance(distanceBet);
								waypoints.get(i).setOrig(origAngle);
								waypoints.get(i).setAngle(angleOff);
								
								textOutline("Angle from last: "+ waypoints.get(i).angleOffset + " degrees. Distance between "+ (i) +" and " +(i+1) + ": "+ waypoints.get(i).distance + " In.", Color.cyan, Color.darkGray, 400, (40*(i-1))+40, 0.25f);
							}	
					}
					
				
				textOutline("Press E to go back", Color.black, Color.white, 740, 600, 0.4f);
				if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
					HvlMenu.setCurrent(UI);
				}
				super.draw(delta);
			}
		};
		Controls = new HvlMenu() {
			public void draw(float delta) {
				textOutline(instructions, Color.black, Color.magenta, 20, 20, 0.35f);
				textOutline("Press E to go back", Color.black, Color.white, 740, 600, 0.4f);
				if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
					HvlMenu.setCurrent(UI);
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_C)) {
					waypoints.clear();
				}
				if(waypoints.size() > 0) {
					if(Keyboard.isKeyDown(Keyboard.KEY_D) && deleteCounter <= 0){
						waypoints.remove(waypoints.size()-1);
						deleteCounter = 50;
					}
					deleteCounter --;
					if(deleteCounter == 0) {
						deleteCounter = 0;
					}
				}
				super.draw(delta);
			}
		};
		HvlMenu.setCurrent(Geo);
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
				HvlMenu.setCurrent(Coords);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				HvlMenu.setCurrent(Controls);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_L)) {
				waypoints.clear();
				HvlMenu.setCurrent(Geo);
			}
		}
	}
	
}
