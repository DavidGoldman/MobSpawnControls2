package com.mcf.davidee.msc.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.UnsignedBytes;
import com.mcf.davidee.msc.network.MSCPacketHandler;

import cpw.mods.fml.common.network.Player;

public class RequestPacket extends MSCPacket {

	private byte request;
	private String mod;

	@Override
	public MSCPacket readData(Object... data) {
		request = (Byte) data[0];
		if (data.length > 1)
			mod = (String) data[1];
		return this;
	}

	@Override
	public byte[] generatePacket() {
		ByteArrayDataOutput dat = ByteStreams.newDataOutput();
		dat.writeByte(request);
		dat.writeUTF(mod != null ? mod : "");
		return dat.toByteArray();
	}

	@Override
	public MSCPacket readPacket(ByteArrayDataInput pkt) {
		request = pkt.readByte();
		mod = pkt.readUTF();
		return this;
	}

	@Override
	public void execute(MSCPacketHandler handler, Player player) {
		handler.handleRequest(PacketType.values()[UnsignedBytes.toInt(request)], mod, player);
	}

}
