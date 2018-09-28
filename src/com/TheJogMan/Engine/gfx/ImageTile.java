package com.TheJogMan.Engine.gfx;

public class ImageTile extends Image
{
	private int tileWidth, tileHeight;
	
	public ImageTile(String path, int tileWidth, int tileHeight)
	{
		super(new ImageData(path));
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}
	
	public int getTileWidth()
	{
		return tileWidth;
	}
	
	public int getTileHeight()
	{
		return tileHeight;
	}
	
	public Image getTile(int tileX, int tileY)
	{
		Image tile = new Image(tileWidth, tileHeight, 0);
		for (int x = 0; x < tileWidth; x++)
		{
			for (int y = 0; y < tileHeight; y++)
			{
				tile.setPixel(x + y * tile.getWidth(), super.getPixel(x + tileX * tileWidth, y + tileY * tileHeight), false);
			}
		}
		return tile;
	}
}