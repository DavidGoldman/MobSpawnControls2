package com.mcf.davidee.msc.config.parser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;

import com.google.common.base.Strings;
import com.mcf.davidee.msc.BiomeNameHelper;
import com.mcf.davidee.msc.Utils;
import com.mcf.davidee.msc.config.ModConfig;
import com.mcf.davidee.msc.grouping.BiomeGroup;
import com.mcf.davidee.msc.grouping.BiomeOperand;
import com.mcf.davidee.msc.grouping.BiomeOperator;
import com.mcf.davidee.msc.spawning.MobHelper;

public class ModConfigParser {

	public final List<String> log;
	public final ModConfig config;
	
	public ModConfigParser(ModConfig config) {
		this.config = config;
		this.log = new ArrayList<String>();
	}

	public BiomeGroup readBiomeGroup(String line) {
		log.clear();

		int index = line.indexOf('='); 
		
		if (index != -1) { //Group definition must contain a '='
			String groupName = line.substring(0, index);
			if (config.getBiomeGroup(groupName) != null) { //Cannot have a group with the same name (case insensitive)
				log.add("Duplicate group name \"" + groupName + "\"");
				return null;
			} //Group cannot be 'master' nor can it be longer than 23 characters or comprised of invalid characters
			if (!NameVerifier.isValidGroupName(groupName)) { 
				log.add("Invalid group name \"" + groupName + "\"");
				return null;
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
					log.add("Unknown operator \"" + s.charAt(0) + "\"");
				else if (op == BiomeOperator.ALL) 
					if (i == 0)
						ops.add(new BiomeOperand());
					else //Op can only be used at the start of a group definition
						log.add("The * operator must be at the start of a group definition");
				else { //Add or subtract op
					String name = s.substring(1);

					if (groupName.equalsIgnoreCase(name)) //Prevent self-references
						log.add("The biome group \"" + groupName + "\" tries to reference itself!");
					else {
						if (name.indexOf('.') != -1) { //Biome
							BiomeGenBase biome = BiomeNameHelper.getBiome(name);
							if (biome == null)
								log.add("Unknown biome \"" + name + "\"");
							else
								ops.add(new BiomeOperand(op, biome));
						} 
						else { //Group Name
							BiomeGroup group = config.getBiomeGroup(name);
							if (group == null)
								log.add("Unknown group \"" + name + "\"");
							else
								ops.add(new BiomeOperand(op, group));
						}
					}
				}
			}
			
			return new BiomeGroup(groupName, ops);
		} 
		else { //Does not contain '='
			log.add("Invalid group declaration");
			return null;
		}
	}

	public void readBiomeSettings(String line) {
		log.clear();
		
		int index = line.indexOf(':'); //Separates name+type from definition
		if (index != -1) {
			String biomeType = line.substring(0, index);
			int indexOfType = biomeType.indexOf('~'); //Separates name from type
			if (indexOfType != -1) {
				String biomeName = biomeType.substring(0, indexOfType);
				String type = biomeType.substring(indexOfType + 1);
				String entities = line.substring(index + 1);

				BiomeGenBase biome = (biomeName.equalsIgnoreCase("master")) ? null : BiomeNameHelper.getBiome(biomeName);
				BiomeGroup group = (biome == null && !biomeName.equalsIgnoreCase("master")) ? config.getBiomeGroup(biomeName) : null;
				EnumCreatureType creatureType = MobHelper.typeOf(type);

				//Biome/Master setting, creature type is valid
				if ((biome != null || biomeName.equalsIgnoreCase("master")) && creatureType != null)
					setBiomeSettings(biome, creatureType, entities.split(","));
				else if (group != null && creatureType != null) //Group setting, creature type is valid
					setGroupSettings(group, creatureType, entities.split(","));
				else { //Warnings
					if (type == null)
						log.add("Invalid creature type \"" + type + "\"");
					if (biome == null)
						log.add("Invalid biome/group \"" + biomeName + "\"");
				}
			} else
				log.add("Invalid configuration setting/command");
		} else
			log.add("Invalid configuration setting/command");
	}

	private void setGroupSettings(BiomeGroup group, EnumCreatureType creatureType, String[] entities) {
		List<SpawnListEntry> list = new ArrayList<SpawnListEntry>();
		for (String ent : entities) {
			SpawnListEntry entry = getEntry(ent, creatureType);
			if (entry != null)
				list.add(entry);
		}
		config.getSpawnMap().setGroupSection(group, creatureType, list);
	}

	private void setBiomeSettings(BiomeGenBase biome, EnumCreatureType creatureType, String[] entities) {
		List<SpawnListEntry> list = new ArrayList<SpawnListEntry>();
		for (String ent : entities) {
			SpawnListEntry entry = getEntry(ent, creatureType);
			if (entry != null)
				list.add(entry);
		}
		config.getSpawnMap().setBiomeSection(biome, creatureType, list);
	}
	
	//Parses a string 'MobName(Weight-MinGroup-MaxGroup)' or 'MobName'
	private SpawnListEntry getEntry(String ent, EnumCreatureType creatureType) {
		if (!ent.isEmpty()) {
			int startP = ent.indexOf('(');
			int endP = ent.indexOf(')');
			if (startP != -1 && endP != -1) { // 3 args, with ( )
				String entName = ent.substring(0, startP);
				String[] args = ent.substring(startP + 1, endP).split("-");
				if (args.length == 3) {
					int _weight = Utils.parseIntDMinMax(args[0], config.weight, 1, 100);
					int _min = Utils.parseIntDMinMax(args[1], config.min, 1, 10);
					int _max = Utils.parseIntDMinMax(args[2], config.max, 1, 15);
					Class<? extends EntityLiving> entityClass = config.getEntityClass(entName, creatureType);
					if (entityClass != null)
						return new SpawnListEntry(entityClass, _weight, _min, _max);
					else
						log.add("Unknown Entity \"" + ent + "\"");
				} else
					log.add("Invalid Entity setting \"" + ent + "\"");
			} else { // No args, just entity class name
				Class<? extends EntityLiving> entityClass = config.getEntityClass(ent, creatureType);
				if (entityClass != null)
					return new SpawnListEntry(entityClass, config.weight, config.min, config.max);
				else
					log.add("Unknown Entity \"" + ent + "\"");
			}

		}
		return null;
	}
	
	public void setTypeSettings(String line) {
		log.clear();
		
		int index = line.indexOf(':'); //Separates creature type from definition
		if (index != -1) {
			String pre = line.substring(0, index);
			String[] creatureNames = line.substring(index + 1).split(",");
			if (!pre.equals("UNKNOWN")) { //Unknown denotes a 'warning' from our mod, noting that an entity 
				// was not able to be automatically grouped
				EnumCreatureType type = MobHelper.typeOf(pre);
				if (type == null)
					log.add("Invalid Creature Type \"" + pre + "\"");
				else { //Valid Creature type, now parse entities
					List<Class<? extends EntityLiving>> classes = new ArrayList<Class<? extends EntityLiving>>();
					
					for (String s : creatureNames) {
						if (Strings.isNullOrEmpty(s)) 
							continue;
						Class<? extends EntityLiving> c = config.getEntityClass(s);
						if (c == null)
							log.add("Unknown Entity \"" + s + "\"");
						else
							classes.add(c);
					}
					config.getTypeMap().setType(type, classes);
				}
			}
		} else
			log.add("Expected character ':'");
	}

}
