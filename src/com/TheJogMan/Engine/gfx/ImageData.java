package com.TheJogMan.Engine.gfx;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageData
{
	int[] pixels;
	int width;
	int height;
	
	public ImageData(String path)
	{
		BufferedImage image = null;
		
		try
		{
			image = ImageIO.read(Image.class.getResourceAsStream(path));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		width = image.getWidth();
		height = image.getHeight();
		pixels = new int[image.getWidth() * image.getHeight()];
		
		for (int x = 0; x < image.getWidth(); x++)
		{
			for (int y = 0; y < image.getHeight(); y++)
			{
				Color color = new Color(image.getRGB(x, y), true);
				pixels[x + y * image.getWidth()] = color.getRGB();
			}
		}
		image.flush();
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
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
		return getPixel(x + y * width);
	}
}