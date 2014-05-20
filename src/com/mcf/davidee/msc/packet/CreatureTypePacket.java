package com.mcf.davidee.msc.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.network.MSCPacketHandler;

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
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException { 
		writeString(mod, to);
		writeString(creatureType, to);
		writeStringArray(mobs, to);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException {
		mod = readString(from);
		creatureType = readString(from);
		mobs = readStringArray(from);
	}

	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleCreatureType(this, player);
	}

}
