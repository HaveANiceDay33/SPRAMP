package com.samuel;

import java.util.ArrayList;

import org.newdawn.slick.Color;

public class Segment{
	ArrayList<Waypoint> myPoints;
	public boolean forward;
	public Segment(ArrayList<Waypoint> points, boolean forward) {
		myPoints = new ArrayList<>();
		this.forward = forward;
		for(int i = 0; i < points.size(); i++) {
			myPoints.add(points.get(i));
		}
	}
	public void draw() {
		
		for(Waypoint segPoints : myPoints) {   //DISPLAYING ALL tempWaypoints
			segPoints.display();		
		}
		
		UI.generateGraphics(myPoints, Color.green);
	}
}
