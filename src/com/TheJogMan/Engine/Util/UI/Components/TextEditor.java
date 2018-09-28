package com.TheJogMan.Engine.Util.UI.Components;

import java.awt.Color;

import com.TheJogMan.Engine.GameContainer;
import com.TheJogMan.Engine.gfx.Canvas;

public class TextEditor extends TextField
{	
	public TextEditor(int x, int y, int width, int height)
	{
		super(x, y, width, height, 1, 1, width - 2, height - 2);
	}

	@Override
	public void textFieldUpdate(GameContainer game)
	{
		
	}

	@Override
	public void textFieldPreDraw(Canvas canvas, GameContainer game, Canvas textCanvas)
	{
		canvas.drawRect(0, 0, width, height, Color.black.getRGB(), false);
	}
	
	@Override
	public void textFieldPostDraw(Canvas canvas, GameContainer game)
	{
		
	}
}