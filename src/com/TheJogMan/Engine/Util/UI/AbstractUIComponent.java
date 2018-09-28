package com.TheJogMan.Engine.Util.UI;

import java.awt.event.MouseEvent;

import com.TheJogMan.Engine.GameContainer;
import com.TheJogMan.Engine.Input;
import com.TheJogMan.Engine.gfx.Canvas;

public abstract class AbstractUIComponent
{
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected boolean shown = true;
	protected boolean remove = false;
	protected boolean hovered = false;
	protected boolean clicked = false;
	protected boolean wasClicked = false;
	protected boolean wasHovered = false;
	protected Canvas canvas;
	
	public abstract void update(GameContainer game);
	public abstract void render(Canvas canvas, GameContainer game);
	
	public AbstractUIComponent(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		canvas = new Canvas(width, height);
		
		UIManager.components.add(this);
	}
	
	public void draw(Canvas canvas, GameContainer game)
	{
		if (shown)
		{
			this.canvas.clear();
			render(this.canvas, game);
			canvas.draw(this.canvas, x, y);
		}
	}
	
	public void run(GameContainer game)
	{
		wasClicked = clicked;
		wasHovered = hovered;
		clicked = false;
		hovered = false;
		
		Input input = game.getInput();
		if (shown && input.getMouseX() >= x && input.getMouseX() < x + width && input.getMouseY() >= y && input.getMouseY() < y + height)
		{
			hovered = true;
		}
		if (hovered && input.isButton(MouseEvent.BUTTON1))
		{
			clicked = true;
		}
		
		update(game);
	}
	
	public boolean shown()
	{
		return shown;
	}
	
	public boolean hovered()
	{
		return hovered;
	}
	
	public boolean clicked()
	{
		return clicked;
	}
	
	public boolean justHovered()
	{
		return !wasHovered && hovered;
	}
	
	public boolean justClicked()
	{
		return !wasClicked && clicked;
	}
	
	public boolean unHovered()
	{
		return wasHovered && !hovered;
	}
	
	public boolean unClicked()
	{
		return wasClicked && !clicked;
	}
	
	public void remove()
	{
		remove = true;
	}
	
	public boolean isRemoved()
	{
		return remove;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void setShown(boolean shown)
	{
		this.shown = shown;
	}
}