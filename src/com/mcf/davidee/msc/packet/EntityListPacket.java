package com.mcf.davidee.msc.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcf.davidee.msc.network.MSCPacketHandler;

import cpw.mods.fml.common.network.Player;

public class EntityListPacket extends MSCPacket{

	public String mod;
	public String[][] entities;

	@Override
	public MSCPacket readData(Object... data) {
		mod = (String) data[0];
		entities = (String[][]) data[1];
		return this;
	}

	@Override
	public byte[] generatePacket() {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(mod);
		for (int i = 0; i < 4; ++i)
			writeStringArray(entities[i], out);
		return out.toByteArray();
	}

	@Override
	public MSCPacket readPacket(ByteArrayDataInput pkt) {
		mod = pkt.readUTF();
		entities = new String[4][];
		for (int i = 0; i < 4; ++i)
			entities[i] = readStringArray(pkt);
		return this;
	}

	@Override
	public void execute(MSCPacketHandler handler, Player player) {
		handler.handleEntityList(this, player);
	}

}
