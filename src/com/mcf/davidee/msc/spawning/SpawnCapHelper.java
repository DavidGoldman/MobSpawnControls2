package com.mcf.davidee.msc.spawning;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.mcf.davidee.msc.MobSpawnControls;

import net.minecraft.entity.EnumCreatureType;


public final class SpawnCapHelper {
	
	public static boolean setSpawnCap(EnumCreatureType type, int value) {
		MobSpawnControls.getLogger().info("Attempting to set spawn cap to " + value + " for " + type );
		try {
			Field field = EnumCreatureType.class.getDeclaredFields()[5];
			field.setAccessible(true);
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(type, value);
			return true;
		}
		catch(Exception e){
			MobSpawnControls.getLogger().throwing("SpawnCapHelper", "setSpawnCap", e);
			return false;
		}
	}
	
}

