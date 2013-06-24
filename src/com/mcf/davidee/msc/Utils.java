package com.mcf.davidee.msc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {
	
	public static int parseIntWithMinMax(String s, int min, int max) throws NumberFormatException {
		int i = Integer.parseInt(s);
		if (i < min)
			return min;
		if (i > max)
			return max;
		return i;

	}

	public static int parseIntDMinMax(String s, int _default, int min, int max) {
		try {
			return parseIntWithMinMax(s, min, max);
		} catch (NumberFormatException e) {
			return _default;
		}
	}

	public static <E> Set<E> asSet(E... arr) {
		Set<E> set = new HashSet<E>();
		for (E obj : arr)
			set.add(obj);
		return set;
	}

	public static <E> List<E> asList(E... arr) {
		List<E> list = new ArrayList<E>();
		for (E obj : arr)
			list.add(obj);
		return list;
	}

	public static <T> void copyInto(List<T> source, List<T> dest) {
		dest.clear();
		for (T obj : source)
			dest.add(obj);
	}

	public static void writeLine(BufferedWriter writer, String line) throws IOException {
		writer.write(line);
		writer.newLine();
	}
}
