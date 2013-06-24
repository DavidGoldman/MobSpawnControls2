package com.mcf.davidee.msc.forge;

import java.util.EnumSet;

import net.minecraft.entity.EnumCreatureType;

import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.config.SpawnConfiguration;
import com.mcf.davidee.msc.reflect.SpawnFrequencyHelper;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class SpawnFreqTicker implements IScheduledTickHandler {

	private boolean reset;
	private int tick;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) { }

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
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

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "msc.spawnfreq.tick";
	}

	@Override
	public int nextTickSpacing() {
		return 1;
	}
}