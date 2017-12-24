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
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryBooleanFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryBooleanSwing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An editor for boolean values.
 *
 * @author Hylke van der Schaaf
 */
public final class EditorBoolean extends EditorDefault<Boolean> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EditorBoolean.class.getName());
	private final boolean deflt;
	private boolean value;

	private FactoryBooleanSwing factorySwing;
	private FactoryBooleanFx factoryFx;

	public EditorBoolean(boolean deflt) {
		this.value = deflt;
		this.deflt = deflt;
	}

	public EditorBoolean(boolean deflt, String label, String description) {
		this.value = deflt;
		this.deflt = deflt;
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void setConfig(JsonElement config) {
		try {
			value = config.getAsBoolean();
		} catch (ClassCastException | IllegalStateException e) {
			value = deflt;
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

	@Override
	public JsonElement getConfig() {
		if (factorySwing != null) {
			value = factorySwing.isSelected();
		}
		if (factoryFx != null) {
			value = factoryFx.isSelected();
		}
		return new JsonPrimitive(value);
	}

	public boolean isSelected() {
		if (factorySwing != null) {
			return factorySwing.isSelected();
		}
		if (factoryFx != null) {
			return factoryFx.isSelected();
		}
		return value;
	}

	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public void setValue(Boolean value) {
		this.value = value;
		fillComponent();
	}

}
