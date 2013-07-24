package com.mcf.davidee.msc.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcf.davidee.msc.network.MSCPacketHandler;

import cpw.mods.fml.common.network.Player;

public class DebugPacket extends MSCPacket {

	public String[] log;
	
	@Override
	public MSCPacket readData(Object... data) {
		log = (String[]) data[0];
		return this;
	}

	@Override
	public byte[] generatePacket() {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		writeStringArray(log, out);
		return out.toByteArray();
	}

	@Override
	public MSCPacket readPacket(ByteArrayDataInput pkt) {
		log = readStringArray(pkt);
		return this;
	}

	@Override
	public void execute(MSCPacketHandler handler, Player player) {
		handler.handleDebug(this, player);
	}

}
