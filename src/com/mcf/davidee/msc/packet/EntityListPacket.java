package com.mcf.davidee.msc.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.network.MSCPacketHandler;

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
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException { 
		writeString(mod, to);
		for (int i = 0; i < 4; ++i)
			writeStringArray(entities[i], to);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException {
		mod = readString(from);
		entities = new String[4][];
		for (int i = 0; i < 4; ++i)
			entities[i] = readStringArray(from);
	}

	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleEntityList(this, player);
	}

}
