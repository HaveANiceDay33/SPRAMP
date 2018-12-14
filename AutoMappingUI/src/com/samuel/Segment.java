package com.samuel;

import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;

import java.util.ArrayList;

import org.newdawn.slick.Color;

public class Segment{
	ArrayList<Waypoint> points;
	public Segment(ArrayList<Waypoint> points) {
		this.points = points;
	}
	public void draw() {
		System.out.println(points.get(0).x);
		ArrayList<Double> xVals = new ArrayList();
		ArrayList<Double> yVals = new ArrayList();
		if(this.points.size() > 0) {
			for(int i = 0; i < this.points.size(); i++) {
				xVals.add((double)this.points.get(i).x);
				yVals.add((double)this.points.get(i).y);
			}
			double[] xArray = new double[xVals.size()];
			double[] yArray = new double[yVals.size()];
			for(int i = 0; i < this.points.size(); i++) {
				xArray[i] = xVals.get(i);
				yArray[i] = yVals.get(i);
			}
			PolynomialRegression functionGen = new PolynomialRegression(xArray, yArray, 5, "x");
			for(double i = xVals.get(0); i < xVals.get(xVals.size()-1); i++) {
				double x = i;
				double y = functionGen.predict(x);
				hvlDrawQuad((float)x, (float)y, 2, 2, Color.red); 
			}
			Main.textOutline(functionGen.toString(), Color.black, Color.blue, -150, 620, 0.24f);
		}
	}
}
