package com.mcf.davidee.msc.network;

import net.minecraft.client.Minecraft;

import com.mcf.davidee.msc.gui.AccessDeniedScreen;
import com.mcf.davidee.msc.gui.BiomeListScreen;
import com.mcf.davidee.msc.gui.DebugScreen;
import com.mcf.davidee.msc.gui.EditCreatureTypeScreen;
import com.mcf.davidee.msc.gui.EditEntityScreen;
import com.mcf.davidee.msc.gui.EntityListScreen;
import com.mcf.davidee.msc.gui.GroupsMenu;
import com.mcf.davidee.msc.gui.MainMenu;
import com.mcf.davidee.msc.gui.MobTypesMenu;
import com.mcf.davidee.msc.gui.ModListScreen;
import com.mcf.davidee.msc.gui.SettingsScreen;
import com.mcf.davidee.msc.gui.SpawnControlMenu;
import com.mcf.davidee.msc.packet.BiomeListPacket;
import com.mcf.davidee.msc.packet.CreatureTypePacket;
import com.mcf.davidee.msc.packet.DebugPacket;
import com.mcf.davidee.msc.packet.EntityListPacket;
import com.mcf.davidee.msc.packet.GroupsPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;
import com.mcf.davidee.msc.packet.settings.BiomeSettingPacket;
import com.mcf.davidee.msc.packet.settings.EntitySettingPacket;
import com.mcf.davidee.msc.packet.settings.SettingsPacket;
import com.mcf.davidee.msc.packet.ModListPacket;

import cpw.mods.fml.common.network.Player;

public class ClientPacketHandler extends MSCPacketHandler{

	
	//TODO Verify that the GUIs are open for the correct mod?
	
	private Minecraft mc;
	
	public ClientPacketHandler() {
		this.mc = Minecraft.getMinecraft();
	}

	@Override
	protected boolean hasPermission(Player player) {
		return true;
	}

	@Override
	public void handleSettings(SettingsPacket pkt, Player player) {
		if(mc.currentScreen instanceof MainMenu)
			mc.displayGuiScreen(new SettingsScreen(pkt,mc.currentScreen));
	}

	@Override
	public void handleAccessDenied(Player player) {
		if(mc.currentScreen == null)
			mc.displayGuiScreen(new AccessDeniedScreen());
	}

	@Override
	public void handleRequest(PacketType type, String mod, Player player) { }

	@Override
	public void handleHandShake(Player player) {
		if (mc.currentScreen == null)
			mc.displayGuiScreen(new MainMenu());
	}

	@Override
	public void handleModList(ModListPacket packet, Player player) {
		if (mc.currentScreen instanceof MainMenu)
			mc.displayGuiScreen(new ModListScreen(packet,mc.currentScreen));
	}

	@Override
	public void handleDebug(DebugPacket packet, Player player) {
		if (mc.currentScreen instanceof MainMenu)
			mc.displayGuiScreen(new DebugScreen(packet,mc.currentScreen));
	}

	@Override
	public void handleCreatureType(CreatureTypePacket packet, Player player) {
		if (mc.currentScreen instanceof MobTypesMenu)
			mc.displayGuiScreen(new EditCreatureTypeScreen(packet,mc.currentScreen));
	}

	@Override
	public void handleGroups(GroupsPacket packet, Player player) {
		if (mc.currentScreen instanceof ModListScreen)
			mc.displayGuiScreen(new GroupsMenu(packet,mc.currentScreen));
		
	}

	@Override
	public void handleBiomeList(BiomeListPacket packet, Player player) {
		if (mc.currentScreen instanceof SpawnControlMenu)
			mc.displayGuiScreen(new BiomeListScreen(packet, mc.currentScreen));
	}

	@Override
	public void handleEntityList(EntityListPacket packet, Player player) {
		if (mc.currentScreen instanceof SpawnControlMenu)
			mc.displayGuiScreen(new EntityListScreen(packet,mc.currentScreen)); 
	}

	@Override
	public void handleEntitySetting(EntitySettingPacket packet, Player player) {
		if (mc.currentScreen instanceof EntityListScreen)
			mc.displayGuiScreen(new EditEntityScreen(packet, mc.currentScreen));
	}

	@Override
	public void handleBiomeSetting(BiomeSettingPacket packet, Player player) {
		// TODO 
		
	}
	
	


}
