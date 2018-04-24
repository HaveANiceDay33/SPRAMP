package com.samuel;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuadc;

import org.newdawn.slick.Color;


public class Action {
	public float x;
	public float y;
	public float size;
	public Color color;
	public String type;

	public Action(float x, float y, float size, Color color, String type) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.color = color;
		this.type = type;

	}
	public void display() {
		hvlDrawQuadc(x, y, size, size, color);
	}
}
	
