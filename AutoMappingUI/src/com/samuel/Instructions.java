package com.samuel;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.menu.HvlMenu;

public class Instructions {
	static HvlMenu Controls;

	static String instructions = "C : Erase all (Only in coords) \nD : Delete last (Only in coords) \nW : Coords\nScroll : Zoom in/out\nRight Click : Drag Map\nESC : exit\nLeft Click : Forward drive\nLeft Click+L-Shift : Backwards drive\n"
			+ "Left Click+KEYCODE : Forward Action\nLeft Click+L Shift+KEYCODE : Backwards Action\nArrow Keys: Adjust LAST point placed"
			+ "\nL : Adjust Robot Width and Length (Clears all points)\nA : Scroll up, Z : Scroll Down\n\nU = up, I = intake, D = down, S = shoot, N = NULL Angle";
	public static void initInstructions() {
		Controls = new HvlMenu() {
			public void draw(float delta) {
				Main.textOutline(instructions, Color.black, Color.white, 20, 20, 0.35f);
				Main.textOutline("Press E to go back", Color.black, Color.white, 740, 600, 0.4f);
				if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
					HvlMenu.setCurrent(Main.UI);
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_C)) {
					Main.waypoints.clear();
				}
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
				super.draw(delta);
			}
		};
	}
}
