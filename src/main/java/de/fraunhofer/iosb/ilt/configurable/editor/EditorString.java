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
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryStringFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryStringSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;

/**
 *
 * @author Hylke van der Schaaf
 */
public class EditorString extends EditorDefault<String> {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface EdOptsString {

		/**
		 * @return The number of lines to show in the editor.
		 */
		int lines() default 1;

		/**
		 * @return The default value.
		 */
		String dflt() default "";

		/**
		 * A comma separated, case insensitive list of profile names. This field
		 * is only editable when one of these profiles is active. The "default"
		 * profile is automatically added to the list.
		 *
		 * @return A comma separated, case insensitive list of profile names.
		 */
		String profilesEdit() default "";
	}

	private String dflt;
	private String value;
	private int lines = 1;

	private Set<String> profilesEdit = csvToReadOnlySet("");
	private String profile = DEFAULT_PROFILE_NAME;

	private FactoryStringSwing factorySwing;
	private FactoryStringFx factoryFx;

	public EditorString() {
	}

	public EditorString(String deflt, int lines) {
		this(deflt, lines, "", "");
	}

	public EditorString(String deflt, int lines, String label, String description) {
		this.dflt = deflt;
		this.value = deflt;
		this.lines = lines;
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void initFor(Field field) {
		EdOptsString annotation = field.getAnnotation(EdOptsString.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Field must have an EdOptsString annotation to use this editor: " + field.getName());
		}
		lines = annotation.lines();
		dflt = annotation.dflt();
		value = dflt;
		profilesEdit = csvToReadOnlySet(annotation.profilesEdit());
	}

	@Override
	public void setConfig(JsonElement config) {
		if (config != null && config.isJsonPrimitive()) {
			value = config.getAsJsonPrimitive().getAsString();
		} else {
			value = dflt;
		}
		fillComponent();
	}

	@Override
	public JsonElement getConfig() {
		readComponent();
		return new JsonPrimitive(value);
	}

	@Override
	public GuiFactorySwing getGuiFactorySwing() {
		if (factoryFx != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factorySwing == null) {
			factorySwing = new FactoryStringSwing(this);
		}
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactoryStringFx(this);
		}
		return factoryFx;
	}

	protected final void setDflt(String dflt) {
		this.dflt = dflt;
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

	public int getLines() {
		return lines;
	}

	public String getRawValue() {
		return value;
	}

	public void setRawValue(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		readComponent();
		return value;
	}

	@Override
	public void setValue(String value) {
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
		return dflt.equals(value);
	}

}
