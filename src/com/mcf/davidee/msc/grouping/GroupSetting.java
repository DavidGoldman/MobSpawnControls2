package com.mcf.davidee.msc.grouping;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

import com.mcf.davidee.msc.BiomeNameHelper;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.Utils;
import com.mcf.davidee.msc.spawning.SpawnList;

public class GroupSetting {

	public final BiomeGroup group;
	
	private SpawnList active;


	public GroupSetting(BiomeGroup group) {
		this.group = group;
		active = new SpawnList();
	}
	
	public GroupSetting(GroupSetting setting) {
		this.group = setting.group.clone();
		this.active = new SpawnList(setting.active);
	}
	
	public SpawnList getSpawnList() {
		return active;
	}

	public void mergeInto(Map<BiomeGenBase,BiomeSetting> map) {
		for (BiomeGenBase biome : group.evaluate()){
			BiomeSetting s = map.get(biome);
			if (s != null)
				s.merge(active);
			else{
				MobSpawnControls.getLogger().severe("Unknown biome (id=" + biome.biomeID +") " + BiomeNameHelper.getBiomeName(biome));
				MobSpawnControls.getLogger().severe("Most likely there is a biome ID conflict");
				BiomeGenBase b = getBiome(map.keySet(),biome.biomeID);
				if (b != null)
					MobSpawnControls.getLogger().severe("Conflict biome (id=" + b.biomeID +") " + BiomeNameHelper.getBiomeName(b));
				throw new RuntimeException("MSC: Unknown biome (id=" + biome.biomeID +") " + BiomeNameHelper.getBiomeName(biome));
			}
		}
	}
	
	
	
	private static BiomeGenBase getBiome(Collection<BiomeGenBase> biomes, int id) {
		for (BiomeGenBase biome : biomes)
			if (biome.biomeID == id)
				return biome;
		return null;
	}
	
	public void write(BufferedWriter writer, String textToIgnore) throws IOException {
		for (EnumCreatureType type : EnumCreatureType.values())
			Utils.writeLine(writer, group.getName() + "~" + type + ":" + 
					SpawnList.entriesToString(active.getSpawnList(type), textToIgnore));
		
		Utils.writeLine(writer,"#");
	}
}
