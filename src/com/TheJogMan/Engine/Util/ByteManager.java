package com.TheJogMan.Engine.Util;

public class ByteManager
{
	public static byte[] fromInt(int value)
	{
		byte[] values = new byte[4];
		values[0] = (byte)(value >> 24);
		value = value - (values[0] << 24);
		values[1] = (byte)(value >> 16);
		value = value - (values[1] << 16);
		values[2] = (byte)(value >> 8);
		values[3] = (byte)(value - (values[2] << 8));
		return values;
	}
	
	public static int convert(byte value)
	{
		int newValue = (int)value;
		if (newValue < 0)
		{
			newValue = 256 + newValue;
		}
		return newValue;
	}
	
	public static int buildInt(byte value1, byte value2)
	{
		int v1 = convert(value1);
		int v2 = convert(value2);
		int finalVal = (v1 << 8) | v2;
		return finalVal;
	}
	
	public static int buildInt(byte value1, byte value2, byte value3, byte value4)
	{
		int v1 = buildInt(value1, value2);
		int v2 = buildInt(value3, value4);
		int finalVal = (v1 << 16) | v2;
		return finalVal;
	}
	
	public static byte[] split(byte value)
	{
		byte[] values = new byte[2];
		int operVal = convert(value);
		values[0] = (byte)(operVal >> 4);
		values[1] = (byte)(operVal - (values[0] << 4));
		return values;
	}
	
	public static byte[] quarter(byte value)
	{
		byte[] values = new byte[4];
		int operVal = convert(value);
		values[0] = (byte)(operVal >> 6);
		operVal = operVal - (values[0] << 6);
		values[1] = (byte)(operVal >> 4);
		operVal = operVal - (values[1] << 4);
		values[2] = (byte)(operVal >> 2);
		values[3] = (byte)(operVal - (values[2] << 2));
		return values;
	}
	
	public static boolean[] getBits(byte value)
	{
		boolean[] bits = new boolean[8];
		int operVal = convert(value);
		for (int c = 0; c < 8; c++)
		{
			bits[c] = (operVal >> (7 - c)) == 1;
			operVal = operVal - (operVal >> (7 - c));
		}
		return bits;
	}
}