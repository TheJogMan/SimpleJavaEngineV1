package com.TheJogMan.Engine.gfx;

import java.awt.Color;

public class Canvas extends Drawable
{
	private int backgroundColor = 0x00000000;
	private int mainColor = 0xffffffff;
	private int[] lightMap;
	private int[] lightBlock;
	private int ambientColor = 0xff3e3e3e;
	private double fontScale = 1;
	private boolean doLighting = false;
	private Font font = Font.STANDARD;
	
	public Canvas(int width, int height)
	{
		super(width, height, Color.BLACK.getRGB());
		newCanvas(width, height, Color.BLACK.getRGB());
	}
	
	public Canvas(int width, int height, Color color)
	{
		super(width, height, color.getRGB());
		newCanvas(width, height, color.getRGB());
	}
	
	public Canvas(int width, int height, int color)
	{
		super(width, height, color);
		newCanvas(width, height, color);
	}
	
	void newCanvas(int width, int height, int color)
	{
		lightMap = new int[pixels.length];
		lightBlock = new int[pixels.length];
	}
	
	public void setFont(Font font)
	{
		this.font = font;
	}
	
	public void setFontScale(double scale)
	{
		fontScale = scale;
	}
	
	public double getFontScale()
	{
		return fontScale;
	}
	
	public Font getFont()
	{
		return font;
	}
	
	public void process()
	{
		if (doLighting)
		{
			for (int i = 0; i < pixels.length; i++)
			{
				float red = ((lightMap[i] >> 16) & 0xff) / 255f;
				float green = ((lightMap[i] >> 8) & 0xff) / 255f;
				float blue = (lightMap[i] & 0xff) / 255f;
				
				pixels[i] = ((int)(((pixels[i] >> 16) & 0xff) * red) << 16 | (int)(((pixels[i] >> 8) & 0xff) * green) << 8 | (int)((pixels[i] & 0xff) * blue));
			}
		}
	}
	
	public void clear()
	{
		for (int i = 0; i < pixels.length; i++)
		{
			pixels[i] = backgroundColor;
			lightMap[i] = ambientColor;
			lightBlock[i] = 0;
		}
	}
	
	public int getAmbientLight()
	{
		return ambientColor;
	}
	
	public void setAmbientLight(Color color)
	{
		ambientColor = color.getRGB();
	}
	
	public void setAmbientLight(int color)
	{
		ambientColor = color;
	}
	
	public void setDoLighting(boolean doLighting)
	{
		this.doLighting = doLighting;
	}
	
	public boolean isLightingDone()
	{
		return doLighting;
	}
	
	public int getBackgroundColor()
	{
		return backgroundColor;
	}
	
	public int getColor()
	{
		return mainColor;
	}
	
	public void setBackgroundColor(Color color)
	{
		setBackgroundColor(color.getRGB());
	}
	
	public void setBackgroundColor(int color)
	{
		backgroundColor = color;
	}
	
	public void setColor(Color color)
	{
		setColor(color.getRGB());
	}
	
	public void setColor(int color)
	{
		mainColor = color;
	}
	
	public void setLightMap(int x, int y)
	{
		setLightMap(x, y, mainColor);
	}
	
	public void setLightMap(int x, int y, Color color)
	{
		setLightMap(x, y, color.getRGB());
	}
	
	public void setLightMap(int x, int y, int value)
	{	
		if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
		{
			return;
		}
		
		int baseColor = lightMap[x + y * WIDTH];
		
		int maxRed = Math.max((baseColor >> 16) & 0xff, (value >> 16) & 0xff);
		int maxGreen = Math.max((baseColor >> 8) & 0xff, (value >> 8) & 0xff);
		int maxBlue = Math.max(baseColor & 0xff, value & 0xff);
		
		lightMap[x + y * WIDTH] = (maxRed << 16 | maxGreen << 8 | maxBlue);
	}
	
	public void setLightBlock(int x, int y, int value)
	{
		if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
		{
			return;
		}
		
		lightBlock[x + y * WIDTH] = value;
	}
	
	public void drawRect(int x, int y, int width, int height)
	{
		drawRect(x,y,width,height,mainColor);
	}
	
	public void drawRect(int x, int y, int width, int height, Color color)
	{
		drawRect(x,y,width,height,color.getRGB());
	}
	
	public void drawRect(int x, int y, int width, int height, int color)
	{
		drawRect(x, y, width, height, color, true);
	}
	
	public void drawRect(int x, int y, int width, int height, int color, boolean fill)
	{
		drawRect(x, y, width, height, color, fill, Light.NONE);
	}
	
	public void drawRect(int x, int y, int width, int height, int color, boolean fill, int lightBlock)
	{
		drawRect(x, y, width, height, color, fill, lightBlock, -1);
	}
	
	public void drawRect(int x, int y, int width, int height, int color, boolean fill, int lightBlock, int illumination)
	{
		//Don't render image if it is completely off the screen
		if (x < -width || y < -height || x > WIDTH || y > HEIGHT)
		{
			return;
		}
		
		int newX = 0;
		int newY = 0;
		int newWidth = width;
		int newHeight = height;
		
		//Clip the image
		if (newWidth + x > WIDTH)
		{
			newWidth -= newWidth + x - WIDTH;
		}
		if (newHeight + y > HEIGHT)
		{
			newHeight -= newHeight + y - HEIGHT;
		}
		if (x < 0)
		{
			newX = 0;
			newWidth += x;
		}
		else
		{
			newX = x;
		}
		if (y < 0)
		{
			newY = 0;
			newHeight += y;
		}
		else
		{
			newY = y;
		}
		
		if (fill)
		{
			for (int dx = newX; dx < newX + newWidth; dx++)
			{
				for (int dy = newY; dy < newY + newHeight; dy++)
				{
					setPixel(dx, dy, color, true);
					setLightBlock(dx, dy, lightBlock);
					if (illumination > 0)
					{
						setLightMap(dx, dy, illumination);
					}
				}
			}
		}
		else
		{
			for (int dx = newX; dx < newX + newWidth; dx++)
			{
				setPixel(dx + newY * WIDTH, color, true);
				setLightBlock(dx, newY, lightBlock);
				setPixel(dx + (newY + newHeight - 1) * WIDTH, color, true);
				setLightBlock(dx, newY + newHeight - 1, lightBlock);
				if (illumination > 0)
				{
					setLightMap(dx + newY * WIDTH, illumination);
					setLightMap(dx + (newY + newHeight - 1) * WIDTH, illumination);
				}
			}
			for (int dy = newY + 1; dy < newY + newHeight - 1; dy++)
			{
				setPixel(newX + dy * WIDTH, color, true);
				setLightBlock(newX, dy, lightBlock);
				setPixel(newX + newWidth - 1 + dy * WIDTH, color, true);
				setLightBlock(newX + newWidth - 1, dy, lightBlock);
				if (illumination > 0)
				{
					setLightMap(newX + dy * WIDTH, illumination);
					setLightMap(newX + newWidth - 1 + dy * WIDTH, illumination);
				}
			}
		}
	}
	
	public void drawText(String text, int offX, int offY)
	{
		drawText(text,offX,offY,mainColor);
	}
	
	public void drawText(String text, int offX, int offY, Color color)
	{
		drawText(text,offX,offY,color.getRGB());
	}
	
	public void drawText(String text, int offX, int offY, int color)
	{
		drawText(text, offX, offY, color, -1);
	}
	
	public void drawText(String text, int offX, int offY, int color, int illumination)
	{
		drawText(text, offX, offY, color, illumination, Light.NONE);
	}
	
	public void drawText(String text, int offX, int offY, int color, int illumination, int lightBlock)
	{
		int offset = 0;
		for (int i = 0; i < text.length(); i++)
		{
			CharacterData data = font.getCharacterData(text.charAt(i));
			int yOrigin = 0;
			if (!data.isUpperCase())
			{
				yOrigin = font.getVerticalSeperator() + 1;
			}
			int yp = offY;
			for (int y = yOrigin; y < yOrigin + font.getCharacterHeight(); y++)
			{
				int xp = offX + offset;
				for (int x = data.getOffset(); x < data.getOffset() + data.getWidth(); x++)
				{
					int pixelColor = font.getFontImage().getPixel(x, y);
					if (pixelColor == 0xffffffff)
					{
						if (xp >= 0 && xp < WIDTH && yp >= 0 && yp < HEIGHT)
						{
							drawRect(xp,yp,(int)fontScale,(int)fontScale, color, true, lightBlock, illumination);
						}
					}
					xp += fontScale;
				}
				yp += fontScale;
			}
			offset += data.getWidth() * fontScale;
		}
	}
	
	public void DrawImageTile(ImageTile image, int offX, int offY, int tileX, int tileY)
	{
		draw(image.getTile(tileX, tileY), offX, offY);
	}
	
	public void drawLight(Light light, int offX, int offY)
	{
		if (doLighting)
		{
			for(int i = 0; i <= light.getDiameter(); i++)
			{
				drawLightLine(light, light.getRadius(),light.getRadius(), i, 0, offX, offY);
				drawLightLine(light, light.getRadius(),light.getRadius(), i, light.getDiameter(), offX, offY);
				drawLightLine(light, light.getRadius(),light.getRadius(), 0, i, offX, offY);
				drawLightLine(light, light.getRadius(),light.getRadius(), light.getDiameter(), i, offX, offY);
			}
		}
	}
	
	private void drawLightLine(Light light, int startX, int startY, int endX, int endY, int offX, int offY)
	{
		int deltaX = Math.abs(endX - startX);
		int deltaY = Math.abs(endY - startY);
		
		int sx = startX < endX ? 1 : -1;
		int sy = startY < endY ? 1 : -1;
		
		int err = deltaX - deltaY;
		int e2;
		
		while(true)
		{
			int screenX = startX - light.getRadius() + offX;
			int screenY = startY - light.getRadius() + offY;
			
			int lightColor = light.getLightMap(startX + startY * light.getDiameter());
			if (lightColor == 0)
			{
				return;
			}
			
			if (screenX < 0 || screenX >= WIDTH || screenY < 0 || screenY >= HEIGHT)
			{
				return;
			}
			
			if (lightBlock[screenX + screenY * WIDTH] == Light.FULL)
			{
				return;
			}
			
			setLightMap(screenX, screenY, lightColor);
			
			if (startX == endX && startY == endY)
			{
				break;
			}
			
			e2 = 2 * err;
			
			if(e2 > -1 * deltaY)
			{
				err -= deltaY;
				startX += sx;
			}
			
			if (e2 < deltaX)
			{
				err += deltaX;
				startY += sy;
			}
		}
	}
	
	public void drawLine(int startX, int startY, int endX, int endY)
	{
		drawLine(startX, startY, endX, endY, mainColor);
	}
	
	public void drawLine(int startX, int startY, int endX, int endY, Color color)
	{
		drawLine(startX, startY, endX, endY, color.getRGB());
	}
	
	public void drawLine(int startX, int startY, int endX, int endY, int color)
	{
		drawLine(startX, startY, endX, endY, color, Light.NONE);
	}
	
	public void drawLine(int startX, int startY, int endX, int endY, int color, int lightBlock)
	{
		int deltaX = Math.abs(endX - startX);
		int deltaY = Math.abs(endY - startY);
		
		int sx = startX < endX ? 1 : -1;
		int sy = startY < endY ? 1 : -1;
		
		int err = deltaX - deltaY;
		int e2;
		
		while(true)
		{
			setPixel(startX, startY, color);
			setLightBlock(startX, startY, lightBlock);
			
			if (startX == endX && startY == endY)
			{
				break;
			}
			
			e2 = 2 * err;
			
			if(e2 > -1 * deltaY)
			{
				err -= deltaY;
				startX += sx;
			}
			
			if (e2 < deltaX)
			{
				err += deltaX;
				startY += sy;
			}
		}
	}
	
	public void draw(Drawable drawable, int x, int y)
	{
		this.draw(drawable, x, y, 1, 1);
	}
	
	public void draw(Drawable drawable, int x, int y, int xScale, int yScale)
	{
		this.draw(drawable, x, y, xScale, yScale, -1);
	}
	
	public void draw(Drawable drawable, int x, int y, int xScale, int yScale, int lightBlock)
	{
		super.draw(drawable, x, y, xScale, yScale);
		if (lightBlock < 0)
		{
			return;
		}
		if (x < -drawable.getWidth() || y < -drawable.getHeight() || x > WIDTH || y > HEIGHT)
		{
			return;
		}
		
		int newX = 0;
		int newY = 0;
		int newWidth = drawable.getWidth();
		int newHeight = drawable.getHeight();
		
		//Clip the image
		if (newWidth + x > WIDTH)
		{
			newWidth -= newWidth + x - WIDTH;
		}
		if (newHeight + y > HEIGHT)
		{
			newHeight -= newHeight + y - HEIGHT;
		}
		if (x < 0)
		{
			newX -= x;
		}
		if (x < 0)
		{
			newY -= x;
		}
		
		//Render image
		for (int dy = newY; y < newHeight; y++)
		{
			for (int dx = newX; x < newWidth; x++)
			{
				if (lightBlock < 0)
				{
					if (drawable.getLightBlock() == Light.FULL)
					{
						setLightBlock(dx + x, dy + y, drawable.getLightBlock());
					}
				}
				else
				{
					setLightBlock(dx + x, dy + y, lightBlock);
				}
			}
		}
	}
}