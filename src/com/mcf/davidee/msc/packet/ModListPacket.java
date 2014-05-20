package com.mcf.davidee.msc.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.network.MSCPacketHandler;

public class ModListPacket extends MSCPacket {

	public String[] mods;

	@Override
	public MSCPacket readData(Object... data) {
		mods = (String[]) data[0];
		return this;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException { 
		writeStringArray(mods, to);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException {
		mods = readStringArray(from);
	}

	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleModList(this, player);
	}

}
