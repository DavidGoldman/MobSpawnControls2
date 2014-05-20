package com.mcf.davidee.msc.packet.settings;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.network.MSCPacketHandler;
import com.mcf.davidee.msc.packet.MSCPacket;

public class EvaluatedBiomePacket extends MSCPacket {
	
	public String mod;
	public String biome;
	public String[][] entities;

	@Override
	public MSCPacket readData(Object... data) {
		mod = (String) data[0];
		biome = (String) data[1];
		entities = (String[][]) data[2];
		return this;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException { 
		writeString(mod, to);
		writeString(biome, to);
		for (int i = 0; i < 4; ++i)
			writeStringArray(entities[i], to);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException { 
		mod = readString(from);
		biome = readString(from);
		entities = new String[4][];
		for (int i = 0; i < 4; ++i)
			entities[i] = readStringArray(from);
	}
	
	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleEvaluatedBiome(this, player);
	}

}
