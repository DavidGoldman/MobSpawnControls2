package com.mcf.davidee.gui.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.mcf.davidee.gui.Checkbox;

/**
 * 
 * Default style checkbox.
 *
 */
public class CheckboxVanilla extends Checkbox {

	private static final ResourceLocation TEXTURE = new ResourceLocation("msc2", "textures/gui/checkbox.png");
	
	public static final int SIZE = 10;

	public CheckboxVanilla(String text) {
		super(SIZE + 2 + Minecraft.getMinecraft().fontRenderer.getStringWidth(text), SIZE, text);
	}

	public CheckboxVanilla(String text, boolean checked) {
		this(text);

		this.check = checked;
	}
	
	@Override
	public void handleClick(int mx, int my) {
		mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
		super.handleClick(mx, my);
	}

	@Override
	public void draw(int mx, int my) {
		mc.renderEngine.func_110577_a(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(x, y, 0, check ? SIZE : 0, SIZE, SIZE);
		mc.fontRenderer.drawStringWithShadow(str, x + SIZE + 1, y + 1, (inBounds(mx, my)) ? 16777120 : 0xffffff);
	}

}
