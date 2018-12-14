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
		ArrayList<Double> xVals = new ArrayList();
		ArrayList<Double> yVals = new ArrayList();
		if(myPoints.size() > 0) {
			for(int i = 0; i < myPoints.size(); i++) {
				xVals.add((double)myPoints.get(i).x);
				yVals.add((double)myPoints.get(i).y);
			}
			double[] xArray = new double[xVals.size()];
			double[] yArray = new double[yVals.size()];
			for(int i = 0; i < myPoints.size(); i++) {
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
