package com.TheJogMan.Engine.Util.UI.Components;

import java.awt.Color;

import com.TheJogMan.Engine.GameContainer;
import com.TheJogMan.Engine.Util.UI.AbstractUIComponent;
import com.TheJogMan.Engine.gfx.Canvas;

public class Switch extends AbstractUIComponent
{
	boolean state = true;
	
	public Switch(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}
	
	@Override
	public void update(GameContainer game)
	{
		if (justClicked())
		{
			state = !state;
		}
	}
	
	@Override
	public void render(Canvas canvas, GameContainer game)
	{
		canvas.drawRect(0, 0, width / 2, height, Color.RED);
		canvas.drawRect(width / 2, 0, width / 2, height, Color.GREEN);
		int dx = 0;
		if (!state)
		{
			dx += width / 2;
		}
		canvas.drawRect(dx, 0, width / 2, height, Color.BLUE);
	}
	
	public void setState(boolean state)
	{
		this.state = state;
	}
	
	public boolean getState()
	{
		return state;
	}
}