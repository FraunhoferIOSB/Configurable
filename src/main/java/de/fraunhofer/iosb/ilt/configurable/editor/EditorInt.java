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

/**
 *
 * @author Hylke van der Schaaf
 */
public final class EditorInt extends EditorDefault<Integer> {

	private final int min;
	private final int max;
	private final int step;
	private final int deflt;
	private int value;

	private FactoryIntSwing factorySwing;
	private FactoryIntFx factoryFx;

	public EditorInt(int min, int max, int step, int deflt, String label, String description) {
		this.deflt = deflt;
		this.value = deflt;
		this.min = min;
		this.max = max;
		this.step = step;
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void setConfig(JsonElement config) {
		if (config != null && config.isJsonPrimitive() && config.getAsJsonPrimitive().isNumber()) {
			value = config.getAsInt();
		} else {
			value = deflt;
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

	public int getDeflt() {
		return deflt;
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
