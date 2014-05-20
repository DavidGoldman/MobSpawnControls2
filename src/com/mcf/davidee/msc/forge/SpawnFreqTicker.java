package com.mcf.davidee.msc.forge;

import net.minecraft.entity.EnumCreatureType;

import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.config.SpawnConfiguration;
import com.mcf.davidee.msc.reflect.SpawnFrequencyHelper;

public class SpawnFreqTicker {

	private boolean reset;
	private int tick;

	public void tick() {
		SpawnConfiguration config = MobSpawnControls.instance.getConfigNoThrow();
		if (config != null) {
			if (reset) {
				reset = false;
				SpawnFrequencyHelper.setSpawnCreature(EnumCreatureType.creature, true);
				tick = 0;
				return;
			}
			if (++tick == config.getSettings().getCreatureFrequency()) {
				SpawnFrequencyHelper.setSpawnCreature(EnumCreatureType.creature, false);
				reset = true;
			}
		} else
			reset = false;
	}
}