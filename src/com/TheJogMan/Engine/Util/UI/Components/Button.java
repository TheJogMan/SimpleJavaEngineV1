package com.TheJogMan.Engine.Util.UI.Components;

import java.awt.Color;

import com.TheJogMan.Engine.GameContainer;
import com.TheJogMan.Engine.Util.UI.AbstractUIComponent;
import com.TheJogMan.Engine.gfx.Canvas;

public class Button extends AbstractUIComponent
{
	static Color normalColor = new Color(255, 255, 255);
	static Color hoveredColor = new Color(120, 120, 120);
	static Color clickedColor = new Color(60, 60, 60);
	static Color borderColor = new Color(0, 0, 0);
	static Color textColor = new Color(0, 0, 0);
	
	String text;
	
	public Button(int x, int y, int width, int height, String text)
	{
		super(x, y, width, height);
		
		this.text = text;
	}
	
	@Override
	public void update(GameContainer game)
	{
		
	}
	
	@Override
	public void render(Canvas canvas, GameContainer game)
	{
		canvas.drawRect(0, 0, width, height, borderColor.getRGB(), false);
		Color backgroundColor = normalColor;
		if (clicked)
		{
			backgroundColor = clickedColor;
		}
		else if (hovered)
		{
			backgroundColor = hoveredColor;
		}
		canvas.drawRect(1, 1, width - 2, height - 2,backgroundColor);
		canvas.drawText(text, 2, 2, textColor);
	}
}