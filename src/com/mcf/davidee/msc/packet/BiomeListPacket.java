package com.mcf.davidee.msc.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.network.MSCPacketHandler;

public class BiomeListPacket extends MSCPacket{
	
	public boolean evalRequest;
	public String mod;
	public String[] groups, biomes;

	@Override
	public MSCPacket readData(Object... data) {
		evalRequest = (Boolean) data[0];
		mod = (String) data[1];
		groups = (String[]) data[2];
		biomes = (String[]) data[3];
		return this;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException { 
		to.writeBoolean(evalRequest);
		writeString(mod, to);
		writeStringArray(groups, to);
		writeStringArray(biomes, to);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException {
		evalRequest = from.readBoolean();
		mod = readString(from);
		groups = readStringArray(from);
		biomes = readStringArray(from);
	}

	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleBiomeList(this, player);
	}

}
