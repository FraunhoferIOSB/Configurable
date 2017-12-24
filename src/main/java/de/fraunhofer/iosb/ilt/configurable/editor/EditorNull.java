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

import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryNullFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryNullSwing;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;

/**
 * An editor that does not edit anything. For cases where you want a class to be
 * configurable, but not actually have an editor.
 *
 * @author Hylke van der Schaaf
 */
public class EditorNull extends EditorDefault<Void> {

	private FactoryNullSwing factorySwing;
	private FactoryNullFx factoryFx;

	public EditorNull() {
	}

	public EditorNull(String label, String description) {
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void setConfig(JsonElement config) {
		// Nothing to configure
	}

	@Override
	public GuiFactorySwing getGuiFactorySwing() {
		if (factoryFx != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factorySwing == null) {
			factorySwing = new FactoryNullSwing();
		}
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactoryNullFx();
		}
		return factoryFx;
	}

	@Override
	public JsonElement getConfig() {
		return JsonNull.INSTANCE;
	}

	@Override
	public Void getValue() {
		return null;
	}

	@Override
	public void setValue(Void value) {
	}

}
