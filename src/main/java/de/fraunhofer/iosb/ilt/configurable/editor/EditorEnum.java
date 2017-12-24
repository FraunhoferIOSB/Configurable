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

import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryEnumFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryEnumSwing;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;

/**
 *
 * @author Hylke van der Schaaf
 * @param <T> The type this editor selects.
 */
public class EditorEnum<T extends Enum<T>> extends EditorDefault<T> {

	private final Class<T> sourceType;
	private final T deflt;
	private T value;

	private FactoryEnumSwing<T> factorySwing;
	private FactoryEnumFx<T> factoryFx;

	public EditorEnum(Class<T> sourceType, T deflt, String label, String description) {
		this.sourceType = sourceType;
		this.deflt = deflt;
		this.value = deflt;
		setLabel(label);
		setDescription(description);
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
			value = deflt;
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

}
