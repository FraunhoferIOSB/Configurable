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
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.ItemInteger;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.RootSchema;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.SchemaItem;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryIntFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryIntSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class EditorInt extends EditorDefault<Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditorInt.class.getName());

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface EdOptsInt {

        int min() default Integer.MIN_VALUE;

        int max() default Integer.MAX_VALUE;

        int step() default 1;

        /**
         * @return The default value. Used if dfltIsNull is false.
         */
        int dflt() default 0;

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

    private int min;
    private int max;
    private int step;
    private Integer dflt;
    private Integer value;

    public Set<String> profilesEdit = csvToReadOnlySet("");
    private String profile = DEFAULT_PROFILE_NAME;

    private FactoryIntSwing factorySwing;
    private FactoryIntFx factoryFx;

    public EditorInt() {
    }

    public EditorInt(int min, int max, int step, int dflt, String label, String description) {
        this.dflt = dflt;
        this.value = dflt;
        this.min = min;
        this.max = max;
        this.step = step;
        setLabel(label);
        setDescription(description);
    }

    @Override
    public void initFor(Field field) {
        EdOptsInt annotation = field.getAnnotation(EdOptsInt.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Field must have an EdIntOpts annotation to use this editor: " + field.getName());
        }
        min = annotation.min();
        max = annotation.max();
        step = annotation.step();
        final boolean isPrimitive = field.getType().isPrimitive();
        final boolean dfltIsNull = annotation.dfltIsNull();
        if (dfltIsNull) {
            if (isPrimitive) {
                LOGGER.error("Flag dfltIsNull set to true on a primitive field: {}", field);
                dflt = 0;
            }
        } else {
            dflt = annotation.dflt();
        }
        value = dflt;
        profilesEdit = csvToReadOnlySet(annotation.profilesEdit());
    }

    @Override
    public void setConfig(JsonElement config) {
        if (config != null && config.isJsonPrimitive() && config.getAsJsonPrimitive().isNumber()) {
            value = config.getAsInt();
        } else {
            value = dflt;
        }
        fillComponent();
    }

    @Override
    public JsonElement getConfig() {
        Integer val = getValue();
        if (val == null) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive(val);
    }

    @Override
    public SchemaItem getJsonSchema(RootSchema rootSchema) {
        ItemInteger item = new ItemInteger()
                .setDeflt(dflt)
                .setTitle(getLabel())
                .setDescription(getDescription());
        if (min > Integer.MIN_VALUE) {
            item.setMinimum(Long.valueOf(min));
        }
        if (max < Integer.MAX_VALUE) {
            item.setMaximum(Long.valueOf(max));
        }
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

    public Integer getDflt() {
        return dflt;
    }

    public int getStep() {
        return step;
    }

    public Integer getRawValue() {
        return value;
    }

    public void setRawValue(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        readComponent();
        if (value == null) {
            return null;
        }
        if (value > max) {
            value = max;
        }
        if (value < min) {
            value = min;
        }
        return value;
    }

    @Override
    public void setValue(Integer value) {
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
