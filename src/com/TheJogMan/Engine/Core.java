package com.TheJogMan.Engine;

import com.TheJogMan.Engine.gfx.Canvas;

public abstract class Core
{
	
	
	public abstract void load(GameContainer game);
	public abstract void draw(Canvas canvas, GameContainer game);
	public abstract void update(GameContainer game);
}