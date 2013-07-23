package com.mcf.davidee.msc.spawning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mcf.davidee.msc.packet.settings.EntitySettingPacket.BiomeEntry;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.SpawnListEntry;

public class SpawnList {
	private List<SpawnListEntry> m, c, w, a;

	public SpawnList() {
		m = new ArrayList<SpawnListEntry>();
		c = new ArrayList<SpawnListEntry>();
		w = new ArrayList<SpawnListEntry>();
		a = new ArrayList<SpawnListEntry>();
	}

	public SpawnList(List<SpawnListEntry> m, List<SpawnListEntry> c, List<SpawnListEntry> w, List<SpawnListEntry> a) {
		this.m = copySpawnListEntries(m);
		this.c = copySpawnListEntries(c);
		this.w = copySpawnListEntries(w);
		this.a = copySpawnListEntries(a);
	}

	public SpawnList(SpawnList entryList) {
		this.m = copySpawnListEntries(entryList.m);
		this.c = copySpawnListEntries(entryList.c);
		this.w = copySpawnListEntries(entryList.w);
		this.a = copySpawnListEntries(entryList.a);
	}

	public SpawnListEntry getEntityEntry(Class entityClass, EnumCreatureType type) {
		return getEntityEntry(entityClass, getSpawnList(type));
	}

	private static SpawnListEntry getEntityEntry(Class entityClass, List<SpawnListEntry> list) {
		for (SpawnListEntry e : list) 
			if (e.entityClass == entityClass)
				return e;
		return null;
	}

	public void removeEntityEntry(Class entityClass, EnumCreatureType type) {
		removeEntityEntry(entityClass, getSpawnList(type));
	}

	private void removeEntityEntry(Class entityClass, List<SpawnListEntry> list) {
		for (Iterator<SpawnListEntry> it = list.iterator(); it.hasNext(); ) 
			if (it.next().entityClass == entityClass) {
				it.remove();
				break;
			}
	}

	public void clear() {
		m.clear();
		c.clear();
		w.clear();
		a.clear();
	}

	public List<SpawnListEntry> getSpawnList(EnumCreatureType type) {
		switch(type){
		case monster:
			return m;
		case creature:
			return c;
		case waterCreature:
			return w;
		case ambient:
			return a;
		default:
			return null;
		}
	}

	public boolean isEmpty() {
		return m.isEmpty() && c.isEmpty() && w.isEmpty() && a.isEmpty();
	}

	public static List<SpawnListEntry> copySpawnListEntries(List<SpawnListEntry> list) {
		List<SpawnListEntry> newList = new ArrayList<SpawnListEntry>(list.size()+1);
		for (SpawnListEntry e : list)
			newList.add(copyEntry(e));
		return newList;
	}

	public static SpawnListEntry copyEntry(SpawnListEntry e) {
		return new SpawnListEntry(e.entityClass,e.itemWeight,e.minGroupCount,e.maxGroupCount);
	}

	public static String entryToString(SpawnListEntry e) {
		return EntityList.classToStringMapping.get(e.entityClass) + "(" + e.itemWeight + "-" + e.minGroupCount + "-" + e.maxGroupCount + ")";
	}


	public static String entriesToString(List<SpawnListEntry> e, String toIgnore) {
		String s = "";
		for (Iterator<SpawnListEntry> it = e.iterator(); it.hasNext(); ){
			s += entryToString(it.next()).replace(toIgnore,"");
			if (it.hasNext())
				s += ",";
		}
		return s;
	}

	public static List<Class> getDisabledEntities(List<SpawnListEntry> spawning, List<Class<? extends EntityLiving>> list) {
		List<Class> disabled = new ArrayList<Class>();
		
		for (Class clazz : list) {
			boolean found = false;

			for (SpawnListEntry e : spawning) {
				if (e.entityClass == clazz) {
					found = true;
					break;
				}
			}
			
			if (!found)
				disabled.add(clazz);

		}
		return disabled;
	}
}
