package com.mcf.davidee.msc.packet.settings;

import java.util.Arrays;
import java.util.Comparator;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcf.davidee.msc.network.MSCPacketHandler;
import com.mcf.davidee.msc.packet.MSCPacket;

import cpw.mods.fml.common.network.Player;

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
	public byte[] generatePacket() {
		ByteArrayDataOutput dat = ByteStreams.newDataOutput();
		dat.writeUTF(mod);
		dat.writeUTF(entity);
		
		dat.writeInt(entries.length);
		for (BiomeEntry e : entries) {
			dat.writeUTF(e.biome);
			dat.writeInt(e.weight);
			dat.writeInt(e.min);
			dat.writeInt(e.max);
		}
		writeStringArray(disabled, dat);
		return dat.toByteArray();
	}

	@Override
	public MSCPacket readPacket(ByteArrayDataInput pkt) {
		mod = pkt.readUTF();
		entity = pkt.readUTF();
		
		entries = new BiomeEntry[pkt.readInt()];
		for (int i = 0; i < entries.length; ++i)
			entries[i] = new BiomeEntry(pkt.readUTF(), pkt.readInt(), pkt.readInt(), pkt.readInt());
		disabled = readStringArray(pkt);
		return this;
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
	public void execute(MSCPacketHandler handler, Player player) {
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
