package com.mcf.davidee.msc.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcf.davidee.msc.network.MSCPacketHandler;

import cpw.mods.fml.common.network.Player;

public class GroupsPacket extends MSCPacket {

	public String mod;
	public String[] groups;
	public String[] biomeNames;

	@Override
	public MSCPacket readData(Object... data) {
		mod = (String) data[0];
		groups = (String[]) data[1];
		biomeNames = (String[]) data[2];
		return this;
	}

	@Override
	public byte[] generatePacket() {
		ByteArrayDataOutput dat = ByteStreams.newDataOutput();
		dat.writeUTF(mod);
		writeStringArray(groups, dat);
		writeStringArray(biomeNames, dat);
		return dat.toByteArray();
	}

	@Override
	public MSCPacket readPacket(ByteArrayDataInput pkt) {
		mod = pkt.readUTF();
		groups = readStringArray(pkt);
		biomeNames = readStringArray(pkt);
		return this;
	}

	@Override
	public void execute(MSCPacketHandler handler, Player player) {
		handler.handleGroups(this, player);
	}

}
