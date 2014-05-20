package com.mcf.davidee.msc.network;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.packet.BiomeListPacket;
import com.mcf.davidee.msc.packet.CreatureTypePacket;
import com.mcf.davidee.msc.packet.DebugPacket;
import com.mcf.davidee.msc.packet.EntityListPacket;
import com.mcf.davidee.msc.packet.GroupsPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;
import com.mcf.davidee.msc.packet.ModListPacket;
import com.mcf.davidee.msc.packet.settings.BiomeSettingPacket;
import com.mcf.davidee.msc.packet.settings.EntitySettingPacket;
import com.mcf.davidee.msc.packet.settings.EvaluatedBiomePacket;
import com.mcf.davidee.msc.packet.settings.EvaluatedGroupPacket;
import com.mcf.davidee.msc.packet.settings.SettingsPacket;

public abstract class MSCPacketHandler {

	public abstract void handleSettings(SettingsPacket pkt, EntityPlayer player);
	public abstract void handleAccessDenied(EntityPlayer player);
	public abstract void handleHandShake(EntityPlayer player);
	public abstract void handleRequest(PacketType packetType, String mod, EntityPlayer player);
	public abstract void handleModList(ModListPacket packet, EntityPlayer player);
	public abstract void handleCreatureType(CreatureTypePacket packet, EntityPlayer player);
	public abstract void handleGroups(GroupsPacket packet, EntityPlayer player);
	public abstract void handleBiomeList(BiomeListPacket packet, EntityPlayer player);
	public abstract void handleBiomeSetting(BiomeSettingPacket packet, EntityPlayer player);
	public abstract void handleEntityList(EntityListPacket packet, EntityPlayer player);
	public abstract void handleEntitySetting(EntitySettingPacket packet, EntityPlayer player);
	public abstract void handleEvaluatedBiome(EvaluatedBiomePacket packet, EntityPlayer player);
	public abstract void handleEvaluatedGroup(EvaluatedGroupPacket packet, EntityPlayer player);
	public abstract void handleDebug(DebugPacket packet, EntityPlayer player);
	
	protected abstract boolean hasPermission(EntityPlayer player);
}
