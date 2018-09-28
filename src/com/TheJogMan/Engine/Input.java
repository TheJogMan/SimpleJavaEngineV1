package com.TheJogMan.Engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.TheJogMan.Engine.Util.EventListeners.AbstractTextEditEvent;
import com.TheJogMan.Engine.Util.EventListeners.AbstractTextInputEvent;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	private GameContainer game;
	
	private char[] upperChars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',')','!','@','#','$','%','^','&','*','(','>','<',':','"','{','}','|','+','_','~','?'};
	private char[] lowerChars = {' ','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','.',',',';','\'','[',']','\\','=','-','`','/'};
	
	private final int NUM_KEYS = 256;
	private boolean[] keys = new boolean[NUM_KEYS];
	private boolean[] keysLast = new boolean[NUM_KEYS];
	
	private final int NUM_BUTTONS = 5;
	private boolean[] buttons = new boolean[NUM_BUTTONS];
	private boolean[] buttonsLast = new boolean[NUM_BUTTONS];
	
	private int mouseX, mouseY;
	private int scroll;
	
	private List<AbstractTextInputEvent> textInputListeners = new ArrayList<AbstractTextInputEvent>();
	private List<AbstractTextEditEvent> textEditListeners = new ArrayList<AbstractTextEditEvent>();
	
	public Input(GameContainer game)
	{
		this.game = game;
		mouseX = 0;
		mouseY = 0;
		scroll = 0;
		
		game.getWindow().getCanvas().addKeyListener(this);
		game.getWindow().getCanvas().addMouseListener(this);
		game.getWindow().getCanvas().addMouseMotionListener(this);
		game.getWindow().getCanvas().addMouseWheelListener(this);
	}
	
	public void registerKeyPressEventListener(AbstractTextInputEvent listener)
	{
		textInputListeners.add(listener);
	}
	
	public void registerTextEditListener(AbstractTextEditEvent listener)
	{
		textEditListeners.add(listener);
	}
	
	public int getMouseX()
	{
		return mouseX;
	}
	
	public int getMouseY()
	{
		return mouseY;
	}
	
	public int getMouseScroll()
	{
		return scroll;
	}
	
	public boolean isButton(int buttonCode)
	{
		if (buttonCode >= 0 && buttonCode <= NUM_BUTTONS)
		{
			return buttons[buttonCode];
		}
		return false;
	}
	
	public boolean isButtonUp(int buttonCode)
	{
		if (buttonCode >= 0 && buttonCode <= NUM_BUTTONS)
		{
			return !buttons[buttonCode] && buttonsLast[buttonCode];
		}
		return false;
	}
	
	public boolean isButtonDown(int buttonCode)
	{
		if (buttonCode >= 0 && buttonCode <= NUM_BUTTONS)
		{
			return buttons[buttonCode] & !buttonsLast[buttonCode];
		}
		return false;
	}
	
	public boolean isKey(int keyCode)
	{
		if (keyCode >= 0 && keyCode <= NUM_KEYS)
		{
			return keys[keyCode];
		}
		return false;
	}
	
	public boolean isKeyUp(int keyCode)
	{
		if (keyCode >= 0 && keyCode <= NUM_KEYS)
		{
			return !keys[keyCode] && keysLast[keyCode];
		}
		return false;
	}
	
	public boolean isKeyDown(int keyCode)
	{
		if (keyCode >= 0 && keyCode <= NUM_KEYS)
		{
			return keys[keyCode] && !keysLast[keyCode];
		}
		return false;
	}
	
	public void update()
	{
		scroll = 0;
		
		for (int i = 1; i < NUM_KEYS; i++)
		{
			keysLast[i] = keys[i];
		}
		
		for (int i = 1; i < NUM_BUTTONS; i++)
		{
			buttonsLast[i] = buttons[i];
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event)
	{
		scroll = event.getWheelRotation();
	}

	@Override
	public void mouseDragged(MouseEvent event)
	{
		mouseX = (int)(event.getX() / game.getWindowScale());
		mouseY = (int)(event.getY() / game.getWindowScale());
	}

	@Override
	public void mouseMoved(MouseEvent event)
	{
		mouseX = (int)(event.getX() / game.getWindowScale());
		mouseY = (int)(event.getY() / game.getWindowScale());
	}

	@Override
	public void mouseClicked(MouseEvent event)
	{
		
	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		if (event.getButton() >= 0 && event.getButton() < buttons.length)
		{
			buttons[event.getButton()] = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
		if (event.getButton() >= 0 && event.getButton() < buttons.length)
		{
			buttons[event.getButton()] = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent event)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent event)
	{
		
	}

	@Override
	public void keyTyped(KeyEvent event)
	{
		char ch = event.getKeyChar();
		boolean isTextChar = false;
		for (int c = 0; c < upperChars.length; c++)
		{
			if (upperChars[c] == ch)
			{
				isTextChar = true;
			}
		}
		for (int c = 0; c < lowerChars.length; c++)
		{
			if (lowerChars[c] == ch)
			{
				isTextChar = true;
			}
		}
		if (isTextChar)
		{
			for (Iterator<AbstractTextInputEvent> iterator = textInputListeners.iterator(); iterator.hasNext();)
			{
				iterator.next().run(event);
			}
		}
		else
		{
			for (Iterator<AbstractTextEditEvent> iterator = textEditListeners.iterator(); iterator.hasNext();)
			{
				iterator.next().run(event);
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent event)
	{
		if (event.getKeyCode() >= 0 && event.getKeyCode() < keys.length)
		{
			keys[event.getKeyCode()] = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent event)
	{
		if (event.getKeyCode() >= 0 && event.getKeyCode() < keys.length)
		{
			keys[event.getKeyCode()] = false;
		}
	}
}