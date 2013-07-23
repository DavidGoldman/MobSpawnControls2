package com.mcf.davidee.msc.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;

import com.mcf.davidee.msc.BiomeNameHelper;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.config.ModConfig;
import com.mcf.davidee.msc.config.SpawnSettings;
import com.mcf.davidee.msc.grouping.BiomeGroup;
import com.mcf.davidee.msc.grouping.SpawnMap;
import com.mcf.davidee.msc.packet.BiomeListPacket;
import com.mcf.davidee.msc.packet.CreatureTypePacket;
import com.mcf.davidee.msc.packet.DebugPacket;
import com.mcf.davidee.msc.packet.EntityListPacket;
import com.mcf.davidee.msc.packet.GroupsPacket;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;
import com.mcf.davidee.msc.packet.ModListPacket;
import com.mcf.davidee.msc.packet.settings.BiomeSettingPacket;
import com.mcf.davidee.msc.packet.settings.BiomeSettingPacket.EntityEntry;
import com.mcf.davidee.msc.packet.settings.EntitySettingPacket;
import com.mcf.davidee.msc.packet.settings.EvaluatedBiomePacket;
import com.mcf.davidee.msc.packet.settings.EvaluatedGroupPacket;
import com.mcf.davidee.msc.packet.settings.SettingsPacket;
import com.mcf.davidee.msc.spawning.CreatureTypeMap;
import com.mcf.davidee.msc.spawning.MobHelper;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ServerPacketHandler extends MSCPacketHandler{

	@Override
	public void handleSettings(SettingsPacket pkt, Player player) {
		SpawnSettings settings = MobSpawnControls.instance.getConfig().getSettings();
		boolean wasMasterEnabled = settings.isMasterEnabled();
		settings.readPacket(pkt);
		if (wasMasterEnabled != settings.isMasterEnabled()) //Resync 
			MobSpawnControls.instance.getConfig().sync();
	}

	@Override
	protected boolean hasPermission(Player player) {
		return MobSpawnControls.instance.getConfig().canPlayerEdit(player);
	}

	public void handleAccessDenied(Player player) { }
	public void handleModList(ModListPacket pkt, Player player) { }
	public void handleBiomeList(BiomeListPacket pkt, Player player) { }
	public void handleEntityList(EntityListPacket pkt, Player player) { } 
	public void handleDebug(DebugPacket pkt, Player player){ }
	public void handleEvaluatedBiome(EvaluatedBiomePacket packet, Player player) { }
	public void handleEvaluatedGroup(EvaluatedGroupPacket packet, Player player) { }
	

	@Override
	public void handleRequest(PacketType type, String dat, Player player) {
		switch(type){
		case SETTINGS:
			PacketDispatcher.sendPacketToPlayer(MobSpawnControls.instance.getConfig().getSettings().createPacket(), player);
			break;
		case MOD_LIST:
			PacketDispatcher.sendPacketToPlayer(MobSpawnControls.instance.getConfig().createModListPacket(), player);
			break;
		case CREATURE_TYPE:
			handleCreatureTypeRequest(dat, player);
			break;
		case GROUPS:
			handleGroupsRequest(dat, player);
			break;
		case BIOME_LIST:
			handleBiomeListRequest(dat, player);
			break;
		case BIOME_SETTING:
			handleBiomeSettingRequest(dat, player);
			break;
		case ENTITY_LIST:
			handleEntityListRequest(dat, player);
			break;
		case ENTITY_SETTING:
			handleEntitySettingRequest(dat, player);
			break;
		case EVALUATED_BIOME:
			handleEvaluatedBiomeRequest(dat, player);
			break;
		case EVALUATED_GROUP:
			handleEvaluatedGroupRequest(dat, player);
			break;
		case DEBUG:
			//TODO
			PacketDispatcher.sendPacketToPlayer(MSCPacket.getPacket(PacketType.DEBUG), player);
			break;
		default:
			break;
		}
	}

	private void handleEvaluatedBiomeRequest(String data, Player player) {
		int colon = data.indexOf(':');
		String mod = data.substring(0,colon), biome = data.substring(colon+1);
		Packet p = MobSpawnControls.instance.getConfig().getModConfig(mod).getSpawnMap().getEvaluatedBiomePacket(mod, biome);
		PacketDispatcher.sendPacketToPlayer(p, player);
	}

	private void handleEvaluatedGroupRequest(String data, Player player) {
		int colon = data.indexOf(':');
		String mod = data.substring(0,colon), group = data.substring(colon+1);
		Packet p = MobSpawnControls.instance.getConfig().getModConfig(mod).getSpawnMap().getEvaluatedGroupPacket(mod, group);
		PacketDispatcher.sendPacketToPlayer(p, player);
	}
	
	private void handleBiomeSettingRequest(String data, Player player) {
		int colon = data.indexOf(':');
		String mod = data.substring(0,colon), biome = data.substring(colon+1);
		ModConfig modConfig = MobSpawnControls.instance.getConfig().getModConfig(mod);
		Packet p = modConfig.getSpawnMap().getBiomeSettingPacket(mod, biome, modConfig);
		PacketDispatcher.sendPacketToPlayer(p, player);
	}

	private void handleEntitySettingRequest(String data, Player player) {
		int colon = data.indexOf(':');
		String mod = data.substring(0,colon), entity = data.substring(colon+1);
		ModConfig config = MobSpawnControls.instance.getConfig().getModConfig(mod);
		Class entityClass = config.getEntityClass(entity);
		EnumCreatureType type = config.getTypeMap().get(entityClass);
		
		Packet p = config.getSpawnMap().getEntitySettingPacket(mod, entity, type, entityClass);
		PacketDispatcher.sendPacketToPlayer(p, player);
	}
	
	@Override
	public void handleHandShake(Player player) {
		PacketDispatcher.sendPacketToPlayer(MSCPacket.getPacket(PacketType.HANDSHAKE), player);
	}

	@Override
	public void handleCreatureType(CreatureTypePacket packet, Player player) {
		ModConfig config = MobSpawnControls.instance.getConfig().getModConfig(packet.mod);
		CreatureTypeMap map = config.getTypeMap();
		for (String str : packet.mobs) {
			int colon = str.indexOf(':');
			String mob = str.substring(0,colon);
			EnumCreatureType type = MobHelper.typeOf(str.substring(colon+1));
			config.getTypeMap().set(config.getEntityClass(mob), type);
		}
		config.save();
	}
	
	@Override
	public void handleGroups(GroupsPacket packet, Player player) {
		ModConfig config = MobSpawnControls.instance.getConfig().getModConfig(packet.mod);
		for (String s : packet.biomeNames) 
			config.processGroupCommand(s);
			
		SpawnMap map = config.getSpawnMap();
		for (String s : packet.groups) 
			map.parseGroup(s);
		
		config.save();
		map.evaluate();
		MobSpawnControls.instance.getConfig().sync();
	}

	private void handleCreatureTypeRequest(String data, Player player) {
		int colon = data.indexOf(':');
		String mod = data.substring(0,colon), mobType = data.substring(colon+1);
		EnumCreatureType type = MobHelper.typeOf(mobType);
		List<String> ent = MobSpawnControls.instance.getConfig().getModConfig(mod).getEntityNames(type);
		PacketDispatcher.sendPacketToPlayer(MSCPacket.getPacket(PacketType.CREATURE_TYPE, 
				mod, mobType, ent.toArray(new String[0])), player);
	}
	
	private void handleGroupsRequest(String mod, Player player) {
		List<BiomeGroup> biomeGroups = MobSpawnControls.instance.getConfig().getModConfig(mod).getBiomeGroups();
		String[] groups = new String[biomeGroups.size()];
		for (int i =0; i < groups.length; ++i)
			groups[i] = biomeGroups.get(i).toString();
		String[] biomes = BiomeNameHelper.getAllBiomeNames().toArray(new String[0]);
		Arrays.sort(biomes);
		PacketDispatcher.sendPacketToPlayer(MSCPacket.getPacket(PacketType.GROUPS, mod, groups, biomes), player);
		
	}
	
	private void handleBiomeListRequest(String dat, Player player) {
		boolean eval = (dat.charAt(0) == '1');
		String mod = dat.substring(1);
		List<BiomeGroup> biomeGroups = MobSpawnControls.instance.getConfig().getModConfig(mod).getBiomeGroups();
		String[] groups = new String[biomeGroups.size()];
		for (int i = 0; i < groups.length; ++i)
			groups[i] = biomeGroups.get(i).getName();
		
		String[] biomes = BiomeNameHelper.getAllBiomeNames().toArray(new String[0]);
		Arrays.sort(biomes);
		
		
		PacketDispatcher.sendPacketToPlayer(MSCPacket.getPacket(PacketType.BIOME_LIST, eval, mod, groups, biomes), player);
	}
	
	private void handleEntityListRequest(String mod, Player player) {
		ModConfig config = MobSpawnControls.instance.getConfig().getModConfig(mod);
		String[][] names = new String[4][];
		for (EnumCreatureType type : EnumCreatureType.values())
			names[type.ordinal()] = config.getEntityNames(type).toArray(new String[0]);
		PacketDispatcher.sendPacketToPlayer(MSCPacket.getPacket(PacketType.ENTITY_LIST, mod, names), player);
	}

	@Override
	public void handleEntitySetting(EntitySettingPacket packet, Player player) {
		ModConfig config = MobSpawnControls.instance.getConfig().getModConfig(packet.mod);
		Class entityClass = config.getEntityClass(packet.entity);
		EnumCreatureType type = config.getTypeMap().get(entityClass);
		config.getSpawnMap().setEntitySettings(config, entityClass, type, packet.entries);
		
		config.save();
		config.getSpawnMap().evaluate();
		MobSpawnControls.instance.getConfig().sync();
	}

	@Override
	public void handleBiomeSetting(BiomeSettingPacket packet, Player player) {
		ModConfig config = MobSpawnControls.instance.getConfig().getModConfig(packet.mod);
		BiomeGenBase biome = (packet.biome.equalsIgnoreCase("master")) ? null : BiomeNameHelper.getBiome(packet.biome);
		BiomeGroup group = (biome == null && !packet.biome.equalsIgnoreCase("master")) ? config.getBiomeGroup(packet.biome) : null;
		SpawnMap map = config.getSpawnMap();
		
		for (EnumCreatureType type : EnumCreatureType.values()) {
			List<SpawnListEntry> spawnList = new ArrayList<SpawnListEntry>();
			EntityEntry[] entries = packet.entries[type.ordinal()];
			for (EntityEntry e : entries)
				spawnList.add(new SpawnListEntry(config.getEntityClass(e.entity), e.weight, e.min, e.max));
			if (group != null)
				map.setGroupSection(group, type, spawnList);
			else
				map.setBiomeSection(biome, type, spawnList);
		}
		
		config.save();
		config.getSpawnMap().evaluate();
		MobSpawnControls.instance.getConfig().sync();
	}

	

}
