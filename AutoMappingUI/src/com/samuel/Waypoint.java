package com.samuel;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuadc;

import org.newdawn.slick.Color;


public class Waypoint {
	public float x;
	public float y;
	public float size;
	public Color color;
	public String type;
	public String action;
	public float distance;
	public double angleOffset;
	public Waypoint(float x, float y, float size, Color color, String type, String action, float distance, double angleOffset) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.color = color;
		this.type = type;
		this.action = action;
		this.angleOffset = angleOffset;
		this.distance = distance;
		//yeet
	}
	public void display() {
		hvlDrawQuadc(x, y, size, size, color);
	}
	public void setDistance(float newDis) {
		this.distance = newDis;
	}
	public void setAngle(double newAng) {
		this.angleOffset = newAng;
	}
	public void setX(float newX) {
		this.x = newX;
	}
	public void setY(float newY) {
		this.y = newY;
	}

}
