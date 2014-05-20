package com.mcf.davidee.msc.packet.settings;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.entity.player.EntityPlayer;

import com.mcf.davidee.msc.network.MSCPacketHandler;
import com.mcf.davidee.msc.packet.MSCPacket;

public class BiomeSettingPacket extends MSCPacket {

	public String mod;
	public String biome;
	public EntityEntry[][] entries;
	public String[][] disabled;

	@Override
	public MSCPacket readData(Object... data) {
		mod = (String) data[0];
		biome = (String) data[1];
		entries = (EntityEntry[][]) data[2];
		disabled = (String[][]) data[3];
		return this;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException {
		writeString(mod, to);
		writeString(biome, to);
		for (int i = 0; i < 4; ++i) {
			EntityEntry[] entities = entries[i];
			to.writeInt(entities.length);
			
			for (EntityEntry e : entities) {
				writeString(e.entity, to);
				to.writeInt(e.weight);
				to.writeInt(e.min);
				to.writeInt(e.max);
			}
		}
		for (int i = 0; i < 4; ++i)
			writeStringArray(disabled[i], to);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException { 
		mod = readString(from);
		biome = readString(from);
		entries = new EntityEntry[4][];
		for (int i = 0; i < 4; ++i) {
			entries[i] = new EntityEntry[from.readInt()];
			for (int j = 0; j < entries[i].length; ++j)
				entries[i][j] = new EntityEntry(readString(from), from.readInt(), from.readInt(), from.readInt());
		}
		disabled = new String[4][];
		for (int i = 0; i < 4; ++i)
			disabled[i] = readStringArray(from);
	}

	public EntityEntry[][] getOrderedEntries() {
		EntityEntry[][] allEntries = new EntityEntry[4][];
		for (int index = 0; index < 4; ++index) {
			EntityEntry[] curEntries = new EntityEntry[entries[index].length + disabled[index].length];
			
			for (int i = 0; i < entries[index].length; ++i)
				curEntries[i] = entries[index][i];
			for (int i = 0; i < disabled[index].length; ++i)
				curEntries[entries[index].length + i] = new EntityEntry(disabled[index][i]);
			Arrays.sort(curEntries, new Comparator<EntityEntry>() {
				public int compare(EntityEntry a, EntityEntry b) {
					return a.entity.compareTo(b.entity);
				}
			});
			allEntries[index] = curEntries;
		}
		return allEntries;
	}

	@Override
	public void execute(MSCPacketHandler handler, EntityPlayer player) {
		handler.handleBiomeSetting(this, player);
	}

	public static class EntityEntry {

		public String entity;
		public int weight;
		public int min;
		public int max;

		public EntityEntry(String entity) {
			this.entity = entity;
			min = 4;
			max = 4;
		}

		public EntityEntry(String entity, int w, int min, int max) {
			this.entity = entity;
			this.weight = w;
			this.min = min;
			this.max = max;
		}

	}

}
