package com.samuel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.newdawn.slick.Color;



public class ProfileLoader {
	float startX = 0;
	float startY = 0;
	float wayCount = 0;
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
	public String fileName;
	public ProfileLoader(String fileName) {
		this.fileName = fileName;
	}
	public void loadProfile() {
		String userHomeFolder = System.getProperty("user.home")+"/Documents/"; 
		  
		   try{
		          FileInputStream fstream = new FileInputStream(userHomeFolder+fileName+"Loader.txt");
		          DataInputStream in = new DataInputStream(fstream);
		          BufferedReader br = new BufferedReader(new InputStreamReader(in));
		          String strLine;
		        

			      while ((strLine = br.readLine()) != null)   {
			    	  String[] info = strLine.split(" ");
			    	  
			    	  
			    	  Waypoint waypoint = new Waypoint(Float.parseFloat(info[0]),Float.parseFloat(info[1]),Float.parseFloat(info[2]), Color.pink,info[4],info[5],Float.parseFloat(info[6]), Double.parseDouble(info[7]),Double.parseDouble(info[8]),Float.parseFloat(info[9]),Float.parseFloat(info[10]));//process record , etc
			    	  if(!strLine.equals("")) {
			    		  Main.waypoints.add(waypoint);
			    	  }
			        	  
			     }
			     in.close();
		          
		          
		   }catch (Exception e){
		     System.err.println("This is the Error: " + e.getMessage().toString());
		   }
	}
}
