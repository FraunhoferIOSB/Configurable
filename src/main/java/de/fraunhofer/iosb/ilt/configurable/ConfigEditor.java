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
import java.lang.reflect.Field;

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
	 * Get a factory that can generate a swing-based gui for this editor.
	 *
	 * @return A factory that can generate a swing-based gui for this editor.
	 */
	public GuiFactorySwing getGuiFactorySwing();

	/**
	 * Get a factory that can generate a JavaFX-based gui for this editor.
	 *
	 * @return A factory that can generate a JavaFX-based gui for this editor.
	 */
	public GuiFactoryFx getGuiFactoryFx();

	/**
	 * Get the human-readable label to use for this editor. Can return an empty
	 * string.
	 *
	 * @return The label to use for this editor.
	 */
	public String getLabel();

	/**
	 * The human readable label for this editor.
	 *
	 * @param label the label to set
	 */
	public void setLabel(String label);

	/**
	 * Get the description for this editor. Can return an empty string.
	 *
	 * @return The description to use for this editor.
	 */
	public String getDescription();

	/**
	 * The longer description for this editor.
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description);

	/**
	 * Initialise the editor for the given Field, using the Field name and type
	 * and any annotations present on the Field.
	 *
	 * @param field the Field to initialise the editor for.
	 */
	public void initFor(Field field);
}
