package com.mcf.davidee.gui.basic;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Button.ButtonHandler;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.Widget;

public abstract class BasicScreen extends GuiScreen {

	private GuiScreen parent;
	private boolean hasInit, closed;
	protected List<Container> containers;
	protected Container selected;

	public BasicScreen(GuiScreen parent) {
		this.parent = parent;
		this.containers = new ArrayList<Container>();
	}

	protected abstract void revalidateGui();
	protected abstract void createGui();
	protected abstract void reopenedGui();

	public GuiScreen getParent() {
		return parent;
	}

	public List<Container> getContainers() {
		return containers;
	}

	public void close() {
		mc.displayGuiScreen(parent);
	}

	protected void unhandledKeyTyped(char c, int code) { }

	protected void drawBackground() {
		drawDefaultBackground();
	}

	@Override
	public void drawScreen(int mx, int my, float f) {
		drawBackground();
		List<Widget> overlays = new ArrayList<Widget>();
		for (int i = containers.size() - 1; i >=0; --i) //Index of 0 means top most
			overlays.addAll(containers.get(i).draw(mx,my));
		for (Widget w : overlays)
			w.draw(mx, my);
	}

	@Override
	public void updateScreen() {
		for (Container c : containers)
			c.update();
	}

	@Override
	protected void mouseClicked(int mx, int my, int code) {
		if (code == 0){
			for (Container c : containers){
				if (c.mouseClicked(mx, my)){
					selected = c;
					break;
				}
			}
			for (Container c : containers)
				if (c != selected)
					c.setFocused(null);
		}
	}

	@Override
	protected void mouseMovedOrUp(int mx, int my, int code) {
		if (code == 0){
			for (Container c : containers)
				c.mouseReleased(mx, my);
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int delta = Mouse.getEventDWheel();
		if (delta != 0 && selected != null)
			selected.mouseWheel(MathHelper.clamp_int(delta,-5,5));
	}

	@Override
	public void keyTyped(char c, int code) {
		boolean handled = (selected != null) ? selected.keyTyped(c, code) : false;
		if (!handled)
			unhandledKeyTyped(c,code);
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		if (!hasInit){
			createGui();
			hasInit = true;
		}
		revalidateGui();
		if (closed){
			reopenedGui();
			closed = false;
		}
	}

	public void drawCenteredStringNoShadow(FontRenderer ft, String str, int cx, int y, int c) {
		ft.drawString(str, cx - ft.getStringWidth(str) / 2, y, c);
	}


	@Override
	public void onGuiClosed() {
		closed = true;
		Keyboard.enableRepeatEvents(false);
	}

	public class CloseHandler implements ButtonHandler{
		public void buttonClicked(Button button) {
			close();
		}
	}

}
