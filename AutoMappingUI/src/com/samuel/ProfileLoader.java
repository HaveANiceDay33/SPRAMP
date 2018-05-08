package com.samuel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;



public class ProfileLoader {
	float startX = 0;
	float startY = 0;
	float wayCount = 0;
	public void loadProfile() {
		   try{
		          FileInputStream fstream = new FileInputStream("/home/lvuser/autos/auto.txt");
		          DataInputStream in = new DataInputStream(fstream);
		          BufferedReader br = new BufferedReader(new InputStreamReader(in));
		          String strLine;
		        

			          while ((strLine = br.readLine()) != null)   {
			        	  String[] info = strLine.split(" ");
			        	  
			        	 // Waypoint waypoint = new Waypoint(info[0],info[1],info[2], info[3]);//process record , etc
			        	  if(!strLine.equals("")) {
			        		 // Main.waypoints.add(waypoint);
			        	  }
			        	  
			          }
			          in.close();
		          
		          
		          
		   }catch (Exception e){
		     System.err.println("This is the Error: " + e.getMessage().toString());
		   }
	}
}
