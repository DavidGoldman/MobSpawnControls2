package com.mcf.davidee.msc.packet.settings;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.network.MSCPacketHandler;
import com.mcf.davidee.msc.packet.MSCPacket;

public class SettingsPacket extends MSCPacket {

	public boolean readOnly;
	public boolean masterEnabled;
	public int[] caps;
	public int creatureFreq;

	@Override
	public MSCPacket readData(Object... data) {
		readOnly = (Boolean) data[0];
		masterEnabled = (Boolean) data[1];
		caps = (int[]) data[2];
		creatureFreq = (Integer) data[3];
		return this;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException { 
		to.writeBoolean(readOnly);
		to.writeBoolean(masterEnabled);
		for (int i = 0; i < caps.length; ++i)
			to.writeInt(caps[i]);
		to.writeInt(creatureFreq);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException { 
		readOnly = from.readBoolean();
		masterEnabled = from.readBoolean();
		caps = new int[4];
		for (int i = 0; i < caps.length; ++i)
			caps[i] = from.readInt();
		creatureFreq = from.readInt();
	}

	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleSettings(this, player);
	}

}
