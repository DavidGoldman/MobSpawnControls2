package com.mcf.davidee.msc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeNameHelper {

	private static Map<String, BiomeGenBase> biomeMap;
	private static Set<BiomeGenBase> biomeSet;
	
	private final static Map<String, String> nameSimplificationMap = new HashMap<String, String>();
	
	static {
		nameSimplificationMap.put("biomesoplenty.biomes", "BOP");
		nameSimplificationMap.put("extrabiomes.module.summa.biome", "EBXL");
		nameSimplificationMap.put("twilightforest.biomes", "TF");
		nameSimplificationMap.put("bwg4.biomes", "BWG4");
		nameSimplificationMap.put("highlands.biome", "HL");
		nameSimplificationMap.put("net.tropicraft.world.biomes", "TROP");
		nameSimplificationMap.put("xolova.blued00r.divinerpg.generation", "DRPG");
	}
	
	
	public static String getBiomeName(BiomeGenBase biome) {
		String name = ((biome.biomeID <= 22) ? "Vanilla" : biome.getClass().getName()) + '.' + biome.biomeName;
		for (Entry<String,String> entry : nameSimplificationMap.entrySet())
			if (name.startsWith(entry.getKey()))
				return entry.getValue() + '.' + biome.biomeName;
		return name;
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
