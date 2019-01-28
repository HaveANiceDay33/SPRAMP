package com.samuel;

import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuadc;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
		super(60, 1440, 720, "Auto Mapping Client v3", new HvlDisplayModeDefault());
	}
	
	static String userHomeFolder = System.getProperty("user.home")+"/Documents/";
	//testing github
	public String functionGenerator(String equalTo, double[] coeffArray) {
		StringBuilder s = new StringBuilder();
		s.append(equalTo + " = ");
		for(int i = coeffArray.length-1; i >= 0; i--) {
			if(coeffArray[i] != 0) {
				s.append(String.format("%.16f %s^%d " + (i==0 ? "" : "+") + " ", coeffArray[i], "x", i));
			}
		}
		return s.toString().replace("+ -", "- ");
	}
	public static void generateGraphics(ArrayList<Waypoint> waypoints, Color lineColor) {
		ArrayList<Double> xVals = new ArrayList();
		ArrayList<Double> yVals = new ArrayList();
		if(waypoints.size() > 0) {
			for(int i = 0; i < waypoints.size(); i++) {
				xVals.add((double)waypoints.get(i).x);
				yVals.add((double)waypoints.get(i).y);
			}
			double[] xArray = new double[xVals.size()];
			double[] yArray = new double[yVals.size()];
			for(int i = 0; i < waypoints.size(); i++) {
				xArray[i] = xVals.get(i);
				yArray[i] = yVals.get(i);
			}
			
			float mouseX = HvlCursor.getCursorX() - (2*WALL_OFFSET);
			
			PolynomialRegression functionGen = new PolynomialRegression(xArray, yArray, 5, "x");
			if(xVals.get(xVals.size()-1) < xVals.get(0)) {
				for(double i = xVals.get(0); i > xVals.get(xVals.size()-1); i--) {
					double x = i;
					double y = functionGen.predict(x);
					hvlDrawQuad((float)x, (float)y, 2, 2, lineColor); 
				}
			}else {
				for(double i = xVals.get(0); i < xVals.get(xVals.size()-1); i++) {
					double x = i;
					double y = functionGen.predict(x);
					hvlDrawQuad((float)x, (float)y, 2, 2, lineColor); 
				}
			}
			float size = (float)generateRadiusAtAPoint(functionGen.coefficients(), functionGen.degree(), mouseX);
			System.out.println(size);
			hvlDrawQuadc(mouseX, ((float)functionGen.predict(mouseX))+(size), size*2, size*2, getTexture(6));
		}
	}
	
	
	
	/**
	 * <p>Returns the radius of a circle tangent to the curve at any given point.
	 * Necessary for voltage calculations</p>
	 * @param coeffs
	 * @param degree
	 * @param x
	 * @return
	 */
	public static double generateRadiusAtAPoint(double [] coeffs, int degree, float x) {
		double TwoDer; 
		double OneDer;
		double[] coefficients = coeffs;
		//calculating derivative coefficients
		double[] deriCoeff = new double[5];
		for(int i = 0; i < degree; i++) {
			deriCoeff[i] = coefficients[i+1] * (i+1); 								  
		}
		double[] secDeriCoeff = new double[4];
		for(int i = 0; i < degree-1; i++) {
			secDeriCoeff[i] = deriCoeff[i+1] * (i+1); 							 
		}
		//calculates first and second derivatives at given point.
		OneDer = (deriCoeff[4]*Math.pow(x, 4))+(deriCoeff[3]*Math.pow(x, 3))+
				(deriCoeff[2]*Math.pow(x, 2))+(deriCoeff[1]*Math.pow(x, 1))+(deriCoeff[0]*Math.pow(x, 0));
		TwoDer = (secDeriCoeff[3]*Math.pow(x, 3))+(secDeriCoeff[2]*Math.pow(x, 2))+
				(secDeriCoeff[1]*Math.pow(x, 1))+(secDeriCoeff[0]*Math.pow(x, 0));
		//returns radius with gross formula. 
		double radCm = 1/((TwoDer)/(Math.pow(1+(OneDer*OneDer), 1.5)));
		
		return radCm;
	}
	
	public double[] generateData(ArrayList<Waypoint> waypoints) {
		ArrayList<Double> xVals = new ArrayList();
		ArrayList<Double> yVals = new ArrayList();
		if(waypoints.size() > 0) {
			for(int i = 0; i < waypoints.size(); i++) {
				xVals.add((double) (Math.round((((waypoints.get(i).x)+WALL_OFFSET)/0.56)) - Math.round((((waypoints.get(0).x)+WALL_OFFSET)/0.56)))); //returns cm
				yVals.add(-((double) (Math.round((((waypoints.get(i).y)-135)/0.56)) - Math.round((((waypoints.get(0).y)-135)/0.56)))));
			}
			double[] xArray = new double[xVals.size()];
			double[] yArray = new double[yVals.size()];
			for(int i = 0; i < waypoints.size(); i++) {
				xArray[i] = xVals.get(i);
				yArray[i] = yVals.get(i);
			}
			PolynomialRegression functionGen = new PolynomialRegression(xArray, yArray, 5, "x");
			
			System.out.println("POSITION:");
			double[] coefficients = functionGen.coefficients();
			for(int i = 0; i < functionGen.degree()+1; i++) {
				double term = coefficients[i];
				System.out.println("x^"+i + ": "+term);
			}
			System.out.println("y(x) = " + functionGen.toString() + "\n");
			//calculating derivative coefficients
			System.out.println("VELOCITY:");
			double[] deriCoeff = new double[5];
			for(int i = 0; i < functionGen.degree(); i++) { //only goes to 4th degree to prevent index out of range... if there is an x^5 term it will get dropped anyway with power rule derivatives
				deriCoeff[i] = coefficients[i+1] * (i+1); //Power rule differentiation... for example: the first index(i = 0) of the derivative array gets populated by the 2nd index of the initial coefficient array, times 1 (the exponent on x)
				double term = deriCoeff[i];
				System.out.println("x^"+i + ": "+term);									 
			}
			System.out.println(functionGenerator("v(x)", deriCoeff) + "\n");
			System.out.println("ACCELERATION:");
			double[] secDeriCoeff = new double[4];
			for(int i = 0; i < functionGen.degree()-1; i++) { //only goes to 3rd degree to prevent index out of range... if there is an x^4 term it will get dropped anyway with power rule derivatives
				secDeriCoeff[i] = deriCoeff[i+1] * (i+1); 
				double term = secDeriCoeff[i];
				System.out.println("x^"+i + ": "+term);									 
			}
			System.out.println(functionGenerator("a(x)", secDeriCoeff));
			System.out.println("\n");
			
			return coefficients;
		}
		return null;
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
	
	public static int background;

	
	double origAngle;
	double prevAngle;
	double angleOff;
	int numPoints;
	boolean ran;
	static float textY;
	String fileName;
	float screenOffset;
	boolean clicked;
	String direct;
	
	static ArrayList<Waypoint> tempWaypoints;
	static ArrayList<Segment> segments;

	public final static float WALL_OFFSET = 92;
	
	@Override
	public void initialize() {
		getTextureLoader().loadResource("field2018");//0 //
		getTextureLoader().loadResource("osFont");//1					//TEXTURES
		getTextureLoader().loadResource("robotFrame2");//2
		getTextureLoader().loadResource("2016field");//3
		getTextureLoader().loadResource("2017field");//4
		getTextureLoader().loadResource("2019field");//5
		getTextureLoader().loadResource("circle");//6
		zoom = 1;
		gameFont =  new HvlFontPainter2D(getTexture(1), HvlFontPainter2D.Preset.FP_INOFFICIAL,.5f,8f,0); //font definition
		zoomer = new HvlCamera2D(540, 360, 0, zoom, HvlCamera2D.ALIGNMENT_CENTER); //Camera definition
		numPoints = 0;
		picSizeX = 928;
		picSizeY = 464;
		xPos = 540;
		yPos = 360;
		screenOffset = 720; //has to be half of screen width
		deleteCounter = 0;
		fineSpeed = (float) 0.125;
		clicked = false;
		ran = false;
		segments = new ArrayList<Segment>();
		tempWaypoints = new ArrayList<Waypoint>();
		textY = 40;
		origAngle = 0;
		xOffsetBet = 0;
		yOffsetBet = 0;
		//DEFAULT BUTTON CONFIG
		HvlComponentDefault.setDefault(HvlLabeledButton.class, new HvlLabeledButton.Builder().setWidth(100).setHeight(50).setFont(gameFont).setTextColor(Color.cyan).setTextScale(0.25f).setOnDrawable(new HvlComponentDrawable() {
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
				if(mouseX < 900 || mouseX > 1435 && mouseY > 75 || mouseY < 25) {
					if(Mouse.isButtonDown(0) && clicked == false) {
						if(tempWaypoints.size() >= 1) {
							Waypoint point = new Waypoint((mouseX / zoomer.getZoom() + (zoomer.getX() - 720)/zoomer.getZoom()), (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(),
								10, Color.yellow, "forward","drive",0, 0, 0, RobotGeometry.robotW, RobotGeometry.robotL);
							tempWaypoints.add(point);
							//Adds tempWaypoints to an arraylist when the user clicks
							numPoints++;
							clicked = true;
							ran = false;
						}
						if(tempWaypoints.size() == 0 && segments.size() == 0) {	
							Waypoint point = new Waypoint((float) (-WALL_OFFSET+(((RobotGeometry.robotL)*0.9)/2)), (mouseY / zoomer.getZoom())+(zoomer.getY() - 360)/zoomer.getZoom(),
									20, Color.orange,"forward", "start",0,0 ,0,RobotGeometry.robotW, RobotGeometry.robotL);
							tempWaypoints.add(point);
							numPoints++;
							clicked = true;
							ran = false;
						}
						if(tempWaypoints.size() == 0 && segments.size() != 0) {	
							Waypoint point = new Waypoint(segments.get(segments.size()-1).myPoints.get(segments.get(segments.size()-1).myPoints.size()-1).x, 
									segments.get(segments.size()-1).myPoints.get(segments.get(segments.size()-1).myPoints.size()-1).y,
									20, Color.blue,"forward", "start",0,0 ,0,RobotGeometry.robotW, RobotGeometry.robotL);
							tempWaypoints.add(point);
							numPoints++;
							clicked = true;
							ran = false;
						}
					}
					if(!Mouse.isButtonDown(0)) {
						clicked = false;
					}
				}
				mouseDerivative = Mouse.getDWheel();
				mouseX = HvlCursor.getCursorX();
				mouseY = HvlCursor.getCursorY();
				//ZOOMING AND MOVING CODE
				if(mouseDerivative > 0) {zoom+=0.05;}
				else if(mouseDerivative < 0) {zoom -= 0.05;}
				if(zoom <= 1) {zoom = 1;}
				
				if(Mouse.isButtonDown(1)) {
					if(mouseX1 < mouseX){zoomer.setX(zoomer.getX() - (mouseX-mouseX1));}
					if(mouseX1 > mouseX){zoomer.setX(zoomer.getX() +(mouseX1-mouseX));}
					if(mouseY1 < mouseY){zoomer.setY(zoomer.getY() - (mouseY-mouseY1));}
					if(mouseY1 > mouseY){zoomer.setY(zoomer.getY() +(mouseY1-mouseY));}				
				}
				
				if(zoomer.getX() >= 1080) {zoomer.setX(1080);}
				if(zoomer.getX()<= 0) {zoomer.setX(0);}
				if(zoomer.getY() >= 720) {zoomer.setY(720);}
				if(zoomer.getY() <= 0) {zoomer.setY(0);}
			
				for(int i = 0; i < tempWaypoints.size(); i++) {
					/////////////////////////////////////
					//**USE WHEN ACTUAL CALC-ING LOL***//
					/////////////////////////////////////
					textOutline((i+1)+". X: "+(Math.round((((tempWaypoints.get(i).x)+WALL_OFFSET)/0.56) - Math.round(((tempWaypoints.get(0).x)+WALL_OFFSET)/0.56)))+
							" cm. Y: "+(Math.round((((tempWaypoints.get(i).y)-135)/0.56) - Math.round(((tempWaypoints.get(0).y)-135)/0.56)))+" cm.",Color.cyan, Color.darkGray, 1030, (25*(i-1))+textY, 0.25f);
					// NOT THIS SHIT BELOW 
					if(i > 0) {
						//Simple distance formula : ((y2-y1)^2 + (x2-x1)^2)^0.5
						distanceBet = Math.round(Math.sqrt(((tempWaypoints.get(i).y-tempWaypoints.get(i-1).y)*(tempWaypoints.get(i).y-tempWaypoints.get(i-1).y))+ 
								((tempWaypoints.get(i).x-tempWaypoints.get(i-1).x)*(tempWaypoints.get(i).x-tempWaypoints.get(i-1).x)))/0.56);
						xOffsetBet = Math.round(tempWaypoints.get(i).x - tempWaypoints.get(i-1).x);
						yOffsetBet = Math.round(tempWaypoints.get(i).y - tempWaypoints.get(i-1).y);
						origAngle = Math.round(Math.toDegrees(Math.atan2(yOffsetBet, xOffsetBet))); //Calculating angle offset 
						angleOff = origAngle-tempWaypoints.get(i-1).origAngle;
						//TRANSFORMATIONS BASED ON CERTAIN SCENARIOS	
						if(tempWaypoints.get(i).type.contains("backwards")) {
							angleOff -= 180;
							distanceBet *= -1;
						}
						if(tempWaypoints.get(i-1).type.contains("backwards")) {
							angleOff+=180;
						}
						if(tempWaypoints.get(i-1).type.contains("backwards") && tempWaypoints.get(i).type.contains("backwards")) {
							//angleOff -= 360;
						}
						//CHOOSES THE SMALLER ANGLE
						if(angleOff > 180) {
							angleOff -= 360;
						}
						if(angleOff < -180) {
							angleOff += 360;
						}
						//CANT GO ABOVE 360	
						if(angleOff <= -360) {
							angleOff+=360;
						}
						if(angleOff >= 360) {
							angleOff-=360;
						}   
			
						tempWaypoints.get(i).setDistance(distanceBet);  //Assigning each waypoint its properties
						tempWaypoints.get(i).setOrig(origAngle);
						tempWaypoints.get(i).setAngle(angleOff);

						
						textOutline("Angle from last: "+ tempWaypoints.get(i).angleOffset, Color.cyan, Color.darkGray, 1265, (25*(i-1))+(textY+5), 0.20f);
					}
					
			}
				
				if((Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_LEFT) ||  
						Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_UP)) && tempWaypoints.size() > 0) {
					//FINE ADJUSTMENT FOR tempWaypoints
					if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && tempWaypoints.size()>1) {tempWaypoints.get(tempWaypoints.size()-1).x += fineSpeed;}
					if(Keyboard.isKeyDown(Keyboard.KEY_LEFT) && tempWaypoints.size()>1) {tempWaypoints.get(tempWaypoints.size()-1).x += -fineSpeed;}
					if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {tempWaypoints.get(tempWaypoints.size()-1).y += -fineSpeed;}
					if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {tempWaypoints.get(tempWaypoints.size()-1).y += fineSpeed;}
				}else {
					if(tempWaypoints.size()>0) {
						tempWaypoints.get(tempWaypoints.size()-1).x += 0;
						tempWaypoints.get(tempWaypoints.size()-1).y += 0;
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
				zoomer.setZoom(zoom);
				zoomer.doTransform(new HvlAction0() { //THIS THING ALLOWS THE ZOOM TO WORK
					@Override
					public void run() {
						hvlDrawQuadc(300, 360, picSizeX, picSizeY, getTexture(background));
						for(Waypoint allPoints : tempWaypoints) {   //DISPLAYING ALL tempWaypoints
								allPoints.display();		
						}
						for(Segment allSegments : segments) {
							allSegments.draw();
						}
						generateGraphics(tempWaypoints, Color.red);
					}
				});
				textOutline("Press Q to see controls", Color.cyan, Color.darkGray, 50, 50, 0.4f);
				if(mouseX < 1095 || mouseX > 1435 && mouseY > 75 || mouseY < 25) {
					if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
						HvlMenu.setCurrent(Coordinates.Coords);
						textY = 40;
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
						HvlMenu.setCurrent(Instructions.Controls);
					}

					if(Keyboard.isKeyDown(Keyboard.KEY_L)) {
						tempWaypoints.clear();
						//RobotGeometry.Geo.getChildOfType(HvlArrangerBox.class,1).getChildOfType(HvlTextBox.class,0).setText("");
						RobotGeometry.Geo.getChildOfType(HvlArrangerBox.class,0).getChildOfType(HvlTextBox.class,0).setText("");
						RobotGeometry.Geo.getChildOfType(HvlArrangerBox.class,0).getChildOfType(HvlTextBox.class,1).setText("");
						HvlMenu.setCurrent(RobotGeometry.Geo);
					}
				}
				super.draw(delta);
			}
		};
		
		//THESE ARE UI ELEMENTS. AN ARRANGERBOX CONTAINS ALL TEXT BOXES AND BUTTONS
		UI.add(new HvlArrangerBox.Builder().setStyle(ArrangementStyle.HORIZONTAL).setWidth(270).setHeight(100).setX(Display.getWidth() - 350).setY(Display.getHeight()-180).build());
		UI.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("New\nSegment").setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				Segment newSegment = new Segment(tempWaypoints);
				segments.add(newSegment);
				tempWaypoints.clear();
			}
		}).build());
		UI.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("Delete\nPoint").setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				if(tempWaypoints.size() > 0) {
					tempWaypoints.remove(tempWaypoints.size()-1);
				}
			}
		}).build());
		UI.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("Delete\nSegment").setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				if(segments.size() > 0) {
					segments.remove(segments.size()-1);
				}
			}
		}).build());
		UI.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("CLEAR\nALL").setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				segments.clear();
				tempWaypoints.clear();
				UI.getChildOfType(HvlArrangerBox.class,1).getChildOfType(HvlTextBox.class,0).setText("");
			}
		}).build());
		UI.add(new HvlArrangerBox.Builder().setStyle(ArrangementStyle.HORIZONTAL).setWidth(250).setHeight(100).setX(Display.getWidth() - 300).setY(Display.getHeight()-100).build());

		UI.getChildOfType(HvlArrangerBox.class, 1).add(new HvlLabeledButton.Builder().setText("Save").setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				int segNum = 1;
				for(Segment segment : segments) {
					System.out.print(segNum + ": ");
					GenerateVoltages.runVirtualPath(generateData(segment.myPoints));
					System.out.println("");
					segNum++;
				}
				System.out.println("----------------------------------------------------------------------------------------------");
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
		HvlMenu.setCurrent(RobotGeometry.Geo);
	}
	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		HvlMenu.updateMenus(delta);
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			exit();
		}

	}
	
}
