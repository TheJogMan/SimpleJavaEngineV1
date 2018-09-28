package com.TheJogMan.Engine.Util;

public class VectorInt
{
	private int x;
	private int y;
	
	public VectorInt(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void set(VectorInt vec2)
	{
		x = vec2.getX();
		y = vec2.getY();
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public void rotate(int angle)
	{
		double radian = Math.toRadians(angle);
		double cos = Math.cos(radian);
		double sin = Math.sin(radian);
		double originalX = (double)x;
		x = (int)((double)x * cos - (double)y * sin);
		y = (int)(originalX * sin + (double)y * cos);
	}
	
	public void add(int number)
	{
		y += number;
		x += number;
	}
	
	public void multiply(int number)
	{
		x *= number;
		y *= number;
	}
	
	public void divide(int number)
	{
		x /= number;
		y /= number;
	}
	
	public void add(VectorInt vec2)
	{
		x += vec2.getX();
		y += vec2.getY();
	}
	
	public void subtract(VectorInt vec2)
	{
		x -= vec2.getX();
		y -= vec2.getY();
	}
	
	public void multiply(VectorInt vec2)
	{
		x *= vec2.getX();
		y *= vec2.getY();
	}
	
	public VectorInt clone()
	{
		return new VectorInt(x,y);
	}
}