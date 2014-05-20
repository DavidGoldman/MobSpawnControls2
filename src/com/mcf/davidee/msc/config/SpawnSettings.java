package com.mcf.davidee.msc.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import net.minecraft.entity.EnumCreatureType;

import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.Utils;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;
import com.mcf.davidee.msc.packet.settings.SettingsPacket;
import com.mcf.davidee.msc.spawning.MobHelper;
import com.mcf.davidee.msc.spawning.SpawnCapHelper;

public class SpawnSettings {

	private File file;
	private boolean masterEnabled, readOnly, canEdit;
	private int[] defaultCaps, spawnCaps;
	private int creatureFreq;

	public SpawnSettings (File folder) {
		file = new File(folder,"Settings.properties");
		defaultCaps = new int[] {EnumCreatureType.monster.getMaxNumberOfCreature(),
				EnumCreatureType.creature.getMaxNumberOfCreature(), 
				EnumCreatureType.ambient.getMaxNumberOfCreature(),
				EnumCreatureType.waterCreature.getMaxNumberOfCreature()
		};
		spawnCaps = Arrays.copyOf(defaultCaps, defaultCaps.length);
		creatureFreq = 400;
		canEdit = true;
	}

	public SpawnSettings(File folder, SpawnSettings other) {
		file = new File(folder,"Settings.properties");
		defaultCaps = Arrays.copyOf(other.spawnCaps, other.spawnCaps.length);
		spawnCaps = Arrays.copyOf(defaultCaps, defaultCaps.length);
		masterEnabled = other.masterEnabled;
		creatureFreq = other.creatureFreq;
		canEdit = other.canEdit;
	}

	public boolean isMasterEnabled() {
		return masterEnabled;
	}
	
	public int getCreatureFrequency() {
		return creatureFreq;
	}
	
	public boolean canEdit() {
		return canEdit;
	}

	public void reset() {
		masterEnabled = false;
		spawnCaps = Arrays.copyOf(defaultCaps, defaultCaps.length);
		for (EnumCreatureType type : EnumCreatureType.values())
			SpawnCapHelper.setSpawnCap(type, spawnCaps[type.ordinal()]);
	}

	public void save() {
		BufferedWriter writer = null;
		if (readOnly)
			return;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			Utils.writeLine(writer,"# Mob Spawn Controls Settings");
			Utils.writeLine(writer,"#");
			Utils.writeLine(writer,"# This file is used to keep track of various MSC settings");
			Utils.writeLine(writer,"#");
			Utils.writeLine(writer,"# readOnly=(true/false)");
			Utils.writeLine(writer,"#");
			Utils.writeLine(writer, "readOnly=" + readOnly);
			Utils.writeLine(writer,"#");
			Utils.writeLine(writer,"# canEditInGame=(true/false)");
			Utils.writeLine(writer,"#");
			Utils.writeLine(writer, "canEditInGame=" + canEdit);
			Utils.writeLine(writer,"#");
			Utils.writeLine(writer,"# masterEnabled=(true/false)");
			Utils.writeLine(writer,"#");
			Utils.writeLine(writer, "masterEnabled=" + masterEnabled);
			Utils.writeLine(writer,"#");
			Utils.writeLine(writer,"# creatureSpawnFrequency=(int)");
			Utils.writeLine(writer,"#");
			Utils.writeLine(writer, "creatureSpawnFrequency=" + creatureFreq);
			Utils.writeLine(writer,"#");
			Utils.writeLine(writer,"# Cap.(monster/creature/waterCreature/ambient)=(int)");
			Utils.writeLine(writer,"#");
			Utils.writeLine(writer,"Cap.monster=" + spawnCaps[0]);
			Utils.writeLine(writer,"Cap.creature=" + spawnCaps[1]);
			Utils.writeLine(writer,"Cap.ambient=" + spawnCaps[2]);
			Utils.writeLine(writer,"Cap.waterCreature=" + spawnCaps[3]);
			Utils.writeLine(writer,"#");
		}
		catch(IOException e){
			MobSpawnControls.getLogger().throwing("SpawnSettings", "save", e);
		}
		finally{
			if (writer != null){
				try { writer.close(); }
				catch(IOException e){ }
			}
		}
	}

	public void load() {
		if (file.exists()){
			BufferedReader reader = null;
			try{
				reader = new BufferedReader(new FileReader(file));
				int num = 1;
				while(reader.ready()){
					String line = reader.readLine();
					if (!line.startsWith("#")){
						int index = line.indexOf('=');
						if (index == -1)
							warn("Expected character '='",num,line);
						else
							setVariable(line.substring(0,index),line.substring(index+1),num);
					}
					++num;
				}
			}
			catch(IOException e){
				MobSpawnControls.getLogger().throwing("SpawnSettings", "load", e);
			}
			finally{
				if (reader != null){
					try { reader.close(); } 
					catch(IOException e) { }
				}
			}
		}
		for (EnumCreatureType type : EnumCreatureType.values())
			SpawnCapHelper.setSpawnCap(type, spawnCaps[type.ordinal()]);
	}

	private void warn(String message, int num, String line) {
		MobSpawnControls.getLogger().warning(message + " in Settings.properties:" + num + " \"" + line + "\"");
	}

	//TODO Perhaps map values?
	private void setVariable(String key, String value, int line) {
		if (key.equalsIgnoreCase("masterenabled"))
			masterEnabled = "true".equalsIgnoreCase(value);
		else if (key.equalsIgnoreCase("readonly"))
			readOnly = "true".equalsIgnoreCase(value);
		else if (key.equalsIgnoreCase("creaturespawnfrequency"))
			creatureFreq = Utils.parseIntDMinMax(value, 400, 1, 400);
		else if (key.equalsIgnoreCase("caneditingame"))
			canEdit = "true".equalsIgnoreCase(value);
		else if (key.startsWith("Cap.")){
			key = key.substring(4);
			EnumCreatureType type = MobHelper.typeOf(key);
			if (type == null)
				warn("Invalid Creature Type \"" + key + "\"", line, "Cap." + key + "=" + value);
			else{
				try {
					spawnCaps[type.ordinal()] = Utils.parseIntWithMinMax(value, 1, 200);
				}
				catch(NumberFormatException e){
					warn(key + " is not a valid integer!", line, "Cap." + key + "=" + value);
				}
			}

		}
		else
			warn("Unknown variable setting \"" + key + "\"", line, key + "="+ value);
	}

	public MSCPacket createPacket() {
		return MSCPacket.getPacket(PacketType.SETTINGS, readOnly, masterEnabled, spawnCaps, creatureFreq);
	}

	public void readPacket(SettingsPacket pkt) {
		readOnly = pkt.readOnly;
		masterEnabled = pkt.masterEnabled;
		spawnCaps = pkt.caps;
		creatureFreq = pkt.creatureFreq;
		for (EnumCreatureType type : EnumCreatureType.values())
			SpawnCapHelper.setSpawnCap(type, spawnCaps[type.ordinal()]);
		save();
	}


}
