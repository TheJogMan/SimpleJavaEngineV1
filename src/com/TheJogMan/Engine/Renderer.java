package com.TheJogMan.Engine;

import java.awt.image.DataBufferInt;

import com.TheJogMan.Engine.gfx.Canvas;

public class Renderer
{
	private Canvas canvas;
	private Canvas activeCanvas;
	private int[] pixels;
	private int width;
	private int height;
	private boolean doClear = true;
	
	public Renderer(GameContainer game)
	{
		width = game.getWindowWidth();
		height = game.getWindowHeight();
		canvas = new Canvas(game.getWindowWidth(), game.getWindowHeight());
		canvas.clear();
		pixels = ((DataBufferInt)game.getWindow().getImage().getRaster().getDataBuffer()).getData();
	}
	
	public void process()
	{
		int[] ps = getCanvas().getPixels();
		for (int c = 0; c < pixels.length; c++)
		{
			pixels[c] = ps[c];
		}
	}
	
	public Canvas getCanvas()
	{
		if (activeCanvas != null)
		{
			return activeCanvas;
		}
		else
		{
			return canvas;
		}
	}
	
	public void setCanvas(Canvas canvas)
	{
		if (canvas.getWidth() == width && canvas.getHeight() == height)
		{
			activeCanvas = canvas;
		}
		else
		{
			System.out.println("Did not set canvas, invalid size!");
		}
	}
	
	public boolean doClear()
	{
		return doClear;
	}
	
	public void setDoClear(boolean doClear)
	{
		this.doClear = doClear;
	}
	
	public void setCanvas()
	{
		activeCanvas = null;
	}
	
	public Canvas getBaseCanvas()
	{
		return canvas;
	}
}