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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.EditorFactory;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryListFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryListSwing;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An editor for a list of editors, all of the same type.
 *
 * @author Hylke van der Schaaf
 * @param <T> The type of editors that edit the items in the list.
 * @param <U> The type of items in the list.
 */
public class EditorList<U, T extends ConfigEditor<U>> extends EditorDefault<List<U>> implements Iterable<T> {

	private final EditorFactory<T> factory;
	private final List<T> value = new ArrayList<>();

	private FactoryListSwing factorySwing;
	private FactoryListFx factoryFx;

	public EditorList(EditorFactory<T> factory) {
		this.factory = factory;
	}

	public EditorList(EditorFactory<T> factory, String label, String description) {
		this.factory = factory;
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void setConfig(JsonElement config) {
		value.clear();
		if (config != null && config.isJsonArray()) {
			JsonArray asArray = config.getAsJsonArray();
			for (JsonElement subConf : asArray) {
				T item = factory.createEditor();
				item.setConfig(subConf);
				value.add(item);
			}
		}
		fillComponent();
	}

	@Override
	public JsonElement getConfig() {
		JsonArray result = new JsonArray();
		for (T item : value) {
			result.add(item.getConfig());
		}
		return result;
	}

	@Override
	public GuiFactorySwing getGuiFactorySwing() {
		if (factoryFx != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factorySwing == null) {
			factorySwing = new FactoryListSwing(this);
		}
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactoryListFx(this);
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

	public void addItem() {
		final T item = factory.createEditor();
		value.add(item);
		fillComponent();
	}

	public void removeItem(T item) {
		value.remove(item);
		fillComponent();
	}

	@Override
	public Iterator<T> iterator() {
		return value.iterator();
	}

	public List<T> getRawValue() {
		return value;
	}

	@Override
	public List<U> getValue() {
		List<U> valList = new ArrayList<>();
		for (T val : this) {
			valList.add(val.getValue());
		}
		return valList;
	}

	@Override
	public void setValue(List<U> value) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
