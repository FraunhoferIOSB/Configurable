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
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryLongFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryLongSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class EditorLong extends EditorDefault<Long> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EditorLong.class.getName());

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface EdOptsLong {

		long min() default Long.MIN_VALUE;

		long max() default Long.MAX_VALUE;

		/**
		 * @return The default value. Used if dfltIsNull is false.
		 */
		long dflt() default 0;

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

	private long min;
	private long max;
	private Long dflt;
	private Long value;

	public Set<String> profilesEdit = csvToReadOnlySet("");
	private String profile = DEFAULT_PROFILE_NAME;

	private FactoryLongSwing factorySwing;
	private FactoryLongFx factoryFx;

	public EditorLong() {
	}

	public EditorLong(long min, long max, long deflt, String label, String description) {
		this.dflt = deflt;
		this.value = deflt;
		this.min = min;
		this.max = max;
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void initFor(Field field) {
		EdOptsLong annotation = field.getAnnotation(EdOptsLong.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Field must have an EdOptsLong annotation to use this editor: " + field.getName());
		}
		min = annotation.min();
		max = annotation.max();
		boolean isPrimitive = field.getType().isPrimitive();
		final boolean dfltIsNull = annotation.dfltIsNull();
		if (dfltIsNull) {
			if (isPrimitive) {
				LOGGER.error("Flag dfltIsNull set to true on a primitive field: {}", field);
				dflt = 0L;
			}
		} else {
			dflt = annotation.dflt();
		}
		value = dflt;
		profilesEdit = csvToReadOnlySet(annotation.profilesEdit());
	}

	@Override
	public void setConfig(JsonElement config) {
		if (config != null && config.isJsonPrimitive() && config.getAsJsonPrimitive().isNumber()) {
			value = config.getAsLong();
		} else {
			value = dflt;
		}
		fillComponent();
	}

	@Override
	public JsonElement getConfig() {
		Long val = getValue();
		if (val == null) {
			return JsonNull.INSTANCE;
		}
		return new JsonPrimitive(val);
	}

	@Override
	public GuiFactorySwing getGuiFactorySwing() {
		if (factoryFx != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factorySwing == null) {
			factorySwing = new FactoryLongSwing(this);
		}
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactoryLongFx(this);
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

	public Long getRawValue() {
		return value;
	}

	public void setRawValue(long value) {
		if (value < min) {
			value = min;
		}
		if (value > max) {
			value = max;
		}
		this.value = value;
	}

	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}

	public Long getDeflt() {
		return dflt;
	}

	@Override
	public Long getValue() {
		readComponent();
		if (value == null) {
			return null;
		}
		if (value > max) {
			value = max;
		}
		if (value < min) {
			value = min;
		}
		return value;
	}

	@Override
	public void setValue(Long value) {
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
