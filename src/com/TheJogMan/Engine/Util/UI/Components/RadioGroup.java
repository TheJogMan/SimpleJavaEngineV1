package com.TheJogMan.Engine.Util.UI.Components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.TheJogMan.Engine.GameContainer;
import com.TheJogMan.Engine.Util.UI.AbstractUIComponent;
import com.TheJogMan.Engine.gfx.Canvas;
import com.TheJogMan.Engine.gfx.Font;

public class RadioGroup extends AbstractUIComponent
{
	List<String> buttons = new ArrayList<String>();
	String selection;
	String label;
	String hovered;
	String lastSelection;
	
	public RadioGroup(int x, int y, int width, int height, String label, List<String> buttons, String def)
	{
		super(x, y, width, height);
		this.label = label;
		this.buttons = buttons;
		selection = def;
		canvas.setBackgroundColor((new Color(60, 60, 60)).getRGB());
	}
	
	public void setSelection(String selection)
	{
		this.selection = selection;
	}
	
	public String getSelection()
	{
		return selection;
	}
	
	public void addOption(String option)
	{
		buttons.add(option);
	}
	
	public String getHovered()
	{
		return hovered;
	}
	
	public boolean selectionChanged()
	{
		if (lastSelection != null)
		{
			return !(lastSelection.compareTo(selection) == 0);
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public void update(GameContainer game)
	{
		lastSelection = selection;
		if (clicked())
		{
			if (hovered.length() > 0)
			{
				selection = hovered;
			}
		}
	}
	
	@Override
	public void render(Canvas canvas, GameContainer game)
	{
		int mouseX = game.getInput().getMouseX() - x;
		int mouseY = game.getInput().getMouseY() - y;
		canvas.drawText(label, 0, 0);
		int wid = 20;
		Font font = canvas.getFont();
		int y = font.getCharacterHeight();
		int x = 0;
		int boxSize = font.getCharacterHeight() - 4;
		hovered = "";
		for (Iterator<String> iterator = buttons.iterator(); iterator.hasNext();)
		{
			String button = iterator.next();
			int strWid = font.getStringWidth(button);
			if (strWid > wid)
			{
				wid = strWid;
			}
			if (mouseX >= x && mouseX < x + strWid + boxSize + 4 && mouseY >= y && mouseY < y + font.getCharacterHeight())
			{
				hovered = button;
			}
			if (hovered.compareTo(button) == 0)
			{
				canvas.drawRect(x, y, strWid + boxSize + 4, font.getCharacterHeight(), Color.GRAY);
			}
			canvas.drawText(button, x + boxSize + 2, y);
			canvas.drawRect(x + 2, y + 2, boxSize, boxSize);
			if (selection.compareTo(button) == 0)
			{
				canvas.drawRect(x + 3, y + 3, boxSize - 2, boxSize - 2, Color.BLACK);
			}
			y += font.getCharacterHeight();
			if (y + font.getCharacterHeight() > height)
			{
				x += wid + 12 + boxSize;
				y = font.getCharacterHeight();
			}
		}
	}
}