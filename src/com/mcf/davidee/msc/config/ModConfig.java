package com.mcf.davidee.msc.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;

import com.google.common.base.Strings;
import com.mcf.davidee.msc.BiomeNameHelper;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.Utils;
import com.mcf.davidee.msc.config.parser.ModConfigParser;
import com.mcf.davidee.msc.grouping.BiomeGroup;
import com.mcf.davidee.msc.grouping.BiomeOperand;
import com.mcf.davidee.msc.grouping.BiomeOperator;
import com.mcf.davidee.msc.grouping.SpawnMap;
import com.mcf.davidee.msc.packet.settings.EntitySettingPacket.BiomeEntry;
import com.mcf.davidee.msc.spawning.CreatureTypeMap;
import com.mcf.davidee.msc.spawning.MobHelper;

import cpw.mods.fml.common.ModContainer;


public class ModConfig {

	private final File saveFile;
	private final List<Class<? extends EntityLiving>> entityClasses;

	public final ModContainer container;
	public final ModConfigParser parser;
	public final String fileName, configName;

	private List<BiomeGroup> groups;
	private CreatureTypeMap typeMap;
	private SpawnMap active, _default;
	private boolean readOnly = false;
	private int lineNum;
	
	public int weight, min, max;

	//Constructor called to create the default config
	public ModConfig(ModContainer container, File dir) {
		this.container = container;
		configName = (container == null) ? "Vanilla" : container.getModId();
		fileName = configName.replaceAll("[\\\\/:*?\"<>|]", "");
		saveFile = new File(dir, fileName + ".cfg");
		entityClasses = ModEntityRecognizer.getEntityClasses(container);

		groups = new ArrayList<BiomeGroup>();
		typeMap = new CreatureTypeMap(entityClasses);
		_default = new SpawnMap(entityClasses);
		active = new SpawnMap(_default);
		weight = 8;
		min = max = 4;
		
		parser = new ModConfigParser(this);
	}

	//Constructor called to create the world config
	public ModConfig(ModContainer container, File folder, ModConfig c) {
		this.container = container;
		configName = (container == null) ? "Vanilla" : container.getModId();
		fileName = configName.replaceAll("[\\\\/:*?\"<>|]", "");
		saveFile = new File(folder, fileName + ".cfg");
		entityClasses = c.entityClasses;

		//Copy everything over from the default config
		groups = new ArrayList<BiomeGroup>();
		for (BiomeGroup g : c.groups)
			groups.add(g.clone());
		typeMap = new CreatureTypeMap(c.typeMap);
		_default = new SpawnMap(c.active);
		active = new SpawnMap(_default);
		weight = c.weight;
		min = c.min;
		max = c.max;
		
		parser = new ModConfigParser(this);
	}

	public SpawnMap getSpawnMap() {
		return active;
	}

	public CreatureTypeMap getTypeMap() {
		return typeMap;
	}
	
	public List<BiomeGroup> getBiomeGroups() {
		return groups;
	}

	//Not sure if this will ever be used
	//If so, perhaps reset the groups and creature type map
	//to that of the default container?
	public void reset() {
		typeMap.generateDefault(entityClasses);
		active = new SpawnMap(_default);
	}

	public void save() {
		if (readOnly)
			return;
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(saveFile));
			Utils.writeLine(writer, "# Mob Spawn Controls Configuration for \""
					+ configName + "\"");
			Utils.writeLine(writer, "#");
			Utils.writeLine(writer, "?ReadOnly=" + readOnly);
			Utils.writeLine(writer, "#");
			Utils.writeLine(writer, "?ItemWeight=" + weight);
			Utils.writeLine(writer, "?MinCount=" + min);
			Utils.writeLine(writer, "?MaxCount=" + max);
			Utils.writeLine(writer, "#");
			Utils.writeLine(writer, "# Creature Type Configuration");

			for (EnumCreatureType type : EnumCreatureType.values())
				Utils.writeLine(writer, getTypeString(type));

			Utils.writeLine(writer, getTypeString(null));
			Utils.writeLine(writer, "#");
			Utils.writeLine(writer, "# Biome Groupings");
			//Write groups
			for (BiomeGroup group : groups)
				Utils.writeLine(writer, '&' + group.toString());
			Utils.writeLine(writer, "#");
			Utils.writeLine(writer, "# Spawn Settings");
			Utils.writeLine(writer, "#");
			//Write config
			active.writeConfig(writer, configName + '.');

		} catch (IOException e) {
			MobSpawnControls.getLogger().throwing("ModConfig", "save", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) { }
			}
		}
	}

	public void load() {
		if (!saveFile.exists())
			return;

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(saveFile));
			groups.clear();
			active.resetGroupsList();
			lineNum = 1;
			while (reader.ready()) {
				String line = reader.readLine();
				if (!line.startsWith("#")) { //Lines starting with # are comments
					if (line.startsWith("~")) //Type setting
						setTypeSettings(line.substring(1));
					else if (line.startsWith("?")) //Variable setting
						readVariable(line.substring(1));
					else if (line.startsWith("&")) //Group Setting
						readGroup(line.substring(1));
					else //Assume biome setting
						readBiomeSettings(line);
				}
				++lineNum;
			}
		} catch (IOException e) {
			MobSpawnControls.getLogger().throwing("ModConfig", "load", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) { }
			}
		}
	}

	//Current groups cannot reference groups that have not yet been read.
	//This is not so much a bug as it is a safety measure, which prevents groups from
	//referencing each other in a chain and causing a recursion crash
	private void readGroup(String line) {
		BiomeGroup group = parser.readBiomeGroup(line);
		
		if (group == null) 
			warn(parser.log.get(0), '&' + line);
		else {
			for (String s : parser.log)
				warn(s, '&' + line);
			
			groups.add(group);
			active.addGroup(group);
		}
	}

	private void readBiomeSettings(String line) {
		parser.readBiomeSettings(line);
		for (String s : parser.log)
			warn(s, line);
	}

	//Should really implement a map for this
	private void readVariable(String line) {
		int index = line.indexOf('=');
		if (index != -1) {
			String var = line.substring(0, index);
			String value = line.substring(index + 1);
			if (var.equalsIgnoreCase("readonly"))
				readOnly = "true".equalsIgnoreCase(value);
			if (var.equalsIgnoreCase("itemweight"))
				weight = Utils.parseIntDMinMax(value, weight, 1, 100);
			if (var.equalsIgnoreCase("mincount"))
				min = Utils.parseIntDMinMax(value, min, 1, 10);
			if (var.equalsIgnoreCase("maxcount")) {
				max = Utils.parseIntDMinMax(value, max, 1, 15);
				if (min > max)
					max = min;
			}
		} else
			warn("Expected character '='", "?" + line);
	}

	//Used to parse Creature Type settings
	private void setTypeSettings(String line) {
		parser.setTypeSettings(line);
		for (String s : parser.log)
			warn(s, '~' + line);
	}

	private void warn(String message, String line) {
		MobSpawnControls.getLogger().warning(message + " in " + fileName + ".cfg:" + lineNum + " \"" + line + "\"");
	}

	private void warn(String message) {
		MobSpawnControls.getLogger().warning(message + " in " + fileName + ".cfg:" + lineNum);
	}

	//Used to write a line multiple times, as indicated by num
	private void writeLines(BufferedWriter writer, String line, int num) throws IOException {
		for (int i = 0; i < num; ++i) {
			writer.write(line);
			writer.newLine();
		}
	}
	
	public BiomeGroup getBiomeGroup(String name) {
		for (BiomeGroup group : groups)
			if (group.getName().equalsIgnoreCase(name))
				return group;
		return null;
	}

	//Used to write creature type config
	private String getTypeString(EnumCreatureType type) {
		String s = "~" + (type == null ? "UNKNOWN" : type) + ":";
		List<String> names = getEntityNames(type);
		for (int i = 0; i < names.size(); ++i) {
			s += names.get(i);
			if (i != names.size() - 1) //Last one does not get a comma
				s += ",";
		}
		return s;
	}

	//Return the simplified Entity name
	public String getEntityName(Class<? extends EntityLiving> cls) {
		String s = (String) EntityList.classToStringMapping.get(cls);
		return (container == null) ? s : s.replace(configName + ".", "");
	}
	
	public Class<? extends EntityLiving> getEntityClass(String s, EnumCreatureType type) {
		Class<? extends EntityLiving> clazz = getEntityClass(s);
		if (clazz != null && typeMap.get(clazz) != type) //Mapped to different type
			clazz = null;
		return clazz;
	}

	public Class<? extends EntityLiving> getEntityClass(String s) {
		//Check for vanilla and Forge-registered entities
		String entityName = s;
		if (container != null)
			entityName = configName + "." + s;

		Class c = (Class) EntityList.stringToClassMapping.get(entityName);
		if (ModEntityRecognizer.isValidEntityClass(c)) {
			if (entityClasses.contains(c)) 
				return c;
		}

		//Check for non Forge-registered entities
		if (container != null) { //Don't check again for vanilla
			c = (Class) EntityList.stringToClassMapping.get(s);
			if (ModEntityRecognizer.isValidEntityClass(c)) {
				if (entityClasses.contains(c)) 
					return c;
			}
		}
		return null;
	}


	//Methods used for networking/gui
	public List<String> getEntityNames(EnumCreatureType type) {
		List<String> strings = new ArrayList<String>();
		List<Class<? extends EntityLiving>> cls = typeMap.getEntitiesOfType(type);
		for (Class<? extends EntityLiving> c : cls)
			strings.add(getEntityName(c));
		Collections.sort(strings);
		return strings;
	}

	public void processGroupCommand(String command) {
		String[] args = command.split(",");
		String id = args[0];
		if (id.equalsIgnoreCase("ren")) { //Rename group
			String old = args[1], newN = args[2];
			for (Iterator<BiomeGroup> it = groups.iterator(); it.hasNext();) {
				BiomeGroup cur = it.next();
				if (cur.getName().equalsIgnoreCase(old)) {
					cur.setName(newN);
					break;
				}
			}
			active.renameGroup(old, newN);
		}
		if (id.equalsIgnoreCase("del")) { //Delete group
			String name = args[1];
			for (Iterator<BiomeGroup> it = groups.iterator(); it.hasNext();)
				if (it.next().getName().equalsIgnoreCase(name)) {
					it.remove();
					break;
				}
			active.removeGroup(name);
		}
		if (id.equalsIgnoreCase("add")) { //Add group
			String name = args[1];
			BiomeGroup group = new BiomeGroup(name);
			groups.add(group);
			active.addGroup(group);
		}
	}

}
