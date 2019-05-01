package com.samuel;

import java.io.BufferedWriter;
import java.io.IOException;

//scp beter1.BOND lvuser@roborio-5811-frc.local:
/**
 * <h1>Virtual Path Generator</h1>
 * 
 * <p>This class generates a comprehensive motion profile, based on a physics model, from the points drawn by the user.
 * It accounts for any combination of directions, displacements, and kinematic variables such as velocity and acceleration.</p>
 * <p>The end result of this class is a generated *.BOND file. These files contain all of the necessary information to run the
 * robot on certain voltages and all of the necessary data to perform feedback while running the motion profile in real life. </p>
 * 
 * @author Samuel Munro
 * @author Peter Salisbury
 *
 */
public class VirtualPathGenerator {
	static double mass = 40.0; // kg
	static double moi = 20.0; // kg * m^2   //this is a number 254 code had, I figure it's close-ish. Definitely need tuning. Trying to account for scrub with this, not so great
	static double wheelRadiusMeters = 0.0762; // m 
	static double wheelBaseWidth = 0.6096; // m   //this is the effective wheel base width empirically 4/3 that of the physical wheel base width (24in --> 32in)
	static double vIntercept = 0.67; //0.67 // V
	static double R = 0.09160305; // ohms
	static double kv = 46.51333;   // rad/s per V 
	static double kt = 0.0183969466;   // N*m per A
	static double g = 10.71; // gear reduction (g:1)
	static int nMotors = 2; //number of motors in a gearbox
	
	static double pathRadius = 100000; //6.1516;//0.4064; // m
	static double velMax; 	// m/s
	static double accMax; // m/s^2
	static double angVelMax; // rad/s
	static double angAccMax; // rad/s^2
	static double dt = 0.02; // s  //adjusting 2/19/19
	
	static double k1 = 2/wheelBaseWidth;
	static double k2 = wheelBaseWidth*mass/(2*moi);
	static double k3 = wheelBaseWidth*mass/2;
	static double Ts = 0.0; // Tune me!
	
	static BufferedWriter fileWriter;
	
	static double currentPosOnArc = 0;
	static double posOnArc = 0;
	
	static int index = 0;
	
	static double stepOnArc;
	static double xPos = 0;
	static double prevAng;
	
	static double time, pos, vel, acc, ang, angVel, angAcc, voltRight, voltLeft;
	static String fileName;
	
	static double step = 0.1;
	
	/**
	 * <h1>Generates voltages for the drivetrain motors based on a velocity and a force.</h1>
	 * @param velocity
	 * @param force
	 * @return
	 */
	public static double voltsForMotion(double velocity, double force) {
		return force*wheelRadiusMeters*R/(g*kt)/nMotors  //Torque (I*R) term
			   + velocity/wheelRadiusMeters*g/kv         //Speed  (V*kv) term
			   + vIntercept*Math.signum(velocity);							 //Friction term
	}

	/**
	 * <p>Returns voltages for left or right drive side with a physics model. 
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
	
	/**
	 * <p>Method for printing out the data to the generated *.BOND file with formatting. All numbers are formatted to 4 decimal places</p>
	 * @param vR
	 * @param vL
	 * @param disp
	 * @param vel
	 * @param ang
	 * @param angVel
	 * @param rad
	 * @param pos
	 */
	public static void writeLine(double vR, double vL, double disp, double vel, double ang, double angVel, double rad, double pos) {
		try {
			fileWriter.write(String.format("%.4f", vR) + " " + String.format("%.4f", vL) + " " +
					String.format("%.4f", disp) + " " + String.format("%.4f", vel)+ " " + String.format("%.4f", ang)+ " " + String.format("%.4f", angVel)+ " " + String.format("%.4f", rad)+ " " + String.format("%.4f", pos));
			fileWriter.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>Returns voltages for left or right drive side with a physics model. 
	 * One of the values printed to the *.BOND output file.</p>
	 * @param rPath
	 * @param vel
	 * @param acc
	 * @param left
	 * @return
	 */
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
	
	/**
	 * <h1>Running the virtual path</h1>
	 * <p>This method is the bread and butter of this program. It takes in an array of coefficients, an arc length, and a Segment object, which contains data such as
	 * velocity, acceleration, angular velocity, angular acceleration, robot direction, displacement, etc. This method first determines whether the robot will run 
	 * a trapezoidal or triangular profile. From here, a comprehensive profile is generated through a variety of calculus, kinematic, and physics formulas.</p>
	 * @param coeffs
	 * @param arcL
	 * @param segment
	 */
	public static void runVirtualPath(double [] coeffs, double arcL, Segment segment) {
		//initialization
		time = pos = vel = acc = ang = angVel = angAcc = prevAng = voltRight = voltLeft = xPos =  stepOnArc = posOnArc = currentPosOnArc = (float) 0.0;
		//grabbing kinematic variables from the passed-in segment
		velMax = segment.getVel();
		accMax = segment.getAcc();
		angVelMax = segment.getAngVel();
		angAccMax = segment.getAngAcc();
		
		if(velMax == 0 || accMax == 0 || angVelMax == 0 || angAccMax == 0) {
			System.out.println("HEY DUMMY SET YOUR VARIABLES\n");
		}
		
		double accelTime = velMax/accMax; //determining acceleration time
		double [] deriCoeff = new double[5];
		double accelDistance = 0.5*accMax*Math.pow((accelTime), 2); //determining acceleration distance
		double decelDistance = accelDistance; //in both trapezoidal and triangular, acceleration time and distance are the same.
		System.out.println("Pre simulation update. Current Time: " + time);
		System.out.println("Total drive distance: " + arcL);
		double direction;
		boolean forward = segment.forward;
		boolean disp = segment.disp;
		if(forward) { //setting direction for accelerations
			direction = 1;
			System.out.println("Going FORWARDS");
		} else {
			direction = -1;
			System.out.println("Going BACKWARDS");
		}
		
		if(arcL > (2*accelDistance)) { //Determining if triangular or trapezoidal. This is a trapezoidal profile
			
			arcL -= (accelDistance + decelDistance); //modifying arc length to isolate the middle (full speed) part of the profile.
			
			double targetTime = (arcL/velMax) + (2* accelTime); //target time of whole profile
			//diagnostics
			System.out.println("Running TRAPEZOID profile.");
			System.out.println("Total Estimated Time: " + targetTime);
			System.out.println("Max Velocity: "+ velMax);
			System.out.println("Max Acceleration: " + accMax);
			System.out.println("Beginning profile generation...");
			System.out.println("Accelerating...");
			
			double minRadius = 100000.0;
			
			//determine a minimum path radius for acceleration correction
			for(int i = 0; i<1000; i++) {
				if(UI.generateRadiusAtAPoint(coeffs, 5, (float) i*100)<minRadius) {
					minRadius = UI.generateRadiusAtAPoint(coeffs, 5, (float) i*100);
				}
			}
			
			//maximum angular acceleration and velocity will get adjusted based on path constraints
			if(angVelMax < velMax / minRadius) { //Velocity 
				velMax = angVelMax * minRadius; 
				System.out.println("Maximum Velocity adjusted to: " + velMax);
				}
			if(angAccMax < accMax / minRadius) { //Acceleration
				accMax = angAccMax * minRadius; 
				System.out.println("Maximum Acceleration adjusted to: " + accMax);
			}// solve entire acceleration portion, may not use all of these points
			
			//point stepping begins here for acceleration portion
			while(time < accelTime) { 
				
				acc = accMax * direction; 
				vel = (vel + acc*dt);
				
				posOnArc += (vel*dt);
				
				//robot direction versus displacement to determine how the velocity is added to the position
				if(forward == false && disp == true) {
					currentPosOnArc+=(Math.abs(vel)*dt);
				} else if (forward == true && disp == false){
					currentPosOnArc-=(Math.abs(vel)*dt);
				} else {
					currentPosOnArc+=(vel*dt);
				}
			    
				
				pathRadius = UI.generateRadiusAtAPoint(coeffs, 5, (float) xPos); //instantaneous path-radius is calculated with a function found in the UI class
				
				for(int i = 0; i < 5; i++) {
					deriCoeff[i] = coeffs[i+1] * (i+1); 								  
				}
				
				//back-calculates a Riemann sum to determine the current x-position along the path
				if(disp) {
					while(stepOnArc < currentPosOnArc*100){
						
						double a = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
								(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0)), 2));
						
						double b = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos-step, 4)) + (deriCoeff[3]*Math.pow(xPos-step, 3))+
								(deriCoeff[2]*Math.pow(xPos-step, 2))+(deriCoeff[1]*Math.pow(xPos-step, 1))+(deriCoeff[0]*Math.pow(xPos-step, 0)), 2));
						
					    double stepVal = step * ((a+b)/2); //complies with trapezoidal Riemann sum h(f(a) + f(b))/2
						
						stepOnArc += stepVal;
						
						xPos += step;
					}
				} else {
					while(stepOnArc > currentPosOnArc*100){
						
						double a = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
								(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0)), 2));
						
						double b = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos-step, 4)) + (deriCoeff[3]*Math.pow(xPos-step, 3))+
								(deriCoeff[2]*Math.pow(xPos-step, 2))+(deriCoeff[1]*Math.pow(xPos-step, 1))+(deriCoeff[0]*Math.pow(xPos-step, 0)), 2));
						
					    double stepVal = step * ((a+b)/2); //complies with trapezoidal Riemann sum h(f(a) + f(b))/2
						
						stepOnArc -= stepVal;
						
						xPos -= step;
					}
				}
				
					
					
				//uses this x-position and the passed-in derivative coefficients to calculate the slope at that point.
				double deriAtxPos = (deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
						(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0));
				
				ang = Math.atan(deriAtxPos); //an angle is determined from this derivative(slope)
				angVel = (ang - prevAng)/dt; //now angular velocity can be determined
				prevAng = ang; //add prev ang from derivative to beginning of new segments
			
				voltLeft = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, true); //solves volatge based on calculated varaibles
				voltRight = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, false);
				
				writeLine(voltRight, voltLeft, posOnArc, vel, ang, angVel, pathRadius, xPos/100); //writes data to text file
				
				index++;
				time += dt;
			}
			
			System.out.println("Max velocity reached.");
			
			while(time < targetTime - accelTime) {
				
				posOnArc += (vel*dt);
				
				if(forward == false && disp == true) {
					currentPosOnArc+=(Math.abs(vel)*dt);
				} else if (forward == true && disp == false){
					currentPosOnArc-=(Math.abs(vel)*dt);
				} else {
					currentPosOnArc+=(vel*dt);
				}
				
				pathRadius = UI.generateRadiusAtAPoint(coeffs, 5, (float) xPos);	
				
				for(int i = 0; i < 5; i++) {
					deriCoeff[i] = coeffs[i+1] * (i+1); 								  
				}
				
				if(disp) {
					while(stepOnArc < currentPosOnArc*100){
						
						double a = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
								(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0)), 2));
						
						double b = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos-step, 4)) + (deriCoeff[3]*Math.pow(xPos-step, 3))+
								(deriCoeff[2]*Math.pow(xPos-step, 2))+(deriCoeff[1]*Math.pow(xPos-step, 1))+(deriCoeff[0]*Math.pow(xPos-step, 0)), 2));
						
					    double stepVal = step * ((a+b)/2); //complies with trapezoidal Riemann sum h(f(a) + f(b))/2
						
						stepOnArc += stepVal;
						
						xPos += step;
					}
				} else {
					while(stepOnArc > currentPosOnArc*100){
						
						double a = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
								(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0)), 2));
						
						double b = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos-step, 4)) + (deriCoeff[3]*Math.pow(xPos-step, 3))+
								(deriCoeff[2]*Math.pow(xPos-step, 2))+(deriCoeff[1]*Math.pow(xPos-step, 1))+(deriCoeff[0]*Math.pow(xPos-step, 0)), 2));
						
					    double stepVal = step * ((a+b)/2); //complies with trapezoidal Riemann sum h(f(a) + f(b))/2
						
						stepOnArc -= stepVal;
						
						xPos -= step;
					}
				}
					
					
				
				double deriAtxPos = (deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
						(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0));
				
				ang = Math.atan(deriAtxPos);
				angVel = (ang - prevAng)/dt; //add prev ang from derivative to beginning of new segments
				prevAng = ang;
				
				voltLeft = solveScrubbyChassisDynamics(pathRadius, vel, 0, angVel, true);
				voltRight = solveScrubbyChassisDynamics(pathRadius, vel, 0, angVel, false);
			 
				writeLine(voltRight, voltLeft, posOnArc, vel, ang, angVel, pathRadius, xPos/100);
				
				index++;
				time += dt;
			}
			System.out.println("Decelerating...");
			
			while(time < targetTime) { 
				acc = -accMax * direction;
				vel = (vel + acc*dt);
				
				posOnArc += (vel*dt);
				
				if(forward == false && disp == true) {
					currentPosOnArc+=(Math.abs(vel)*dt);
				} else if (forward == true && disp == false){
					currentPosOnArc-=(Math.abs(vel)*dt);
				} else {
					currentPosOnArc+=(vel*dt);
				}
				
				pathRadius = UI.generateRadiusAtAPoint(coeffs, 5, (float) xPos);	
				for(int i = 0; i < 5; i++) {
					deriCoeff[i] = coeffs[i+1] * (i+1); 								  
				}
				if(disp) {
					while(stepOnArc < currentPosOnArc*100){
						
						double a = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
								(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0)), 2));
						
						double b = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos-step, 4)) + (deriCoeff[3]*Math.pow(xPos-step, 3))+
								(deriCoeff[2]*Math.pow(xPos-step, 2))+(deriCoeff[1]*Math.pow(xPos-step, 1))+(deriCoeff[0]*Math.pow(xPos-step, 0)), 2));
						
					    double stepVal = step * ((a+b)/2); //complies with trapezoidal Riemann sum h(f(a) + f(b))/2
						
						stepOnArc += stepVal;
						
						xPos += step;
					}
				} else {
					while(stepOnArc > currentPosOnArc*100){
						
						double a = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
								(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0)), 2));
						
						double b = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos-step, 4)) + (deriCoeff[3]*Math.pow(xPos-step, 3))+
								(deriCoeff[2]*Math.pow(xPos-step, 2))+(deriCoeff[1]*Math.pow(xPos-step, 1))+(deriCoeff[0]*Math.pow(xPos-step, 0)), 2));
						
					    double stepVal = step * ((a+b)/2); //complies with trapezoidal Riemann sum h(f(a) + f(b))/2
						
						stepOnArc -= stepVal;
						
						xPos -= step;
					}
				}
					
					
				
				double deriAtxPos = (deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
						(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0));
				
				ang = Math.atan(deriAtxPos);
				angVel = (ang - prevAng)/dt; //add prev ang from derivative to beginning of new segments
				prevAng = ang;
				
				voltLeft = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, true);
				voltRight = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, false);

				writeLine(voltRight, voltLeft, posOnArc, vel, ang, angVel, pathRadius, xPos/100);
				index++;
				time += dt;
			}
		} else if (arcL < (2*accelDistance)) {
			
			double triAccelTime = Math.sqrt(arcL / accMax);
			
			double targetTime = 2 * triAccelTime;
			
			System.out.println("Running TRIANGLE profile.");
			System.out.println("Total Estimated Time: " + targetTime);
			System.out.println("Max Velocity: "+ velMax);
			System.out.println("Max Acceleration: " + accMax);
			System.out.println("Beginning profile generation...");
			System.out.println("Accelerating...");
			
			double minRadius = 100000.0;
			
			for(int i = 0; i<1000; i++) {
				if(UI.generateRadiusAtAPoint(coeffs, 5, (float) i*100)<minRadius) {
					minRadius = UI.generateRadiusAtAPoint(coeffs, 5, (float) i*100);
				}
			}
			
			if(angVelMax < velMax / minRadius) { //Velocity 
					velMax = angVelMax * minRadius; 
					System.out.println("Maximum Velocity adjusted to: " + velMax);
				}
				if(angAccMax < accMax / minRadius) { //Acceleration
					accMax = angAccMax * minRadius; 
					System.out.println("Maximum Acceleration adjusted to: " + accMax);
				}// solve entire acceleration portion, may not use all of these points
				
			while(time < targetTime/2) { 
				
				acc = accMax * direction; 
				vel = (vel + acc*dt);
				
				posOnArc += (vel*dt);
				if(forward == false && disp == true) {
					currentPosOnArc+=(Math.abs(vel)*dt);
				} else if (forward == true && disp == false){
					currentPosOnArc-=(Math.abs(vel)*dt);
				} else {
					currentPosOnArc+=(vel*dt);
				}
			    
				pathRadius = UI.generateRadiusAtAPoint(coeffs, 5, (float) xPos);
				
				for(int i = 0; i < 5; i++) {
					deriCoeff[i] = coeffs[i+1] * (i+1); 								  
				}
				
				if(disp) {
					while(stepOnArc < currentPosOnArc*100){
						
						double a = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
								(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0)), 2));
						
						double b = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos-step, 4)) + (deriCoeff[3]*Math.pow(xPos-step, 3))+
								(deriCoeff[2]*Math.pow(xPos-step, 2))+(deriCoeff[1]*Math.pow(xPos-step, 1))+(deriCoeff[0]*Math.pow(xPos-step, 0)), 2));
						
					    double stepVal = step * ((a+b)/2); //complies with trapezoidal Riemann sum h(f(a) + f(b))/2
						
						stepOnArc += stepVal;
						
						xPos += step;
					}
				} else {
					while(stepOnArc > currentPosOnArc*100){
						
						double a = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
								(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0)), 2));
						
						double b = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos-step, 4)) + (deriCoeff[3]*Math.pow(xPos-step, 3))+
								(deriCoeff[2]*Math.pow(xPos-step, 2))+(deriCoeff[1]*Math.pow(xPos-step, 1))+(deriCoeff[0]*Math.pow(xPos-step, 0)), 2));
						
					    double stepVal = step * ((a+b)/2); //complies with trapezoidal Riemann sum h(f(a) + f(b))/2
						
						stepOnArc -= stepVal;
						
						xPos -= step;
					}
				}
					
					
				
				double deriAtxPos = (deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
						(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0));
				
				ang = Math.atan(deriAtxPos);
				angVel = (ang - prevAng)/dt; //add prev ang from derivative to beginning of new segments
				prevAng = ang;
				
				voltLeft = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, true);
				voltRight = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, false);
				
				writeLine(voltRight, voltLeft, posOnArc, vel, ang, angVel, pathRadius, xPos/100);
				
				index++;
				time += dt;
			}
			
			System.out.println("Max velocity reached.");
			System.out.println("Decelerating...");
			while(time < targetTime) {
				acc = -accMax * direction;
				vel = (vel + acc*dt);
				
				posOnArc += (vel*dt);
				if(forward == false && disp == true) {
					currentPosOnArc+=(Math.abs(vel)*dt);
				} else if (forward == true && disp == false){
					currentPosOnArc-=(Math.abs(vel)*dt);
				} else {
					currentPosOnArc+=(vel*dt);
				}
				
				pathRadius = UI.generateRadiusAtAPoint(coeffs, 5, (float) xPos);	
				for(int i = 0; i < 5; i++) {
					deriCoeff[i] = coeffs[i+1] * (i+1); 								  
				}
				if(disp) {
					while(stepOnArc < currentPosOnArc*100){
						
						double a = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
								(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0)), 2));
						
						double b = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos-step, 4)) + (deriCoeff[3]*Math.pow(xPos-step, 3))+
								(deriCoeff[2]*Math.pow(xPos-step, 2))+(deriCoeff[1]*Math.pow(xPos-step, 1))+(deriCoeff[0]*Math.pow(xPos-step, 0)), 2));
						
					    double stepVal = step * ((a+b)/2); //complies with trapezoidal Riemann sum h(f(a) + f(b))/2
						
						stepOnArc += stepVal;
						
						xPos += step;
					}
				} else {
					while(stepOnArc > currentPosOnArc*100){
						
						double a = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
								(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0)), 2));
						
						double b = Math.sqrt(1 + Math.pow((deriCoeff[4]*Math.pow(xPos-step, 4)) + (deriCoeff[3]*Math.pow(xPos-step, 3))+
								(deriCoeff[2]*Math.pow(xPos-step, 2))+(deriCoeff[1]*Math.pow(xPos-step, 1))+(deriCoeff[0]*Math.pow(xPos-step, 0)), 2));
						
					    double stepVal = step * ((a+b)/2); //complies with trapezoidal Riemann sum h(f(a) + f(b))/2
						
						stepOnArc -= stepVal;
						
						xPos -= step;
					}
				}
					
					
				
				double deriAtxPos = (deriCoeff[4]*Math.pow(xPos, 4)) + (deriCoeff[3]*Math.pow(xPos, 3))+
						(deriCoeff[2]*Math.pow(xPos, 2))+(deriCoeff[1]*Math.pow(xPos, 1))+(deriCoeff[0]*Math.pow(xPos, 0));
				
				ang = Math.atan(deriAtxPos);
				angVel = (ang - prevAng)/dt; //add prev ang from derivative to beginning of new segments
				prevAng = ang;
				
				voltLeft = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, true);
				voltRight = solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, false);

				writeLine(voltRight, voltLeft, posOnArc, vel, ang, angVel, pathRadius, xPos/100);
				index++;
				time += dt;
			}
		}
			
	}
}
