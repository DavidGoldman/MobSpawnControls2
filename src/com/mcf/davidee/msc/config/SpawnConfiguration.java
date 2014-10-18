package com.mcf.davidee.msc.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.biome.BiomeGenBase;

import com.mcf.davidee.msc.BiomeNameHelper;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;
import com.mcf.davidee.msc.reflect.BiomeReflector;
import com.mcf.davidee.msc.spawning.CreatureTypeMap;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.ModContainer;

public class SpawnConfiguration {

	private List<ModConfig> configs;
	private SpawnSettings settings;

	public SpawnConfiguration(File folder) {
		folder.mkdirs();
		MobSpawnControls.getLogger().info("Generating configs in directory \"" + folder.getAbsolutePath() + "\"");
		settings = new SpawnSettings(folder);
		configs = new ArrayList<ModConfig>();
		configs.add(new ModConfig(null, folder));
		for (ModContainer c : ModEntityFinder.getEntityMods())
			configs.add(new ModConfig(c, folder));
	}

	public SpawnConfiguration(File folder, SpawnConfiguration _default) {
		folder.mkdirs();
		MobSpawnControls.getLogger().info("Generating configs in directory \"" + folder.getAbsolutePath() + "\"");
		settings = new SpawnSettings(folder, _default.settings);
		configs = new ArrayList<ModConfig>();
		for (ModConfig c : _default.configs)
			configs.add(new ModConfig(c.container, folder, c));
	}

	public boolean canPlayerEdit(EntityPlayer p) {
		if (settings.canEdit() && p instanceof EntityPlayerMP) {
			EntityPlayerMP mp = (EntityPlayerMP) p;
			if (mp.mcServer == null || mp.getCommandSenderName() == null)
				return false;
			
			// New method of checking whether the player is opped using GameProfile.
			ServerConfigurationManager configManager	= mp.mcServer.getConfigurationManager();
			EntityPlayerMP player						= configManager.func_152612_a(mp.getCommandSenderName());
			GameProfile profile							= player.getGameProfile();
			
			return !mp.mcServer.isDedicatedServer() || configManager.func_152596_g(profile);
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
	public MSCPacket createModListPacket() {
		String[] names = new String[configs.size()];
		for (int i = 0; i < names.length; ++i)
			names[i] = configs.get(i).configName;
		return MSCPacket.getPacket(PacketType.MOD_LIST, (Object)names);
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
