package com.mcf.davidee.msc.packet.settings;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcf.davidee.msc.network.MSCPacketHandler;
import com.mcf.davidee.msc.packet.MSCPacket;

import cpw.mods.fml.common.network.Player;

public class EvaluatedGroupPacket extends MSCPacket {
	
	public String mod;
	public String group;
	public String[] biomes;
	
	@Override
	public MSCPacket readData(Object... data) {
		mod = (String) data[0];
		group = (String) data[1];
		biomes = (String[]) data[2];
		return this;
	}
	
	@Override
	public byte[] generatePacket() {
		ByteArrayDataOutput dat = ByteStreams.newDataOutput();
		dat.writeUTF(mod);
		dat.writeUTF(group);
		writeStringArray(biomes, dat);
		return dat.toByteArray();
	}
	
	@Override
	public MSCPacket readPacket(ByteArrayDataInput pkt) {
		mod = pkt.readUTF();
		group = pkt.readUTF();
		biomes = readStringArray(pkt);
		return this;
	}
	
	@Override
	public void execute(MSCPacketHandler handler, Player player) {
		handler.handleEvaluatedGroup(this, player);
	}
	
}
