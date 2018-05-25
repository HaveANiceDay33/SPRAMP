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
		String[] info = null;
		Color color = Color.transparent;
		String type;
		String action;
		   try{
		          FileInputStream fstream = new FileInputStream(userHomeFolder+fileName);
		          System.out.println(fileName);
		          DataInputStream in = new DataInputStream(fstream);
		          BufferedReader br = new BufferedReader(new InputStreamReader(in));
		          String strLine;

			      while ((strLine = br.readLine()) != null)   {
			    	  info = strLine.split(" "); 
			    	  x = Float.parseFloat(info[0])-83;
			    	  y = Float.parseFloat(info[1])+197;
			    	  size = Float.parseFloat(info[2]);
			    	  color = Color.transparent;
			    	  type = info[4];
			    	  action = info[5];
			    	  distance = Float.parseFloat(info[6]);
			    	  angleOffset = Float.parseFloat(info[7]);
			    	  origAngle = Float.parseFloat(info[8]);
			    	  sizeX = Float.parseFloat(info[9]);
			    	  sizeY  =Float.parseFloat(info[10]);
			    	  if(type.equals("backwards")) {
			    		  
			    	  		if(action.equals("drive")) {
			    	  			color = Color.red;
			    	  		}else if(action.equals("shoot")) {
			    	  			color = Color.yellow;
			    	  		}else if(action.equals("intake")) {
			    	  			color = Color.magenta;
			    	  		}else if(action.equals("up")) {
			    	  			color = Color.pink;
			    	  		}else if(action.equals("down")) {
			    	  			color = Color.blue;
			    	  		}else if(action.equals("start")) {
					    		color = Color.red;
					    	}
			    	  	}
			    	  	if(type.equals("forward")) {
			    	  	
			    	  		if(action.equals("drive")) {
			    	  			color = Color.green;
			    	  		}else if(action.equals("shoot")) {
			    	  			color = Color.yellow;
			    	  		}else if(action.equals("intake")) {
			    	  			color = Color.magenta;
			    	  		}else if(action.equals("up")) {
			    	  			color = Color.pink;
			    	  		}else if(action.equals("down")) {
			    	  			color = Color.blue;
			    	  		}else if(action.equals("start")) {
					    		color = Color.orange;
					    	}
			    	  }
			 
			    	  if(type.equals("fowardnoAngle") || type.equals("backwardsnoAngle")) {
			    		  color = Color.transparent;
			    	  }
			    	  Waypoint waypoint = new Waypoint(x,y,size,color,type,action,distance, angleOffset, origAngle, sizeX, sizeY);//process record , etc

			    	  if(!strLine.equals("")) {
			    		  Main.waypoints.add(waypoint);
			    	  }
			    	  
			    	  
			     }
			  
			     RobotGeometry.robotW = sizeX;
			     RobotGeometry.robotL = sizeY;
			     in.close();
		          
		          
		   }catch (Exception e){
		     System.err.println("This is the Error: " + e.getMessage().toString());
		   }
	}
}
