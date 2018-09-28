package com.TheJogMan.Engine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Window
{
	private JFrame frame;
	private BufferedImage image;
	private Canvas canvas;
	private Graphics graphics;
	private BufferStrategy bufferStrategy;
	
	public Window(GameContainer game)
	{
		image = new BufferedImage(game.getWindowWidth(), game.getWindowHeight(), BufferedImage.TYPE_INT_RGB);
		canvas = new Canvas();
		Dimension dimension = new Dimension((int)(game.getWindowWidth() * game.getWindowScale()),(int)(game.getWindowHeight() * game.getWindowScale()));
		canvas.setPreferredSize(dimension);
		canvas.setMaximumSize(dimension);
		canvas.setMinimumSize(dimension);
		
		frame = new JFrame(game.getWindowTitle());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(canvas, BorderLayout.CENTER);
		frame.setAutoRequestFocus(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.toFront();
		
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();
		graphics = bufferStrategy.getDrawGraphics();
	}
	
	public void update()
	{
		graphics.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
		bufferStrategy.show();
	}
	
	public Canvas getCanvas()
	{
		return canvas;
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
	
	public JFrame getFrame()
	{
		return frame;
	}
}