/*
 * Copyright (C) 2021 Fraunhofer IOSB
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.configurable.JsonSchema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author scf
 */
public class ItemObject extends SchemaItemAbstract<ItemObject> {

	private Map<String, SchemaItem> properties;

	private boolean additionalProperties = false;

	private List<String> required;
	private List<String> defaultProperties;

	private List<SchemaItem> oneOf;

	public ItemObject() {
		super("object");
	}

	@Override
	public ItemObject getThis() {
		return this;
	}

	public boolean isAdditionalProperties() {
		return additionalProperties;
	}

	public ItemObject setAdditionalProperties(boolean additionalProperties) {
		this.additionalProperties = additionalProperties;
		return this;
	}

	public Map<String, SchemaItem> getProperties() {
		return properties;
	}

	public ItemObject addProperty(String name, SchemaItem property) {
		if (properties == null) {
			properties = new HashMap<>();
		}
		properties.put(name, property);
		return this;
	}

	public ItemObject addProperty(String name, boolean isOptional, SchemaItem property) {
		if (properties == null) {
			properties = new HashMap<>();
		}
		properties.put(name, property);
		if (!isOptional) {
			addRequired(name);
			addDefaultProperty(name);
		}
		return this;
	}

	public void setProperties(Map<String, SchemaItem> properties) {
		this.properties = properties;
	}

	public List<String> getRequired() {
		if (required == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(required);
	}

	public ItemObject addRequired(String name) {
		if (required == null) {
			required = new ArrayList<>();
		}
		required.add(name);
		return this;
	}

	public void setRequired(List<String> required) {
		this.required = required;
	}

	/**
	 * @return the oneOf
	 */
	public List<SchemaItem> getOneOf() {
		return oneOf;
	}

	/**
	 * @param oneOf the oneOf to set
	 */
	public void setOneOf(List<SchemaItem> oneOf) {
		this.oneOf = oneOf;
	}

	/**
	 * @return the defaultProperties
	 */
	public List<String> getDefaultProperties() {
		if (defaultProperties == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(defaultProperties);
	}

	public ItemObject addDefaultProperty(String name) {
		if (defaultProperties == null) {
			defaultProperties = new ArrayList<>();
		}
		defaultProperties.add(name);
		return this;
	}

	/**
	 * @param defaultProperties the defaultProperties to set
	 */
	public void setDefaultProperties(List<String> defaultProperties) {
		this.defaultProperties = defaultProperties;
	}

}
