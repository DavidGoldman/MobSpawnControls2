package com.mcf.davidee.msc.grouping;

public enum BiomeOperator {
	ADD('+'), SUBTRACT('-'), ALL('*');

	public final char name;

	private BiomeOperator(char c) {
		this.name = c;
	}

	public static BiomeOperator operatorOf(char c) {
		for (BiomeOperator op : values())
			if (op.name == c)
				return op;
		return null;
	}
}