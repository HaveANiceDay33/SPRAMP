package com.samuel;

import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;

import java.util.ArrayList;

import org.newdawn.slick.Color;

public class Segment{
	ArrayList<Waypoint> myPoints;
	public Segment(ArrayList<Waypoint> points) {
		myPoints = new ArrayList<>();
		for(int i = 0; i < points.size(); i++) {
			myPoints.add(points.get(i));
		}
	}
	public void draw() {
		
		for(Waypoint segPoints : myPoints) {   //DISPLAYING ALL tempWaypoints
			segPoints.display();		
		}
		
		Main.generateGraphics(myPoints, Color.green);
	}
}
