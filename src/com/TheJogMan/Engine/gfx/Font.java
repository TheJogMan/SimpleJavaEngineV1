package com.TheJogMan.Engine.gfx;

public class Font
{
	public static final Font STANDARD = new Font("/fonts/set1.png");
	
	private Image fontImage;
	private int[] upperOffsets;
	private int[] upperWidths;
	private int[] lowerOffsets;
	private int[] lowerWidths;
	private int verticalSeperator;
	private int characterHeight;
	private char[] upperChars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',')','!','@','#','$','%','^','&','*','(','>','<',':','"','{','}','|','+','_','~','?'};
	private char[] lowerChars = {' ','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','.',',',';','\'','[',']','\\','=','-','`','/'};
	
	public Font(String path)
	{
		fontImage = new Image(new ImageData(path));
		upperOffsets = new int[upperChars.length];
		upperWidths = new int[upperChars.length];
		lowerOffsets = new int[lowerChars.length];
		lowerWidths = new int[lowerChars.length];
		upperOffsets[0] = 0;
		lowerOffsets[0] = 0;
		
		int currentChar = 0;
		for (int i = 0; i < fontImage.getWidth(); i++)
		{
			if (fontImage.getPixel(i, 0) == 0xffff0000)
			{
				if (currentChar + 1 < upperOffsets.length)
				{
					upperOffsets[currentChar + 1] = i + 1;
				}
				upperWidths[currentChar] = i - 1 - upperOffsets[currentChar];
				currentChar++;
			}
		}
		upperWidths[upperWidths.length - 1] = fontImage.getWidth() - upperOffsets[upperOffsets.length - 1];
		
		for (int i = 0; i < fontImage.getHeight(); i++)
		{
			if (fontImage.getPixel(0, i) == 0xffff0000)
			{
				verticalSeperator = i;
				characterHeight = i;
				break;
			}
		}
		
		currentChar = 0;
		for (int i = 0; i < fontImage.getWidth(); i++)
		{
			if (fontImage.getPixel(i, verticalSeperator + 1) == 0xffff0000)
			{
				if (currentChar + 1 < lowerOffsets.length)
				{
					lowerOffsets[currentChar + 1] = i + 1;
				}
				lowerWidths[currentChar] = i - 1 - lowerOffsets[currentChar];
				currentChar++;
			}
		}
		lowerWidths[lowerWidths.length - 1] = fontImage.getWidth() - lowerOffsets[lowerOffsets.length - 1];
	}
	
	public int getStringWidth(String str)
	{
		int wid = 0;
		for (int index = 0; index < str.length(); index++)
		{
			wid += getCharacterWidth(str.charAt(index));
		}
		return wid;
	}
	
	public int getVerticalSeperator()
	{
		return verticalSeperator;
	}
	
	public int getCharacterHeight()
	{
		return characterHeight;
	}
	
	public Image getFontImage()
	{
		return fontImage;
	}
	
	public CharacterData getCharacterData(char character)
	{
		int offset = 0;
		int width = 0;
		boolean isUpperCase = true;
		boolean found = false;
		
		for (int i = 0; i < lowerChars.length; i++)
		{
			if (lowerChars[i] == character)
			{
				offset = lowerOffsets[i];
				width = lowerWidths[i];
				isUpperCase = false;
				found = true;
				break;
			}
		}
		if (isUpperCase)
		{
			for (int i = 0; i < upperChars.length; i++)
			{
				if (upperChars[i] == character)
				{
					offset = upperOffsets[i];
					width = upperWidths[i];
					found = true;
					break;
				}
			}
		}
		
		if (!found)
		{
			return getCharacterData(' ');
		}
		
		return new CharacterData(character, this, offset, isUpperCase, width);
	}
	
	public int getCharacterX(char character)
	{
		CharacterData data = getCharacterData(character);
		return data.getOffset();
	}
	
	public int getCharacterWidth(char character)
	{
		if (character == '	')
		{
			CharacterData data = getCharacterData(' ');
			return data.getWidth() * 3;
		}
		else
		{
			CharacterData data = getCharacterData(character);
			return data.getWidth();
		}
	}
	
	public int getCharacterY(char character)
	{
		CharacterData data = getCharacterData(character);
		if (data.isUpperCase())
		{
			return 0;
		}
		else
		{
			return verticalSeperator + 1;
		}
	}
}