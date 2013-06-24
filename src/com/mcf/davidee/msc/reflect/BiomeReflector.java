package com.mcf.davidee.msc.reflect;

import java.lang.reflect.Field;
import java.util.List;

import com.mcf.davidee.msc.BiomeNameHelper;
import com.mcf.davidee.msc.MobSpawnControls;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;

public class BiomeReflector {
	
	//TODO Cache
	public static List<SpawnListEntry> reflectList(BiomeGenBase biome, EnumCreatureType type) {
		try {
			int ordin = (type == EnumCreatureType.waterCreature) ? 2 : (type == EnumCreatureType.ambient) ? 3 : type.ordinal();
			return reflect(biome,ordin);
		}
		catch(Exception e){
			MobSpawnControls.getLogger().severe("Unable to reflect list for biome " + BiomeNameHelper.getBiomeName(biome) + " of type " + type);
			MobSpawnControls.getLogger().throwing("BiomeReflector", "reflectList", e);
			throw new RuntimeException(e);
		}
	}
	
	private static List<SpawnListEntry> reflect(BiomeGenBase biome, int ordinal) throws Exception {
		Field f = BiomeGenBase.class.getDeclaredFields()[35+ordinal];
		f.setAccessible(true);
		return (List<SpawnListEntry>)f.get(biome);
	}
	
}
