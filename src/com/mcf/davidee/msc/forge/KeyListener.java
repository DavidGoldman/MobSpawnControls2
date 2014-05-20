package com.mcf.davidee.msc.forge;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class KeyListener {

	private final KeyBinding spawnGUI;

	public KeyListener () {
		spawnGUI = new KeyBinding("key.spawngui", Keyboard.KEY_F6, "key.categories.misc");
		LanguageRegistry.instance().addStringLocalization("key.spawngui", "MSC GUI");
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if (!FMLClientHandler.instance().isGUIOpen(GuiChat.class))
			if (spawnGUI.isPressed()) 
				MobSpawnControls.DISPATCHER.sendToServer(MSCPacket.getPacket(PacketType.HANDSHAKE));
			
	}

}
