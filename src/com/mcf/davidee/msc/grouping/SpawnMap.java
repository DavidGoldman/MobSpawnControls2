package com.mcf.davidee.msc.grouping;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;

import com.mcf.davidee.msc.BiomeNameHelper;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.Utils;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;
import com.mcf.davidee.msc.packet.settings.EntitySettingPacket.BiomeEntry;
import com.mcf.davidee.msc.reflect.BiomeReflector;
import com.mcf.davidee.msc.spawning.SpawnList;

public class SpawnMap {

	private List<GroupSetting> groups;
	private Map<BiomeGenBase,BiomeSetting> biomeMap;
	private SpawnList master;

	public SpawnMap(List<Class<? extends EntityLiving>> validClasses) {
		groups = new ArrayList<GroupSetting>();
		biomeMap = new TreeMap<BiomeGenBase,BiomeSetting>(new BiomeComparator());
		master = new SpawnList();

		for (BiomeGenBase biome : BiomeNameHelper.getAllBiomes()){
			if (biome != null)
				biomeMap.put(biome, new BiomeSetting(biome,validClasses));
		}
	}

	public SpawnMap(SpawnMap other) {
		groups = new ArrayList<GroupSetting>();
		biomeMap = new TreeMap<BiomeGenBase,BiomeSetting>(new BiomeComparator());
		master = new SpawnList(other.master);

		for (GroupSetting gs : other.groups)
			groups.add(new GroupSetting(gs));
		for (Entry<BiomeGenBase,BiomeSetting> entry : other.biomeMap.entrySet())
			biomeMap.put(entry.getKey(), new BiomeSetting(entry.getValue()));
	}

	public void evaluate() {
		for (BiomeSetting s : biomeMap.values())
			s.prepareForMerge();
		for (GroupSetting s : groups)
			s.mergeInto(biomeMap);
	}

	public void sync(boolean masterEnabled) {
		if (masterEnabled){
			for (BiomeGenBase biome : biomeMap.keySet()){
				for (EnumCreatureType type : EnumCreatureType.values()){
					BiomeReflector.reflectList(biome, type).addAll(master.getSpawnList(type));
				}
			}
		}
		else{
			for (BiomeSetting s : biomeMap.values())
				s.sync();
		}
	}

	public SpawnList getMasterList() {
		return master;
	}

	public void addGroup(BiomeGroup group) {
		groups.add(new GroupSetting(group));
	}

	public Packet getEntitySettingPacket(String mod, String entity, Class entityClass) {
		List<BiomeEntry> spawning = new ArrayList<BiomeEntry>();
		List<String> notSpawning = new ArrayList<String>();

		for (GroupSetting s : groups) {
			SpawnListEntry e = s.getSpawnList().getEntityEntry(entityClass);
			if (e == null)
				notSpawning.add(s.group.getName());
			else 
				spawning.add(new BiomeEntry(s.group.getName(), e.itemWeight, e.minGroupCount, e.maxGroupCount));
		}

		for (Entry<BiomeGenBase, BiomeSetting> entry : biomeMap.entrySet()) {
			String biomeName = BiomeNameHelper.getBiomeName(entry.getKey());
			SpawnListEntry e = entry.getValue().getBiomeSettings().getEntityEntry(entityClass);
			if (e == null)
				notSpawning.add(biomeName);
			else
				spawning.add(new BiomeEntry(biomeName, e.itemWeight, e.minGroupCount, e.maxGroupCount));
		}

		return MSCPacket.getPacket(PacketType.ENTITY_SETTING, mod, entity, 
				spawning.toArray(new BiomeEntry[0]), notSpawning.toArray(new String[0]));
	}

	public void setEntitySettings(Class entityClass, EnumCreatureType type, BiomeEntry[] entries) {
		//TODO 
		//Currently entities may be in different spawn lists than their defined type
		//Most likely this will have to change otherwise this option will not work correctly
	}

	public void setBiomeSection(BiomeGenBase biome, EnumCreatureType type, List<SpawnListEntry> entries) {
		List<SpawnListEntry> list = (biome == null) ? master.getSpawnList(type) : biomeMap.get(biome).getBiomeSettingsForEdit().getSpawnList(type);
		list.clear();
		for (SpawnListEntry entry : entries)
			list.add(entry);
	}

	public void setGroupSection(BiomeGroup group, EnumCreatureType type, List<SpawnListEntry> entries) {
		List<SpawnListEntry> list = getSettings(group).getSpawnList().getSpawnList(type);
		list.clear();
		for (SpawnListEntry entry : entries)
			list.add(entry);
	}

	public void removeGroup(String name) {
		for (Iterator<GroupSetting> it = groups.iterator(); it.hasNext();)
			if (it.next().group.getName().equalsIgnoreCase(name)){
				it.remove();
				break;
			}
	}

	public void renameGroup(String oldN, String newN) {
		for (Iterator<GroupSetting> it = groups.iterator(); it.hasNext(); ) {
			GroupSetting cur = it.next();
			if (cur.group.getName().equalsIgnoreCase(oldN)){
				cur.group.setName(newN);
				break;
			}
		}
	}

	public void parseGroup(String line) {
		int index = line.indexOf('=');
		String group = line.substring(0,index), def = line.substring(index+1);
		Set<BiomeOperand> ops = new LinkedHashSet<BiomeOperand>();
		if (!def.isEmpty()){
			String[] args = def.split(",");
			for (String s : args){
				BiomeOperator op = BiomeOperator.operatorOf(s.charAt(0));
				if (op == BiomeOperator.ALL)
					ops.add(new BiomeOperand());
				else{
					String name = s.substring(1);
					if (name.indexOf('.') != -1)
						ops.add(new BiomeOperand(op,BiomeNameHelper.getBiome(name)));
					else
						ops.add(new BiomeOperand(op,getGroup(name)));
				}
			}
		}
		getGroup(group).setOps(ops);
	}

	private BiomeGroup getGroup(String group) {
		for (GroupSetting s : groups)
			if (s.group.getName().equalsIgnoreCase(group))
				return s.group;
		throw new IllegalArgumentException(group + " is not a valid group");
	}

	public void resetGroupsList() {
		groups.clear();
	}

	public void writeConfig(BufferedWriter writer, String textToIgnore) throws IOException {
		for (EnumCreatureType type : EnumCreatureType.values())
			Utils.writeLine(writer,  "Master~" + type + ":" + SpawnList.entriesToString(master.getSpawnList(type),textToIgnore));

		Utils.writeLine(writer,"#");
		for (GroupSetting gs : groups)
			gs.write(writer, textToIgnore);

		Utils.writeLine(writer,"#");
		for (BiomeSetting s : biomeMap.values())
			s.write(writer, textToIgnore);
	}

	private GroupSetting getSettings(BiomeGroup group) {
		for (GroupSetting s : groups)
			if (s.group == group)
				return s;
		MobSpawnControls.getLogger().severe("Could not find group \"" + group + "\"");
		throw new RuntimeException("MSC: Unknown group \"" + group.getName() + "\"");
	}

	private static class BiomeComparator implements Comparator<BiomeGenBase> {
		public int compare(BiomeGenBase a, BiomeGenBase b) {
			return BiomeNameHelper.getBiomeName(a).compareTo(BiomeNameHelper.getBiomeName(b));
		}
	}

}
