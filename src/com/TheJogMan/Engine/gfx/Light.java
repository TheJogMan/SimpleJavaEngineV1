package com.TheJogMan.Engine.gfx;

import java.awt.Color;

public class Light
{
	public static final int NONE = 0;
	public static final int FULL = 1;
	
	private int radius;
	private int diameter;
	private int color;
	private int[] lightMap;
	
	public Light(int radius, Color color)
	{
		init(radius, color.getRGB());
	}
	
	public Light(int radius, int color)
	{
		init(radius, color);
	}
	
	public void init(int radius, int color)
	{
		this.radius = radius;
		this.color = color;
		diameter = radius * 2;
		lightMap = new int[diameter * diameter];
		
		for (int y = 0; y < diameter; y++)
		{
			for (int x = 0; x < diameter; x++)
			{
				double distance = Math.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius));
				
				if (distance < radius)
				{
					double power = 1 - (distance / radius);
					lightMap[x + y * diameter] = ((int)(((color >> 16) & 0xff) * power) << 16 | (int)(((color >> 8) & 0xff) * power) << 8 | (int)((color & 0xff) * power));
				}
				else
				{
					lightMap[x + y * diameter] = 0;
				}
			}
		}
	}
	
	public int getRadius()
	{
		return radius;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public int getDiameter()
	{
		return diameter;
	}
	
	public int getLightMap(int index)
	{
		if (index >= 0 && index < lightMap.length)
		{
			return lightMap[index];
		}
		return 0;
	}
}