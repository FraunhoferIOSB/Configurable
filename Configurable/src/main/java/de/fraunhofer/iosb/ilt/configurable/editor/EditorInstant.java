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
package de.fraunhofer.iosb.ilt.configurable.editor;

import static de.fraunhofer.iosb.ilt.configurable.ConfigEditor.DEFAULT_PROFILE_NAME;
import static de.fraunhofer.iosb.ilt.configurable.annotations.AnnotationHelper.csvToReadOnlySet;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.ItemString;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.RootSchema;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.SchemaItem;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryInstantFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryInstantSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Editor for time Instant.
 */
public final class EditorInstant extends EditorDefault<Instant> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditorInstant.class.getName());

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface EdOptsInstant {

        /**
         * @return The default value. Used if dfltIsNull is false.
         */
        String dflt() default "";

        /**
         * If set to true, the default value of the editor is null.
         *
         * @return if true, the default value of the editor is null, not the
         * value of dflt.
         */
        boolean dfltIsNull() default false;

        /**
         * A comma separated, case insensitive list of profile names. This field
         * is only editable when one of these profiles is active. The "default"
         * profile is automatically added to the list.
         *
         * @return A comma separated, case insensitive list of profile names.
         */
        String profilesEdit() default "";
    }

    private Instant dflt;
    private Instant value;

    public Set<String> profilesEdit = csvToReadOnlySet("");
    private String profile = DEFAULT_PROFILE_NAME;

    private FactoryInstantSwing factorySwing;
    private FactoryInstantFx factoryFx;

    public EditorInstant() {
    }

    public EditorInstant(Instant dflt, String label, String description) {
        this.dflt = dflt;
        this.value = dflt;
        setLabel(label);
        setDescription(description);
    }

    @Override
    public void initFor(Field field) {
        EdOptsInstant annotation = field.getAnnotation(EdOptsInstant.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Field must have an EdIntOpts annotation to use this editor: " + field.getName());
        }
        final boolean dfltIsNull = annotation.dfltIsNull();
        if (!dfltIsNull) {
            try {
                dflt = Instant.parse(annotation.dflt());
            } catch (DateTimeParseException ex) {
                dflt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            }
        }
        value = dflt;
        profilesEdit = csvToReadOnlySet(annotation.profilesEdit());
    }

    @Override
    public void setConfig(JsonElement config) {
        if (config != null && config.isJsonPrimitive() && config.getAsJsonPrimitive().isString()) {
            try {
                value = Instant.parse(config.getAsString());
            } catch (DateTimeParseException ex) {
                LOGGER.error("Failed to parse {}", config);
                value = dflt;
            }
        } else {
            value = dflt;
        }
        fillComponent();
    }

    @Override
    public JsonElement getConfig() {
        Instant val = getValue();
        if (val == null) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive(val.toString());
    }

    @Override
    public SchemaItem getJsonSchema(RootSchema rootSchema) {
        ItemString item = new ItemString()
                .setDeflt(dflt)
                .setTitle(getLabel())
                .setDescription(getDescription());

        if (rootSchema == null) {
            return new RootSchema(item);
        }
        return item;
    }

    @Override
    public GuiFactorySwing getGuiFactorySwing() {
        if (factoryFx != null) {
            throw new IllegalArgumentException("Can not mix different types of editors.");
        }
        if (factorySwing == null) {
            factorySwing = new FactoryInstantSwing(this);
        }
        return factorySwing;
    }

    @Override
    public GuiFactoryFx getGuiFactoryFx() {
        if (factorySwing != null) {
            throw new IllegalArgumentException("Can not mix different types of editors.");
        }
        if (factoryFx == null) {
            factoryFx = new FactoryInstantFx(this);
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

    public Instant getDflt() {
        return dflt;
    }

    public Instant getRawValue() {
        return value;
    }

    public void setRawValue(Instant value) {
        this.value = value;
    }

    @Override
    public Instant getValue() {
        readComponent();
        if (value == null) {
            return null;
        }
        return value;
    }

    @Override
    public Instant getDefaultValue() {
        return dflt;
    }

    @Override
    public void setValue(Instant value) {
        this.value = value;
        fillComponent();
    }

    @Override
    public void setProfile(String profile) {
        this.profile = profile;
        fillComponent();
    }

    public void setProfilesEdit(String csv) {
        profilesEdit = csvToReadOnlySet(csv);
    }

    @Override
    public boolean canEdit() {
        return profilesEdit.contains(profile);
    }

    @Override
    public boolean isDefault() {
        readComponent();
        return Objects.equals(dflt, value);
    }

}
