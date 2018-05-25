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
				Main.textOutline(instructions, Color.cyan, Color.darkGray, 20, 20, 0.35f);
				Main.textOutline("Press E to go back", Color.cyan, Color.darkGray, 740, 600, 0.4f);
				if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
					HvlMenu.setCurrent(Main.UI);
				}
				super.draw(delta);
			}
		};
	}
}
