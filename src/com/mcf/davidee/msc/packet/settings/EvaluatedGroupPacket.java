package com.mcf.davidee.msc.packet.settings;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.network.MSCPacketHandler;
import com.mcf.davidee.msc.packet.MSCPacket;

public class EvaluatedGroupPacket extends MSCPacket {
	
	public String mod;
	public String group;
	public String[] biomes;
	
	@Override
	public MSCPacket readData(Object... data) {
		mod = (String) data[0];
		group = (String) data[1];
		biomes = (String[]) data[2];
		return this;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException {
		writeString(mod, to);
		writeString(group, to);
		writeStringArray(biomes, to);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException { 
		mod = readString(from);
		group = readString(from);
		biomes = readStringArray(from);
	}
	
	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleEvaluatedGroup(this, player);
	}
	
}
