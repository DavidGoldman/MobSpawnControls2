package com.mcf.davidee.msc.network;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.mcf.davidee.msc.packet.BiomeListPacket;
import com.mcf.davidee.msc.packet.CreatureTypePacket;
import com.mcf.davidee.msc.packet.DebugPacket;
import com.mcf.davidee.msc.packet.EntityListPacket;
import com.mcf.davidee.msc.packet.GroupsPacket;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;
import com.mcf.davidee.msc.packet.ModListPacket;
import com.mcf.davidee.msc.packet.settings.BiomeSettingPacket;
import com.mcf.davidee.msc.packet.settings.EntitySettingPacket;
import com.mcf.davidee.msc.packet.settings.SettingsPacket;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public abstract class MSCPacketHandler implements IPacketHandler {

	public abstract void handleSettings(SettingsPacket pkt, Player player);
	public abstract void handleAccessDenied(Player player);
	public abstract void handleHandShake(Player player);
	public abstract void handleRequest(PacketType packetType, String mod, Player player);
	public abstract void handleModList(ModListPacket packet, Player player);
	public abstract void handleCreatureType(CreatureTypePacket packet, Player player);
	public abstract void handleGroups(GroupsPacket packet, Player player);
	public abstract void handleBiomeList(BiomeListPacket packet, Player player);
	public abstract void handleBiomeSetting(BiomeSettingPacket packet, Player player);
	public abstract void handleEntityList(EntityListPacket packet, Player player);
	public abstract void handleEntitySetting(EntitySettingPacket packet, Player player);
	public abstract void handleDebug(DebugPacket packet, Player player);
	protected abstract boolean hasPermission(Player player);
	
	@Override
	public final void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		if (hasPermission(player))
			MSCPacket.readPacket(packet.data).execute(this,player);
		else
			PacketDispatcher.sendPacketToPlayer(MSCPacket.getPacket(PacketType.ACCESS_DENIED), player);
	}
}
