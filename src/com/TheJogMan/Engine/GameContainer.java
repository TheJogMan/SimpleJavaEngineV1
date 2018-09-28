package com.TheJogMan.Engine;

import com.TheJogMan.Engine.Util.UI.UIManager;
import com.TheJogMan.Engine.gfx.Canvas;

public class GameContainer implements Runnable
{
	private Thread thread;
	private Window window;
	private Renderer renderer;
	private Input input;
	private AbstractGame game;
	
	private boolean running = false;
	private final double CYCLE_CAP = 1.0 / 60.0;
	private final double NANO_FACTOR = 1000000000.0;
	
	private int windowWidth = 800;
	private int windowHeight = 600;
	private int fps = 0;
	private float windowScale = 1F;
	private String windowTitle = "Game Engine";
	
	public GameContainer(AbstractGame game, String name, int width, int height)
	{
		this.game = game;
		windowTitle = name;
		windowWidth = width;
		windowHeight = height;
	}
	
	public GameContainer(AbstractGame game, String name)
	{
		this.game = game;
		windowTitle = name;
	}
	
	public GameContainer(AbstractGame game)
	{
		this.game = game;
	}
	
	public void start()
	{
		UIManager.game = this;
		
		window = new Window(this);
		renderer = new Renderer(this);
		input = new Input(this);
		
		thread = new Thread(this);
		thread.run();
	}
	
	public void stop()
	{
		
	}
	
	private void dispose()
	{
		
	}
	
	@Override
	public void run()
	{
		running = true;
		
		boolean render = false;
		double firstTime = 0;
		double lastTime = System.nanoTime() / NANO_FACTOR;
		double passedTime = 0;
		double unprocessedTime = 0;
		
		double frameTime = 0;
		int frames = 0;
		fps = 0;
		
		game.init(this);
		
		while (running)
		{
			render = false;
			firstTime = System.nanoTime() / NANO_FACTOR;
			passedTime = firstTime - lastTime;
			lastTime = firstTime;
			
			unprocessedTime += passedTime;
			frameTime += passedTime;
			
			while (unprocessedTime >= CYCLE_CAP)
			{
				unprocessedTime -= CYCLE_CAP;
				render = true;
				
				UIManager.update();
				game.update(this, (float)CYCLE_CAP);
				
				input.update();
				
				if (frameTime >= 1.0)
				{
					frameTime = 0;
					fps = frames;
					frames = 0;
				}
			}
			
			if (render)
			{
				Canvas canvas = renderer.getCanvas();
				if (renderer.doClear())
				{
					canvas.clear();
				}
				game.render(this, canvas, renderer);
				UIManager.draw(canvas);
				canvas.process();
				renderer.process();
				window.update();
				
				frames++;
			}
			else
			{
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		dispose();
	}
	
	public int getFPS()
	{
		return fps;
	}
	
	public Renderer getRenderer()
	{
		return renderer;
	}
	
	public int getWindowWidth()
	{
		return windowWidth;
	}
	
	public int getWindowHeight()
	{
		return windowHeight;
	}
	
	public float getWindowScale()
	{
		return windowScale;
	}
	
	public String getWindowTitle()
	{
		return windowTitle;
	}
	
	public Window getWindow()
	{
		return window;
	}
	
	public Input getInput()
	{
		return input;
	}
}