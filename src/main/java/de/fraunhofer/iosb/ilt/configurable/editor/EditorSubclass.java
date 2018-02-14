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

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.Configurable;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.Reflection;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactorySubclsFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactorySubclsSwing;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An editor that offers a selection of a class that implements an interface or
 * extends a class.
 *
 * @author Hylke van der Schaaf
 * @param <C> The class type that provides context at runtime.
 * @param <D> The class type that provides context while editing.
 * @param <T> The type of object returned by getValue.
 */
public class EditorSubclass<C, D, T> extends EditorDefault< T> {

	private static final String KEY_CLASSNAME = "className";
	private static final String KEY_CLASSCONFIG = "classConfig";
	private static final Logger LOGGER = LoggerFactory.getLogger(EditorSubclass.class);
	private final Class<?> iface;
	private boolean merge = false;
	private String nameField = KEY_CLASSNAME;
	private String className = "";
	private JsonElement classConfig;
	private ConfigEditor classEditor;
	private String prefix = "";
	private C context;
	private D edtCtx;
	private String selectLabel = "Available Classes:";

	private FactorySubclsSwing factorySwing;
	private FactorySubclsFx factoryFx;

	public EditorSubclass(final C context, final D edtCtx, Class<?> iface, boolean merge, String nameField) {
		this.iface = iface;
		this.merge = merge;
		this.nameField = nameField;
		setContexts(context, edtCtx);
	}

	public EditorSubclass(final C context, final D edtCtx, final Class<? extends T> iface, final String label, final String description) {
		this.iface = iface;
		setLabel(label);
		setDescription(description);
		setContexts(context, edtCtx);
	}

	/**
	 * @param iface The interface or superclass the presented options should
	 * implement or extend.
	 * @param label The label to use for this instance.
	 * @param description The description describing this instance.
	 * @param merge Should the class name be merged into the configuration.
	 * @param nameField The name of the field to use for storing the className.
	 * @param context
	 * @param edtCtx
	 */
	public EditorSubclass(final C context, final D edtCtx, final Class<? extends T> iface, final String label, final String description, final boolean merge, final String nameField) {
		this.iface = iface;
		this.merge = merge;
		this.nameField = nameField;
		setLabel(label);
		setDescription(description);
		setContexts(context, edtCtx);
	}

	public final void setContexts(final C context, final D edtCtx) {
		this.context = context;
		this.edtCtx = edtCtx;
	}

	@Override
	public JsonElement getConfig() {
		readComponent();
		JsonObject result;
		if (merge && classConfig != null && classConfig.isJsonObject()) {
			result = classConfig.getAsJsonObject();
			result.add(nameField, new JsonPrimitive(className));
		} else {
			result = new JsonObject();
			result.add(KEY_CLASSNAME, new JsonPrimitive(className));
			result.add(KEY_CLASSCONFIG, classConfig);
		}
		return result;
	}

	@Override
	public void setConfig(JsonElement config) {
		if (config != null && config.isJsonObject()) {
			JsonObject confObj = config.getAsJsonObject();
			if (merge) {
				JsonElement classNameElem = confObj.get(nameField);
				if (classNameElem != null && classNameElem.isJsonPrimitive()) {
					className = classNameElem.getAsString();
				}

				classConfig = confObj;
			} else {
				JsonElement classNameElem = confObj.get(KEY_CLASSNAME);
				if (classNameElem != null && classNameElem.isJsonPrimitive()) {
					className = classNameElem.getAsString();
				}

				classConfig = confObj.get(KEY_CLASSCONFIG);
			}
		}
		if (className == null || className.isEmpty()) {
			LOGGER.info("Empty class name.");
		}
		setClassName(className);
	}

	/**
	 * Get the configuration of the selected class.
	 *
	 * @return the configuration of the selected class.
	 */
	public JsonElement getClassConfig() {
		return this.classConfig;
	}

	/**
	 * Set the configuration of the selected class.
	 *
	 * @param classConfig the configuration of the selected class.
	 */
	public void setClassConfig(final JsonElement classConfig) {
		this.classConfig = classConfig;
	}

	@Override
	public GuiFactorySwing getGuiFactorySwing() {
		if (factoryFx != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factorySwing == null) {
			factorySwing = new FactorySubclsSwing(this);
			factorySwing.setSelectLabel(selectLabel);
		}
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactorySubclsFx(this);
			factoryFx.setSelectLabel(selectLabel);
		}
		return factoryFx;
	}

	private void fillComponent() {
		if (factorySwing != null) {
			factorySwing.fillComponent();
		}
		if (factoryFx != null) {
			factoryFx.fillComponent();
		}
	}

	public String getClassName() {
		return className;
	}

	public ConfigEditor getClassEditor() {
		return classEditor;
	}

	public String getPrefix() {
		return prefix;
	}

	public String[] getClasses() {
		List<Class> subtypes = Reflection.getSubtypesOf(iface, false);
		int i = 0;
		String[] result = new String[subtypes.size()];
		for (Class subtype : subtypes) {
			result[i] = subtype.getName();
			i++;
		}
		if (result.length == 0) {
			return result;
		}
		boolean end = false;
		int length = 0;
		if (result.length > 1) {
			while (!end && length < result[0].length()) {
				length++;
				String test = result[0].substring(0, length);
				for (String name : result) {
					if (!name.startsWith(test)) {
						end = true;
						break;
					}
				}
				if (!end && result[0].charAt(length - 1) == '.') {
					prefix = test;
				}
			}
			LOGGER.debug("Found prefix to be: {}", prefix);
		}
		if (length > 0) {
			for (i = 0; i < result.length; i++) {
				result[i] = result[i].substring(prefix.length());
			}
		}
		Arrays.sort(result);
		return result;
	}

	public void setClassName(final String name) {
		className = name;
		if (name == null || name.isEmpty()) {
			return;
		}
		Class<?> loadedClass = null;
		Object instance = null;
		ClassLoader cl = getClass().getClassLoader();
		try {
			loadedClass = cl.loadClass(className);
		} catch (ClassNotFoundException e) {
			LOGGER.trace("Could not find class {}. Not a full class name?", className);
			LOGGER.trace("Exception loading class.", e);
		}

		if (loadedClass == null) {
			className = findClass(className);
			try {
				loadedClass = cl.loadClass(className);
			} catch (ClassNotFoundException e) {
				LOGGER.warn("Exception loading class.", e);
			}
		}

		if (loadedClass != null) {
			try {
				instance = loadedClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				LOGGER.warn("Exception instantiating class {}.", className);
				LOGGER.trace("Exception instantiating class.", e);
			}
		}

		if (instance != null && instance instanceof Configurable) {
			Configurable confInstance = (Configurable) instance;
			classEditor = confInstance.getConfigEditor(context, edtCtx);
			classEditor.setConfig(classConfig);
		} else {
			LOGGER.warn("Class {} is not configurable.", className);
			classEditor = null;
		}

		fillComponent();
	}

	private String findClass(String from) {
		String[] classes = getClasses();
		for (String name : classes) {
			if (name.endsWith(from)) {
				LOGGER.debug("Mapping {} to {}.", from, name);
				return name;
			}
		}
		return from;
	}

	private void readComponent() {
		if (classEditor != null) {
			classConfig = classEditor.getConfig();
		}
	}

	@Override
	public T getValue() {
		readComponent();
		try {
			ClassLoader cl = getClass().getClassLoader();
			if (Strings.isNullOrEmpty(className)) {
				return null;
			}
			Class<?> loadedClass = cl.loadClass(className);
			Object instance = loadedClass.newInstance();

			if (instance instanceof Configurable) {
				Configurable confInstance = (Configurable) instance;
				confInstance.configure(classConfig, context, edtCtx);
			}
			return (T) instance;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			LOGGER.warn("Exception instantiating class {}.", className);
			LOGGER.trace("Exception instantiating class.", e);
			return null;
		}
	}

	@Override
	public void setValue(T value) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public EditorSubclass<C, D, T> setSelectLabel(String selectLabel) {
		this.selectLabel = selectLabel;
		return this;
	}

	/**
	 * Helper method to test if the given config is a valid configuration for
	 * this EditorSubclass.
	 *
	 * @param config The configuration to test.
	 * @return true if the configuration is valid.
	 */
	public boolean testConfig(final JsonElement config) {
		if (config == null || !config.isJsonObject()) {
			return false;
		}
		final JsonObject confObj = config.getAsJsonObject();
		if (this.merge) {
			final JsonElement classNameElem = confObj.get(this.nameField);
			if (classNameElem == null || !classNameElem.isJsonPrimitive()) {
				return false;
			}
		} else {
			final JsonElement classNameElem = confObj.get(KEY_CLASSNAME);
			if (classNameElem == null || !classNameElem.isJsonPrimitive()) {
				return false;
			}

			if (!confObj.has(KEY_CLASSCONFIG)) {
				return false;
			}
		}
		return true;
	}
}
