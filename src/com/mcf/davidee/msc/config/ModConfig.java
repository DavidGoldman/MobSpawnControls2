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
	public final String fileName, configName;

	private List<BiomeGroup> groups;
	private CreatureTypeMap typeMap;
	private SpawnMap active, _default;
	private boolean readOnly = false;
	private int lineNum;
	private int weight, min, max;


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
	}

	public SpawnMap getSpawnMap() {
		return active;
	}

	public CreatureTypeMap getTypeMap() {
		return typeMap;
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

	private boolean groupNameValid(String group) {
		if (group.length() > 23)
			return false;
		for (char c : group.toCharArray()) 
			if (!Character.isLetterOrDigit(c))
				return false;
		return true;
	}

	//Current groups cannot reference groups that have not yet been read.
	//This is not so much a bug as it is a safety measure, which prevents groups from
	//referencing each other in a chain and causing a recursion crash
	private void readGroup(String line) {
		int index = line.indexOf('='); 
		if (index != -1) { //Group definition must contain a '='
			String groupName = line.substring(0, index);
			if (getGroup(groupName) != null) { //Cannot have a group with the same name (case insensitive)
				warn("Duplicate group name \"" + groupName + "\"", '&' + line);
				return;
			} //Group cannot be 'master' nor can it be longer than 23 characters or comprised of invalid characters
			if (groupName.equalsIgnoreCase("master") || !groupNameValid(groupName)) { 
				warn("Invalid group name \"" + groupName + "\"", '&' + line);
				return;
			}
			Set<BiomeOperand> ops = new LinkedHashSet<BiomeOperand>();
			//Parse separated by commas
			String[] parts = line.substring(index + 1).split(",");
			for (int i = 0; i < parts.length; ++i) {
				String s = parts[i];
				if (Strings.isNullOrEmpty(s))
					continue;
				BiomeOperator op = BiomeOperator.operatorOf(s.charAt(0));
				if (op == null) //Must be valid op
					warn("Unknown operator \"" + s.charAt(0) + "\"", '&' + line);
				else if (op == BiomeOperator.ALL) {
					if (i == 0)
						ops.add(new BiomeOperand());
					else //Op can only be used at the start of a group definition
						warn("The * operator must be at the start of a group definition", '&' + line);
				} else { //Add or subtract op
					String name = s.substring(1);
					if (groupName.equalsIgnoreCase(name)) //Prevent self-references
						warn("The biome group \"" + groupName + "\" tries to reference itself!", '&' + line);
					else {
						if (name.indexOf('.') != -1) { //Biome
							BiomeGenBase biome = BiomeNameHelper.getBiome(name);
							if (biome == null)
								warn("Unknown biome \"" + name + "\"", '&' + line);
							else
								ops.add(new BiomeOperand(op, biome));
						} else { //Group Name
							BiomeGroup group = getGroup(name);
							if (group == null)
								warn("Unknown group \"" + name + "\"", '&' + line);
							else
								ops.add(new BiomeOperand(op, group));
						}
					}
				}
			}
			//Finally done, add the group
			BiomeGroup group = new BiomeGroup(groupName, ops);
			groups.add(group);
			active.addGroup(group);
		} else //Does not contain '='
			warn("Invalid group declaration", '&' + line);
	}

	//Used when loading settings
	private BiomeGroup getGroup(String name) {
		for (BiomeGroup group : groups)
			if (group.getName().equalsIgnoreCase(name))
				return group;
		return null;
	}

	//Sets group OR biome setting
	private void readBiomeSettings(String line) {
		int index = line.indexOf(':'); //Separates name+type from definition
		if (index != -1) {
			String biomeType = line.substring(0, index);
			int indexOfType = biomeType.indexOf('~'); //Separates name from type
			if (indexOfType != -1) {
				String biomeName = biomeType.substring(0, indexOfType);
				String type = biomeType.substring(indexOfType + 1);
				String entities = line.substring(index + 1);
				
				BiomeGenBase biome = (biomeName.equalsIgnoreCase("master")) ? null
						: BiomeNameHelper.getBiome(biomeName);
				BiomeGroup group = (biome == null) ? getGroup(biomeName) : null;
				EnumCreatureType creatureType = MobHelper.typeOf(type);
				
				//Biome/Master setting, creature type is valid
				if ((biome != null || biomeName.equalsIgnoreCase("master")) && creatureType != null)
					setBiomeSettings(biome, creatureType, entities.split(","));
				else if (group != null && creatureType != null) //Group setting, creature type is valid
					setGroupSettings(group, creatureType, entities.split(","));
				else { //Warnings
					if (type == null)
						warn("Invalid creature type \"" + type + "\"", line);
					if (biome == null)
						warn("Invalid biome/group \"" + biomeName + "\"", line);
				}
			} else
				warn("Invalid configuration setting/command", line);
		} else
			warn("Invalid configuration setting/command", line);
	}

	private void setGroupSettings(BiomeGroup group, EnumCreatureType type, String[] entities) {
		List<SpawnListEntry> list = new ArrayList<SpawnListEntry>();
		for (String ent : entities) {
			SpawnListEntry entry = getEntry(ent);
			if (entry != null)
				list.add(entry);
		}
		active.setGroupSection(group, type, list);
	}

	private void setBiomeSettings(BiomeGenBase biome, EnumCreatureType type, String[] entities) {
		List<SpawnListEntry> list = new ArrayList<SpawnListEntry>();
		for (String ent : entities) {
			SpawnListEntry entry = getEntry(ent);
			if (entry != null)
				list.add(entry);
		}
		active.setBiomeSection(biome, type, list);
	}

	//Parses a string 'MobName(Weight-MinGroup-MaxGroup)' or 'MobName'
	private SpawnListEntry getEntry(String ent) {
		if (!ent.isEmpty()) {
			int startP = ent.indexOf('(');
			int endP = ent.indexOf(')');
			if (startP != -1 && endP != -1) { // 3 args, with ( )
				String entName = ent.substring(0, startP);
				String[] args = ent.substring(startP + 1, endP).split("-");
				if (args.length == 3) {
					int _weight = Utils.parseIntDMinMax(args[0], weight, 1, 100);
					int _min = Utils.parseIntDMinMax(args[1], min, 1, 10);
					int _max = Utils.parseIntDMinMax(args[2], max, 1, 15);
					Class<? extends EntityLiving> entityClass = getEntityClass(entName);
					if (entityClass != null)
						return new SpawnListEntry(entityClass, _weight, _min, _max);
					else
						warn("Unknown Entity \"" + ent + "\"");
				} else
					warn("Invalid Entity setting \"" + ent + "\"");
			} else { // No args, just entity class name
				Class<? extends EntityLiving> entityClass = getEntityClass(ent);
				if (entityClass != null)
					return new SpawnListEntry(entityClass, weight, min, max);
				else
					warn("Unknown Entity \"" + ent + "\"");
			}

		}
		return null;
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
		int index = line.indexOf(':'); //Separates creature type from definition
		if (index != -1) {
			String pre = line.substring(0, index);
			String[] creatureNames = line.substring(index + 1).split(",");
			if (!pre.equals("UNKNOWN")) { //Unknown denotes a 'warning' from our mod, noting that an entity 
				                          // was not able to be automatically grouped
				EnumCreatureType type = MobHelper.typeOf(pre);
				if (type == null)
					warn("Invalid Creature Type \"" + pre + "\"", "~" + line);
				else { //Valid Creature type, now parse entities
					List<Class<? extends EntityLiving>> cls = new ArrayList<Class<? extends EntityLiving>>();
					for (String s : creatureNames) {
						if (Strings.isNullOrEmpty(s)) 
							continue;
						Class<? extends EntityLiving> c = getEntityClass(s);
						if (c == null)
							warn("Unknown Entity \"" + s + "\"", "~" + line);
						else
							cls.add(c);
					}
					typeMap.setType(type, cls);
				}
			}
		} else
			warn("Expected character ':'", "~" + line);
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
	private String getEntityName(Class<? extends EntityLiving> cls) {
		String s = (String) EntityList.classToStringMapping.get(cls);
		return (container == null) ? s : s.replace(configName + ".", "");
	}

	public Class<? extends EntityLiving> getEntityClass(String s) {
		//Check for vanilla and Forge-registered entities
		String entityName = s;
		if (container != null)
			entityName = configName + "." + s;
		
		Class c = (Class) EntityList.stringToClassMapping.get(entityName);
		if (ModEntityRecognizer.isValidEntityClass(c)) {
			if (entityClasses.contains(c)) //Make sure it's one of our own classes
				return c;
			else { //Warn them
				warn("Reference to outside entity \"" + entityName + "\"");
				return null;
			}
		}
		
		//Check for non Forge-registered entities
		if (container != null) { //Don't check again for vanilla
			c = (Class) EntityList.stringToClassMapping.get(s);
			if (ModEntityRecognizer.isValidEntityClass(c)) {
				if (entityClasses.contains(c)) //Make sure it's one of our own classes
					return c;
				else { //Warn them
					warn("Reference to outside entity \"" + entityName + "\"");
					return null;
				}
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

	public List<BiomeGroup> getBiomeGroups() {
		return groups;
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

	public Packet getEntitySettingsPacket(String entity) {
		Class entityClass = getEntityClass(entity);
		return active.getEntitySettingPacket(configName, entity, entityClass);
	}

}
