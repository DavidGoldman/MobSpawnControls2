package com.mcf.davidee.msc.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

import com.mcf.davidee.msc.BiomeNameHelper;
import com.mcf.davidee.msc.MSCLogFormatter;
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

public class ServerPacketHandler extends MSCPacketHandler {

	@Override
	public void handleSettings(SettingsPacket pkt, EntityPlayer player) {
		SpawnSettings settings = MobSpawnControls.instance.getConfig().getSettings();
		boolean wasMasterEnabled = settings.isMasterEnabled();
		settings.readPacket(pkt);
		if (wasMasterEnabled != settings.isMasterEnabled()) //Resync 
			MobSpawnControls.instance.getConfig().sync();
	}

	@Override
	protected boolean hasPermission(EntityPlayer player) {
		return MobSpawnControls.instance.getConfig().canPlayerEdit(player);
	}

	public void handleAccessDenied(EntityPlayer player) { }
	public void handleModList(ModListPacket pkt, EntityPlayer player) { }
	public void handleBiomeList(BiomeListPacket pkt, EntityPlayer player) { }
	public void handleEntityList(EntityListPacket pkt, EntityPlayer player) { } 
	public void handleDebug(DebugPacket pkt, EntityPlayer player){ }
	public void handleEvaluatedBiome(EvaluatedBiomePacket packet, EntityPlayer player) { }
	public void handleEvaluatedGroup(EvaluatedGroupPacket packet, EntityPlayer player) { }
	

	@Override
	public void handleRequest(PacketType type, String dat, EntityPlayer player) {
		EntityPlayerMP mpPlayer = (EntityPlayerMP)player;
		
		switch(type){
		case SETTINGS:
			MobSpawnControls.DISPATCHER.sendTo(MobSpawnControls.instance.getConfig().getSettings().createPacket(), mpPlayer);
			break;
		case MOD_LIST:
			MobSpawnControls.DISPATCHER.sendTo(MobSpawnControls.instance.getConfig().createModListPacket(), mpPlayer);
			break;
		case CREATURE_TYPE:
			handleCreatureTypeRequest(dat, mpPlayer);
			break;
		case GROUPS:
			handleGroupsRequest(dat, mpPlayer);
			break;
		case BIOME_LIST:
			handleBiomeListRequest(dat, mpPlayer);
			break;
		case BIOME_SETTING:
			handleBiomeSettingRequest(dat, mpPlayer);
			break;
		case ENTITY_LIST:
			handleEntityListRequest(dat, mpPlayer);
			break;
		case ENTITY_SETTING:
			handleEntitySettingRequest(dat, mpPlayer);
			break;
		case EVALUATED_BIOME:
			handleEvaluatedBiomeRequest(dat, mpPlayer);
			break;
		case EVALUATED_GROUP:
			handleEvaluatedGroupRequest(dat, mpPlayer);
			break;
		case DEBUG:
			handleDebugRequest(mpPlayer);
			break;
		default:
			break;
		}
	}

	private void handleDebugRequest(EntityPlayerMP player) {
		String[] log = MSCLogFormatter.log.toArray(new String[0]);
		MobSpawnControls.DISPATCHER.sendTo(MSCPacket.getPacket(PacketType.DEBUG, (Object)log), player);
	}

	private void handleEvaluatedBiomeRequest(String data, EntityPlayerMP player) {
		int colon = data.indexOf(':');
		String mod = data.substring(0,colon), biome = data.substring(colon+1);
		MSCPacket p = MobSpawnControls.instance.getConfig().getModConfig(mod).getSpawnMap().getEvaluatedBiomePacket(mod, biome);
		MobSpawnControls.DISPATCHER.sendTo(p, player);
	}

	private void handleEvaluatedGroupRequest(String data, EntityPlayerMP player) {
		int colon = data.indexOf(':');
		String mod = data.substring(0,colon), group = data.substring(colon+1);
		MSCPacket p = MobSpawnControls.instance.getConfig().getModConfig(mod).getSpawnMap().getEvaluatedGroupPacket(mod, group);
		MobSpawnControls.DISPATCHER.sendTo(p, player);
	}
	
	private void handleBiomeSettingRequest(String data, EntityPlayerMP player) {
		int colon = data.indexOf(':');
		String mod = data.substring(0,colon), biome = data.substring(colon+1);
		ModConfig modConfig = MobSpawnControls.instance.getConfig().getModConfig(mod);
		MSCPacket p = modConfig.getSpawnMap().getBiomeSettingPacket(mod, biome, modConfig);
		MobSpawnControls.DISPATCHER.sendTo(p, player);
	}

	private void handleEntitySettingRequest(String data, EntityPlayerMP player) {
		int colon = data.indexOf(':');
		String mod = data.substring(0,colon), entity = data.substring(colon+1);
		ModConfig config = MobSpawnControls.instance.getConfig().getModConfig(mod);
		Class<? extends EntityLiving> entityClass = config.getEntityClass(entity);
		EnumCreatureType type = config.getTypeMap().get(entityClass);
		
		MSCPacket p = config.getSpawnMap().getEntitySettingPacket(mod, entity, type, entityClass);
		MobSpawnControls.DISPATCHER.sendTo(p, player);
	}
	
	@Override
	public void handleHandShake(EntityPlayer player) {
		MobSpawnControls.DISPATCHER.sendTo(MSCPacket.getPacket(PacketType.HANDSHAKE), (EntityPlayerMP)player);
	}

	@Override
	public void handleCreatureType(CreatureTypePacket packet, EntityPlayer player) {
		ModConfig config = MobSpawnControls.instance.getConfig().getModConfig(packet.mod);
		CreatureTypeMap map = config.getTypeMap();
		for (String str : packet.mobs) {
			int colon = str.indexOf(':');
			String mob = str.substring(0,colon);
			EnumCreatureType type = MobHelper.typeOf(str.substring(colon+1));
			map.set(config.getEntityClass(mob), type);
		}
		config.save();
	}
	
	@Override
	public void handleGroups(GroupsPacket packet, EntityPlayer player) {
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

	private void handleCreatureTypeRequest(String data, EntityPlayerMP player) {
		int colon = data.indexOf(':');
		String mod = data.substring(0, colon), mobType = data.substring(colon+1);
		EnumCreatureType type = MobHelper.typeOf(mobType);
		List<String> ent = MobSpawnControls.instance.getConfig().getModConfig(mod).getEntityNames(type);
		
		MSCPacket p = MSCPacket.getPacket(PacketType.CREATURE_TYPE, mod, mobType, ent.toArray(new String[0]));
		MobSpawnControls.DISPATCHER.sendTo(p, player);
	}
	
	private void handleGroupsRequest(String mod, EntityPlayerMP player) {
		List<BiomeGroup> biomeGroups = MobSpawnControls.instance.getConfig().getModConfig(mod).getBiomeGroups();
		String[] groups = new String[biomeGroups.size()];
		for (int i =0; i < groups.length; ++i)
			groups[i] = biomeGroups.get(i).toString();
		String[] biomes = BiomeNameHelper.getAllBiomeNames().toArray(new String[0]);
		Arrays.sort(biomes);
		
		MSCPacket p = MSCPacket.getPacket(PacketType.GROUPS, mod, groups, biomes);
		MobSpawnControls.DISPATCHER.sendTo(p, player);
	}
	
	private void handleBiomeListRequest(String dat, EntityPlayerMP player) {
		boolean eval = (dat.charAt(0) == '1');
		String mod = dat.substring(1);
		List<BiomeGroup> biomeGroups = MobSpawnControls.instance.getConfig().getModConfig(mod).getBiomeGroups();
		String[] groups = new String[biomeGroups.size()];
		for (int i = 0; i < groups.length; ++i)
			groups[i] = biomeGroups.get(i).getName();
		
		String[] biomes = BiomeNameHelper.getAllBiomeNames().toArray(new String[0]);
		Arrays.sort(biomes);
		
		MSCPacket p = MSCPacket.getPacket(PacketType.BIOME_LIST, eval, mod, groups, biomes);
		MobSpawnControls.DISPATCHER.sendTo(p, player);
	}
	
	private void handleEntityListRequest(String mod, EntityPlayerMP player) {
		ModConfig config = MobSpawnControls.instance.getConfig().getModConfig(mod);
		String[][] names = new String[4][];
		for (EnumCreatureType type : EnumCreatureType.values())
			names[type.ordinal()] = config.getEntityNames(type).toArray(new String[0]);
		
		MSCPacket p = MSCPacket.getPacket(PacketType.ENTITY_LIST, mod, names);
		MobSpawnControls.DISPATCHER.sendTo(p, player);
	}

	@Override
	public void handleEntitySetting(EntitySettingPacket packet, EntityPlayer player) {
		ModConfig config = MobSpawnControls.instance.getConfig().getModConfig(packet.mod);
		Class<? extends EntityLiving> entityClass = config.getEntityClass(packet.entity);
		EnumCreatureType type = config.getTypeMap().get(entityClass);
		config.getSpawnMap().setEntitySettings(config, entityClass, type, packet.entries);
		
		config.save();
		config.getSpawnMap().evaluate();
		MobSpawnControls.instance.getConfig().sync();
	}

	@Override
	public void handleBiomeSetting(BiomeSettingPacket packet, EntityPlayer player) {
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
