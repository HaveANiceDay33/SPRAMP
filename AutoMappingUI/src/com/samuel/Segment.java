package com.samuel;

import java.util.ArrayList;

import org.newdawn.slick.Color;

public class Segment{
	ArrayList<Waypoint> myPoints;
	public boolean forward;
	private double vel, angVel, acc, angAcc;
	public Segment(ArrayList<Waypoint> points, boolean forward, double vel, double acc, double angVel, double angAcc) {
		myPoints = new ArrayList<>();
		this.vel = vel;
		this.angVel = angVel;
		this.acc = acc;
		this.angAcc = angAcc;
		this.forward = forward;
		for(int i = 0; i < points.size(); i++) {
			myPoints.add(points.get(i));
		}
	}
	public void draw() {
		for(Waypoint segPoints : myPoints) {   //DISPLAYING ALL tempWaypoints
			segPoints.display();		
		}
		if(forward) {
			UI.generateGraphics(myPoints, Color.green);
		} else {
			UI.generateGraphics(myPoints, Color.magenta);
		}
		
	}
	
	public double getVel() {
		return this.vel;
	}
	
	public double getAcc() {
		return this.acc;
	}
	
	public double getAngVel() {
		return this.angVel;
	}
	
	public double getAngAcc() {
		return this.angAcc;
	}
}
