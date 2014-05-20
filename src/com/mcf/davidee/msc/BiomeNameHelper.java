package com.mcf.davidee.msc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeNameHelper {
	
	public static final int MAX_REG_ID = 39;
	public static final List<Integer> VANILLA_MUT = Arrays.asList(129, 130, 131, 132, 133, 134, 140, 149, 151, 155, 156, 157, 158, 160, 162, 163, 164, 165, 166, 167);
	
	

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
	
	//TODO This better - recognize vanilla biomes some other way
	public static boolean isVanilla(BiomeGenBase biome) {
		return biome.biomeID <= MAX_REG_ID || VANILLA_MUT.contains(biome.biomeID);
	}
	
	
	public static String getBiomeName(BiomeGenBase biome) {
		String name = ((isVanilla(biome)) ? "Vanilla" : biome.getClass().getName()) + '.' + biome.biomeName;
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

		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
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
