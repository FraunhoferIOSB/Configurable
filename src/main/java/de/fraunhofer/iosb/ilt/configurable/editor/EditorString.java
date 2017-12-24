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

import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryStringFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryStringSwing;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;

/**
 *
 * @author Hylke van der Schaaf
 */
public class EditorString extends EditorDefault<String> {

	private final String deflt;
	private String value;
	private int lines = 5;

	private FactoryStringSwing factorySwing;
	private FactoryStringFx factoryFx;

	public EditorString(String deflt, int lines) {
		this.deflt = deflt;
		this.value = deflt;
		this.lines = lines;
	}

	public EditorString(String deflt, int lines, String label, String description) {
		this.deflt = deflt;
		this.value = deflt;
		this.lines = lines;
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void setConfig(JsonElement config) {
		if (config != null && config.isJsonPrimitive()) {
			value = config.getAsJsonPrimitive().getAsString();
		} else {
			value = deflt;
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

}
