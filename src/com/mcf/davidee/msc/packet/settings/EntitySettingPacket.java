package com.mcf.davidee.msc.packet.settings;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.network.MSCPacketHandler;
import com.mcf.davidee.msc.packet.MSCPacket;

public class EntitySettingPacket extends MSCPacket {

	public String mod;
	public String entity;
	public BiomeEntry[] entries;
	public String[] disabled;
	
	@Override
	public MSCPacket readData(Object... data) {
		mod = (String) data[0];
		entity = (String) data[1];
		entries = (BiomeEntry[]) data[2];
		disabled = (String[]) data[3];
		return this;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException { 
		writeString(mod, to);
		writeString(entity, to);
		
		to.writeInt(entries.length);
		for (BiomeEntry e : entries) {
			writeString(e.biome, to);
			to.writeInt(e.weight);
			to.writeInt(e.min);
			to.writeInt(e.max);
		}
		writeStringArray(disabled, to);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException { 
		mod = readString(from);
		entity = readString(from);
		
		entries = new BiomeEntry[from.readInt()];
		for (int i = 0; i < entries.length; ++i)
			entries[i] = new BiomeEntry(readString(from), from.readInt(), from.readInt(), from.readInt());
		disabled = readStringArray(from);
	}
	
	public BiomeEntry[] getOrderedEntries() {
		BiomeEntry[] allEntries = new BiomeEntry[entries.length + disabled.length];
		for (int i = 0; i < entries.length; ++i)
			allEntries[i] = entries[i];
		for (int i = 0; i < disabled.length; ++i)
			allEntries[entries.length + i] = new BiomeEntry(disabled[i]);
		Arrays.sort(allEntries, new Comparator<BiomeEntry>() {
			public int compare(BiomeEntry a, BiomeEntry b) {
				return a.biome.compareTo(b.biome);
			}
		});
		return allEntries;
	}

	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleEntitySetting(this, player);
	}

	public static class BiomeEntry {
		public String biome;
		public int weight;
		public int min;
		public int max;
		
		public BiomeEntry (String biome) {
			this.biome = biome;
			min = 4;
			max = 4;
		}
		
		public BiomeEntry(String biome, int w, int min, int max) {
			this.biome = biome;
			this.weight = w;
			this.min = min;
			this.max = max;
		}
		
	}
}
