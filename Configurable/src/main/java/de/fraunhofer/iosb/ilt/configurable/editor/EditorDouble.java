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
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.ItemNumber;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.RootSchema;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.SchemaItem;
import static de.fraunhofer.iosb.ilt.configurable.annotations.AnnotationHelper.csvToReadOnlySet;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryDoubleFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryDoubleSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hylke van der Schaaf
 */
public class EditorDouble extends EditorDefault<Double> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EditorDouble.class.getName());

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface EdOptsDouble {

		double min() default Double.NEGATIVE_INFINITY;

		double max() default Double.POSITIVE_INFINITY;

		double step() default Double.MIN_VALUE;

		/**
		 * @return The default value. Used if dfltIsNull is false.
		 */
		double dflt() default 0;

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

	private double min;
	private double max;
	private double step;
	private Double dflt;
	private Double value;

	public Set<String> profilesEdit = csvToReadOnlySet("");
	private String profile = DEFAULT_PROFILE_NAME;

	private FactoryDoubleSwing factorySwing;
	private FactoryDoubleFx factoryFx;

	public EditorDouble() {
	}

	public EditorDouble(double min, double max, double step, double deflt) {
		this.dflt = deflt;
		this.value = deflt;
		this.min = min;
		this.max = max;
		this.step = step;
	}

	public EditorDouble(double min, double max, double step, double deflt, String label, String description) {
		this.dflt = deflt;
		this.value = deflt;
		this.min = min;
		this.max = max;
		this.step = step;
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void initFor(Field field) {
		EdOptsDouble annotation = field.getAnnotation(EdOptsDouble.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Field must have an EdOptsDouble annotation to use this editor: " + field.getName());
		}
		min = annotation.min();
		max = annotation.max();
		step = annotation.step();
		boolean isPrimitive = field.getType().isPrimitive();
		final boolean dfltIsNull = annotation.dfltIsNull();
		if (dfltIsNull) {
			if (isPrimitive) {
				LOGGER.error("Flag dfltIsNull set to true on a primitive field: {}", field);
				dflt = 0D;
			}
		} else {
			dflt = annotation.dflt();
		}
		value = dflt;
		profilesEdit = csvToReadOnlySet(annotation.profilesEdit());
	}

	@Override
	public void setConfig(JsonElement config) {
		if (config != null && config.isJsonPrimitive()) {
			value = config.getAsDouble();
		} else {
			value = dflt;
		}
		fillComponent();
	}

	@Override
	public JsonElement getConfig() {
		Double val = getValue();
		if (val == null) {
			return JsonNull.INSTANCE;
		}
		return new JsonPrimitive(val);

	}

	@Override
	public SchemaItem getJsonSchema(RootSchema rootSchema) {
		ItemNumber item = new ItemNumber()
				.setDeflt(dflt)
				.setTitle(getLabel())
				.setDescription(getDescription());
		if (min > Double.NEGATIVE_INFINITY) {
			item.setMinimum(BigDecimal.valueOf(min));
		}
		if (max < Double.POSITIVE_INFINITY) {
			item.setMaximum(BigDecimal.valueOf(max));
		}
		if (rootSchema == null) {
			return new RootSchema(item);
		}
		return item;
	}

	@Override
	public GuiFactorySwing getGuiFactorySwing() {
		if (factoryFx != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factorySwing == null) {
			factorySwing = new FactoryDoubleSwing(this);
		}
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactoryDoubleFx(this);
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

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public Double getDeflt() {
		return dflt;
	}

	public double getStep() {
		return step;
	}

	public Double getRawValue() {
		return value;
	}

	public void setRawValue(double value) {
		if (value < min) {
			value = min;
		}
		if (value > max) {
			value = max;
		}
		this.value = value;
	}

	@Override
	public Double getValue() {
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
	public void setValue(Double value) {
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
