package com.mcf.davidee.gui.vanilla;

import com.mcf.davidee.gui.Scrollbar;
import com.mcf.davidee.gui.Widget;

/**
 * 
 * Default style scrollbar.
 *
 */
public class ScrollbarVanilla extends Scrollbar {

	public ScrollbarVanilla(int width, int height) {
		super(width, height);

	}

	@Override
	protected void drawBoundary(int x, int y, int width, int height) {
		drawRect(x, y, x + width, y + height, 0x80000000);
	}

	@Override
	protected void drawScrollbar(int x, int y, int width, int height) {
		drawGradientRect(x, y, x + width, y + height, 0x80ffffff, 0x80222222);
	}

	@Override
	protected void shiftChildren(int dy) {
		for (Widget w : container.getWidgets()) {
			if (w instanceof Shiftable)
				((Shiftable) w).shiftY(dy);
		}
	}

}
