package com.TheJogMan.Engine.Util.UI.Components;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.TheJogMan.Engine.GameContainer;
import com.TheJogMan.Engine.Util.TextManagement;
import com.TheJogMan.Engine.Util.EventListeners.AbstractTextEditEvent;
import com.TheJogMan.Engine.Util.EventListeners.AbstractTextInputEvent;
import com.TheJogMan.Engine.Util.UI.AbstractUIComponent;
import com.TheJogMan.Engine.Util.UI.UIManager;
import com.TheJogMan.Engine.gfx.Canvas;
import com.TheJogMan.Engine.gfx.Font;

public abstract class TextField extends AbstractUIComponent
{
	//Trigger enums
	public static enum TextChangeTrigger
	{
		Unknown, TextEntered, TextDeleted, SelectionDeleted, TextPasted, ChangeUndone, TextSet;
	}
	
	//Events
	abstract class TextFieldEvent
	{
		boolean cancelled = false;
		TextField textField;
		
		public TextFieldEvent(TextField textField)
		{
			this.textField = textField;
		}
		
		public boolean isCancelled() {return cancelled;}
		public void setCancelled(boolean cancelled) {this.cancelled = cancelled;}
	}
	class TextChangeEvent extends TextFieldEvent
	{
		TextChangeTrigger trigger = TextChangeTrigger.Unknown;
		String newText;
		
		public TextChangeEvent(TextField textField, String newText, TextChangeTrigger trigger)
		{
			super(textField);
			this.newText = newText;
			if (trigger != null)
			{
				this.trigger = trigger;
			}
		}
		
		public String getNewText()
		{
			return newText;
		}
		
		public TextChangeTrigger getTrigger()
		{
			return trigger;
		}
	}
	
	//Listeners
	abstract class TextChangeEventListener
	{
		public abstract void run(TextChangeEvent event);
	}
	
	//Input Event Listeners
	class KeyPressEvent extends AbstractTextInputEvent
	{
		@Override
		public void run(KeyEvent event)
		{
			if (selected)
			{
				addText("" + event.getKeyChar(), TextChangeTrigger.TextEntered);
			}
		}
	}
	class TextEditEvent extends AbstractTextEditEvent
	{
		@Override
		public void run(KeyEvent event)
		{
			if (selected)
			{
				switch(KeyEvent.getExtendedKeyCodeForChar(event.getKeyChar()))
				{
					case KeyEvent.VK_BACK_SPACE:
					{
						doBackspace();
						break;
					}
					case KeyEvent.VK_DELETE:
					{
						doDelete();
						break;
					}
					case KeyEvent.VK_ENTER:
					{
						addText("\n", TextChangeTrigger.TextEntered);
						break;
					}
					default:
					{
						//nothing
					}
				}
			}
		}
	}
	
	boolean selected = false;
	boolean selectionActive = false;
	boolean allowMultiLine = true;
	boolean leftWasDown = false;
	boolean rightWasDown = false;
	boolean upWasDown = false;
	boolean downWasDown = false;
	boolean lineWrap = false;
	boolean verticalScrolling = true;
	boolean horizontalScrolling = true;
	boolean mouseInTextRegion = true;
	int cursorPos = 0;
	int selectionStart = 0;
	int selectionLength = 0;
	int cursorFlash = 60;
	int leftDownTime = 0;
	int rightDownTime = 0;
	int upDownTime = 0;
	int downDownTime = 0;
	int downTime = 30;
	int textCanvasX = 0;
	int textCanvasY = 0;
	int horizontalScroll = 0;
	int verticalScroll = 0;
	int hoveredPosition = -1;
	int lastClickPosition = -1;
	int lastCursorXMoveResult = -1;
	Canvas textCanvas;
	String text = "";
	String[] changeHistory = new String[50];
	
	List<TextChangeEventListener> textChangeEvents = new ArrayList<TextChangeEventListener>();
	
	public abstract void textFieldUpdate(GameContainer game);
	public abstract void textFieldPreDraw(Canvas canvas, GameContainer game, Canvas textCanvas);
	public abstract void textFieldPostDraw(Canvas canvas, GameContainer game);
	
	public TextField(int x, int y, int width, int height, int textCanvasX, int textCanvasY, int textCanvasWidth, int textCanvasHeight)
	{
		super(x, y, width, height);
		this.textCanvasX = textCanvasX;
		this.textCanvasY = textCanvasY;
		textCanvas = new Canvas(textCanvasWidth, textCanvasHeight);
		textCanvas.setBackgroundColor(Color.WHITE);
		UIManager.game.getInput().registerKeyPressEventListener(new KeyPressEvent());
		UIManager.game.getInput().registerTextEditListener(new TextEditEvent());
	}
	
	public void registerTextChangeEventListener(TextChangeEventListener listener)
	{
		textChangeEvents.add(listener);
	}
	
	@Override
	public void render(Canvas canvas, GameContainer game)
	{
		textCanvas.clear();
		List<String> lines = TextManagement.format(text);
		if (lineWrap)
		{
			lines = TextManagement.formatToWidth(lines, textCanvas.getFont(), textCanvas.getFontScale(), textCanvas.getWidth());
		}
		int dx = 0;
		int dy = 0;
		if (verticalScrolling) dy -= verticalScroll;
		if (horizontalScrolling) dx -= horizontalScroll;
		int currentChar = 0;
		int charHeight = (int)(textCanvas.getFont().getCharacterHeight() * textCanvas.getFontScale());
		int mouseX = game.getInput().getMouseX() - getX() - textCanvasX;
		int mouseY = game.getInput().getMouseY() - getY() - textCanvasY;
		int cursorRenderX = 0;
		int cursorRenderY = 0;
		hoveredPosition = -1;
		int widestLineLength = 0;
		for (Iterator<String> iterator = lines.iterator(); iterator.hasNext();)
		{
			String line = iterator.next();
			int lineWidth = (int)(textCanvas.getFont().getStringWidth(line) * textCanvas.getFontScale());
			if (lineWidth > widestLineLength)
			{
				widestLineLength = lineWidth;
			}
			int charX = dx;
			for (int c = 0; c < line.length(); c++)
			{
				char ch = line.charAt(c);
				int charWidth = (int)(textCanvas.getFont().getCharacterWidth(ch) * textCanvas.getFontScale());
				if (inSelection(currentChar))
				{
					textCanvas.drawRect(charX, dy, charWidth, charHeight, Color.CYAN);
					cursorRenderX = charX;
					cursorRenderY = dy;
				}
				if (cursorPos == currentChar && selected && cursorFlash > 30)
				{
					textCanvas.drawLine(charX, dy, charX, dy + charHeight, Color.BLACK);
					cursorRenderX = charX;
					cursorRenderY = dy;
				}
				if (mouseX >= charX && mouseY >= dy)
				{
					if (c == line.length() - 1)
					{
						if (mouseY < dy + charHeight)
						{
							hoveredPosition = currentChar;
						}
					}
					else
					{
						if (mouseX < charX + charWidth)
						{
							if (!iterator.hasNext())
							{
								hoveredPosition = currentChar;
							}
							else
							{
								if (mouseY < dy + charHeight)
								{
									hoveredPosition = currentChar;
								}
							}
						}
					}
				}
				textCanvas.drawText("" + ch, charX, dy, Color.BLACK);
				charX += charWidth;
				currentChar++;
			}
			if (!iterator.hasNext())
			{
				if (mouseY > dy && mouseX > charX)
				{
					hoveredPosition = text.length() + 1;
				}
				if (currentChar == cursorPos && selected && cursorFlash > 30 && line.length() > 0)
				{
					int cursorX = charX;
					int cursorY = dy;
					if (line.charAt(line.length() - 1) == '\n')
					{
						cursorX = dx;
						cursorY += charHeight;
					}
					textCanvas.drawLine(cursorX, cursorY, cursorX, cursorY + charHeight, Color.BLACK);
					cursorRenderX = cursorX;
					cursorRenderY = cursorY;
				}
			}
			dy = dy + charHeight;
		}
		if (selected && cursorFlash > 30 && cursorPos == 0)
		{
			textCanvas.drawLine(-horizontalScroll, -verticalScroll, 0, (int)(textCanvas.getFont().getCharacterHeight() * textCanvas.getFontScale()), Color.BLACK);
			cursorRenderX = -horizontalScroll;
			cursorRenderY = -verticalScroll;
		}
		
		textFieldPreDraw(canvas, game, textCanvas);
		canvas.draw(textCanvas, textCanvasX, textCanvasY);
		textFieldPostDraw(canvas, game);
		
		if (cursorRenderX < 0)
		{
			horizontalScroll -= -cursorRenderX;
		}
		else if (cursorRenderX > width - 5)
		{
			horizontalScroll += cursorRenderX - (width - 5);
		}
		if (horizontalScroll < 0)
		{
			horizontalScroll = 0;
		}
		if (widestLineLength < width - 5)
		{
			horizontalScroll = 0;
		}
		
		if (cursorRenderY < 0)
		{
			verticalScroll -= (int)(textCanvas.getFont().getCharacterHeight() * textCanvas.getFontScale());
		}
		else if (cursorRenderY > height - (int)(textCanvas.getFont().getCharacterHeight() * textCanvas.getFontScale()) - 3)
		{
			verticalScroll += cursorRenderY - (height - (int)(textCanvas.getFont().getCharacterHeight() * textCanvas.getFontScale()) - 3);
		}
		if (verticalScroll < 0)
		{
			verticalScroll = 0;
		}
		if (lines.size() * (int)(textCanvas.getFont().getCharacterHeight() * textCanvas.getFontScale()) < height)
		{
			verticalScroll = 0;
		}
	}
	
	@Override
	public void update(GameContainer game)
	{
		int mouseX = game.getInput().getMouseX() - getX() - textCanvasX;
		int mouseY = game.getInput().getMouseY() - getY() - textCanvasY;
		if (mouseX >= 0 && mouseX < textCanvas.getWidth() && mouseY >= 0 && mouseY < textCanvas.getHeight())
		{
			mouseInTextRegion = true;
		}
		else
		{
			mouseInTextRegion = false;
		}
		if (clicked())
		{
			selected = true;
			cursorFlash = 60;
		}
		if (game.getInput().isButtonDown(MouseEvent.BUTTON1))
		{
			lastClickPosition = -1;
			if (!hovered())
			{
				selected = false;
			}
			else
			{
				if (hoveredPosition >= 0)
				{
					lastClickPosition = hoveredPosition;
					cursorPos = hoveredPosition;
					
					//figure out the x position of the cursor
					List<String> lines = TextManagement.format(text);
					if (lineWrap)
					{
						lines = TextManagement.formatToWidth(lines, textCanvas.getFont(), textCanvas.getFontScale(), textCanvas.getWidth());
					}
					int currentX = 0;
					int charsPassed = 0;
					for (Iterator<String> lineIterator = lines.iterator(); lineIterator.hasNext();)
					{
						String line = lineIterator.next();
						if (cursorPos >= charsPassed && cursorPos < charsPassed + line.length())
						{
							currentX = cursorPos - charsPassed;
							break;
						}
						charsPassed += line.length();
					}
					
					
					lastCursorXMoveResult = currentX;
					selectionActive = false;
				}
			}
		}
		
		int prePos = cursorPos;
		if (selected)
		{
			if (game.getInput().isButton(MouseEvent.BUTTON1))
			{
				if (hoveredPosition >= 0 && lastClickPosition >= 0)
				{
					cursorPos = hoveredPosition;
					if (lastClickPosition == hoveredPosition)
					{
						selectionActive = false;
					}
					else
					{
						selectionActive = true;
						selectionStart = lastClickPosition;
						selectionLength = hoveredPosition - lastClickPosition;
					}
				}
			}
			cursorFlash--;
			if (cursorFlash < 0)
			{
				cursorFlash = 60;
			}
		
			if (game.getInput().isKey(KeyEvent.VK_LEFT))
			{
				if (!leftWasDown || leftDownTime > downTime)
				{
					moveCursor(-1, 0, game.getInput().isKey(KeyEvent.VK_SHIFT));
				}
				else
				{
					leftDownTime++;
				}
				leftWasDown = true;
			}
			else
			{
				leftWasDown = false;
				leftDownTime = 0;
			}
			if (game.getInput().isKey(KeyEvent.VK_RIGHT))
			{
				if (!rightWasDown || rightDownTime > downTime)
				{
					moveCursor(1, 0, game.getInput().isKey(KeyEvent.VK_SHIFT));
				}
				else
				{
					rightDownTime++;
				}
				rightWasDown = true;
			}
			else
			{
				rightWasDown = false;
				rightDownTime = 0;
			}
			if (game.getInput().isKey(KeyEvent.VK_UP))
			{
				if (!upWasDown || upDownTime > downTime)
				{
					moveCursor(0, -1, game.getInput().isKey(KeyEvent.VK_SHIFT));
				}
				else
				{
					upDownTime++;
				}
				upWasDown = true;
			}
			else
			{
				upWasDown = false;
				upDownTime = 0;
			}if (game.getInput().isKey(KeyEvent.VK_DOWN))
			{
				if (!downWasDown || downDownTime > downTime)
				{
					moveCursor(0, 1, game.getInput().isKey(KeyEvent.VK_SHIFT));
				}
				else
				{
					downDownTime++;
				}
				downWasDown = true;
			}
			else
			{
				downWasDown = false;
				downDownTime = 0;
			}
			
			if (cursorPos < 0)
			{
				cursorPos = 0;
			}
			else if (cursorPos > text.length())
			{
				cursorPos = text.length();
			}
			
			if (game.getInput().isKey(KeyEvent.VK_CONTROL))
			{
				if (game.getInput().isKeyDown(KeyEvent.VK_V))
				{
					try
					{
						addText((String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor), TextChangeTrigger.TextPasted);
					}
					catch (HeadlessException | UnsupportedFlavorException | IOException e)
					{
						//e.printStackTrace();
					}
				}
				if (game.getInput().isKeyDown(KeyEvent.VK_C))
				{
					StringSelection selection = new StringSelection(getSelectedText());
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
				}
				if (game.getInput().isKeyDown(KeyEvent.VK_A))
				{
					selectionActive = true;
					selectionStart = 0;
					selectionLength = text.length();
				}
				if (game.getInput().isKeyDown(KeyEvent.VK_Z))
				{
					undo();
				}
			}
		}
		
		textFieldUpdate(game);
		if (cursorPos < 0)
		{
			cursorPos = 0;
		}
		else if (cursorPos > text.length())
		{
			cursorPos = text.length();
		}
		if (cursorPos != prePos)
		{
			cursorFlash = 60;
		}
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setFont(Font font)
	{
		textCanvas.setFont(font);
	}
	
	public void setFontScale(double scale)
	{
		textCanvas.setFontScale(scale);
	}
	
	public Font getFont()
	{
		return textCanvas.getFont();
	}
	
	public double getFontScale()
	{
		return textCanvas.getFontScale();
	}
	
	public void doBackspace()
	{
		int newPos = cursorPos;
		String newText = text;
		
		if (selectionActive)
		{
			newText = getWithoutSelectedText();
			if (selectionLength > 0)
			{
				newPos -= getSelectedText().length();
			}
			selectionActive = false;
		}
		else
		{
			if (newText.length() == 1 && cursorPos == 1)
			{
				newText = "";
				newPos = 0;
			}
			else
			{
				if (newPos > 0)
				{
					if (newPos == newText.length())
					{
						newText = newText.substring(0, newPos - 1);
					}
					else
					{
						if (newPos == 1)
						{
							newText = newText.substring(1, newText.length());
						}
						else
						{
							newText = newText.substring(0, newPos - 1) + newText.substring(newPos, newText.length());
						}
					}
					newPos--;
				}
			}
		}
		
		TextChangeEvent textEvent = new TextChangeEvent(this, newText, TextChangeTrigger.TextDeleted);
		for (Iterator<TextChangeEventListener> iterator = textChangeEvents.iterator(); iterator.hasNext();)
		{
			iterator.next().run(textEvent);
		}
		if (!textEvent.isCancelled())
		{
			addToHistory(text);
			text = newText;
			if (newPos < 0)
			{
				newPos = 0;
			}
			else if (newPos > text.length())
			{
				newPos = text.length();
			}
			if (newPos != cursorPos)
			{
				cursorPos = newPos;
				cursorFlash = 60;
			}
		}
	}
	
	public void doDelete()
	{
		String newText = text;
		
		if (selectionActive)
		{
			newText = getWithoutSelectedText();
			if (selectionLength > 0)
			{
				cursorPos -= selectionLength;
				cursorFlash = 60;
			}
			selectionActive = false;
		}
		else
		{
			if (newText.length() == 1 && cursorPos == 0)
			{
				newText = "";
			}
			else
			{
				if (cursorPos < newText.length())
				{
					if (cursorPos == 0)
					{
						newText = newText.substring(1, newText.length());
					}
					else
					{
						if (cursorPos == newText.length() - 1)
						{
							newText = newText.substring(0, newText.length() - 1);
						}
						else
						{
							newText = newText.substring(0, cursorPos) + newText.substring(cursorPos + 1, newText.length());
						}
					}
				}
			}
		}
		
		TextChangeEvent textEvent = new TextChangeEvent(this, newText, TextChangeTrigger.TextDeleted);
		for (Iterator<TextChangeEventListener> iterator = textChangeEvents.iterator(); iterator.hasNext();)
		{
			iterator.next().run(textEvent);
		}
		if (!textEvent.isCancelled())
		{
			addToHistory(text);
			text = newText;
		}
	}
	
	public void setText(String text)
	{
		setText(text, true);
	}
	
	public void setText(String text, boolean recordToHistory)
	{
		setText(text, recordToHistory, TextChangeTrigger.TextSet);
	}
	
	public void setText(String text, boolean recordToHistory, TextChangeTrigger trigger)
	{
		TextChangeEvent event = new TextChangeEvent(this, text, trigger);
		for (Iterator<TextChangeEventListener> iterator = textChangeEvents.iterator(); iterator.hasNext();)
		{
			iterator.next().run(event);
		}
		if (!event.isCancelled())
		{
			if (recordToHistory) addToHistory(this.text);
			this.text = text;
		}
	}
	
	public String getSelectedText()
	{
		if (selectionActive)
		{
			String newText = "";
			for (int c = 0; c < text.length(); c++)
			{
				if (inSelection(c))
				{
					newText = newText + text.charAt(c);
				}
			}
			return newText;
		}
		return "";
	}
	
	public String getWithoutSelectedText()
	{
		if (selectionActive)
		{
			String newText = "";
			for (int c = 0; c < text.length(); c++)
			{
				if (!inSelection(c))
				{
					newText = newText + text.charAt(c);
				}
			}
			return newText;
		}
		return text;
	}
	
	public boolean inSelection(int position)
	{
		if (selectionActive)
		{
			if (selectionLength > 0)
			{
				if (position >= selectionStart && position < selectionStart + selectionLength)
				{
					return true;
				}
			}
			else
			{
				if (position >= selectionStart + selectionLength && position < selectionStart)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public void addText(String text, TextChangeTrigger trigger)
	{
		if (trigger != null)
		{
			trigger = TextChangeTrigger.Unknown;
		}
		
		if (!allowMultiLine)
		{
			String newText = "";
			for (int c = 0; c < text.length(); c++)
			{
				if (text.charAt(c) != '\n') newText = newText + text.charAt(c);
			}
			text = newText;
			if (text.length() < 1) return;
		}
		
		String newText = this.text;
		int newPos = cursorPos;
		boolean selectionNowActive = selectionActive;
		
		if (selectionActive)
		{
			newText = getWithoutSelectedText();
			if (selectionLength > 0) newPos = selectionStart - 1;
			selectionNowActive = false;
		}
		
		if (newText.length() == 0)
		{
			newText = text;
			newPos++;
		}
		else
		{
			if (newPos == 0)
			{
				newText = text + newText;
			}
			else if (newPos == newText.length())
			{
				newText = newText + text;
			}
			else
			{
				newText = newText.substring(0, newPos) + text + newText.substring(newPos, newText.length());
			}
		}
		newPos = newPos + text.length();
		
		TextChangeEvent event = new TextChangeEvent(this, newText, trigger);
		for (Iterator<TextChangeEventListener> iterator = textChangeEvents.iterator(); iterator.hasNext();)
		{
			iterator.next().run(event);
		}
		if (!event.isCancelled())
		{
			addToHistory(this.text);
			this.text = newText;
			cursorPos = newPos;
			selectionActive = selectionNowActive;
			cursorFlash = 60;
		}
	}
	
	public void addToHistory(String text)
	{
		for (int c = changeHistory.length - 2; c >= 0; c--)
		{
			changeHistory[c + 1] = changeHistory[c];
		}
		changeHistory[0] = text;
	}
	
	public void undo()
	{
		if (changeHistory[0] != null)
		{
			setText(changeHistory[0], false, TextChangeTrigger.ChangeUndone);
			for (int c = 1; c < changeHistory.length; c++)
			{
				changeHistory[c - 1] = changeHistory[c];
			}
			changeHistory[changeHistory.length - 1] = null;
		}
	}
	
	public void moveCursor(int moveX, int moveY, boolean shifted)
	{
		boolean setXMoveResult = (moveX != 0);
		List<String> lines = TextManagement.format(text);
		if (lineWrap)
		{
			lines = TextManagement.formatToWidth(lines, textCanvas.getFont(), textCanvas.getFontScale(), textCanvas.getWidth());
		}
		int currentY = 0;
		int currentX = 0;
		int charsPassed = 0;
		int[] lineLengths = new int[lines.size()];
		int lineCounter = 0;
		for (Iterator<String> lineIterator = lines.iterator(); lineIterator.hasNext();)
		{
			lineLengths[lineCounter] = lineIterator.next().length();
			lineCounter++;
		}
		for (Iterator<String> lineIterator = lines.iterator(); lineIterator.hasNext();)
		{
			String line = lineIterator.next();
			if (cursorPos >= charsPassed && cursorPos < charsPassed + line.length())
			{
				currentX = cursorPos - charsPassed;
				break;
			}
			charsPassed += line.length();
			currentY++;
		}
		if (currentY == lineLengths.length)
		{
			currentY--;
			currentX = lineLengths[currentY];
		}
		int prePosition = cursorPos;
		while (moveY != 0)
		{
			if (moveY < 0)
			{
				currentY--;
				moveY++;
			}
			else if (moveY > 0)
			{
				currentY++;
				moveY--;
			}
			
			if (currentY < 0)
			{
				currentY = 0;
				currentX = 0;
				moveY = 0;
			}
			else if (currentY >= lineLengths.length)
			{
				currentY = lineLengths.length - 1;
				currentX = lineLengths[currentY];
				moveY = 0;
			}
			else
			{
				if (lastCursorXMoveResult >= 0)
				{
					currentX = lastCursorXMoveResult;
				}
				if (lineLengths[currentY] <= currentX)
				{
					currentX = lineLengths[currentY] - 1;
					if (currentY == lineLengths.length - 1)
					{
						currentX++;
					}
				}
			}
		}
		
		while (moveX != 0)
		{
			if (moveX > 0)
			{
				currentX++;
				moveX--;
			}
			else if (moveX < 0)
			{
				currentX--;
				moveX++;
			}
			if (currentX < 0)
			{
				if (currentY > 0)
				{
					currentY--;
					currentX = lineLengths[currentY] - 1;
				}
				else
				{
					currentX = 0;
					moveX = 0;
				}
			}
			else if (currentX > lineLengths[currentY] - 1)
			{
				if (currentY < lineLengths.length - 1)
				{
					currentY++;
					currentX = 0;
				}
				else
				{
					currentX = lineLengths[currentY];
					moveX = 0;
				}
			}
		}
		
		if (setXMoveResult)
		{
			lastCursorXMoveResult = currentX;
		}
		
		int newPosition = currentX;
		for (int c = 0; c < currentY; c++)
		{
			newPosition += lineLengths[c];
		}
		
		if (newPosition != prePosition)
		{
			cursorPos = newPosition;
			cursorFlash = 60;
			
			if (shifted)
			{
				int change = newPosition - prePosition;
				if (selectionActive)
				{
					selectionLength += change;
				}
				else
				{
					selectionActive = true;
					selectionStart = prePosition;
					selectionLength = change;
				}
			}
			else
			{
				selectionActive = false;
			}
		}
	}
}