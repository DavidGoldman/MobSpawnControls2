package com.mcf.davidee.msc.forge;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.packet.Packet250CustomPayload;

import org.lwjgl.input.Keyboard;

import com.mcf.davidee.msc.gui.AccessDeniedScreen;
import com.mcf.davidee.msc.gui.MainMenu;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class KeyListener extends KeyHandler{
	

	public KeyListener () {
		super(new KeyBinding[] { new KeyBinding("key.spawngui",Keyboard.KEY_F6)}, new boolean[] {false} );
		LanguageRegistry.instance().addStringLocalization("key.spawngui", "MSC GUI");
	}

	@Override
	public String getLabel() {
		return "msc.keylistener";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		if (Minecraft.getMinecraft().currentScreen == null)
			PacketDispatcher.sendPacketToServer(MSCPacket.getPacket(PacketType.HANDSHAKE));
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) { }

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
