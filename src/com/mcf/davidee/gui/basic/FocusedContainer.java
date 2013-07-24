package com.mcf.davidee.gui.basic;

import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.Scrollbar;
import com.mcf.davidee.gui.Widget;
import com.mcf.davidee.gui.focusable.FocusableWidget;

public class FocusedContainer extends Container {

	public FocusedContainer() {
		super();

	}

	public FocusedContainer(Scrollbar scrollbar, int shiftAmount, int extraScrollHeight) {
		super(scrollbar, shiftAmount, extraScrollHeight);

	}

	@Override
	public void setFocused(FocusableWidget f) {
		if (f != null)
			super.setFocused(f);
	}

	@Override
	public void addWidgets(Widget... arr) {
		super.addWidgets(arr);

		if (focusIndex == -1 && focusList.size() > 0) {
			focusIndex = 0;
			focusList.get(focusIndex).focusGained();
		}
	}
}
