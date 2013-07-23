package com.mcf.davidee.msc.packet.settings;

import java.util.Arrays;
import java.util.Comparator;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcf.davidee.msc.network.MSCPacketHandler;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.settings.EntitySettingPacket.BiomeEntry;

import cpw.mods.fml.common.network.Player;

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
	public byte[] generatePacket() {
		ByteArrayDataOutput dat = ByteStreams.newDataOutput();
		dat.writeUTF(mod);
		dat.writeUTF(biome);
		for (int i = 0; i < 4; ++i) {
			EntityEntry[] entities =  entries[i];
			dat.writeInt(entities.length);

			for (EntityEntry e : entities) {
				dat.writeUTF(e.entity);
				dat.writeInt(e.weight);
				dat.writeInt(e.min);
				dat.writeInt(e.max);
			}
		}
		for (int i = 0; i < 4; ++i)
			writeStringArray(disabled[i], dat);
		return dat.toByteArray();
	}

	@Override
	public MSCPacket readPacket(ByteArrayDataInput pkt) {
		mod = pkt.readUTF();
		biome = pkt.readUTF();
		entries = new EntityEntry[4][];
		for (int i = 0; i < 4; ++i) {
			entries[i] = new EntityEntry[pkt.readInt()];
			for (int j = 0; j < entries[i].length; ++j)
				entries[i][j] = new EntityEntry(pkt.readUTF(), pkt.readInt(), pkt.readInt(), pkt.readInt());
		}
		disabled = new String[4][];
		for (int i = 0; i < 4; ++i)
			disabled[i] = readStringArray(pkt);
		return this;
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
	public void execute(MSCPacketHandler handler, Player player) {
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
