package com.mcf.davidee.gui;

import net.minecraft.util.MathHelper;

import com.mcf.davidee.gui.Scrollbar.Shiftable;

public abstract class Slider extends Widget implements Shiftable {

	public interface SliderFormat {
		String format(Slider slider);
	}

	protected SliderFormat format;
	protected float value;
	protected boolean dragging;

	public Slider(int width, int height, float value, SliderFormat format) {
		super(width, height);

		this.value = MathHelper.clamp_float(value, 0, 1);
		this.format = format;
	}

	@Override
	public boolean click(int mx, int my) {
		if (inBounds(mx, my)) {
			value = (float) (mx - (this.x + 4)) / (float) (this.width - 8);
			value = MathHelper.clamp_float(value, 0, 1);
			dragging = true;
			return true;
		}
		return false;
	}
	
	@Override
	public void handleClick(int mx, int my) {
		value = (float) (mx - (this.x + 4)) / (float) (this.width - 8);
		value = MathHelper.clamp_float(value, 0, 1);
		dragging = true;
	}

	@Override
	public void mouseReleased(int mx, int my) {
		dragging = false;
	}

	@Override
	public void shiftY(int dy) {
		this.y += dy;
	}

	public float getValue() {
		return value;
	}

}
