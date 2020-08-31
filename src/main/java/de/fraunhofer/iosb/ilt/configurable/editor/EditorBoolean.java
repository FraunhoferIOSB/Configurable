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
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import static de.fraunhofer.iosb.ilt.configurable.ConfigEditor.DEFAULT_PROFILE_NAME;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import static de.fraunhofer.iosb.ilt.configurable.annotations.AnnotationHelper.csvToReadOnlySet;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryBooleanFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryBooleanSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An editor for boolean values.
 *
 * @author Hylke van der Schaaf
 */
public final class EditorBoolean extends EditorDefault<Boolean> {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface EdOptsBool {

		/**
		 * @return The default value. Used if dfltIsNull is false.
		 */
		boolean dflt() default false;

		/**
		 * If set to true, the default value of the editor is null.
		 *
		 * @return if true, the default value of the editor is null, not the
		 * value of dflt.
		 */
		boolean dfltIsNull() default false;

		/**
		 * A comma separated, case insensitive list of profile names. This field
		 * is only editable when one of these profiles is active. The "default"
		 * profile is automatically added to the list.
		 *
		 * @return A comma separated, case insensitive list of profile names.
		 */
		String profilesEdit() default "";
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(EditorBoolean.class.getName());
	private Boolean dflt;
	private Boolean value;

	public Set<String> profilesEdit = csvToReadOnlySet("");
	private String profile = DEFAULT_PROFILE_NAME;

	private FactoryBooleanSwing factorySwing;
	private FactoryBooleanFx factoryFx;

	public EditorBoolean() {
	}

	public EditorBoolean(boolean deflt) {
		this.value = deflt;
		this.dflt = deflt;
	}

	public EditorBoolean(boolean deflt, String label, String description) {
		this.value = deflt;
		this.dflt = deflt;
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void initFor(Field field) {
		EdOptsBool annotation = field.getAnnotation(EdOptsBool.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Field must have an EdOptsBool annotation to use this editor: " + field.getName());
		}
		final boolean isPrimitive = field.getType().isPrimitive();
		final boolean dfltIsNull = annotation.dfltIsNull();
		if (dfltIsNull) {
			if (isPrimitive) {
				LOGGER.error("Flag dfltIsNull set to true on a primitive field: {}", field);
				dflt = false;
			}
		} else {
			dflt = annotation.dflt();
		}
		value = dflt;
		profilesEdit = csvToReadOnlySet(annotation.profilesEdit());
	}

	@Override
	public void setConfig(JsonElement config) {
		try {
			if (config == null) {
				value = dflt;
			} else {
				value = config.getAsBoolean();
			}
		} catch (ClassCastException | IllegalStateException e) {
			value = dflt;
			LOGGER.trace("", e);
			LOGGER.debug("Value is not a boolean: {}.", config.toString());
		}
		fillComponent();
	}

	@Override
	public GuiFactorySwing getGuiFactorySwing() {
		if (factoryFx != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factorySwing == null) {
			factorySwing = new FactoryBooleanSwing(this);
		}
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactoryBooleanFx(this);
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
			value = factorySwing.isSelected();
		}
		if (factoryFx != null) {
			value = factoryFx.isSelected();
		}
	}

	@Override
	public JsonElement getConfig() {
		readComponent();
		if (value == null) {
			return JsonNull.INSTANCE;
		}
		return new JsonPrimitive(value);
	}

	@Override
	public Boolean getValue() {
		readComponent();
		return value;
	}

	@Override
	public void setValue(Boolean value) {
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
		return Objects.equals(dflt, value);
	}

}
