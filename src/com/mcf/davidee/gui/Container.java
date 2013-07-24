package com.mcf.davidee.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.util.MathHelper;

import com.mcf.davidee.gui.Scrollbar.Shiftable;
import com.mcf.davidee.gui.focusable.FocusableWidget;

public class Container {

	protected List<FocusableWidget> focusList;
	protected List<Widget> widgets;

	protected int left, right, top, bottom, shiftAmount, extraScrollHeight;
	protected int cHeight, focusIndex;
	protected Scrollbar scrollbar;
	protected Widget lastSelected;

	public Container(Scrollbar scrollbar, int shiftAmount, int extraScrollHeight) {
		this.scrollbar = scrollbar;
		this.shiftAmount = shiftAmount;
		this.extraScrollHeight = extraScrollHeight;
		this.widgets = new ArrayList<Widget>();
		this.focusList = new ArrayList<FocusableWidget>();
		this.focusIndex = -1;

		if (scrollbar != null)
			scrollbar.setContainer(this);
	}

	public Container() {
		this(null, 0, 0);
	}

	public void revalidate(int x, int y, int width, int height) {
		this.left = x;
		this.right = x + width;
		this.top = y;
		this.bottom = y + height;
		calculateContentHeight();
		if (scrollbar != null)
			scrollbar.revalidate(top, bottom);
	}

	public List<Widget> getWidgets() {
		return widgets;
	}

	public List<FocusableWidget> getFocusableWidgets() {
		return focusList;
	}

	public void addWidgets(Widget... arr) {
		for (Widget w : arr) {
			widgets.add(w);
			if (w instanceof FocusableWidget)
				focusList.add((FocusableWidget) w);
		}
		calculateContentHeight();
	}

	private void calculateContentHeight() {
		int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

		for (Widget w : widgets) {
			if (w instanceof Shiftable) {
				if (w.y < minY)
					minY = w.y;
				if (w.y + w.height > maxY)
					maxY = w.y + w.height;
			}
		}
		cHeight = (minY > maxY) ? 0 : maxY - minY + 1 + extraScrollHeight;
	}

	public int getContentHeight() {
		return cHeight;
	}

	public void update() {
		for (Widget w : widgets)
			w.update();
	}

	public List<Widget> draw(int mx, int my) {
		List<Widget> overlays = new ArrayList<Widget>();

		for (Widget w : widgets) {
			if (w.shouldRender(top, bottom)) {
				w.draw(mx, my);
				overlays.addAll(w.getTooltips());
			}
		}

		if (scrollbar != null && scrollbar.shouldRender(top, bottom))
			scrollbar.draw(mx, my);

		return overlays;
	}

	public void setFocused(FocusableWidget f) {
		int newIndex = (f == null) ? -1 : focusList.indexOf(f);
		if (focusIndex != newIndex) {
			if (focusIndex != -1)
				focusList.get(focusIndex).focusLost();
			if (newIndex != -1)
				focusList.get(newIndex).focusGained();

			focusIndex = newIndex;
		}
	}

	public boolean mouseClicked(int mx, int my) {
		if (mx >= left && my >= top && mx < right && my < bottom) {
			boolean resetFocus = true;

			if (scrollbar != null && scrollbar.shouldRender(top, bottom)
					&& scrollbar.inBounds(mx, my))
				return true;

			for (Widget w : widgets) {
				if (w.shouldRender(top, bottom) && w.click(mx, my)) {
					lastSelected = w;
					if (w instanceof FocusableWidget) {
						setFocused((FocusableWidget) w);
						resetFocus = false;
					}
					w.handleClick(mx, my);
					break;
				}
			}
			if (resetFocus)
				setFocused(null);
			return true;
		}
		return false;
	}

	public FocusableWidget deleteFocused() {
		if (hasFocusedWidget()) {
			FocusableWidget w = getFocusedWidget();
			if (lastSelected == w)
				lastSelected = null;
			focusList.remove(focusIndex);
			if (focusList.size() == 0)
				focusIndex = -1;
			else {
				focusIndex = MathHelper.clamp_int(focusIndex, 0, focusList.size() - 1);
				focusList.get(focusIndex).focusGained();
			}

			int index = widgets.indexOf(w), offset = Integer.MAX_VALUE;
			for (int i = index + 1; i < widgets.size(); ++i) {
				Widget cur = widgets.get(i);
				if (cur instanceof Shiftable) {
					if (offset == Integer.MAX_VALUE)
						offset = w.getY() - cur.getY();
					((Shiftable) cur).shiftY(offset);
				}
			}
			widgets.remove(w);
			calculateContentHeight();
			if (scrollbar != null)
				scrollbar.onChildRemoved();

			return w;
		}
		return null;
	}

	public void removeFocusableWidgets() {
		focusIndex = -1;
		if (lastSelected instanceof FocusableWidget)
			lastSelected = null;
		
		widgets.removeAll(focusList);
		focusList.clear();
		
		calculateContentHeight();
		if (scrollbar != null)
			scrollbar.onChildRemoved();
	}

	public void mouseReleased(int mx, int my) {
		if (lastSelected != null) {
			lastSelected.mouseReleased(mx, my);
			lastSelected = null;
		}
	}

	public boolean hasFocusedWidget() {
		return focusIndex != -1;
	}

	public FocusableWidget getFocusedWidget() {
		return focusList.get(focusIndex);
	}

	public boolean keyTyped(char c, int code) {
		boolean handled = (focusIndex != -1) ? focusList.get(focusIndex)
				.keyTyped(c, code) : false;
				if (!handled) {
					switch (code) {
					case Keyboard.KEY_UP:
						shift(-1);
						handled = true;
						break;
					case Keyboard.KEY_DOWN:
						shift(1);
						handled = true;
						break;
					case Keyboard.KEY_TAB:
						shiftFocusToNext();
						handled = true;
						break;
					}
				}
				return handled;
	}

	protected void shiftFocusToNext() {
		if (focusIndex != -1 && focusList.size() > 1) {
			int newIndex = (focusIndex + 1) % focusList.size();
			if (newIndex != focusIndex) {
				focusList.get(focusIndex).focusLost();
				focusList.get(newIndex).focusGained();
				if (scrollbar != null && scrollbar.shouldRender(top, bottom))
					scrollbar.shift((focusIndex - newIndex) * shiftAmount);
				focusIndex = newIndex;
			}
		}
	}

	protected void shiftFocus(int newIndex) {
		if (focusIndex != newIndex) {
			focusList.get(focusIndex).focusLost();
			focusList.get(newIndex).focusGained();
			if (scrollbar != null && scrollbar.shouldRender(top, bottom))
				scrollbar.shift((focusIndex - newIndex) * shiftAmount);
			focusIndex = newIndex;
		}
	}

	protected void shift(int delta) {
		if (focusIndex != -1)
			shiftFocus(MathHelper.clamp_int(focusIndex + delta, 0,
					focusList.size() - 1));
		else if (scrollbar != null && scrollbar.shouldRender(top, bottom))
			scrollbar.shift(delta * 4);
	}

	public void mouseWheel(int delta) {
		if (scrollbar != null && scrollbar.shouldRender(top, bottom))
			scrollbar.shift(delta);
		else {
			boolean done = false;
			if (focusIndex != -1)
				done = focusList.get(focusIndex).mouseWheel(delta);
			else
				for (Iterator<Widget> it = widgets.iterator(); it.hasNext() && !done;)
					done = it.next().mouseWheel(delta);
		}
	}

	public int left() {
		return left;
	}

	public int right() {
		return right;
	}

	public int top() {
		return top;
	}

	public int bottom() {
		return bottom;
	}

}
