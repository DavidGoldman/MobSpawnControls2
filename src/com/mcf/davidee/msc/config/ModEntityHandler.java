package com.mcf.davidee.msc.config;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class ModEntityHandler {
	
	private static List<ModContainer> entityMods = null;

	public static List<ModContainer> getEntityMods() {
		if (entityMods == null) {
			entityMods = new ArrayList<ModContainer>();
			for (ModContainer c : Loader.instance().getActiveModList())
				if (ModEntityRecognizer.hasEntities(c))
					entityMods.add(c);
		}
		return entityMods;
	}

}
