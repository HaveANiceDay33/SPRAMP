package com.samuel;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import org.newdawn.slick.Color;

import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlCheckbox;
import com.osreboot.ridhvl.menu.component.HvlTextBox;

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
			if(vals[0].equals("attributes")) {
				break;
			}
			Waypoint loadPoint = new Waypoint(Float.parseFloat(vals[0]), Float.parseFloat(vals[1]), 
					10, Color.yellow, Float.parseFloat(vals[2]), Float.parseFloat(vals[3]));
			UI.tempWaypoints.add(loadPoint);
			
		}
		String[] attr = reader.nextLine().split(" ");
		MenuManager.ui.getChildOfType(HvlArrangerBox.class, 2).getChildOfType(HvlTextBox.class, 0).setText(attr[2]);
		MenuManager.ui.getChildOfType(HvlArrangerBox.class, 2).getChildOfType(HvlTextBox.class, 1).setText(attr[3]);
		MenuManager.ui.getChildOfType(HvlArrangerBox.class, 2).getChildOfType(HvlTextBox.class, 2).setText(attr[4]);
		MenuManager.ui.getChildOfType(HvlArrangerBox.class, 2).getChildOfType(HvlTextBox.class, 3).setText(attr[5]);
		if(attr[0].equals("true")) {
			MenuManager.ui.getChildOfType(HvlArrangerBox.class, 3).getFirstOfType(HvlCheckbox.class).setChecked(true);
		} else {
			MenuManager.ui.getChildOfType(HvlArrangerBox.class, 3).getFirstOfType(HvlCheckbox.class).setChecked(false);
		}
		
	}
	
	
}
