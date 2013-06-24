package com.mcf.davidee.gui.basic;

import java.util.ArrayList;
import java.util.List;

import com.mcf.davidee.gui.Widget;

import net.minecraft.client.gui.GuiScreen;

public abstract class OverlayScreen extends BasicScreen {

	protected BasicScreen bg;

	public OverlayScreen(BasicScreen bg) {
		super(bg);

		this.bg = bg;
	}

	@Override
	public void drawBackground() {
		bg.drawScreen(-1, -1, 0);
	}

	@Override
	protected void revalidateGui() {
		bg.width = width;
		bg.height = height;
		bg.revalidateGui();
	}

	@Override
	protected void reopenedGui() { }

}
