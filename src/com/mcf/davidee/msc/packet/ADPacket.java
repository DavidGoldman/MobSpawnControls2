package com.mcf.davidee.msc.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.network.MSCPacketHandler;

public class ADPacket extends MSCPacket {

	@Override
	public MSCPacket readData(Object... data) {
		return this;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException { }

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException { }

	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleAccessDenied(player);
	}
}
