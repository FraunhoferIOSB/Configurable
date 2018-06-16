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
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryLongFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryLongSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class EditorLong extends EditorDefault<Long> {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface EdOptsLong {

		int min();

		int max();

		int dflt();
	}

	private long min;
	private long max;
	private long dflt;
	private long value;

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

	public long getRawValue() {
		return value;
	}

	public void setRawValue(long value) {
		this.value = value;
	}

	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}

	public long getDeflt() {
		return dflt;
	}

	@Override
	public Long getValue() {
		readComponent();
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

}
