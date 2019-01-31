package com.samuel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.lwjgl.opengl.ARBCLEvent;
import org.lwjgl.opengl.ARBClearBufferObject;

import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlTextBox;

public class VirtualPathGenerator {
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
	
	static double pathRadius = 100000; //6.1516;//0.4064; // m
	static double velMax = 2.0; 	// m/s
	static double accMax = 2.0; // m/s^2
	static double angVelMax = 2.0; // rad/s
	static double angAccMax = 2.0; // rad/s^2
	static double dt = 0.02; // s
	
	static double k1 = 2/wheelBaseWidth;
	static double k2 = wheelBaseWidth*mass/(2*moi);
	static double k3 = wheelBaseWidth*mass/2;
	static double Ts = 0.0; // Tune me!
	
	static BufferedWriter fileWriter;
	
	static int index = 0;
	
	static double time, pos, vel, acc, ang, angVel, angAcc, voltRight, voltLeft;
	static String fileName;
	
	public static double voltsForMotion(double velocity, double force) {
		return force*wheelRadiusMeters*R/(g*kt)/nMotors  //Torque (I*R) term
			   + velocity/wheelRadiusMeters*g/kv         //Speed  (V*kv) term
			   + vIntercept;							 //Friction term
	}

	/**
	 * <p>Returns voltages for left or right drive side with some gross physics. 
	 * One of the values printed to the *.BOND output file.</p>
	 * @param rPath
	 * @param vel
	 * @param acc
	 * @param left
	 * @return
	 */
	public static double solveChassisDynamics(double rPath, double vel, double acc, boolean left) {
		if(left) {
			return voltsForMotion(
				vel*(k1*rPath-1)/(k1*rPath),
				(mass*acc*(k2*rPath - 1))/(2*k2*rPath));
		}else{
			return voltsForMotion(
				vel*(k1*rPath+1)/(k1*rPath),
				(mass*acc*(k2*rPath + 1))/(2*k2*rPath));
		}
	}
	
	public static double solveScrubbyChassisDynamics(double rPath, double vel, double acc, double angVel, boolean left ){
		if(left) {
			//System.out.println("Vel Left: " + vel*(k1*rPath-1)/(k1*rPath) + " Force Left: " + ((k3-moi/rPath)*mass*acc-angVel*Ts*mass)/(2*k3));
			return voltsForMotion(
					vel*(k1*rPath-1)/(k1*rPath),
					((k3-moi/rPath)*mass*acc-angVel*Ts*mass)/(2*k3));
		}else {
			//System.out.println("Vel Right: " + vel*(k1*rPath+1)/(k1*rPath) + " Force Right: " + ((k3+moi/rPath)*mass*acc+angVel*Ts*mass)/(2*k3));
			return voltsForMotion(
					vel*(k1*rPath+1)/(k1*rPath),
					((k3+moi/rPath)*mass*acc+angVel*Ts*mass)/(2*k3));
		}
	}
	
	public static void runVirtualPath(double [] coeffs, double arcL) {
		
		time = pos = vel = acc = ang = angVel = angAcc = voltRight = voltLeft = 0.0;
		
		double accelTime = velMax/accMax;
		double decelTime = velMax/accMax;
		
		double accelDistance = 0.5*accMax*Math.pow((accelTime), 2);
		double decelDistance = 0.5*accMax*Math.pow((decelTime), 2);
		
		System.out.println("Pre simulation update. Current Time: " + time);
		System.out.println("Total drive distance: " + arcL);
		
		arcL -= (accelDistance + decelDistance);
		
		double targetTime = (arcL/velMax) + accelTime + decelTime;
		
		System.out.println("Total Estimated Time: " + targetTime);
		System.out.println("Max Velocity: "+ velMax);
		System.out.println("Max Acceleration: " + accMax);
		System.out.println("Beginning profile generation...");
		System.out.println("Accelerating...");
		
		while(time < accelTime) { 
			if(angVelMax < velMax / pathRadius) { //Velocity 
				velMax = angVelMax * pathRadius; 
				System.out.println("Maximum Velocity adjusted to: " + velMax);
			}
			if(angAccMax < accMax / pathRadius) { //Acceleration
				accMax = angAccMax * pathRadius; 
				System.out.println("Maximum Acceleration adjusted to: " + accMax);
			}// solve entire acceleration portion, may not use all of these points
			acc = accMax; 
			vel = vel + acc*dt;
			
			angVel = angVel + angAcc*dt;
			ang = ang + angVel*dt;
			angAcc = angAccMax*Math.signum(pathRadius);
			pos = pos + vel*Math.cos(Math.toRadians(ang))*dt;
			pathRadius = UI.generateRadiusAtAPoint(coeffs, 5, (float) pos*100);	
			voltLeft = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, true);
			voltRight = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, false);
			
			try {
				fileWriter.write(String.format("%.4f", voltRight) + " " + String.format("%.4f", voltLeft) + " " +
						String.format("%.4f", vel) + " " + String.format("%.4f", angVel)+ " " + String.format("%.4f", pathRadius));
				fileWriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			index++;
			time += dt;
		}
		
		System.out.println("Max velocity reached.");
		
		while(time < targetTime - decelTime) {
			if(angVelMax < velMax / pathRadius) { //Velocity 
				velMax = angVelMax * pathRadius; 
				System.out.println("Maximum Velocity adjusted to: " + velMax);
			}
			if(angAccMax < accMax / pathRadius) { //Acceleration
				accMax = angAccMax * pathRadius; 
				System.out.println("Maximum Acceleration adjusted to: " + accMax);
			}
			pos = pos + vel*Math.cos(Math.toRadians(ang))*dt;
			pathRadius = UI.generateRadiusAtAPoint(coeffs, 5, (float) pos*100);
			
			voltLeft = solveScrubbyChassisDynamics(pathRadius, vel, 0, angVel, true);
			voltRight = solveScrubbyChassisDynamics(pathRadius, vel, 0, angVel, false);
		 
			try {
				fileWriter.write(String.format("%.4f", voltRight) + " " + String.format("%.4f", voltLeft) + " " +
						String.format("%.4f", vel) + " " + String.format("%.4f", angVel)+ " " + String.format("%.4f", pathRadius));
				fileWriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			index++;
			time += dt;
		}
		System.out.println("Decelerating...");
		while(time < targetTime) {
			if(angVelMax < velMax / pathRadius) { //Velocity 
				velMax = angVelMax * pathRadius; 
				System.out.println("Maximum Velocity adjusted to: " + velMax);
			}
			if(angAccMax < accMax / pathRadius) { //Acceleration
				accMax = angAccMax * pathRadius; 
				System.out.println("Maximum Acceleration adjusted to: " + accMax);
			}// solve entire acceleration portion, may not use all of these points
			acc = accMax; 
			vel = vel - acc*dt;
			
			ang = ang + angVel*dt;
			angVel = angVel - angAcc*dt;
			pos = pos + vel*Math.cos(Math.toRadians(ang))*dt;
			pathRadius = UI.generateRadiusAtAPoint(coeffs, 5, (float) pos*100);
			
			voltLeft = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, true);
			voltRight = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, false);
		
			
			try {
				fileWriter.write(String.format("%.4f", voltRight) + " " + String.format("%.4f", voltLeft) + " " +
						String.format("%.4f", vel) + " " + String.format("%.4f", angVel)+ " " + String.format("%.4f", pathRadius));
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
