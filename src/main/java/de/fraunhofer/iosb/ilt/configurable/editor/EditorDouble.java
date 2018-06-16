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
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryDoubleFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryDoubleSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 *
 * @author Hylke van der Schaaf
 */
public class EditorDouble extends EditorDefault<Double> {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface EdOptsDouble {

		double min();

		double max();

		double step();

		double dflt();
	}
	private double min;
	private double max;
	private double step;
	private double dflt;
	private double value;

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
		dflt = annotation.dflt();
		value = dflt;
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
		return new JsonPrimitive(getValue());
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

	public double getDeflt() {
		return dflt;
	}

	public double getStep() {
		return step;
	}

	public double getRawValue() {
		return value;
	}

	public void setRawValue(double value) {
		this.value = value;
	}

	@Override
	public Double getValue() {
		readComponent();
		return value;
	}

	@Override
	public void setValue(Double value) {
		this.value = value;
		fillComponent();
	}
}
