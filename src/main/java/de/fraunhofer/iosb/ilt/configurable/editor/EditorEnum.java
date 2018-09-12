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
import com.google.gson.JsonPrimitive;
import static de.fraunhofer.iosb.ilt.configurable.ConfigEditor.DEFAULT_PROFILE_NAME;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import static de.fraunhofer.iosb.ilt.configurable.annotations.AnnotationHelper.csvToReadOnlySet;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryEnumFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryEnumSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;

/**
 *
 * @author Hylke van der Schaaf
 * @param <T> The type this editor selects.
 */
public class EditorEnum<T extends Enum<T>> extends EditorDefault<T> {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface EdOptsEnum {

		/**
		 * @return The Enum to present the values of.
		 */
		Class<? extends Enum> sourceType();

		/**
		 * @return The enum.name of the default value.
		 */
		String dflt();

		/**
		 * A comma separated, case insensitive list of profile names. This field
		 * is only editable when one of these profiles is active. The "default"
		 * profile is automatically added to the list.
		 *
		 * @return A comma separated, case insensitive list of profile names.
		 */
		String profilesEdit() default "";
	}

	private Class<T> sourceType;
	private T dflt;
	private T value;

	public Set<String> profilesEdit = csvToReadOnlySet("");
	private String profile = DEFAULT_PROFILE_NAME;

	private FactoryEnumSwing<T> factorySwing;
	private FactoryEnumFx<T> factoryFx;

	public EditorEnum() {
	}

	public EditorEnum(Class<T> sourceType, T deflt, String label, String description) {
		this.sourceType = sourceType;
		this.dflt = deflt;
		this.value = deflt;
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void initFor(Field field) {
		EdOptsEnum annotation = field.getAnnotation(EdOptsEnum.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Field must have an EdOptsEnum annotation to use this editor: " + field.getName());
		}
		sourceType = (Class<T>) annotation.sourceType();
		dflt = Enum.valueOf(sourceType, annotation.dflt());
		value = dflt;
		profilesEdit = csvToReadOnlySet(annotation.profilesEdit());
	}

	@Override
	public void setConfig(JsonElement config) {
		if (config != null && config.isJsonPrimitive()) {
			JsonPrimitive prim = config.getAsJsonPrimitive();
			if (prim.isString()) {
				value = Enum.valueOf(sourceType, config.getAsString());
			} else if (prim.isNumber()) {
				T[] list = sourceType.getEnumConstants();
				int ord = prim.getAsInt();
				if (ord >= 0 && ord < list.length) {
					value = list[ord];
				}
			}
		} else {
			value = dflt;
		}
		fillComponent();
	}

	@Override
	public JsonElement getConfig() {
		readComponent();
		return new JsonPrimitive(value.name());
	}

	@Override
	public GuiFactorySwing getGuiFactorySwing() {
		if (factoryFx != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factorySwing == null) {
			factorySwing = new FactoryEnumSwing<>(this, this);
		}
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactoryEnumFx<>(this, this);
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

	private void readComponent() {
		if (factorySwing != null) {
			factorySwing.readComponent();
		}
		if (factoryFx != null) {
			factoryFx.readComponent();
		}
	}

	public Class<T> getSourceType() {
		return sourceType;
	}

	public T getRawValue() {
		return value;
	}

	public void setRawValue(T value) {
		this.value = value;
	}

	@Override
	public T getValue() {
		readComponent();
		return value;
	}

	@Override
	public void setValue(T value) {
		this.value = value;
		fillComponent();
	}

	@Override
	public void setProfile(String profile) {
		this.profile = profile;
		fillComponent();
	}

	public void setProfilesEdit(String csv) {
		profilesEdit = csvToReadOnlySet(csv);
	}

	@Override
	public boolean canEdit() {
		return profilesEdit.contains(profile);
	}

	@Override
	public boolean isDefault() {
		readComponent();
		return dflt == value;
	}

}
