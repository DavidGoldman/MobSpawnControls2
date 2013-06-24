package com.mcf.davidee.msc.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcf.davidee.msc.network.MSCPacketHandler;

import cpw.mods.fml.common.network.Player;

public class BiomeListPacket extends MSCPacket{
	
	public boolean evalRequest;
	public String mod;
	public String[] groups, biomes;

	@Override
	public MSCPacket readData(Object... data) {
		evalRequest = (Boolean) data[0];
		mod = (String) data[1];
		groups = (String[]) data[2];
		biomes = (String[]) data[3];
		return this;
	}

	@Override
	public byte[] generatePacket() {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeBoolean(evalRequest);
		out.writeUTF(mod);
		writeStringArray(groups, out);
		writeStringArray(biomes, out);
		return out.toByteArray();
	}

	@Override
	public MSCPacket readPacket(ByteArrayDataInput pkt) {
		evalRequest = pkt.readBoolean();
		mod = pkt.readUTF();
		groups = readStringArray(pkt);
		biomes = readStringArray(pkt);
		return this;
	}

	@Override
	public void execute(MSCPacketHandler handler, Player player) {
		handler.handleBiomeList(this, player);
	}

}
