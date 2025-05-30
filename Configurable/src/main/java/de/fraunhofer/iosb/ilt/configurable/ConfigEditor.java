/*
 * Copyright (C) 2024 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
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
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.RootSchema;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.SchemaItem;
import java.lang.reflect.Field;

/**
 * Interface defining configuration editors.
 *
 * @author hylke
 * @param <T> The type of object returned by getValue.
 */
public interface ConfigEditor<T> {

    public static final String DEFAULT_PROFILE_NAME = "default";

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
     * @throws ConfigurationException when the configuration can not be used to
     * create a value.
     */
    public T getValue() throws ConfigurationException;

    /**
     * Get the default value defined by annotations.Returns null if there is no
     * default value.
     *
     * @return the default value as defined by annotations.
     * @throws ConfigurationException when the configuration can not be used to
     * create a value.
     */
    public default T getDefaultValue() throws ConfigurationException {
        return null;
    }

    /**
     * Set the value in the editor. Used for saving an (externally) updated
     * configuration.
     *
     * @param value the value in the editor.
     */
    public void setValue(T value);

    /**
     * Get the Schema for this editor, in the form of a root schema.
     *
     * @return The schema for this editor, as a root schema.
     */
    public default RootSchema getJsonRootSchema() {
        SchemaItem jsonSchema = getJsonSchema(null);
        if (jsonSchema instanceof RootSchema) {
            return (RootSchema) jsonSchema;
        }
        throw new IllegalStateException("getJsonSchema did not return a RootSchema.");
    }

    /**
     * Get the JSON Schema for this editor, using the given rootSchema for
     * shared $defs.
     *
     * @param rootSchema the root schema to use for $defs. If null, the return
     * value must be a root schema.
     * @return the schema for this editor.
     */
    public SchemaItem getJsonSchema(RootSchema rootSchema);

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

    /**
     * Initialise the editor for the given Field, using the Field name and type
     * and the annotation on the Field, identified by the given key.
     *
     * @param field the Field to initialise the editor for.
     * @param key the key to use to identify the annotation to load the
     * configuration from.
     */
    public default void initFor(Field field, String key) {
        initFor(field);
    }

    /**
     * Sets the currently active profile.
     *
     * @param profile the currently active profile.
     */
    public default void setProfile(String profile) {
        // does nothing by default.
    }

    /**
     * True if the component is editable in the current profile.
     *
     * @return True if the component is editable in the current profile.
     */
    public default boolean canEdit() {
        return true;
    }

    public default boolean isDefault() {
        return false;
    }
}
