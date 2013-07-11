package com.mcf.davidee.msc.reflect;

public class BiomeClassLoader {

	/*
	 * 
	 * This class is required because of the way MSC2 caches biome information.
	 * The default config is generated after mods have loaded, but not all 
	 * biomes have actually been initialized, since static variables do
	 * not initialize until their class is loaded.
	 *
	 */
	
	public static void loadClass(String s) {
		try {
			Class.forName(s);
		} 
		catch (Exception e) { }
	}

	public static void loadBiomeClasses() {
		loadClass("twilightforest.biomes.TFBiomeBase");
		loadClass("net.tropicraft.world.biomes.BiomeGenTropicraft");
	}
}
