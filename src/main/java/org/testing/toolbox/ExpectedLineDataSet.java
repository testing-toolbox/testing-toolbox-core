package org.testing.toolbox;

import static org.apache.commons.lang3.StringUtils.join;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Object representation of a row in database
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class ExpectedLineDataSet {
	private String tableName;
	private Map<String, String> attributes;

	/**
	 * Constructor.
	 * 
	 * @param tableName
	 */
	public ExpectedLineDataSet(String tableName) {
		this.tableName = tableName;
		attributes = new HashMap<String, String>();
	}

	/**
	 * Add attribute (expected column value).
	 * 
	 * @param name
	 * @param value
	 */
	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}

	/**
	 * Build expected Xpath query for an extracted dataset.
	 * 
	 * @return String
	 */
	public String buildXpath() {
		String xpath = "/dataset/" + tableName;

		if (!attributes.isEmpty()) {
			xpath += "[";

			String key;
			Set<String> keys = attributes.keySet();
			Iterator<String> it = keys.iterator();
			List<String> lstConditions = new ArrayList<String>();

			while (it.hasNext()) {
				key = it.next();
				lstConditions.add("@" + key + "=\"" + attributes.get(key) + "\"");
			}

			lstConditions.removeAll(Arrays.asList("", null));
			String conditions = join(lstConditions, " and ");

			xpath += conditions + "]";
		}

		return xpath;
	}
}
