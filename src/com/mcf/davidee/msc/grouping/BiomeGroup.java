package com.mcf.davidee.msc.grouping;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGroup {

	private String name;
	private Set<BiomeOperand> ops;

	public BiomeGroup(String name) {
		this.name = name;
		ops = new LinkedHashSet<BiomeOperand>();
	}

	public BiomeGroup(String name, Set<BiomeOperand> ops) {
		this.name = name;
		this.ops = ops;
	}

	public Set<BiomeGenBase> evaluate() {
		Set<BiomeGenBase> biomes = new HashSet<BiomeGenBase>();
		for (BiomeOperand op : ops) {
			switch (op.operator) {
			case ADD:
			case ALL:
				biomes.addAll(op.getBiomes());
				break;
			case SUBTRACT:
				biomes.removeAll(op.getBiomes());
				break;
			}
		}
		return biomes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOps(Set<BiomeOperand> ops) {
		this.ops = ops;
	}

	public String toString() {
		String str = name + "=";
		for (Iterator<BiomeOperand> it = ops.iterator(); it.hasNext();) {
			str += it.next().toString();
			if (it.hasNext())
				str += ',';
		}
		return str;
	}

	public BiomeGroup clone() {
		Set<BiomeOperand> newOps = new LinkedHashSet<BiomeOperand>();
		for (BiomeOperand op : ops)
			newOps.add(op.clone());
		return new BiomeGroup(name, newOps);
	}
}
