package com.samuel;

import java.util.ArrayList;

public class GenerateVoltages {
	double mass = 40.0; // kg
	double moi = 20.0; // kg * m^2   //this is a number 254 code had, I figure it's close-ish. Definitely need tuning. Trying to account for scrub with this, not so great
	double wheelRadiusMeters = 0.0762; // m 
	double wheelBaseWidth = 0.8128; // m   //this is the effective wheel base width empirically 4/3 that of the physical wheel base width (24in --> 32in)
	double vIntercept = 0.67; //0.67 // V
	double R = 0.09160305; // ohms
	double kv = 46.51333;   // rad/s per V 
	double kt = 0.0183969466;   // N*m per A
	double g = 10.71; // gear reduction (g:1)
	int nMotors = 2; //number of motors in a gearbox
	
	double pathRadius; //6.1516;//0.4064; // m
	double radialDistance;// m   //Target distance to travel
	double velMax = 2.0; 	// m/s
	double accMax = 2.0; // m/s^2
	double angVelMax = 2.0; // rad/s
	double angAccMax = 2.0; // rad/s^2
	double dt = 0.02; // s
	
	ArrayList<Double> voltagesLeft = new ArrayList();
	ArrayList<Double> voltagesRight = new ArrayList();
	
	int index = 0;
	
	public double voltsForMotion(double velocity, double force) {
		return force*wheelRadiusMeters*R/(g*kt)/nMotors  //Torque (I*R) term
			   + velocity/wheelRadiusMeters*g/kv         //Speed  (V*kv) term
			   + vIntercept;							 //Friction term
	}
	
	double k1 = 2/wheelBaseWidth;
	double k2 = wheelBaseWidth*mass/(2*moi);
	
	public double solveChassisDynamics(double rPath, double vel, double acc, boolean left) { // this should return a two quantity object (drivebaseState) or something like that, but again, for now... no 
		if(left) {
			System.out.println("Target left wheel velocity: " + vel*(k1*rPath-1)/(k1*rPath) + " Target left wheel force: " + (mass*acc*(k2*rPath - 1))/(2*k2*rPath));
			return voltsForMotion(
				vel*(k1*rPath-1)/(k1*rPath),
				(mass*acc*(k2*rPath - 1))/(2*k2*rPath));
		}else{
			System.out.println("Target right wheel velocity: " + vel*(k1*rPath+1)/(k1*rPath) + " Target right wheel force: " + (mass*acc*(k2*rPath + 1))/(2*k2*rPath));
			return voltsForMotion(
				vel*(k1*rPath+1)/(k1*rPath),
				(mass*acc*(k2*rPath + 1))/(2*k2*rPath));
		}
	}
	double k3 = wheelBaseWidth*mass/2;
	double Ts = 60.0; // Tune me!
	public double solveScrubbyChassisDynamics( double rPath, double vel, double acc, double angVel, boolean left ){
		if(left) {
			System.out.println("Vel Left: " + vel*(k1*rPath-1)/(k1*rPath) + " Force Left: " + ((k3-moi/rPath)*mass*acc-angVel*Ts*mass)/(2*k3));
			return voltsForMotion(
					vel*(k1*rPath-1)/(k1*rPath),
					((k3-moi/rPath)*mass*acc-angVel*Ts*mass)/(2*k3));
					//(mass*acc*(k2*rPath - 1))/(2*k2*rPath));
		}else {
			System.out.println("Vel Right: " + vel*(k1*rPath+1)/(k1*rPath) + " Force Right: " + ((k3+moi/rPath)*mass*acc+angVel*Ts*mass)/(2*k3));
			return voltsForMotion(
					vel*(k1*rPath+1)/(k1*rPath),
					((k3+moi/rPath)*mass*acc+angVel*Ts*mass)/(2*k3));
					//(mass*acc*(k2*rPath + 1))/(2*k2*rPath));
		}
	}
	
	public void runVirtualPath() {
		double time, pos, vel, acc, ang, angVel, angAcc;
		time = pos = vel = acc = ang = angVel = angAcc = 0.0;
		
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
		
		System.out.println("Beginning profile generation");
		while(vel < velMax) { // solve entire acceleration portion, may not use all of these points
			acc = accMax; 
			vel = vel + acc*dt;
			pos = pos + vel*dt;
			
			angAcc = angAccMax*Math.signum(pathRadius); //accounts for direction of curvature
			angVel = angVel + angAcc*dt;
			ang = ang + angVel*dt;
			
	//		voltagesLeft.add(Robot.driveSUB.solveChassisDynamics(pathRadius, vel, acc, true));
	//		voltagesRight.add(Robot.driveSUB.solveChassisDynamics(pathRadius, vel, acc, false));
			voltagesLeft.add(solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, true));
			voltagesRight.add(solveScrubbyChassisDynamics(pathRadius, vel, acc, angVel, false));
			System.out.println(time + " " + vel + " " + angVel + " " + acc + " " + angAcc + " " + voltagesLeft.get(index) + " " + voltagesRight.get(index));
			
			index++;
			time += dt;
		}
		System.out.println("Max velocity reached");
		
		while(time<2.5) {
			voltagesLeft.add(solveScrubbyChassisDynamics(pathRadius, vel, 0, angVel, true));
			voltagesRight.add(solveScrubbyChassisDynamics(pathRadius, vel, 0, angVel, false));
			System.out.println(time + " " + vel + " " + angVel + " " + acc + " " + angAcc + " " + voltagesLeft.get(index) + " " + voltagesRight.get(index));
			
			index++;
			time += dt;
		}
	}
}
