package com.mcf.davidee.msc.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.network.MSCPacketHandler;

public class DebugPacket extends MSCPacket {

	public String[] log;
	
	@Override
	public MSCPacket readData(Object... data) {
		log = (String[]) data[0];
		return this;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException { 
		writeStringArray(log, to);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException {
		log = readStringArray(from);
	}

	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleDebug(this, player);
	}

}
