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
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.ItemNumber;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.RootSchema;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.SchemaItem;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryBigDecimalFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryBigDecimalSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Set;

/**
 *
 * @author Hylke van der Schaaf
 */
public class EditorBigDecimal extends EditorDefault<BigDecimal> {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface EdOptsBigDecimal {

        double min() default Double.NEGATIVE_INFINITY;

        double max() default Double.POSITIVE_INFINITY;

        /**
         * The default value, must not be NaN, NEGATIVE_INFINITY nor
         * POSITIVE_INFINITY
         *
         * @return The default value. Used if dfltIsNull is false.
         */
        double dflt() default 0;

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

    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal dflt;
    private BigDecimal value;

    public Set<String> profilesEdit = csvToReadOnlySet("");
    private String profile = DEFAULT_PROFILE_NAME;

    private FactoryBigDecimalSwing factorySwing;
    private FactoryBigDecimalFx factoryFx;

    public EditorBigDecimal() {
    }

    public EditorBigDecimal(BigDecimal min, BigDecimal max, BigDecimal deflt) {
        this.dflt = deflt;
        this.value = deflt;
        this.min = min;
        this.max = max;
    }

    public EditorBigDecimal(BigDecimal min, BigDecimal max, BigDecimal deflt, String label, String description) {
        this.dflt = deflt;
        this.value = deflt;
        this.min = min;
        this.max = max;
        setLabel(label);
        setDescription(description);
    }

    private BigDecimal fromDouble(double value) {
        if (value == Double.NEGATIVE_INFINITY || value == Double.POSITIVE_INFINITY || value == Double.NaN) {
            return null;
        }
        return new BigDecimal(value);
    }

    @Override
    public void initFor(Field field) {
        EdOptsBigDecimal annotation = field.getAnnotation(EdOptsBigDecimal.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Field must have an EdOptsDouble annotation to use this editor: " + field.getName());
        }
        min = fromDouble(annotation.min());
        max = fromDouble(annotation.max());
        if (!annotation.dfltIsNull()) {
            dflt = new BigDecimal(annotation.dflt());
        }
        value = dflt;
        profilesEdit = csvToReadOnlySet(annotation.profilesEdit());
    }

    @Override
    public void setConfig(JsonElement config) {
        if (config != null && config.isJsonPrimitive()) {
            value = config.getAsBigDecimal();
        } else {
            value = dflt;
        }
        fillComponent();
    }

    @Override
    public JsonElement getConfig() {
        readComponent();
        if (value == null) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive(value);
    }

    @Override
    public SchemaItem getJsonSchema(RootSchema rootSchema) {
        ItemNumber item = new ItemNumber()
                .setDeflt(dflt)
                .setTitle(getLabel())
                .setDescription(getDescription());
        if (min != null) {
            item.setMinimum(min);
        }
        if (max != null) {
            item.setMaximum(max);
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
            factorySwing = new FactoryBigDecimalSwing(this);
        }
        return factorySwing;
    }

    @Override
    public GuiFactoryFx getGuiFactoryFx() {
        if (factorySwing != null) {
            throw new IllegalArgumentException("Can not mix different types of editors.");
        }
        if (factoryFx == null) {
            factoryFx = new FactoryBigDecimalFx(this);
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

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getDeflt() {
        return dflt;
    }

    public BigDecimal getRawValue() {
        return value;
    }

    public void setRawValue(BigDecimal value) {
        if (min != null && min.compareTo(value) > 0) {
            value = min;
        }
        if (max != null && max.compareTo(value) < 0) {
            value = max;
        }
        this.value = value;
    }

    @Override
    public BigDecimal getValue() {
        readComponent();
        return value;
    }

    @Override
    public void setValue(BigDecimal value) {
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
        return dflt == value;
    }

}
