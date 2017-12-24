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
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryColorFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryColorSwing;
import java.awt.Color;

/**
 *
 * @author Hylke van der Schaaf
 */
public class EditorColor extends EditorDefault<Color> {

	private boolean editAlpla = true;
	private int red;
	private int green;
	private int blue;
	private int alpha = 255;

	private FactoryColorSwing factorySwing;
	private FactoryColorFx factoryFx;

	public EditorColor(Color deflt) {
		this.red = deflt.getRed();
		this.green = deflt.getGreen();
		this.blue = deflt.getBlue();
		this.alpha = deflt.getAlpha();
	}

	public EditorColor(Color deflt, boolean editAlpha) {
		this(deflt);
		this.editAlpla = editAlpha;
	}

	public EditorColor(final Color deflt, final boolean editAlpha, final String label, final String description) {
		this(deflt);
		this.editAlpla = editAlpha;
		setLabel(label);
		setDescription(description);
	}

	private static int getInt(JsonObject confObj, int dflt, String... names) {
		for (final String name : names) {
			final JsonElement element = confObj.get(name);
			if (element != null && element.isJsonPrimitive()) {
				return element.getAsInt();
			}
		}
		return dflt;
	}

	@Override
	public void setConfig(JsonElement config) {
		if (config.isJsonObject()) {
			JsonObject confObj = config.getAsJsonObject();
			red = getInt(confObj, red, "r", "red");
			green = getInt(confObj, green, "g", "green");
			blue = getInt(confObj, blue, "b", "blue");
			alpha = getInt(confObj, alpha, "a", "alpha");
		}
		fillComponent();
	}

	@Override
	public JsonElement getConfig() {
		readComponent();
		JsonObject config = new JsonObject();
		config.add("r", new JsonPrimitive(red));
		config.add("g", new JsonPrimitive(green));
		config.add("b", new JsonPrimitive(blue));
		if (editAlpla && alpha != 255) {
			config.add("a", new JsonPrimitive(alpha));
		}
		return config;
	}

	@Override
	public GuiFactorySwing getGuiFactorySwing() {
		if (factoryFx != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factorySwing == null) {
			factorySwing = new FactoryColorSwing(this);
		}
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactoryColorFx(this);
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

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public boolean isEditAlpla() {
		return editAlpla;
	}

	@Override
	public Color getValue() {
		readComponent();
		return new Color(red, green, blue, alpha);
	}

	@Override
	public void setValue(Color value) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
