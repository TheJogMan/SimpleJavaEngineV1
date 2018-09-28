package com.TheJogMan.Engine.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.TheJogMan.Engine.gfx.Font;

public class TextManagement
{
	static char[] wordSeperators = {' ','	'};
	static char[] spaceCharacters = {' ','	','\n'};
	
	public static List<String> format(String text)
	{
		List<String> lines = new ArrayList<String>();
		String currentLine = "";
		for (int c = 0; c < text.length(); c++)
		{
			char ch = text.charAt(c);
			if (ch == '\n')
			{
				lines.add(currentLine + "\n");
				currentLine = "";
			}
			else
			{
				currentLine = currentLine + ch;
			}
		}
		if (currentLine.length() > 0) lines.add(currentLine);
		if (lines.size() == 0) lines.add("");
		return lines;
	}
	
	public static List<String> formatToWidth(List<String> text, Font font, double scale, int width)
	{
		List<String> lines = new ArrayList<String>();
		for (Iterator<String> textIterator = text.iterator(); textIterator.hasNext();)
		{
			List<String> textLines = formatToWidth(textIterator.next(), font, scale, width);
			for (Iterator<String> lineIterator = textLines.iterator(); lineIterator.hasNext();)
			{
				lines.add(lineIterator.next());
			}
		}
		return lines;
	}
	
	public static List<String> formatToWidth(String text, Font font, double scale, int width)
	{
		List<String> lines = new ArrayList<String>();
		List<String> words = new ArrayList<String>();
		String currentWord = "";
		String currentLine = "";
		
		for (int c = 0; c < text.length(); c++)
		{
			char ch = text.charAt(c);
			boolean seperator = false;
			for (int a = 0; a < wordSeperators.length; a++)
			{
				if (wordSeperators[a] == ch)
				{
					seperator = true;
					break;
				}
			}
			if (seperator)
			{
				words.add(currentWord);
				words.add("" + ch);
				currentWord = "";
			}
			else
			{
				currentWord = currentWord + ch;
			}
		}
		if (currentWord.length() > 0) words.add(currentWord);
		
		if (words.size() > 0)
		{
			int currentIndex = 0;
			do
			{
				currentWord = words.get(currentIndex);
				if (font.getStringWidth(currentLine + currentWord) * scale <= width)
				{
					currentLine = currentLine + currentWord;
					currentIndex++;
				}
				else
				{
					if (currentLine.length() > 0)
					{
						lines.add(currentLine);
						currentLine = "";
					}
					else currentIndex++;
				}
			}
			while(currentIndex < words.size());
			if (currentLine.length() > 0) lines.add(currentLine);
		}
		
		return lines;
	}
	
	public static List<String> trimToHeight(List<String> lines, Font font, double scale, int height)
	{
		while (lines.size() * scale > height)
		{
			lines.remove(lines.size() - 1);
		}
		return lines;
	}
	
	public static List<String> trimSpace(List<String> lines)
	{
		for (Iterator<String> iterator = lines.iterator(); iterator.hasNext();)
		{
			String line = iterator.next();
			int index = lines.indexOf(line);
			boolean hadSpace;
			do
			{
				hadSpace = false;
				for (int c = 0; c < spaceCharacters.length; c++)
				{
					if (spaceCharacters[c] == line.charAt(0))
					{
						hadSpace = true;
						break;
					}
				}
				if (hadSpace) line = line.substring(1, line.length());
			}
			while (hadSpace);
			do
			{
				hadSpace = false;
				for (int c = 0; c < spaceCharacters.length; c++)
				{
					if (spaceCharacters[c] == line.charAt(line.length()))
					{
						hadSpace = true;
						break;
					}
				}
				if (hadSpace) line = line.substring(0, line.length() - 1);
			}
			while (hadSpace);
			lines.set(index, line);
		}
		return lines;
	}
}