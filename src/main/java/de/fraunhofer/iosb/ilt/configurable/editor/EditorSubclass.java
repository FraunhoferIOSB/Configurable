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
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableClass;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactorySubclsFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactorySubclsSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface EdOptsSubclass {

		/**
		 * @return The interface or superclass that the selectable classes must
		 * implement/extend.
		 */
		Class<?> iface();

		/**
		 * @return The flag indicating the selected class name and the
		 * configuration of this class should be merged into one JSON object.
		 */
		boolean merge() default false;

		/**
		 * @return The name of the json field that holds the name of the
		 * selected class.
		 */
		String nameField() default KEY_CLASSNAME;
	}

	private static final String KEY_CLASSNAME = "className";
	private static final String KEY_CLASSCONFIG = "classConfig";
	private static final Logger LOGGER = LoggerFactory.getLogger(EditorSubclass.class);

	public static class classItem implements Comparable<classItem> {

		public String className;
		public String displayName;
		public String jsonName;

		public classItem(String className) {
			this(className, className, className);
		}

		public classItem(String className, String displayName, String jsonName) {
			this.className = className;
			this.displayName = displayName;
			this.jsonName = jsonName;
		}

		@Override
		public String toString() {
			return displayName;
		}

		@Override
		public int compareTo(classItem o) {
			return displayName.compareTo(o.displayName);
		}

	}

	private Map<String, classItem> classesByClassName = new HashMap<>();
	private Map<String, classItem> classesByJsonName = new HashMap<>();
	private Map<String, classItem> classesByDisplayName = new TreeMap<>();
	/**
	 * The interface or superclass that the selectable classes must
	 * implement/extend.
	 */
	private Class<?> iface;
	/**
	 * The flag indicating the selected class name and the configuration of this
	 * class should be merged into one JSON object.
	 */
	private boolean merge = false;
	/**
	 * The name of the json field that holds the name of the selected class.
	 */
	private String nameField = KEY_CLASSNAME;
	private String jsonName = "";
	private JsonElement classConfig;
	private ConfigEditor classEditor;
	private C context;
	private D edtCtx;
	private String selectLabel = "Available Classes:";

	private FactorySubclsSwing factorySwing;
	private FactorySubclsFx factoryFx;

	public EditorSubclass() {
	}

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

	@Override
	public void initFor(Field field) {
		final EdOptsSubclass annotation = field.getAnnotation(EdOptsSubclass.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Field must have an EdOptsSubclass annotation to use this editor: " + field.getName());
		}
		iface = annotation.iface();
		merge = annotation.merge();
		nameField = annotation.nameField();
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
			result.add(nameField, new JsonPrimitive(jsonName));
		} else {
			result = new JsonObject();
			result.add(KEY_CLASSNAME, new JsonPrimitive(jsonName));
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
					jsonName = classNameElem.getAsString();
				}

				classConfig = confObj;
			} else {
				JsonElement classNameElem = confObj.get(KEY_CLASSNAME);
				if (classNameElem != null && classNameElem.isJsonPrimitive()) {
					jsonName = classNameElem.getAsString();
				}

				classConfig = confObj.get(KEY_CLASSCONFIG);
			}
		}
		if (jsonName == null || jsonName.isEmpty()) {
			LOGGER.info("Empty class name.");
		}
		setJsonName(jsonName);
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

	public String getJsonName() {
		return jsonName;
	}

	public ConfigEditor getClassEditor() {
		return classEditor;
	}

	public Map<String, classItem> getClassesByClassName() {
		initClasses();
		return classesByClassName;
	}

	public Map<String, classItem> getClassesByDisplayName() {
		initClasses();
		return classesByDisplayName;
	}

	public Map<String, classItem> getClassesByJsonName() {
		initClasses();
		return classesByJsonName;
	}

	private void initClasses() {
		if (!classesByJsonName.isEmpty()) {
			return;
		}
		List<Class<?>> subtypes = Reflection.getSubtypesOf(iface, false, true);
		for (Class<?> subtype : subtypes) {
			classItem item = new classItem(subtype.getName());
			ConfigurableClass annotation = subtype.getAnnotation(ConfigurableClass.class);
			if (annotation != null) {
				if (!annotation.jsonName().isEmpty()) {
					item.jsonName = annotation.jsonName();
					item.displayName = item.jsonName;
				}
				if (!annotation.displayName().isEmpty()) {
					item.displayName = annotation.displayName();
				}
			}
			if (classesByJsonName.containsKey(item.displayName)) {
				classItem conflict = classesByJsonName.get(item.displayName);
				LOGGER.warn("Name conflict, a class with jsonName {} already exists. {} and {}.", item.jsonName, conflict.className, item.jsonName);
				item.displayName = item.className;
			}
			classesByJsonName.put(item.jsonName, item);
			classesByClassName.put(item.className, item);
		}
		findPrefix();
		for (classItem item : classesByJsonName.values()) {
			if (classesByDisplayName.containsKey(item.displayName)) {
				classItem conflict = classesByDisplayName.get(item.displayName);
				LOGGER.warn("Name conflict, a class with displayName {} already exists. {} and {}.", item.displayName, conflict.className, item.className);
				item.displayName = item.className;
			}
			classesByDisplayName.put(item.displayName, item);
		}
	}

	private void findPrefix() {
		if (classesByJsonName.isEmpty()) {
			return;
		}
		String prefix = null;
		for (classItem item : classesByJsonName.values()) {
			if (prefix == null) {
				prefix = shortenPrefix(item.displayName);
				continue;
			}
			while (!prefix.isEmpty() && !item.displayName.startsWith(prefix)) {
				prefix = shortenPrefix(prefix);
			}
			if (prefix.isEmpty()) {
				break;
			}
		}
		LOGGER.debug("Found prefix to be: {}", prefix);
		for (classItem item : classesByJsonName.values()) {
			item.displayName = item.displayName.substring(prefix.length());
		}
	}

	private String shortenPrefix(String prefix) {
		int idx = prefix.lastIndexOf('.', prefix.length() - 2);
		if (idx == -1) {
			return "";
		}
		return prefix.substring(0, idx + 1);
	}

	public void setJsonName(final String name) {
		jsonName = name;
		String className = jsonName;
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
			className = findClassName(className);
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

	public String findClassName(String from) {
		classItem item = findClassItem(from);
		if (item == null) {
			return from;
		}
		return item.className;
	}

	public classItem findClassItem(String from) {
		initClasses();
		classItem item = classesByJsonName.get(from);
		if (item != null) {
			LOGGER.debug("Mapping {} to {}.", from, item.className);
			return item;
		}
		item = classesByDisplayName.get(from);
		if (item != null) {
			LOGGER.debug("Mapping {} to {}.", from, item.className);
			return item;
		}
		for (classItem clazz : classesByJsonName.values()) {
			if (clazz.className.endsWith(from)) {
				return clazz;
			}
		}
		return null;
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
			if (Strings.isNullOrEmpty(jsonName)) {
				return null;
			}
			Class<?> loadedClass = cl.loadClass(jsonName);
			Object instance = loadedClass.newInstance();

			if (instance instanceof Configurable) {
				Configurable confInstance = (Configurable) instance;
				confInstance.configure(classConfig, context, edtCtx);
			}
			return (T) instance;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			LOGGER.warn("Exception instantiating class {}.", jsonName);
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

	/**
	 * The interface or superclass that the selectable classes must
	 * implement/extend.
	 *
	 * @return the interface class.
	 */
	public Class<?> getIface() {
		return iface;
	}

	/**
	 * The flag indicating the selected class name and the configuration of this
	 * class should be merged into one JSON object.
	 *
	 * @return the merge setting
	 */
	public boolean isMerge() {
		return merge;
	}

	/**
	 * The flag indicating the selected class name and the configuration of this
	 * class should be merged into one JSON object.
	 *
	 * @param merge the merge to set
	 */
	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	/**
	 * The name of the json field that holds the name of the selected class.
	 *
	 * @return the nameField
	 */
	public String getNameField() {
		return nameField;
	}

	/**
	 * The name of the json field that holds the name of the selected class.
	 *
	 * @param nameField the nameField to set
	 */
	public void setNameField(String nameField) {
		this.nameField = nameField;
	}
}
