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
package de.fraunhofer.iosb.ilt.configurable;

import com.google.gson.JsonElement;
import javafx.scene.Node;
import javax.swing.JComponent;

/**
 * Interface defining configuration editors.
 *
 * @author hylke
 * @param <T> The type of object returned by getValue.
 */
public interface ConfigEditor<T> {

	/**
	 * Load the given configuration into this editor.
	 *
	 * @param config the configuration to load into this editor.
	 */
	public void setConfig(JsonElement config);

	/**
	 * Get the current (edited) state of the configuration.
	 *
	 * @return The current (edited) configuration.
	 */
	public JsonElement getConfig();

	/**
	 * Get the value configured in the editor.
	 *
	 * @return the value configured in the editor.
	 */
	public T getValue();

	/**
	 * Set the value in the editor. Used for saving an (externally) updated
	 * configuration.
	 *
	 * @param value the value in the editor.
	 */
	public void setValue(T value);

	/**
	 * Get the swing Component that represents the editor.
	 *
	 * @return The swing Component that represents the editor.
	 */
	public JComponent getComponent();

	/**
	 * Get the JavaFX Node that represents the editor.
	 *
	 * @return The JavaFX Node that represents the editor.
	 */
	public Node getNode();

	/**
	 * Get the human-readable label to use for this editor. Can return an empty
	 * string.
	 *
	 * @return The label to use for this editor.
	 */
	default String getLabel() {
		return "";
	}

	/**
	 * Get the description for this editor. Can return an empty string.
	 *
	 * @return The description to use for this editor.
	 */
	default String getDescription() {
		return "";
	}
}
