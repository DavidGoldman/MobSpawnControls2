package com.mcf.davidee.msc.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcf.davidee.msc.network.MSCPacketHandler;

import cpw.mods.fml.common.network.Player;

public class CreatureTypePacket extends MSCPacket {

	public String mod;
	public String creatureType;
	public String[] mobs;

	@Override
	public MSCPacket readData(Object... data) {
		mod = (String) data[0];
		creatureType = (String) data[1];
		mobs = (String[]) data[2];
		return this;
	}

	@Override
	public byte[] generatePacket() {
		ByteArrayDataOutput dat = ByteStreams.newDataOutput();
		dat.writeUTF(mod);
		dat.writeUTF(creatureType);
		writeStringArray(mobs, dat);
		return dat.toByteArray();
	}

	@Override
	public MSCPacket readPacket(ByteArrayDataInput pkt) {
		mod = pkt.readUTF();
		creatureType = pkt.readUTF();
		mobs = readStringArray(pkt);
		return this;
	}

	@Override
	public void execute(MSCPacketHandler handler, Player player) {
		handler.handleCreatureType(this, player);
	}

}
