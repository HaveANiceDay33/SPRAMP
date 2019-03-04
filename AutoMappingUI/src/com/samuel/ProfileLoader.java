package com.samuel;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import org.newdawn.slick.Color;

public class ProfileLoader {
	String file;
	Scanner reader;
	public ProfileLoader(String file) {
		this.file = file;
		UI.tempWaypoints.clear();
		UI.segments.clear();
		try {
			 reader = new Scanner(new FileReader(UI.userHomeFolder + this.file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(reader.hasNextLine()) {
			String[] vals = reader.nextLine().split(" ");
			Waypoint loadPoint = new Waypoint(Float.parseFloat(vals[0]), Float.parseFloat(vals[1]), 
					10, Color.yellow, MenuManager.robotW, MenuManager.robotL);
			UI.tempWaypoints.add(loadPoint);
		}
	}
	
	
}
