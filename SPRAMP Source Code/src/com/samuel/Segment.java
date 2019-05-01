package com.samuel;

import java.util.ArrayList;

import org.newdawn.slick.Color;

public class Segment{
	ArrayList<Waypoint> segPoints;
	public boolean forward, disp;
	private double vel, angVel, acc, angAcc, arcLength = 0;
	public Segment(ArrayList<Waypoint> points, boolean forward, boolean disp, double vel, double acc, double angVel, double angAcc) {
		segPoints = new ArrayList<>();
		this.vel = vel;
		this.angVel = angVel;
		this.acc = acc;
		this.angAcc = angAcc;
		this.forward = forward;
		this.disp = disp;
		for(int i = 0; i < points.size(); i++) {
			segPoints.add(points.get(i));
		}
	}
	public void draw() {
		for(Waypoint segPoints : segPoints) {   //DISPLAYING ALL tempWaypoints
			segPoints.display();		
		}
		if(forward) {
			UI.generateGraphics(segPoints, Color.green);
		} else {
			UI.generateGraphics(segPoints, Color.magenta);
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
	
	public double getArcLengthMeters() {
		return this.arcLength/100;
	}
	
	public void setArcLengthCM(double arcLArg) {
		this.arcLength = arcLArg; 
	}
}
