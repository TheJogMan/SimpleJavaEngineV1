package com.TheJogMan.Engine.Util;

public class Vector
{
	private double x;
	private double y;
	
	public Vector(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public void set(Vector vec2)
	{
		x = vec2.getX();
		y = vec2.getY();
	}
	
	public void setX(double x)
	{
		this.x = x;
	}
	
	public void setY(double y)
	{
		this.y = y;
	}
	
	public void rotate(double angle)
	{
		double radian = Math.toRadians(angle);
		double cos = Math.cos(radian);
		double sin = Math.sin(radian);
		double originalX = x;
		x = x * cos - y * sin;
		y = originalX * sin + y * cos;
	}
	
	public double getAngle(Vector vec2)
	{
		double angle = getDot(vec2) / (getLength() * vec2.getLength());
		angle = Math.acos(angle);
		angle = Math.toDegrees(angle);
		return angle;
	}
	
	public double getLength()
	{
		return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
	}
	
	public double getDot(Vector vec2)
	{
		return (vec2.getX() * getX()) + (vec2.getY() * getY());
	}
	
	public void add(double number)
	{
		y += number;
		x += number;
	}
	
	public void multiply(double number)
	{
		x *= number;
		y *= number;
	}
	
	public void divide(double number)
	{
		x /= number;
		y /= number;
	}
	
	public void add(Vector vec2)
	{
		x += vec2.getX();
		y += vec2.getY();
	}
	
	public void subtract(Vector vec2)
	{
		x -= vec2.getX();
		y -= vec2.getY();
	}
	
	public void multiply(Vector vec2)
	{
		x *= vec2.getX();
		y *= vec2.getY();
	}
	
	public Vector clone()
	{
		return new Vector(x,y);
	}
}