package com.TheJogMan.Engine.gfx;

import java.awt.Color;

public class Image extends Drawable
{
	private int lightBlock = Light.NONE;
	
	public Image(ImageData data)
	{
		super(data.getWidth(), data.getHeight(), (new Color(0, 0, 0, 0)).getRGB());
		for (int x = 0; x < data.getWidth(); x++)
		{
			for (int y = 0; y < data.getHeight(); y++)
			{
				Color color = new Color(data.getPixel(x, y), true);
				setPixel(x + y * data.getWidth(), color.getRGB(), false);
			}
		}
	}
	
	public Image(int width, int height, int defaultColor)
	{
		super(width, height, defaultColor);
	}
	
	public Image(Drawable drawable)
	{
		super(drawable.getWidth(), drawable.getHeight(), (new Color(0, 0, 0, 0)).getRGB());
		for (int index = 0; index < getWidth() * getHeight(); index++)
		{
			setPixel(index, drawable.getPixel(index), false);
		}
	}
	
	public int getLightBlock()
	{
		return lightBlock;
	}
	
	public void setLightBlock(int lightBlock)
	{
		this.lightBlock = lightBlock;
	}
}