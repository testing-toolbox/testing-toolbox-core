package org.testing.toolbox;

import java.util.HashMap;
import java.util.Map;

/**
 * Replacements class (fluent coding style).
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class Replacements {
	private Map<String, String> mapReplacements;

	/**
	 * Private constructor : use the newInstance methode.
	 */
	private Replacements() {
		mapReplacements = new HashMap<>();
	}

	/**
	 * New instance.
	 * 
	 * @return Replacements
	 */
	public static Replacements newInstance() {
		return new Replacements();
	}

	/**
	 * Adding replacement.
	 * 
	 * @param key
	 * @param value
	 * @return Replacements
	 */
	public Replacements add(String key, String value) {
		mapReplacements.put(key, value);
		return this;
	}

	/**
	 * Return a map of replacements.
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> toMap() {
		return new HashMap<>(mapReplacements);
	}
}
