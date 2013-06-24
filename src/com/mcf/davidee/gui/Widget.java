package com.mcf.davidee.gui;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;

public abstract class Widget extends Gui {

	protected Minecraft mc = Minecraft.getMinecraft();
	protected int x, y, width, height;
	protected boolean enabled;

	public Widget(int width, int height) {
		this.width = width;
		this.height = height;
		this.enabled = true;
	}
	
	public Widget(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.enabled = true;
	}

	public abstract void draw(int mx, int my);
	public abstract boolean click(int mx, int my);

	public void handleClick(int mx, int my){ }
	public void update(){ }
	public void mouseReleased(int mx, int my){ }
	
	public boolean keyTyped(char c, int code) { 
		return false;
	}
	
	public boolean mouseWheel(int delta) { 
		return false;
	}

	public List<Widget> getTooltips() {
		return Collections.emptyList();
	}

	public boolean inBounds(int mx, int my) {
		return mx >= x && my >= y && mx < x + width && my < y + height;
	}
	
	public boolean shouldRender(int topY, int bottomY) {
		return  y + height >= topY && y <= bottomY;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
