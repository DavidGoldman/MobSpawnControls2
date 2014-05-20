package com.mcf.davidee.msc.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.network.MSCPacketHandler;

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
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException { 
		writeString(mod, to);
		writeStringArray(groups, to);
		writeStringArray(biomeNames, to);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException {
		mod = readString(from);
		groups = readStringArray(from);
		biomeNames = readStringArray(from);
	}

	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleGroups(this, player);
	}

}
