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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
		public final String name;
		public final String label;

		public Item(final String name, final ConfigEditor<V> editor, final boolean optional, final int colwidth) {
			this.name = name;
			final String edLabel = editor.getLabel();
			if (edLabel == null || edLabel.isEmpty()) {
				label = name;
			} else {
				label = edLabel;
			}
			this.editor = editor;
			this.optional = optional;
			this.colwidth = colwidth;
		}

		public String getName() {
			return name;
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

	public void addOption(String name, ConfigEditor editor, boolean optional) {
		if (options.containsKey(name)) {
			throw new IllegalArgumentException("Map already contains an editor for " + name);
		}
		options.put(name, new Item<>(name, editor, optional, 1));
		if (optional) {
			optionalOptions.add(name);
		} else {
			addItem(name);
		}
	}

	public void addOption(String name, ConfigEditor editor, boolean optional, int width) {
		options.put(name, new Item<>(name, editor, optional, width));
		if (optional) {
			optionalOptions.add(name);
		} else {
			addItem(name);
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

	public void addItem(final String key) {
		value.add(key);
		if (factorySwing != null) {
			factorySwing.addItem(key);
		}
		if (factoryFx != null) {
			factoryFx.addItem(key);
		}
	}

	public void removeItem(final String key) {
		final Item<V> item = options.get(key);
		if (item.optional) {
			value.remove(key);
			if (factorySwing != null) {
				factorySwing.removeItem(item);
			}
			if (factoryFx != null) {
				factoryFx.removeItem(item);
			}
		}
	}

	/**
	 * For each of the keys in the map, tries to call set{Keyname}(keyValue) on
	 * the target.
	 *
	 * @param target The target to call the setters on.
	 */
	public void setContentsOn(final Object target) {
		for (String key : value) {
			Object val = options.get(key).editor.getValue();
			if (val == null) {
				continue;
			}

			String methodName = "set" + key.substring(0, 1).toUpperCase(Locale.ROOT) + key.substring(1);
			AbstractEditorMap.callMethodOn(methodName, target, val);
		}
	}

	private static boolean callMethodOn(final String methodName, final Object target, final Object val) {
		final Class<? extends Object> aClass = target.getClass();
		final Class<? extends Object> vClass = val.getClass();
		try {
			Class<? extends Object> current = aClass;
			while (current.getSuperclass() != null) {
				final Method[] declaredMethods = current.getDeclaredMethods();
				for (final Method method : declaredMethods) {
					final String mName = method.getName();
					final int pCount = method.getParameterCount();
					if (pCount == 1 && mName.equals(methodName)) {
						final Class<?> pt0 = method.getParameterTypes()[0];
						// unfortunately this does not do autoboxing.
						final boolean assignable = pt0.isAssignableFrom(vClass);
						if (assignable) {
							method.invoke(target, val);
							return true;
						} else {
							LOGGER.debug("Method found, but wrong parameter.");
						}
					}
				}
				current = current.getSuperclass();
			}
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | UnsupportedOperationException exc) {
			LOGGER.debug("", exc);
		}
		LOGGER.debug("Failed call method {} on {}.", methodName, aClass.getName());
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
