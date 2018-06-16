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
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryIntFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryIntSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class EditorInt extends EditorDefault<Integer> {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface EdOptsInt {

		int min();

		int max();

		int step();

		int dflt();
	}
	private int min;
	private int max;
	private int step;
	private int dflt;
	private int value;

	private FactoryIntSwing factorySwing;
	private FactoryIntFx factoryFx;

	public EditorInt() {
	}

	public EditorInt(int min, int max, int step, int dflt, String label, String description) {
		this.dflt = dflt;
		this.value = dflt;
		this.min = min;
		this.max = max;
		this.step = step;
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void initFor(Field field) {
		EdOptsInt annotation = field.getAnnotation(EdOptsInt.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Field must have an EdIntOpts annotation to use this editor: " + field.getName());
		}
		min = annotation.min();
		max = annotation.max();
		step = annotation.step();
		dflt = annotation.dflt();
		value = dflt;
	}

	@Override
	public void setConfig(JsonElement config) {
		if (config != null && config.isJsonPrimitive() && config.getAsJsonPrimitive().isNumber()) {
			value = config.getAsInt();
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
			factorySwing = new FactoryIntSwing(this);
		}
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactoryIntFx(this);
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

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public int getDflt() {
		return dflt;
	}

	public int getStep() {
		return step;
	}

	public int getRawValue() {
		return value;
	}

	public void setRawValue(int value) {
		this.value = value;
	}

	@Override
	public Integer getValue() {
		readComponent();
		return value;
	}

	@Override
	public void setValue(Integer value) {
		this.value = value;
		fillComponent();
	}
}
