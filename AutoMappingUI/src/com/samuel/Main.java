package com.samuel;

import com.osreboot.ridhvl.action.HvlAction0;
import com.osreboot.ridhvl.display.HvlDisplayMode;
import com.osreboot.ridhvl.display.collection.HvlDisplayModeDefault;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.painter.HvlCamera2D;
import com.osreboot.ridhvl.painter.HvlCursor;
import com.osreboot.ridhvl.painter.painter2d.HvlFontPainter2D;
import com.osreboot.ridhvl.painter.painter2d.HvlPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuadc;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;

public class Main extends HvlTemplateInteg2D{

	public static void main(String [] args){
		new Main();
	}
	public Main(){
		super(60, 1080, 720, "Line following v1", new HvlDisplayModeDefault());
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
	HvlCamera2D zoomer;
	float picSizeX;
	float picSizeY;
	float mouseX;
	float mouseY;
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
	double prevAngle;
	double angleOff;
	int numPoints;
	
	boolean clicked;
	String instructions;
	String direct;
	
	ArrayList<Waypoint>waypoints;
	
	@Override
	public void initialize() {
		getTextureLoader().loadResource("filed2018");//0
		getTextureLoader().loadResource("osFont");//1
		zoom = 1;
		gameFont =  new HvlFontPainter2D(getTexture(1), HvlFontPainter2D.Preset.FP_INOFFICIAL,.5f,8f,0);
		zoomer = new HvlCamera2D(540, 360, 0, zoom, HvlCamera2D.ALIGNMENT_CENTER);
		numPoints = 0;
		picSizeX = 1080;
		picSizeY = 720;
		xPos = 540;
		yPos = 360;
		deleteCounter = 0;
		clicked = false;
		waypoints = new ArrayList<Waypoint>();
		
		instructions = "C: erase all \nD: delete last \nW: Coords   \nScroll: Zoom in/out \nRight Click: drag  \nLeft Click: set waypoint\nESC: exit\nL-Shift: backwards waypoint";
		direct = "    Press Q to see all controls";
		xOffsetBet = 0;
		yOffsetBet = 0;
		// TODO Auto-generated method stub
		UI = new HvlMenu() {
			public void draw(float delta) {
				
				if(Mouse.isButtonDown(0) && clicked == false && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_A)) {
					if(waypoints.size() >= 1) {
						Waypoint point = new Waypoint((mouseX / zoomer.getZoom() + (zoomer.getX() - 540)/zoomer.getZoom()), (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 10, Color.green, "forward", 0, 0);
						waypoints.add(point);
						numPoints++;
						clicked = true;
					}
					if(waypoints.size() ==0 && mouseX <= 540) {
						Waypoint point = new Waypoint(157, (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 20, Color.orange, "start",0 ,0);
						waypoints.add(point);
						numPoints++;
						clicked = true;
					}	
					if(waypoints.size() == 0 && mouseX > 540) {
						Waypoint point = new Waypoint(930, (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 20, Color.orange, "start", 0, 0);
						waypoints.add(point);
						numPoints++;
						clicked = true;
					}	
					
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Mouse.isButtonDown(0) && clicked == false) {
					if(waypoints.size() >= 1) {
						Waypoint point = new Waypoint((mouseX / zoomer.getZoom() + (zoomer.getX() - 540)/zoomer.getZoom()), (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(), 10, Color.red, "backwards",0, 0);
						waypoints.add(point);
						numPoints++;
						clicked = true;
					}
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_A) && Mouse.isButtonDown(0) && clicked == false) {
					if(waypoints.size() >= 1) {
					
						
						clicked = true;
					}
				}
				if(!Mouse.isButtonDown(0)) {
					clicked = false;
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
				
				
				mouseX1 = mouseX;
				mouseY1 = mouseY;
				
				zoomer.setZoom(zoom);
				zoomer.doTransform(new HvlAction0() {

					@Override
					public void run() {
						hvlDrawQuadc(540, 360, picSizeX, picSizeY, getTexture(0));
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
		Coords = new HvlMenu() {
			public void draw(float delta) {
				for(int i = 0; i < waypoints.size(); i++) {	
						textOutline("Coord "+(i+1)+": "+Math.round((((waypoints.get(i).x)-157)/0.4646)/2.56)+" In.      "+Math.round((((waypoints.get(i).y)-135)/0.4646)/2.56)+" In.", Color.cyan, Color.darkGray, 40, (40 *i) + 20, 0.3f);
						
						if(i > 0) {
							distanceBet = Math.round(Math.sqrt(((waypoints.get(i).y-waypoints.get(i-1).y)*(waypoints.get(i).y-waypoints.get(i-1).y))+ ((waypoints.get(i).x-waypoints.get(i-1).x)*(waypoints.get(i).x-waypoints.get(i-1).x)))/0.4646/2.56);
							
		
							xOffsetBet = Math.round(waypoints.get(i).x - waypoints.get(i-1).x);
							yOffsetBet = Math.round(waypoints.get(i).y - waypoints.get(i-1).y);
							
							angleOff = Math.round((Math.toDegrees(Math.atan2(yOffsetBet, xOffsetBet))-waypoints.get(i-1).angleOffset));
							
					
							if(waypoints.get(i).type.equals("backwards")) {
								angleOff -= 180;
								distanceBet *= -1;
							}
							if(waypoints.get(i-1).type.equals("backwards")) {
								angleOff+=90;
							}
							
							waypoints.get(i).setDistance(distanceBet);
							waypoints.get(i).setAngle(angleOff);
							
							textOutline("Angle: "+ waypoints.get(i).angleOffset + " degrees. Distance between "+ (i) +" and " +(i+1) + ": "+ waypoints.get(i).distance + " In.", Color.cyan, Color.darkGray, 400, (40*(i-1))+40, 0.25f);
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
				textOutline(instructions, Color.black, Color.white, 20, 20, 0.4f);
				textOutline("Press E to go back", Color.black, Color.white, 740, 600, 0.4f);
				if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
					HvlMenu.setCurrent(UI);
				}
				super.draw(delta);
			}
		};
		HvlMenu.setCurrent(UI);
	}
	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		HvlMenu.updateMenus(delta);
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			exit();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_C)) {
			waypoints.clear();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			HvlMenu.setCurrent(Coords);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			HvlMenu.setCurrent(Controls);
		}
	}
	
}
