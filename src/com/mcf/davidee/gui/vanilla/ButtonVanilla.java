package com.mcf.davidee.gui.vanilla;

import net.minecraft.client.resources.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Button.ButtonHandler;

public class ButtonVanilla extends Button {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/widgets.png");
	
	protected String str;

	public ButtonVanilla(int width, int height, String text, ButtonHandler handler) {
		super(width, height, handler);
		
		this.str = text;
	}
	
	public ButtonVanilla(String text, ButtonHandler handler) {
		this(200, 20, text, handler);
	}

	@Override
	public void draw(int mx, int my) {
		mc.renderEngine.func_110577_a(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		boolean hover = inBounds(mx, my);
		int u = 0, v = 46 + getStateOffset(hover);

		if (width == 200 && height == 20) //Full size button
			drawTexturedModalRect(x, y, u, v, width, height);
		else{ 
			drawTexturedModalRect(x, y, u, v, width/2, height/2);
			drawTexturedModalRect(x+width/2, y, u +200 - width /2, v, width/2, height/2);
			drawTexturedModalRect(x, y+height/2, u, v+20-height/2, width/2, height/2);
			drawTexturedModalRect(x+width/2, y+height/2, u + 200-width/2, v+20-height/2, width/2, height/2);
		}
		drawCenteredString(mc.fontRenderer, str, x + width / 2, y + (height - 8) / 2, getTextColor(hover));
	}

	private int getStateOffset(boolean hover) {
		return ((enabled) ? ((hover) ? 40 : 20) : 0);
	}
	
	private int getTextColor(boolean hover) {
		return ((enabled) ? ((hover) ? 16777120 : 14737632) : 6250336);
	}
	
	public String getText() {
		return str;
	}
	
	public void handleClick(int mx, int my) {
		mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
		super.handleClick(mx, my);
	}
	
}
