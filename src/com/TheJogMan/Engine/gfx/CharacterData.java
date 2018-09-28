package com.TheJogMan.Engine.gfx;

public class CharacterData
{
	private char character;
	private Font font;
	private int characterX;
	private boolean isUpperCase;
	private int characterWidth;
	
	public CharacterData(char character, Font font, int characterX, boolean isUpperCase, int characterWidth)
	{
		this.character = character;
		this.font = font;
		this.characterX = characterX;
		this.isUpperCase = isUpperCase;
		this.characterWidth = characterWidth;
	}
	
	public char getCharacter()
	{
		return character;
	}
	
	public Font getFont()
	{
		return font;
	}
	
	public int getOffset()
	{
		return characterX;
	}
	
	public int getWidth()
	{
		return characterWidth;
	}
	
	public boolean isUpperCase()
	{
		return isUpperCase;
	}
}