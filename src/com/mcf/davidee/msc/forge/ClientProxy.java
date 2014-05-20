package com.mcf.davidee.msc.forge;

import java.io.File;

import com.mcf.davidee.msc.network.ClientPacketHandler;
import com.mcf.davidee.msc.network.MSCPacketHandler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {
	
	protected final MSCPacketHandler client;

	public ClientProxy() {
		FMLCommonHandler.instance().bus().register(new KeyListener());
		client = new ClientPacketHandler();
	}

	public File getMinecraftDirectory() {
		return FMLClientHandler.instance().getClient().mcDataDir;
	}
	
	@Override
	public MSCPacketHandler getClientHandler() {
		return client;
	}

}
