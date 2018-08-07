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
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.Configurable;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryClassFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryClassSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An editor that wraps a single class. This is like an editorSubclass with only
 * a single implementing class. Useful in EditorList where you need an editor
 * that can create instances of the class it edits.
 *
 * @author Hylke van der Schaaf
 * @param <C> The class type that provides context at runtime.
 * @param <D> The class type that provides context while editing.
 * @param <T> The type of object returned by getValue.
 */
public final class EditorClass<C, D, T> extends EditorDefault<T> {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface EdOptsClass {

		/**
		 * The configurable class to configure.
		 *
		 * @return The configurable class to configure.
		 */
		Class<?> clazz();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(EditorClass.class);

	private Class<T> clazz;
	private JsonElement classConfig;
	private ConfigEditor classEditor;

	private String profile = DEFAULT_PROFILE_NAME;

	private FactoryClassSwing factorySwing;
	private FactoryClassFx factoryFx;

	private C context;
	private D edtCtx;

	public EditorClass() {
	}

	/**
	 * @param context The Object that provides context at runtime.
	 * @param edtCtx The Object that provides context while editing.
	 * @param clazz The class to wrap.
	 */
	public EditorClass(final C context, final D edtCtx, final Class<T> clazz) {
		this.clazz = clazz;
		setContexts(context, edtCtx);
	}

	/**
	 * @param context The Object that provides context at runtime.
	 * @param edtCtx The Object that provides context while editing.
	 * @param clazz The class to wrap.
	 * @param label The label to use when showing this editor in a GUI.
	 * @param description The description of the editor.
	 */
	public EditorClass(final C context, final D edtCtx, final Class<T> clazz, final String label, final String description) {
		this.clazz = clazz;
		setLabel(label);
		setDescription(description);
		setContexts(context, edtCtx);
	}

	@Override
	public void initFor(Field field) {
		final EdOptsClass annotation = field.getAnnotation(EdOptsClass.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Field must have an EdOptsClass annotation to use this editor: " + field.getName());
		}
		clazz = (Class<T>) annotation.clazz();
	}

	public final void setContexts(final C context, final D edtCtx) {
		this.context = context;
		this.edtCtx = edtCtx;
	}

	@Override
	public void setConfig(final JsonElement classConfig) {
		this.classConfig = classConfig;
		initClass();
	}

	@Override
	public JsonElement getConfig() {
		readComponent();
		return classConfig;
	}

	@Override
	public GuiFactorySwing getGuiFactorySwing() {
		if (factoryFx != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factorySwing == null) {
			factorySwing = new FactoryClassSwing(this);
		}
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactoryClassFx(this);
		}
		return factoryFx;
	}

	/**
	 * Set the name of the class selected in this editor.
	 */
	public void initClass() {
		Object instance = null;
		try {
			instance = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException exc) {
			LOGGER.warn("Exception instantiating class {}.", clazz.getName());
			LOGGER.trace("Exception instantiating class.", exc);
		}

		if (instance instanceof Configurable) {
			final Configurable confInstance = (Configurable) instance;
			classEditor = confInstance.getConfigEditor(context, edtCtx);
			classEditor.setConfig(classConfig);
		} else {
			LOGGER.warn("Class {} is not configurable.", clazz.getName());
			classEditor = new EditorString("", 6);
			classEditor.setConfig(classConfig);
		}
		classEditor.setProfile(profile);

		fillComponent();
	}

	private void fillComponent() {
		if (factorySwing != null) {
			factorySwing.fillComponent();
		}
		if (factoryFx != null) {
			factoryFx.fillComponent();
		}
	}

	public ConfigEditor getClassEditor() {
		return classEditor;
	}

	private void readComponent() {
		if (classEditor == null) {
			initClass();
		}
		if (classEditor != null) {
			classConfig = classEditor.getConfig();
		}
	}

	/**
	 * Set the configuration of the selected class.
	 *
	 * @param classConfig the configuration of the selected class.
	 */
	public void setClassConfig(final JsonElement classConfig) {
		this.classConfig = classConfig;
	}

	public JsonElement getClassConfig() {
		return classConfig;
	}

	@Override
	public T getValue() {
		readComponent();
		try {
			final Object instance = clazz.newInstance();

			if (instance instanceof Configurable) {
				final Configurable confInstance = (Configurable) instance;
				confInstance.configure(classConfig, context, edtCtx);
				classEditor = confInstance.getConfigEditor(context, edtCtx);
				classEditor.setProfile(profile);
				fillComponent();
			}
			return (T) instance;
		} catch (InstantiationException | IllegalAccessException | ClassCastException exc) {
			LOGGER.warn("Exception instantiating class {}.", clazz.getName());
			LOGGER.trace("Exception instantiating class.", exc);
			return null;
		}
	}

	@Override
	public void setValue(T value) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setProfile(String profile) {
		this.profile = profile;
		if (classEditor != null) {
			classEditor.setProfile(profile);
		}
	}

}
