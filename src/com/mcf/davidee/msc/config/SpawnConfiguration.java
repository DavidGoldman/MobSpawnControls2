package com.mcf.davidee.msc.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.biome.BiomeGenBase;

import com.mcf.davidee.msc.BiomeNameHelper;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;
import com.mcf.davidee.msc.reflect.BiomeReflector;
import com.mcf.davidee.msc.spawning.CreatureTypeMap;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.Player;

public class SpawnConfiguration {

	private File folder;
	private List<ModConfig> configs;
	private SpawnSettings settings;

	public SpawnConfiguration(File folder) {
		this.folder = folder;
		folder.mkdirs();
		MobSpawnControls.getLogger().info("Generating configs in directory \"" + folder.getAbsolutePath() + "\"");
		settings = new SpawnSettings(folder);
		configs = new ArrayList<ModConfig>();
		configs.add(new ModConfig(null, folder));
		for (ModContainer c : ModEntityFinder.getEntityMods())
			configs.add(new ModConfig(c, folder));
	}

	public SpawnConfiguration(File folder, SpawnConfiguration _default) {
		this.folder = folder;
		folder.mkdirs();
		MobSpawnControls.getLogger().info("Generating configs in directory \"" + folder.getAbsolutePath() + "\"");
		settings = new SpawnSettings(folder, _default.settings);
		configs = new ArrayList<ModConfig>();
		for (ModConfig c : _default.configs)
			configs.add(new ModConfig(c.container, folder, c));
	}

	public boolean canPlayerEdit(Player p) {
		if (settings.canEdit() && p instanceof EntityPlayerMP) {
			EntityPlayerMP mp = (EntityPlayerMP) p;
			if (mp.mcServer == null || mp.username == null)
				return false;
			return !mp.mcServer.isDedicatedServer() || mp.mcServer.getConfigurationManager().isPlayerOpped(mp.username);
		}
		return false;
	}

	public void save() {
		settings.save();
		for (ModConfig config : configs)
			config.save();
	}

	public void load() {
		settings.load();
		for (ModConfig config : configs) {
			config.load();
			config.getSpawnMap().evaluate();
		}
		sync();
	}

	//Revaluates the biome groups
	public void reEval() {
		for (ModConfig config : configs) 
			config.getSpawnMap().evaluate();
	}

	public void sync() {
		//Clear the list, removing old entries
		for (BiomeGenBase biome : BiomeNameHelper.getAllBiomes()) 
			for (EnumCreatureType type : EnumCreatureType.values())
				BiomeReflector.reflectList(biome, type).clear();

		//Step through the mods and each mod's entities 
		for (ModConfig config : configs)
			config.getSpawnMap().sync(settings.isMasterEnabled());
	}

	public void reset() {
		settings.reset();
		for (ModConfig config : configs)
			config.reset();
		sync();
	}

	//Mostly helper functions dealing with the GUI
	public Packet createModListPacket() {
		String[] names = new String[configs.size()];
		for (int i = 0; i < names.length; ++i)
			names[i] = configs.get(i).configName;
		return MSCPacket.getPacket(PacketType.MOD_LIST, (Object) names);
	}

	public SpawnSettings getSettings() {
		return settings;
	}

	public CreatureTypeMap getTypeMap(String mod) {
		for (ModConfig c : configs)
			if (c.configName.equalsIgnoreCase(mod))
				return c.getTypeMap();
		return null;
	}

	public ModConfig getModConfig(String mod) throws IllegalArgumentException {
		for (ModConfig c : configs)
			if (c.configName.equalsIgnoreCase(mod))
				return c;
		throw new IllegalArgumentException("Unknown mod " + mod);
	}

}
