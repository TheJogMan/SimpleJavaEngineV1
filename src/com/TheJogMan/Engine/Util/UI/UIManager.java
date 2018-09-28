package com.TheJogMan.Engine.Util.UI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.TheJogMan.Engine.GameContainer;
import com.TheJogMan.Engine.gfx.Canvas;

public class UIManager
{
	public static GameContainer game;
	static List<AbstractUIComponent> components = new ArrayList<AbstractUIComponent>();
	
	public static void update()
	{
		List<Integer> removeIDs = new ArrayList<Integer>();
		for (Iterator<AbstractUIComponent> iterator = components.iterator(); iterator.hasNext();)
		{
			AbstractUIComponent component = iterator.next();
			int index = components.indexOf(component);
			
			component.run(game);
			
			if (component.isRemoved())
			{
				removeIDs.add(index);
			}
		}
		for (Iterator<Integer> iterator = removeIDs.iterator(); iterator.hasNext();)
		{
			components.remove(iterator.next().intValue());
		}
	}
	
	public static void draw(Canvas canvas)
	{
		for (Iterator<AbstractUIComponent> iterator = components.iterator(); iterator.hasNext();)
		{
			AbstractUIComponent component = iterator.next();
			
			component.draw(canvas, game);
		}
	}
}