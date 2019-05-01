package com.samuel;

import org.newdawn.slick.Color;

import com.osreboot.ridhvl.display.collection.HvlDisplayModeDefault;
import com.osreboot.ridhvl.painter.HvlCamera2D;
import com.osreboot.ridhvl.painter.painter2d.HvlFontPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;

public class Main extends HvlTemplateInteg2D{
	
	public static void main(String [] args){
		new Main();
	}
	public Main(){
		super(60, 1440, 720, "S.P.R.A.M.P. - By Samuel Munro and Peter Salisbury", new HvlDisplayModeDefault());
	}
	
	public static final int
	FONT_INDEX = 0,
	FRAME_INDEX = 1,
	FIELD_INDEX = 2,
	CIRCLE_INDEX = 3,
	LOGO_INDEX = 4;
	
	//Method for drawing text with an outline. Much more visually appealing
	public static void textOutline(String text, Color textColor, Color outlineColor, float x, float y, float size) {
		gameFont.drawWord(text, x+1, y, outlineColor, size);
		gameFont.drawWord(text, x, y+1, outlineColor, size);
		gameFont.drawWord(text, x, y-1, outlineColor, size);
		gameFont.drawWord(text, x-1, y, outlineColor, size);
		gameFont.drawWord(text, x, y, textColor, size);
	}

	
	static HvlCamera2D zoomer;
	static HvlFontPainter2D gameFont;
	static float zoom = 1;
	
	@Override
	public void initialize() {
		getTextureLoader().loadResource("osFont");					//TEXTURES
		getTextureLoader().loadResource("robotFrame2");
		getTextureLoader().loadResource("fieldupdated");
		getTextureLoader().loadResource("circle");
		getTextureLoader().loadResource("logo");
		
		gameFont =  new HvlFontPainter2D(getTexture(FONT_INDEX), HvlFontPainter2D.Preset.FP_INOFFICIAL,.5f,8f,0); //font definition
		zoomer = new HvlCamera2D(540, 360, 0, zoom, HvlCamera2D.ALIGNMENT_CENTER); //Camera definition
		
		MenuManager.init(); 
	}
	@Override
	public void update(float delta) {
		MenuManager.update(delta);
	}
	
}
