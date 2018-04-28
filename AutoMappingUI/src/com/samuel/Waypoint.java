package com.samuel;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuadc;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlResetRotation;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlRotate;

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
	public double origAngle;
	public float sizeX;
	public float sizeY;
	public Waypoint(float x, float y, float size, Color color, String type, String action, float distance, double angleOffset, double origAngle, float sizeX, float sizeY) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.color = color;
		this.type = type;
		this.action = action;
		this.angleOffset = angleOffset;
		this.distance = distance;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		//yeet
	}
	public void display() {
		hvlDrawQuadc(x, y, size, size, color);
		
		hvlRotate(x,y, (float)origAngle);
		hvlDrawQuadc(x, y, sizeY, sizeX, Main.getTexture(2));
		hvlResetRotation();
	}
	public void setDistance(float newDis) {
		this.distance = newDis;
	}
	public void setAngle(double newAng) {
		this.angleOffset = newAng;
	}
	public void setOrig(double newOr) {
		this.origAngle = newOr;
	}
	public void setX(float newX) {
		this.x = newX;
	}
	public void setY(float newY) {
		this.y = newY;
	}

}
