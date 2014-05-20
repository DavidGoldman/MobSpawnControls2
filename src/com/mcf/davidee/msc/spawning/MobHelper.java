package com.mcf.davidee.msc.spawning;

import java.util.Set;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

import com.mcf.davidee.msc.config.ModEntityRecognizer;
import com.mcf.davidee.msc.reflect.BiomeReflector;

public class MobHelper {
	
	private static CreatureTypeMap defaultMap = new CreatureTypeMap();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void populateDefaultMap(){
		for (Class c: (Set<Class>)EntityList.classToStringMapping.keySet()){
			
			if (ModEntityRecognizer.isValidEntityClass(c)){
				boolean found = false;
				outer:
				for (BiomeGenBase biome: BiomeGenBase.getBiomeGenArray()){ 
					if (biome != null){
						for (EnumCreatureType type : EnumCreatureType.values()){ //Inner For
							for (SpawnListEntry entry : BiomeReflector.reflectList(biome, type)){
								if (entry.entityClass == c){
									defaultMap.set(c, type);
									found = true;
									break outer;
								}
							}
						} // End Inner For
					}
					
				} //End Outer For
				
				if (!found)
					defaultMap.set(c, null);
				
			}
			
		}
	}
	
	public static EnumCreatureType getDefaultMobType(Class<? extends EntityLiving> cls) {
		EnumCreatureType type = defaultMap.get(cls);
		if (type == null) {
			if (EntityAmbientCreature.class.isAssignableFrom(cls))
				return EnumCreatureType.ambient;
			if (EntityWaterMob.class.isAssignableFrom(cls))
				return EnumCreatureType.waterCreature;
			if (IMob.class.isAssignableFrom(cls))
				return EnumCreatureType.monster;
			if (EntityCreature.class.isAssignableFrom(cls))
				return EnumCreatureType.creature;
		}
		return type;
	}

	public static EnumCreatureType typeOf(String s) {
		for (EnumCreatureType t : EnumCreatureType.values())
			if (t.toString().equalsIgnoreCase(s))
				return t;
		return null;
	}
}
