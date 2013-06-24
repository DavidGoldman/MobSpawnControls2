package com.mcf.davidee.msc.reflect;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.mcf.davidee.msc.MobSpawnControls;

import net.minecraft.entity.EnumCreatureType;

public class SpawnFrequencyHelper {
	
	public static boolean setSpawnCreature(EnumCreatureType type, boolean value) {
		try {
			Field field = EnumCreatureType.class.getDeclaredFields()[8];
			field.setAccessible(true);
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(type, value);
			return true;
		}
		catch(Exception e){
			MobSpawnControls.getLogger().throwing("SpawnFrequencyHelper", "setSpawnCreature", e);
			return false;
		}
	}
	
	public static boolean getSpawnCreature(EnumCreatureType type) {
		return !type.getAnimal();
	}
	
}
