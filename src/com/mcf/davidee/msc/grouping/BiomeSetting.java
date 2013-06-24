package com.mcf.davidee.msc.grouping;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;

import com.mcf.davidee.msc.BiomeNameHelper;
import com.mcf.davidee.msc.Utils;
import com.mcf.davidee.msc.reflect.BiomeReflector;
import com.mcf.davidee.msc.spawning.SpawnList;

public class BiomeSetting {

	private SpawnList active, biomeSettings;
	private BiomeGenBase biome;
	private boolean inGroup, defEdited;

	public BiomeSetting(BiomeGenBase biome, List<Class<? extends EntityLiving>> validClasses) {
		this.biome = biome;
		active = new SpawnList();
		List<SpawnListEntry> m = SpawnList.copySpawnListEntries(BiomeReflector.reflectList(biome, EnumCreatureType.monster));
		List<SpawnListEntry> c = SpawnList.copySpawnListEntries(BiomeReflector.reflectList(biome, EnumCreatureType.creature));
		List<SpawnListEntry> w = SpawnList.copySpawnListEntries(BiomeReflector.reflectList(biome, EnumCreatureType.waterCreature));
		List<SpawnListEntry> a = SpawnList.copySpawnListEntries(BiomeReflector.reflectList(biome, EnumCreatureType.ambient));
		removeInvalidEntries(m,validClasses);
		removeInvalidEntries(c,validClasses);
		removeInvalidEntries(w,validClasses);
		removeInvalidEntries(a,validClasses);
		biomeSettings = new SpawnList(m,c,w,a);
	}

	public BiomeSetting(BiomeSetting setting) {
		this.biome = setting.biome;
		this.active = new SpawnList(setting.active);
		this.biomeSettings = new SpawnList(setting.biomeSettings);
		this.inGroup = setting.inGroup;
	}

	public void prepareForMerge() {
		inGroup = false;
		for (EnumCreatureType type : EnumCreatureType.values())
			Utils.copyInto(biomeSettings.getSpawnList(type), active.getSpawnList(type));
	}

	public void sync() {
		for (EnumCreatureType type : EnumCreatureType.values()) {
			BiomeReflector.reflectList(biome, type).addAll(active.getSpawnList(type));
		}
	}
	
	public void write(BufferedWriter writer, String textToIgnore) throws IOException {
		String biomeName = BiomeNameHelper.getBiomeName(biome);
		if (!inGroup || !biomeSettings.isEmpty()){
			for (EnumCreatureType type : EnumCreatureType.values()){
				List<SpawnListEntry> list = biomeSettings.getSpawnList(type);
				Utils.writeLine(writer, biomeName + "~" + type + ":" +  SpawnList.entriesToString(list,textToIgnore));
			}
			Utils.writeLine(writer,"#");
		}
	}
	
	public void merge(SpawnList list) {
		/*
		 * If a biome definition isn't present in the config but it is present in a group, 
		 * we can infer that the biome setting is an empty spawn list.
		 */
		if (!inGroup && !defEdited){ 
			active.clear();
			biomeSettings.clear();
		}
		inGroup = true;
		for (EnumCreatureType type : EnumCreatureType.values()){
			List<SpawnListEntry> myEntries = active.getSpawnList(type);
			List<SpawnListEntry> theirEntries = list.getSpawnList(type);
			mergeLists(myEntries,theirEntries);
		}
	}

	public SpawnList getBiomeSettingsForEdit() {
		defEdited = true;
		return biomeSettings;
	}
	
	public SpawnList getBiomeSettings() {
		return biomeSettings;
	}

	private void mergeLists(List<SpawnListEntry> myEntries, List<SpawnListEntry> theirEntries) {
		for (SpawnListEntry entry : theirEntries){
			boolean found = false;

			for (SpawnListEntry myEntry : myEntries){
				if (myEntry.entityClass == entry.entityClass){
					found = true;
					break;
				}
			}

			if (!found)
				myEntries.add(SpawnList.copyEntry(entry));
		}
	}
	
	private void removeInvalidEntries(List<SpawnListEntry> entries, List<Class<? extends EntityLiving>> validClasses) {
		for (Iterator<SpawnListEntry> it = entries.iterator(); it.hasNext();)
			if (!validClasses.contains(it.next().entityClass))
				it.remove();
	}

}
