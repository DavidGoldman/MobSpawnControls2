package com.mcf.davidee.msc.grouping;

import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;

import com.mcf.davidee.msc.BiomeNameHelper;
import com.mcf.davidee.msc.Utils;

public class BiomeOperand {

	public final BiomeOperator operator;

	private BiomeGroup group;
	private BiomeGenBase biome;
	private boolean isGroup, isAll;

	public BiomeOperand() {
		this.operator = BiomeOperator.ALL;
		isAll = true;
	}

	public BiomeOperand(BiomeOperator op, BiomeGroup group) {
		this.operator = op;
		this.group = group;
		isGroup = true;
	}

	public BiomeOperand(BiomeOperator op, BiomeGenBase biome) {
		this.operator = op;
		this.biome = biome;
	}

	public Set<BiomeGenBase> getBiomes() {
		if (isAll)
			return BiomeNameHelper.getAllBiomes();
		if (isGroup)
			return group.evaluate();
		else
			return Utils.asSet(biome);
	}

	public String toString() {
		if (isAll)
			return "*";
		return operator.name + ((isGroup) ? group.getName() : BiomeNameHelper.getBiomeName(biome));
	}

	public BiomeOperand clone() {
		if (isAll)
			return new BiomeOperand();
		if (isGroup)
			return new BiomeOperand(operator, group.clone());
		return new BiomeOperand(operator, biome);
	}
}
