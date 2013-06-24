package com.mcf.davidee.msc.forge;

import java.io.File;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;

public class ClientProxy extends CommonProxy {

	public ClientProxy() {
		KeyBindingRegistry.registerKeyBinding(new KeyListener());
	}

	public File getMinecraftDirectory() {
		return FMLClientHandler.instance().getClient().getMinecraftDir();
	}

}
