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
import com.google.gson.JsonPrimitive;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditors;
import de.fraunhofer.iosb.ilt.configurable.Configurable;
import de.fraunhofer.iosb.ilt.configurable.ConfigurableFactory;
import de.fraunhofer.iosb.ilt.configurable.ConfigurationException;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.Reflection;
import de.fraunhofer.iosb.ilt.configurable.Utils;
import static de.fraunhofer.iosb.ilt.configurable.annotations.AnnotationHelper.csvToReadOnlySet;
import static de.fraunhofer.iosb.ilt.configurable.annotations.AnnotationHelper.getConfigurableConstructor;
import static de.fraunhofer.iosb.ilt.configurable.annotations.AnnotationHelper.instantiateFrom;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableClass;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactorySubclsFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactorySubclsSwing;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
public class EditorSubclass<C, D, T> extends EditorDefault<T> {

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

		/**
		 * @return An optional annotation that the presented classes must
		 * implement.
		 */
		Class<? extends Annotation> requiredAnnotation() default NoFilter.class;

		/**
		 * A comma separated, case insensitive list of profile names. This field
		 * is only editable when one of these profiles is active. The "default"
		 * profile is automatically added to the list.
		 *
		 * @return A comma separated, case insensitive list of profile names.
		 */
		String profilesEdit() default "";

		/**
		 * If true, the class names are shortened by removing any common prefix.
		 * For example, if all classes are in the java.lang package, and thus
		 * all class names start with "java.lang.", then "java.lang." is removed
		 * from the names displayed in the dialog box. In the JSON, the full
		 * name is used.
		 *
		 * @return true if class names should be shortened.
		 */
		boolean shortenClassNames() default false;
	}

	/**
	 * The default requiredAnnotation that specifies that classes are not
	 * filtered. Since annotation values can not be null.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface NoFilter {
		// Empty by design.
	}

	/**
	 * A simple annotation to use as requiredAnnotation.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface Expose {
		// Empty by design.
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
	 * An annotation that the presented classes must implement.
	 */
	private Class<? extends Annotation> requiredAnnotation = NoFilter.class;

	/**
	 * The flag indicating the selected class name and the configuration of this
	 * class should be merged into one JSON object.
	 */
	private boolean merge = false;

	/**
	 * Show the full class name in the box, not the shortened version.
	 */
	private boolean shortenClassNames = false;

	/**
	 * The name of the json field that holds the name of the selected class.
	 */
	private String nameField = KEY_CLASSNAME;
	private String jsonName = "";
	private JsonElement classConfig;
	private T instance;
	private ConfigEditor classEditor;
	private C context;
	private D edtCtx;
	private String selectLabel = "Type:";

	public Set<String> profilesEdit = csvToReadOnlySet("");
	private String profile = DEFAULT_PROFILE_NAME;

	private FactorySubclsSwing factorySwing;
	private FactorySubclsFx factoryFx;

	public EditorSubclass() {
	}

	public EditorSubclass(final C context, final D edtCtx, Class<? extends T> iface, boolean merge, String nameField) {
		this(context, edtCtx, iface, "", "", merge, nameField);
	}

	public EditorSubclass(final C context, final D edtCtx, final Class<? extends T> iface, final String label, final String description) {
		this(context, edtCtx, iface, label, description, false, KEY_CLASSNAME);
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
		requiredAnnotation = annotation.requiredAnnotation();
		shortenClassNames = annotation.shortenClassNames();
		profilesEdit = csvToReadOnlySet(annotation.profilesEdit());
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
		String name = null;
		jsonName = "";
		if (config == null || !config.isJsonObject()) {
			classConfig = null;
		} else {
			JsonObject confObj = config.getAsJsonObject();
			if (merge) {
				JsonElement classNameElem = confObj.get(nameField);
				if (classNameElem != null && classNameElem.isJsonPrimitive()) {
					name = classNameElem.getAsString();
				}

				classConfig = confObj;
			} else {
				JsonElement classNameElem = confObj.get(KEY_CLASSNAME);
				if (classNameElem != null && classNameElem.isJsonPrimitive()) {
					name = classNameElem.getAsString();
				}

				classConfig = confObj.get(KEY_CLASSCONFIG);
			}
		}
		setJsonName(name);
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
			if (requiredAnnotation != NoFilter.class && subtype.getAnnotation(requiredAnnotation) == null) {
				LOGGER.debug("Ignoring class {}, not annotated with {}.", subtype, requiredAnnotation);
				continue;
			}

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

		if (shortenClassNames) {
			findPrefix();
		}

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
		if (Utils.isNullOrEmpty(name)) {
			LOGGER.debug("Empty class name.");
			instance = null;
			classEditor = null;
			fillComponent();
			return;
		}
		if (name.equals(jsonName)) {
			return;
		}
		jsonName = name;

		instance = null;
		if (!Utils.isNullOrEmpty(jsonName)) {
			final ConfigurableFactory factory = findFactory(context, edtCtx);
			try {
				final Class<?> subclassType = factory.loadClass(jsonName);
				classEditor = ConfigEditors.buildEditorFromClass(subclassType, context, edtCtx).orElse(null);

			} catch (final ClassNotFoundException exc) {
				LOGGER.warn("Exception loading class {}.", jsonName);
				LOGGER.debug("Exception loading class.", exc);
			}

			if (classEditor == null) {
				try {
					instance = (T) factory.instantiate(jsonName, classConfig, context, edtCtx);
				} catch (final ConfigurationException exc) {
					LOGGER.warn("Exception instantiating class {}.", jsonName);
					LOGGER.debug("Exception instantiating class.", exc);
				}
			}
		}

		if (instance instanceof Configurable) {
			final Configurable confInstance = (Configurable) instance;
			classEditor = confInstance.getConfigEditor(context, edtCtx);
		}

		if (classEditor != null) {
			classEditor.setConfig(classConfig);
			classEditor.setProfile(profile);
		} else {
			LOGGER.warn("Class {} is not configurable.", jsonName);
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
		if (Utils.isNullOrEmpty(from)) {
			return null;
		}
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
	public T getValue() throws ConfigurationException {
		readComponent();
		if (Utils.isNullOrEmpty(jsonName)) {
			// Nothing configured, nothing to return.
			return null;
		}
		if (instance == null) {
			instance = tryToInstantiate();
		} else if (instance instanceof Configurable) {
			Configurable confInstance = (Configurable) instance;
			confInstance.configure(classConfig, context, edtCtx, classEditor);
		}
		return instance;
	}

	private T tryToInstantiate() throws ConfigurationException {
		try {
			return instantiate();
		} catch (ReflectiveOperationException | IllegalArgumentException exc) {
			throw new ConfigurationException(exc);
		}
	}

	private T instantiate() throws ReflectiveOperationException, ConfigurationException, IllegalArgumentException {
		final ConfigurableFactory factory = findFactory(context, edtCtx);
		final Class<?> subclassClass = factory.loadClass(jsonName);
		final Optional<Constructor<?>> configurableConstructor = getConfigurableConstructor(subclassClass);
		if (configurableConstructor.isPresent()) {
			return instantiateFrom(configurableConstructor.get(), classConfig, context, edtCtx);
		}

		return (T) factory.instantiate(jsonName, classConfig, context, edtCtx);
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

	/**
	 * An annotation that the presented classes must implement.
	 *
	 * @return the required Annotation class
	 */
	public Class<? extends Annotation> getRequiredAnnotation() {
		return requiredAnnotation;
	}

	/**
	 * An annotation that the presented classes must implement.
	 *
	 * @param requiredAnnotation the required Annotation class
	 */
	public void setRequiredAnnotation(Class<? extends Annotation> requiredAnnotation) {
		this.requiredAnnotation = requiredAnnotation;
	}

	public void setProfilesEdit(String csv) {
		profilesEdit = csvToReadOnlySet(csv);
	}

	@Override
	public void setProfile(String profile) {
		this.profile = profile;
		if (classEditor != null) {
			classEditor.setProfile(profile);
		}
	}

	@Override
	public boolean canEdit() {
		return profilesEdit.contains(profile);
	}

	private ConfigurableFactory findFactory(final C context, final D edtCtx) {
		if (edtCtx instanceof ConfigurableFactory) {
			return (ConfigurableFactory) edtCtx;
		}
		if (context instanceof ConfigurableFactory) {
			return (ConfigurableFactory) context;
		}
		return new FactoryImp();
	}

	private class FactoryImp implements ConfigurableFactory {

		private Class<? extends T> loadClass() throws ReflectiveOperationException, ConfigurationException {
			if (Utils.isNullOrEmpty(jsonName)) {
				throw new ConfigurationException("No class specified.");
			}
			String name = jsonName;

			Class<? extends T> loadedClass = null;
			ClassLoader cl = getClass().getClassLoader();
			try {
				loadedClass = (Class<? extends T>) cl.loadClass(name);
			} catch (ClassNotFoundException e) {
				LOGGER.trace("Could not find class {}. Not a full class name?", name);
				LOGGER.trace("Exception loading class.", e);
			}

			if (loadedClass == null) {
				name = findClassName(name);
				loadedClass = (Class<? extends T>) cl.loadClass(name);
			}
			return loadedClass;
		}

		@Override
		public Object instantiate(String className, JsonElement config, Object context, Object edtCtx) throws ConfigurationException {
			try {
				Class<? extends T> loadedClass = loadClass();
				return instantiate(loadedClass, config, context, edtCtx);
			} catch (ReflectiveOperationException | SecurityException exc) {
				throw new ConfigurationException(exc);
			}
		}

		@Override
		public <T> T instantiate(Class<? extends T> clazz, JsonElement config, Object context, Object edtCtx) throws ConfigurationException {
			T newInstance;
			try {
				newInstance = clazz.getDeclaredConstructor().newInstance();
			} catch (ReflectiveOperationException | SecurityException exc) {
				throw new ConfigurationException(exc);
			}
			if (newInstance instanceof Configurable) {
				Configurable confInstance = (Configurable) newInstance;
				confInstance.configure(classConfig, context, edtCtx, null);
			}
			return newInstance;
		}
	}
}
