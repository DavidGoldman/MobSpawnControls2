package com.mcf.davidee.msc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeNameHelper {

	private static Map<String, BiomeGenBase> biomeMap;
	private static Set<BiomeGenBase> biomeSet;

	public static String getBiomeName(BiomeGenBase biome) {
		return ((biome.biomeID <= 22) ? "Vanilla" : biome.getClass().getName()) + '.' + biome.biomeName;
	}

	public static BiomeGenBase getBiome(String biomeName) {
		return biomeMap.get(biomeName);
	}

	public static void initBiomeMap() {
		if (biomeMap != null)
			return;
		
		biomeMap = new HashMap<String, BiomeGenBase>();
		biomeSet = new HashSet<BiomeGenBase>();

		for (BiomeGenBase biome : BiomeGenBase.biomeList) {
			if (biome != null) {
				biomeMap.put(getBiomeName(biome), biome);
				biomeSet.add(biome);
			}
		}
	}

	public static Set<BiomeGenBase> getAllBiomes() {
		return biomeSet;
	}

	public static Set<String> getAllBiomeNames() {
		return biomeMap.keySet();
	}
}
