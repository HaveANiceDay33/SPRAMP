package com.samuel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlTextBox;

public class GenerateVoltages {
	static double mass = 40.0; // kg
	static double moi = 20.0; // kg * m^2   //this is a number 254 code had, I figure it's close-ish. Definitely need tuning. Trying to account for scrub with this, not so great
	static double wheelRadiusMeters = 0.0762; // m 
	static double wheelBaseWidth = 0.8128; // m   //this is the effective wheel base width empirically 4/3 that of the physical wheel base width (24in --> 32in)
	static double vIntercept = 0.67; //0.67 // V
	static double R = 0.09160305; // ohms
	static double kv = 46.51333;   // rad/s per V 
	static double kt = 0.0183969466;   // N*m per A
	static double g = 10.71; // gear reduction (g:1)
	static int nMotors = 2; //number of motors in a gearbox
	
	static double pathRadius = 2; //6.1516;//0.4064; // m
	static double radialDistance;// m   //Target distance to travel
	static double velMax = 2.0; 	// m/s
	static double accMax = 2.0; // m/s^2
	static double angVelMax = 2.0; // rad/s
	static double angAccMax = 2.0; // rad/s^2
	static double dt = 0.02; // s
	
	static BufferedWriter fileWriter;
	
	static int index = 0;
	
	public static double voltsForMotion(double velocity, double force) {
		return force*wheelRadiusMeters*R/(g*kt)/nMotors  //Torque (I*R) term
			   + velocity/wheelRadiusMeters*g/kv         //Speed  (V*kv) term
			   + vIntercept;							 //Friction term
	}
	
	static double k1 = 2/wheelBaseWidth;
	static double k2 = wheelBaseWidth*mass/(2*moi);
	
	public static double solveChassisDynamics(double rPath, double vel, double acc, boolean left) { // this should return a two quantity object (drivebaseState) or something like that, but again, for now... no 
		if(left) {
			//System.out.println("Target left wheel velocity: " + vel*(k1*rPath-1)/(k1*rPath) + " Target left wheel force: " + (mass*acc*(k2*rPath - 1))/(2*k2*rPath));
			return voltsForMotion(
				vel*(k1*rPath-1)/(k1*rPath),
				(mass*acc*(k2*rPath - 1))/(2*k2*rPath));
		}else{
			//System.out.println("Target right wheel velocity: " + vel*(k1*rPath+1)/(k1*rPath) + " Target right wheel force: " + (mass*acc*(k2*rPath + 1))/(2*k2*rPath));
			return voltsForMotion(
				vel*(k1*rPath+1)/(k1*rPath),
				(mass*acc*(k2*rPath + 1))/(2*k2*rPath));
		}
	}
	static double k3 = wheelBaseWidth*mass/2;
	static double Ts = 60.0; // Tune me!
	public static double solveScrubbyChassisDynamics( double rPath, double vel, double acc, double angVel, boolean left ){
		if(left) {
			//System.out.println("Vel Left: " + vel*(k1*rPath-1)/(k1*rPath) + " Force Left: " + ((k3-moi/rPath)*mass*acc-angVel*Ts*mass)/(2*k3));
			return voltsForMotion(
					vel*(k1*rPath-1)/(k1*rPath),
					((k3-moi/rPath)*mass*acc-angVel*Ts*mass)/(2*k3));
					//(mass*acc*(k2*rPath - 1))/(2*k2*rPath));
		}else {
			//System.out.println("Vel Right: " + vel*(k1*rPath+1)/(k1*rPath) + " Force Right: " + ((k3+moi/rPath)*mass*acc+angVel*Ts*mass)/(2*k3));
			return voltsForMotion(
					vel*(k1*rPath+1)/(k1*rPath),
					((k3+moi/rPath)*mass*acc+angVel*Ts*mass)/(2*k3));
					//(mass*acc*(k2*rPath + 1))/(2*k2*rPath));
		}
	}
	static double time, pos, vel, acc, ang, angVel, angAcc, voltRight, voltLeft;
	static String fileName;
	public static void runVirtualPath(double [] coeffs) {
		time = pos = vel = acc = ang = angVel = angAcc = voltRight = voltLeft = 0.0;
		
		fileName = Main.UI.getChildOfType(HvlArrangerBox.class, 1).getFirstOfType(HvlTextBox.class).getText();
		File outputFile = new File(Main.userHomeFolder, fileName + ".BOND");
		
		try {
			fileWriter = new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			System.out.println("Could not write to output file");
		}
		
		System.out.println("Pre simulation update. Time: " + time);
		// Unify constraints from user specified path following constants
		if(angVelMax < velMax / pathRadius) { //Velocity 
			velMax = angVelMax * pathRadius; 
			System.out.println("Maximum Velocity adjusted to: " + velMax);
		}
		System.out.println("Max Velocity: " + velMax);
		if(angAccMax < accMax / pathRadius) { //Acceleration
			accMax = angAccMax * pathRadius; 
			System.out.println("Maximum Acceleration adjusted to: " + accMax);
		}
		System.out.println("Max Acceleration: " + accMax);
		
		System.out.println("Beginning profile generation...");
		System.out.println("Accelerating...");
		while(vel < velMax) { // solve entire acceleration portion, may not use all of these points
			acc = accMax; 
			vel = vel + acc*dt;
			pos = pos + vel*dt;
			
			angAcc = angAccMax*Math.signum(pathRadius); //accounts for direction of curvature
			angVel = angVel + angAcc*dt;
			ang = ang + angVel*dt;
			//pathRadius = Main.generateRadiusAtAPoint(coeffs, 5, x);
			voltLeft = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, true);
			voltRight = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, false);
			try {
				fileWriter.write(String.format("%.4f", voltRight) + " " + String.format("%.4f", voltLeft) + " " + String.format("%.4f", vel) + " " + String.format("%.4f", angVel));
				fileWriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			index++;
			time += dt;
		}
		System.out.println("Max velocity reached.");
		
		while(time < 2.5) {
			//pathRadius = Main.generateRadiusAtAPoint(coeffs, 5, x);
			voltLeft = solveScrubbyChassisDynamics(pathRadius, vel, 0, angVel, true);
			voltRight = solveScrubbyChassisDynamics(pathRadius, vel, 0, angVel, false);
			try {
				fileWriter.write(String.format("%.4f", voltRight) + " " + String.format("%.4f", voltLeft) + " " + String.format("%.4f", vel) + " " + String.format("%.4f", angVel));
				fileWriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			index++;
			time += dt;
		}
		System.out.println("Decelerating...");
		while(vel > 0) { // solve entire acceleration portion, may not use all of these points
			acc = accMax; 
			vel = vel - acc*dt;
			pos = pos - vel*dt;
			
			angAcc = angAccMax*Math.signum(pathRadius); //accounts for direction of curvature
			angVel = angVel - angAcc*dt;
			ang = ang + angVel*dt;
			//pathRadius = Main.generateRadiusAtAPoint(coeffs, 5, x);
			
			voltLeft = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, true);
			voltRight = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, false);
			
			try {
				fileWriter.write(String.format("%.4f", voltRight) + " " + String.format("%.4f", voltLeft) + " " + String.format("%.4f", vel) + " " + String.format("%.4f", angVel));
				fileWriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			index++;
			time += dt;
		}
		System.out.println("Complete!");
		System.out.println("Profile generated with name: " + fileName + ".BOND");
		try {
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
