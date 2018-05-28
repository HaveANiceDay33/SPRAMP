package com.samuel;

import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;

import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.action.HvlAction1;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlButton;
import com.osreboot.ridhvl.menu.component.HvlComponentDrawable;
import com.osreboot.ridhvl.menu.component.HvlSpacer;
import com.osreboot.ridhvl.menu.component.HvlTextBox;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox.ArrangementStyle;
import com.osreboot.ridhvl.menu.component.collection.HvlLabeledButton;

public class RobotGeometry {
	static HvlMenu Geo;
	static float robotW;
	static float robotL;
	static String userHomeFolder = System.getProperty("user.home")+"/Documents/";
	public static void initMenu() {

		Geo = new HvlMenu() {
			public void draw(float delta) {
				Main.textOutline("              Welcome to HavaANiceDay's Auto Mapping Client Application! \nKeep in mind that this is under constant development. Not everything is stable : " ,Color.cyan, Color.darkGray,60,30, 0.4f);
				Main.textOutline(")",Color.cyan, Color.darkGray,1345,70, 0.4f);
				Main.textOutline("Set robot width and length :",Color.cyan, Color.darkGray,550,220, 0.3f);
				Main.textOutline("Width : ",Color.cyan, Color.darkGray,470,280, 0.4f);
				Main.textOutline("Length : ",Color.cyan, Color.darkGray,470,360, 0.4f);
				super.draw(delta);
			}
		};
		Geo.add(new HvlArrangerBox.Builder().setStyle(ArrangementStyle.VERTICAL).setWidth(250).setHeight(400).setX((Display.getWidth()/2)-125).setY((Display.getHeight()/2)-200).build());

		Geo.getFirstArrangerBox().add(new HvlSpacer(30, 30));
		
		Geo.getFirstArrangerBox().add(new HvlTextBox.Builder().setWidth(200).setHeight(50).setFont(Main.gameFont).setTextColor(Color.darkGray).setTextScale(0.25f).setOffsetY(20).setOffsetX(20).setText("").setNumbersOnly(true).setFocusedDrawable(new HvlComponentDrawable() {	
			@Override
			public void draw(float delta, float x, float y, float width, float height) {
				hvlDrawQuad(x,y,width,height, Color.lightGray);	
			}
		}).setUnfocusedDrawable(new HvlComponentDrawable() {
			
			@Override
			public void draw(float delta, float x, float y, float width, float height) {
				hvlDrawQuad(x,y,width,height, Color.green);	
			}
		}).build());
		
		Geo.getFirstArrangerBox().add(new HvlSpacer(30, 30));
		
		Geo.getFirstArrangerBox().add(new HvlTextBox.Builder().setWidth(200).setHeight(50).setFont(Main.gameFont).setTextColor(Color.darkGray).setTextScale(0.25f).setOffsetY(20).setOffsetX(20).setText("").setNumbersOnly(true).setFocusedDrawable(new HvlComponentDrawable() {	
			@Override
			public void draw(float delta, float x, float y, float width, float height) {
				hvlDrawQuad(x,y,width,height, Color.lightGray);	
			}
		}).setUnfocusedDrawable(new HvlComponentDrawable() {
			
			@Override
			public void draw(float delta, float x, float y, float width, float height) {
				hvlDrawQuad(x,y,width,height, Color.green);	
			}
		}).build());
		Geo.getChildOfType(HvlArrangerBox.class, 0).add(new HvlSpacer(30, 30));
	
		Geo.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("Set W/H").setClickedCommand(new HvlAction1<HvlButton>() {
			
			@Override
			public void run(HvlButton a) {
				//fileName = UI.getFirstArrangerBox().getFirstOfType(HvlTextBox.class).getText();
				if(!Geo.getFirstArrangerBox().getFirstOfType(HvlTextBox.class).getText().equals("")){
					robotW = Float.parseFloat(Geo.getFirstArrangerBox().getFirstOfType(HvlTextBox.class).getText());
					robotL = Float.parseFloat(Geo.getFirstArrangerBox().getChildOfType(HvlTextBox.class, 1).getText());
					Main.UI.getChildOfType(HvlArrangerBox.class,1).getChildOfType(HvlTextBox.class,0).setText("");
					Main.background = 0;
					HvlMenu.setCurrent(Main.UI);
				}
	
			}
		}).build());
		
		Geo.add(new HvlArrangerBox.Builder().setStyle(ArrangementStyle.HORIZONTAL).setWidth(250).setHeight(400).setX((Display.getWidth()/2)-125).setY((Display.getHeight()/2)+00).build());


		Geo.getChildOfType(HvlArrangerBox.class,1).add(new HvlLabeledButton.Builder().setText("Load").setClickedCommand(new HvlAction1<HvlButton>() {
	
			@Override
			public void run(HvlButton a) {

					Main.waypoints.clear();
					
					FileDialog dialog = new FileDialog((Frame)null, "Select a .BOND file");
					dialog.setMode(FileDialog.LOAD);
					dialog.setFile("*.BOND");
					dialog.setDirectory(userHomeFolder);
					Main.background = 0;
					dialog.setVisible(true);
					if(!(dialog.getFile() == null)){
						String file = dialog.getFile();
						
						ProfileLoader loader = new ProfileLoader(file);
						loader.loadProfile();

						Main.UI.getChildOfType(HvlArrangerBox.class,1).getChildOfType(HvlTextBox.class,0).setText(file.replaceAll("Loader.BOND", ""));
						HvlMenu.setCurrent(Main.UI);
					}
		
	
			}
		}).build());
		
		Geo.add(new HvlArrangerBox.Builder().setStyle(ArrangementStyle.VERTICAL).setWidth(250).setHeight(400).setX((Display.getWidth()/2)+400).setY((Display.getHeight()/2)-185).build());
		Geo.getChildOfType(HvlArrangerBox.class,2).add(new HvlLabeledButton.Builder().setText("Use Spectre").setWidth(200). setClickedCommand(new HvlAction1<HvlButton>() {
			
			@Override
			public void run(HvlButton a) {

					Main.waypoints.clear();
					
					robotW = 35;
					robotL = 45;
					Main.background  = 3;
					HvlMenu.setCurrent(Main.UI);
			}
		}).build());
		
		Geo.getChildOfType(HvlArrangerBox.class, 2).add(new HvlSpacer(70, 30));

		Geo.getChildOfType(HvlArrangerBox.class,2).add(new HvlLabeledButton.Builder().setText("Use Skyfall").setWidth(200). setClickedCommand(new HvlAction1<HvlButton>() {
			
			@Override
			public void run(HvlButton a) {

					Main.waypoints.clear();
					
					robotW = 42;
					robotL = 36;
					Main.background  = 4;
					HvlMenu.setCurrent(Main.UI);
			}
		}).build());
		
		Geo.getChildOfType(HvlArrangerBox.class, 2).add(new HvlSpacer(70, 30));
		
		Geo.getChildOfType(HvlArrangerBox.class,2).add(new HvlLabeledButton.Builder().setText("Use GoldenEye").setWidth(200). setClickedCommand(new HvlAction1<HvlButton>() {
			
			@Override
			public void run(HvlButton a) {

					Main.waypoints.clear();
					
					robotW = 34;
					robotL = 46;
					Main.background = 0;
					HvlMenu.setCurrent(Main.UI);
			}
		}).build());

	}
	
}
