package com.samuel;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.menu.HvlMenu;

public class Coordinates {
	static HvlMenu Coords;
	public static void initCoords() {
		Coords = new HvlMenu() {
			
			public void draw(float delta) {
				if(Main.waypoints.size() > 0) {
					if(Keyboard.isKeyDown(Keyboard.KEY_D) && Main.deleteCounter <= 0){
						Main.waypoints.remove(Main.waypoints.size()-1);
						
						Main.deleteCounter = 50;
					}
					Main.deleteCounter --;
					if(Main.deleteCounter == 0) {
						Main.deleteCounter = 0;
					}
				}
					for(int i = 0; i < Main.waypoints.size(); i++) {

						if(Keyboard.isKeyDown(Keyboard.KEY_C)) {
							Main.waypoints.clear();
						}

						if(Main.waypoints.size()!=0) {
							Main.textOutline("Coord "+(i+1)+": "+Math.round((((Main.waypoints.get(i).x)-157+240)/0.4646)/2.56)+" In.      "+Math.round((((Main.waypoints.get(i).y)-135)/0.4646)/2.56)+" In.", Color.cyan, Color.darkGray, 40, (40 *i) + (Main.textY-20), 0.3f);

						}
							
							if(i > 0) {
		
								Main.textOutline("Angle from last: "+ Main.waypoints.get(i).angleOffset + " degrees. Distance between "+ (i) +" and " +(i+1) + ": "+ Main.waypoints.get(i).distance + " In.", Color.cyan, Color.darkGray, 400, (40*(i-1))+Main.textY+25, 0.25f);
								Main.textOutline(Main.waypoints.get(i).action, Color.cyan, Color.darkGray, 1200, (40*(i-1))+Main.textY+25, 0.25f);
								
								if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
									Main.textY -= .1;
								}
								if(Keyboard.isKeyDown(Keyboard.KEY_Z)) {
									Main.textY+=.1;
								}
							}	
					}
					
				
				Main.textOutline("Press E to go back",  Color.cyan, Color.darkGray, 740, 600, 0.4f);
				if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
					HvlMenu.setCurrent(Main.UI);
				}
				super.draw(delta);
			}
		};
	}
}
