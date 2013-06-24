package com.mcf.davidee.msc.packet;

import com.google.common.io.ByteArrayDataInput;
import com.mcf.davidee.msc.network.MSCPacketHandler;

import cpw.mods.fml.common.network.Player;

public class ADPacket extends MSCPacket {

	@Override
	public MSCPacket readData(Object... data) {
		return this;
	}

	@Override
	public byte[] generatePacket() {
		return new byte[0];
	}

	@Override
	public MSCPacket readPacket(ByteArrayDataInput pkt) {
		return this;
	}

	@Override
	public void execute(MSCPacketHandler handler, Player player) {
		handler.handleAccessDenied(player);
	}

}
