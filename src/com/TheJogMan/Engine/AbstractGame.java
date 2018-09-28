package com.TheJogMan.Engine;

import com.TheJogMan.Engine.gfx.Canvas;

public abstract interface AbstractGame
{
	public abstract void init(GameContainer game);
	public abstract void render(GameContainer game, Canvas canvas, Renderer renderer);
	public abstract void update(GameContainer game, float deltaTime);
}