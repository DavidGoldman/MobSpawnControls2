package com.mcf.davidee.msc.forge;

import java.io.File;

import com.mcf.davidee.msc.network.MSCPacketHandler;
import com.mcf.davidee.msc.network.ServerPacketHandler;

public class CommonProxy {
	
	protected final MSCPacketHandler server;
	
	public CommonProxy() {
		server = new ServerPacketHandler();
	}
	
	public File getMinecraftDirectory() {
		return new File(".");
	}
	
	public MSCPacketHandler getServerHandler() {
		return server;
	}
	
	//If you call getClientHandler() on the server, you're going to have a bad time.
	public MSCPacketHandler getClientHandler() {
		return server;
	}
	
}
