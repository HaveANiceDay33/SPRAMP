package com.samuel;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuadc;

import org.newdawn.slick.Color;


public class Waypoint {
	public float x;
	public float y;
	public float size;
	public Color color;
	public Waypoint(float x, float y, float size,Color color) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.color = color;
	}
	public void display() {
		hvlDrawQuadc(x, y, size, size, color);
	}

}
