package com.TheJogMan.Engine.gfx;

import java.awt.Color;

public abstract class Drawable
{
	final int WIDTH;
	final int HEIGHT;
	int lightBlock;
	int[] pixels;
	
	public Drawable(int width, int height, int color, int lightBlock)
	{
		this.WIDTH = width;
		this.HEIGHT = height;
		this.lightBlock = lightBlock;
		pixels = new int[width * height];
		for (int c = 0; c < pixels.length; c++)
		{
			pixels[c] = color;
		}
	}
	
	public Drawable(int width, int height, int color)
	{
		this.WIDTH = width;
		this.HEIGHT = height;
		this.lightBlock = Light.FULL;
		pixels = new int[width * height];
		for (int c = 0; c < pixels.length; c++)
		{
			pixels[c] = color;
		}
	}
	
	public void setLightBlock(int lightBlock)
	{
		this.lightBlock = lightBlock;
	}
	
	public int getLightBlock()
	{
		return lightBlock;
	}
	
	public int getWidth()
	{
		return WIDTH;
	}
	
	public int getHeight()
	{
		return HEIGHT;
	}
	
	public int[] getPixels()
	{
		return pixels;
	}
	
	public void setPixels(int[] pixels)
	{
		this.pixels = pixels;
	}
	
	public void setPixel(int index, int color, boolean doAlphaBlend)
	{
		if (index >= 0 && index < pixels.length)
		{
			int alpha = (color >> 24) & 0xff;
			
			if (alpha == 255 || !doAlphaBlend)
			{
				pixels[index] = color;
			}
			else
			{
				int pixelColor = pixels[index];
				int newRed = ((pixelColor >> 16) & 0xff) - (int)((((pixelColor >> 16) & 0xff) - ((color >> 16) & 0xff)) * alpha / 255f);
				int newGreen = ((pixelColor >> 8) & 0xff) - (int)((((pixelColor >> 8) & 0xff) - ((color >> 8) & 0xff)) * alpha / 255f);
				int newBlue = (pixelColor & 0xff) - (int)(((pixelColor & 0xff) - (color & 0xff)) * alpha / 255f);
				pixels[index] = (255 << 24 | newRed << 16 | newGreen << 8 | newBlue);
			}
		}
	}
	
	public void setPixel(int x, int y, int color, boolean doAlphaBlend)
	{
		if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT)
		{
			setPixel(x + y * WIDTH, color, doAlphaBlend);
		}
	}
	
	public void setPixel(int x, int y, int color)
	{
		setPixel(x, y, color, true);
	}
	
	public void setPixel(int index, Color color)
	{
		setPixel(index, color.getRGB(), true);
	}
	
	public void setPixel(int x, int y, Color color)
	{
		setPixel(x, y, color.getRGB());
	}
	
	public int getPixel(int index)
	{
		if (index >= 0 && index < pixels.length)
		{
			return pixels[index];
		}
		return 0;
	}
	
	public int getPixel(int x, int y)
	{
		if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT)
		{
			return getPixel(x + y * WIDTH);
		}
		else
		{
			return 0;
		}
	}
	
	public void draw(Drawable drawable, int x, int y)
	{
		draw(drawable, x, y, 1, 1);
	}
	
	public void draw(Drawable drawable, int x, int y, double xScale, double yScale)
	{
		if (x > getWidth() || y > getHeight() || x + drawable.getWidth() * xScale < 0 || y + drawable.getHeight() * yScale < 0)
		{
			return;
		}
		int originX = 0;
		int originY = 0;
		int width = (int)(drawable.getWidth() * xScale);
		int height = (int)(drawable.getHeight() * yScale);
		if (x + width > getWidth())
		{
			width-=(x + width - getWidth());
		}
		if (y + height > getHeight())
		{
			height-=(y + height - getHeight());
		}
		if (x < 0)
		{
			originX-=x;
			x = 0;
		}
		if (y < 0)
		{
			originY-=y;
			y = 0;
		}
		for (int sx = originX; sx < width; sx++)
		{
			for (int sy = originY; sy < height; sy++)
			{
				if (xScale != 1 || yScale != 1)
				{
					int dx = x + (int)((sx - originX) * xScale);
					int dy = y + (int)((sy - originY) * yScale);
					int color = drawable.getPixel(sx, sy);
					for (int rx = dx; rx < dx + xScale; rx++)
					{
						for (int ry = dy; ry < dy + yScale; ry++)
						{
							setPixel(rx, ry, color);
						}
					}
				}
				else
				{
					setPixel((sx + x - originX) + (sy + y - originY) * getWidth(),drawable.getPixel(sx + sy * drawable.getWidth()), true);
				}
			}
		}
	}
	
	public void fill(Color color)
	{
		fill(color.getRGB());
	}
	
	public void fill(int color)
	{
		for (int i = 0; i < pixels.length; i++)
		{
			pixels[i] = color;
		}
	}
	
	public int getAverageColor()
	{
		int red = 0;
		int green = 0;
		int blue = 0;
		for (int i = 0; i < pixels.length; i++)
		{
			red += (pixels[i] >> 16) & 0xff;
			green += (pixels[i] >> 8) & 0xff;
			blue += pixels[i] & 0xff;
		}
		return new Color((int)(red / pixels.length),(int)(green / pixels.length),(int)(blue / pixels.length)).getRGB();
	}
	
	public Image applyShading(double percentage)
	{
		Image newImage = new Image(WIDTH, HEIGHT, 0);
		
		for (int x = 0; x < WIDTH; x++)
		{
			for (int y = 0; y < HEIGHT; y++)
			{
				//get color from original image
				Color pixelColor = new Color(getPixel(x,y));
				int red = pixelColor.getRed();
				int green = pixelColor.getGreen();
				int blue = pixelColor.getBlue();
				int alpha = pixelColor.getAlpha();
				
				//dim the color
				red = red - (int)((double)red * percentage);
				green = green - (int)((double)green * percentage);
				blue = blue - (int)((double)blue * percentage);
				
				//make sure the values are still in the range of 0-255
				if (red < 0) red = 0; else if (red > 255) red = 255;
				if (green < 0) green = 0; else if (green > 255) green = 255;
				if (blue < 0) blue = 0; else if (blue > 255) blue = 255;
				
				//set the color for the new image
				Color newColor = new Color(red,green,blue, alpha);
				newImage.setPixel(x, y, newColor.getRGB());
			}
		}
		
		return newImage;
	}
}