package com.mcf.davidee.msc.spawning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;

public class CreatureTypeMap {
	private Map<Class<? extends EntityLiving>, EnumCreatureType> map;
	
	public CreatureTypeMap() {
		map = new HashMap<Class<? extends EntityLiving>, EnumCreatureType>();
	}

	public CreatureTypeMap(List<Class<? extends EntityLiving>> list) {
		map = new HashMap<Class<? extends EntityLiving>, EnumCreatureType>();
		generateDefault(list);
	}
	
	public CreatureTypeMap(CreatureTypeMap otherMap) {
		map = new HashMap<Class<? extends EntityLiving>, EnumCreatureType>();
		for (Class<? extends EntityLiving> cls : otherMap.map.keySet())
			map.put(cls, otherMap.map.get(cls));
	}

	
	
	public List<Class<? extends EntityLiving>> getEntitiesOfType(EnumCreatureType type) {
		List<Class<? extends EntityLiving>> list = new ArrayList<Class<? extends EntityLiving>>();
		for (Entry<Class<? extends EntityLiving>, EnumCreatureType> entry : map.entrySet()) 
			if (entry.getValue() == type)
				list.add(entry.getKey());
		return list;
	}

	public void generateDefault(List<Class<? extends EntityLiving>> list) {
		map.clear();
		for (Class<? extends EntityLiving> cls : list) 
			map.put(cls, MobHelper.getDefaultMobType(cls));
	}

	public void setType(EnumCreatureType type, List<Class<? extends EntityLiving>> cls) {
		for (Entry<Class<? extends EntityLiving>, EnumCreatureType> entry : map.entrySet()) 
			if (entry.getValue() == type)
				entry.setValue(null);
		
		for (Class<? extends EntityLiving> c : cls)
			map.put(c, type);
	}
	
	public void set(Class<? extends EntityLiving> cls, EnumCreatureType type) {
		map.put(cls, type);
	}

	public EnumCreatureType get(Class<? extends EntityLiving> cls) {
		return map.get(cls);
	}

}
