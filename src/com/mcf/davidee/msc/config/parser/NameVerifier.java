package com.mcf.davidee.msc.config.parser;

import com.google.common.base.Strings;

public class NameVerifier {
	
	public static boolean isValidGroupName(String groupName) {
		if (Strings.isNullOrEmpty(groupName) || groupName.equalsIgnoreCase("master") || groupName.length() > 23)
			return false;
		
		for (char c : groupName.toCharArray()) 
			if (!Character.isLetterOrDigit(c))
				return false;
		return true;
	}
	
}
