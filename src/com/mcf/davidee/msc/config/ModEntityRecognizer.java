package com.mcf.davidee.msc.config;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;

import com.google.common.collect.ListMultimap;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;

public class ModEntityRecognizer {

	private static ListMultimap<ModContainer, EntityRegistration> registr = getRegistrations();

	public static List<EntityRegistration> getRegistrations(ModContainer c) {
		return registr.get(c);
	}

	private static ListMultimap<ModContainer, EntityRegistration> getRegistrations() {
		return ObfuscationReflectionHelper.getPrivateValue(EntityRegistry.class, EntityRegistry.instance(), "entityRegistrations");
	}

	@SuppressWarnings("unchecked")
	public static List<Class<? extends EntityLiving>> getEntityClasses(ModContainer c) {
		if (c == null)
			return getVanillaEntityClasses();
		List<EntityRegistration> regs = getRegistrations(c);
		List<Class<? extends EntityLiving>> cls = new ArrayList<Class<? extends EntityLiving>>(regs.size());
		for (EntityRegistration r : regs) {
			if (isValidEntityClass(r.getEntityClass()))
				cls.add((Class<? extends EntityLiving>) r.getEntityClass());
		}
		return cls;
	}

	@SuppressWarnings("unchecked")
	private static List<Class<? extends EntityLiving>> getVanillaEntityClasses() {
		List<Class<? extends EntityLiving>> cls = new ArrayList<Class<? extends EntityLiving>>();
		for (Object o : EntityList.classToStringMapping.entrySet()) {
			Entry<Class<? extends Entity>, String> entry = (Entry<Class<? extends Entity>, String>) o;
			if (isValidEntityClass(entry.getKey()) && EntityRegistry.instance().lookupModSpawn(entry.getKey(),false) == null)
				cls.add((Class<? extends EntityLiving>) entry.getKey());
		}
		return cls;

	}

	public static boolean hasEntities(ModContainer c) {
		for (EntityRegistration r : getRegistrations(c)) 
			if (isValidEntityClass(r.getEntityClass()))
				return true;
		return false;
	}

	public static boolean isValidEntityClass(Class<?> c) {
		return c != null && !Modifier.isAbstract(c.getModifiers()) && EntityLiving.class.isAssignableFrom(c);
	}

}
