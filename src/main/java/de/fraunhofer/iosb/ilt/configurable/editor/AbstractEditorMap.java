/*
 * Copyright (C) 2017 Fraunhofer IOSB
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
package de.fraunhofer.iosb.ilt.configurable.editor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryMapFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryMapSwing;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An editor for a list of editors, all of the same type.
 *
 * @author Hylke van der Schaaf
 * @param <T> The type of object returned by getValue.
 * @param <V> The type of object in the map.
 */
public abstract class AbstractEditorMap<T, V> extends EditorDefault<T> implements Iterable<String> {

	public static final class Item< V> {

		public final ConfigEditor<V> editor;
		public final boolean optional;
		public final int colwidth;
		public final String jsonName;
		public final String fieldName;
		public final String label;

		public Item(final String fieldName, final String jsonName, final ConfigEditor<V> editor, final boolean optional, final int colwidth) {
			this.fieldName = fieldName;
			this.jsonName = jsonName;
			final String edLabel = editor.getLabel();
			if (edLabel == null || edLabel.isEmpty()) {
				label = jsonName;
			} else {
				label = edLabel;
			}
			this.editor = editor;
			this.optional = optional;
			this.colwidth = colwidth;
		}

		public String getName() {
			return jsonName;
		}

		@Override
		public String toString() {
			return label;
		}

	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEditorMap.class.getName());
	/**
	 * All options
	 */
	protected final Map<String, Item<V>> options = new LinkedHashMap<>();
	/**
	 * The names of the selected options.
	 */
	protected final Set<String> value = new HashSet<>();
	/**
	 * The names of the optional options.
	 */
	private final List<String> optionalOptions = new ArrayList<>();
	/**
	 * How many columns we want to have. Defaults to 1.
	 */
	private final int columns;

	private FactoryMapSwing factorySwing;
	private FactoryMapFx factoryFx;

	public AbstractEditorMap() {
		columns = 1;
	}

	public AbstractEditorMap(int columns) {
		this.columns = columns;
	}

	public AbstractEditorMap(int columns, String label, String description) {
		this.columns = columns;
		setLabel(label);
		setDescription(description);
	}

	/**
	 * Add an option (Field) to the Map.
	 *
	 * @param name The name to use for the option in the JSON config.
	 * @param editor The editor to use for editing the option.
	 * @param optional Flag indicating the option is optional.
	 */
	public void addOption(String name, ConfigEditor editor, boolean optional) {
		addOption(name, name, editor, optional, 1);
	}

	/**
	 * Add an option (Field) to the Map.
	 *
	 * @param name The name to use for the option in the JSON config.
	 * @param editor The editor to use for editing the option.
	 * @param optional Flag indicating the option is optional.
	 * @param width The number of columns the editor should take in the GUI.
	 */
	public void addOption(String name, ConfigEditor editor, boolean optional, int width) {
		addOption(name, name, editor, optional, width);
	}

	public void addOption(String fieldName, String jsonName, ConfigEditor editor, boolean optional, int width) {
		if (options.containsKey(jsonName)) {
			throw new IllegalArgumentException("Map already contains an editor for " + jsonName);
		}
		options.put(jsonName, new Item<>(fieldName, jsonName, editor, optional, width));
		if (optional) {
			optionalOptions.add(jsonName);
		} else {
			addItem(jsonName);
		}
	}

	@Override
	public void setConfig(JsonElement config) {
		value.clear();

		if (config != null && config.isJsonObject()) {
			final JsonObject asObj = config.getAsJsonObject();
			for (final Map.Entry<String, JsonElement> entry : asObj.entrySet()) {
				final String key = entry.getKey();
				final JsonElement itemConfig = entry.getValue();
				final Item<V> item = options.get(key);
				if (item == null) {
					if (!"$type".equals(key)) {
						LOGGER.debug("Unknown entry {} in configuration.", key);
					}
				} else {
					item.editor.setConfig(itemConfig);
					value.add(key);
				}
			}
		}
		for (final Map.Entry<String, Item<V>> entry : options.entrySet()) {
			final String key = entry.getKey();
			final Item<V> val = entry.getValue();
			if (!val.optional) {
				value.add(key);
			}
		}

		if (factorySwing != null) {
			factorySwing.fillComponent();
		}
		if (factoryFx != null) {
			factoryFx.fillComponent();
		}
	}

	@Override
	public JsonElement getConfig() {
		final JsonObject result = new JsonObject();
		for (final String key : value) {
			final Item<V> item = options.get(key);
			result.add(key, item.editor.getConfig());
		}
		return result;
	}

	@Override
	public GuiFactorySwing getGuiFactorySwing() {
		if (factoryFx != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factorySwing == null) {
			factorySwing = new FactoryMapSwing(this);
		}
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactoryMapFx(this);
		}
		return factoryFx;
	}

	public void addItem(final String jsonName) {
		value.add(jsonName);
		if (factorySwing != null) {
			factorySwing.addItem(jsonName);
		}
		if (factoryFx != null) {
			factoryFx.addItem(jsonName);
		}
	}

	public void removeItem(final String jsonName) {
		final Item<V> item = options.get(jsonName);
		if (item.optional) {
			value.remove(jsonName);
			if (factorySwing != null) {
				factorySwing.removeItem(item);
			}
			if (factoryFx != null) {
				factoryFx.removeItem(item);
			}
		}
	}

	/**
	 * For each of the keys in the map, tries set the value of the field on the
	 * target object. It first tries to set the field with the fieldName
	 * directly. If that does not work, it tries to call the setter
	 * set{fieldName}(fieldValue) on the target.
	 *
	 * @param target The target to set the fields, or call the setters on.
	 */
	public void setContentsOn(final Object target) {
		for (Item<V> item : options.values()) {
			Object val = item.editor.getValue();
			if (val == null) {
				continue;
			}
			String fieldName = item.fieldName;

			String methodName = "set" + fieldName.substring(0, 1).toUpperCase(Locale.ROOT) + fieldName.substring(1);
			if (AbstractEditorMap.callMethodOn(methodName, target, val)) {
				// using setting worked.
				continue;
			}

			Field field = FieldUtils.getField(target.getClass(), fieldName, true);
			try {
				FieldUtils.writeField(field, target, val, true);
				continue;
			} catch (IllegalAccessException ex) {
				LOGGER.trace("Exception:", ex);
			}
			LOGGER.warn("Could not set field {}.", field);
		}
	}

	private static boolean callMethodOn(final String methodName, final Object target, final Object val) {
		try {
			MethodUtils.invokeMethod(target, methodName, val);
			return true;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exc) {
			LOGGER.trace("Failed to call setter.", exc);
		}
		LOGGER.debug("Failed call method {} on {}.", methodName, target.getClass().getName());
		return false;
	}

	@Override
	public Iterator<String> iterator() {
		return value.iterator();
	}

	public Set<String> getRawValue() {
		return value;
	}

	public V getValue(final String name) {
		final Item<V> item = options.get(name);
		return item.editor.getValue();
	}

	/**
	 * Checks if the given option is set.
	 *
	 * @param name The option to check.
	 * @return true if the option is set, false otherwise.
	 */
	public boolean isOptionSet(final String name) {
		return value.contains(name);
	}

	/**
	 * The names of the optional options.
	 *
	 * @return the optionalOptions
	 */
	public final List<String> getOptionalOptions() {
		return optionalOptions;
	}

	/**
	 * How many columns we want to have. Defaults to 1.
	 *
	 * @return	How many columns we want to have.
	 */
	public final int getColumns() {
		return columns;
	}

	public Map<String, Item<V>> getOptions() {
		return options;
	}

}
