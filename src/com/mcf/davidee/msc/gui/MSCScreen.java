package com.mcf.davidee.msc.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiScreen;

import com.mcf.davidee.guilib.core.Button;
import com.mcf.davidee.guilib.core.Button.ButtonHandler;
import com.mcf.davidee.guilib.basic.BasicScreen;

public abstract class MSCScreen extends BasicScreen implements ButtonHandler{

	public MSCScreen(GuiScreen parent) {
		super(parent);

	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	protected void unhandledKeyTyped(char c, int code) {
		if (code == Keyboard.KEY_ESCAPE)
			mc.displayGuiScreen(null);
	}

	public void setEnabled(boolean aFlag, Button... buttons) {
		for (Button b : buttons)
			b.setEnabled(aFlag);
	}

	protected void reopenedGui() { }
	public void buttonClicked(Button button) { }

}
