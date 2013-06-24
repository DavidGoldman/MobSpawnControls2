package com.mcf.davidee.msc.packet.settings;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcf.davidee.msc.network.MSCPacketHandler;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.settings.EntitySettingPacket.BiomeEntry;

import cpw.mods.fml.common.network.Player;

public class BiomeSettingPacket extends MSCPacket{

	//TODO Fix. This was copied from EntitySettingsPacket, which does not have to deal with
	//          CreatureTypes. Either all creature types must be sent in this packet or the 
	//          actual creature type must be specified.  
	public String mod;
	public String entity;
	public EntityEntry[] entries;
	public String[] empty;
	
	@Override
	public MSCPacket readData(Object... data) {
		mod = (String) data[0];
		entity = (String) data[1];
		entries = (EntityEntry[]) data[2];
		empty = (String[]) data[3];
		return this;
	}

	@Override
	public byte[] generatePacket() {
		ByteArrayDataOutput dat = ByteStreams.newDataOutput();
		dat.writeUTF(mod);
		dat.writeUTF(entity);
		
		dat.writeInt(entries.length);
		for (EntityEntry e : entries) {
			dat.writeUTF(e.entity);
			dat.writeInt(e.weight);
			dat.writeInt(e.min);
			dat.writeInt(e.max);
		}
		writeStringArray(empty, dat);
		return dat.toByteArray();
	}

	@Override
	public MSCPacket readPacket(ByteArrayDataInput pkt) {
		mod = pkt.readUTF();
		entity = pkt.readUTF();
		
		entries = new EntityEntry[pkt.readInt()];
		for (int i = 0; i < entries.length; ++i)
			entries[i] = new EntityEntry(pkt.readUTF(), pkt.readInt(), pkt.readInt(), pkt.readInt());
		empty = this.readStringArray(pkt);
		return this;
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
		
		public EntityEntry (String entity) {
			this.entity = entity;
		}
		
		public EntityEntry(String entity, int w, int min, int max) {
			this.entity = entity;
			this.weight = w;
			this.min = min;
			this.max = max;
		}
		
	}
	
}
