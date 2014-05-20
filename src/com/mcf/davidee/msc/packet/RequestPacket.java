package com.mcf.davidee.msc.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.primitives.UnsignedBytes;
import com.mcf.davidee.msc.network.MSCPacketHandler;

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
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException {
		to.writeByte(request);
		writeString(mod != null ? mod : "", to);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException { 
		request = from.readByte();
		mod = readString(from);
	}

	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleRequest(PacketType.values()[UnsignedBytes.toInt(request)], mod, player);
	}

}
