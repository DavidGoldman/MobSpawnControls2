package com.mcf.davidee.gui.vanilla;

import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.mcf.davidee.gui.Slider;

/**
 * 
 * Vanilla GuiSlider in Widget form.
 *
 */
public class SliderVanilla extends Slider {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/widgets.png");

	public SliderVanilla(int width, int height, float value, SliderFormat format) {
		super(width, height, value, format);

	}

	public SliderVanilla(float value, SliderFormat format) {
		this(150, 20, value, format);
	}

	@Override
	public void handleClick(int mx, int my) {
		super.handleClick(mx, my);
		mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
	}

	@Override
	public void draw(int mx, int my) {
		if (dragging){
			value = (float)(mx - (this.x + 4)) / (float)(this.width - 8);
			value = MathHelper.clamp_float(value, 0, 1);
		}

		mc.renderEngine.func_110577_a(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(x, y, 0, 46, width / 2, height);
		drawTexturedModalRect(x + width / 2, y, 200 - width / 2, 46, width / 2, height);
		drawTexturedModalRect(x + (int)(value * (float)(width - 8)), y, 0, 66, 4, 20);
		drawTexturedModalRect(x + (int)(value * (float)(width - 8)) + 4, y, 196, 66, 4, 20);
		drawCenteredString(mc.fontRenderer, format.format(this), x + width / 2, y + (height - 8) / 2, 
				(inBounds(mx,my)) ? 16777120 : 0xffffff);
	}

}
