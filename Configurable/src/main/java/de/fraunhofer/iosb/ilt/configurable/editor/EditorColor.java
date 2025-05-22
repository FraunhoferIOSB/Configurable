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
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.ItemInteger;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.ItemObject;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.RootSchema;
import de.fraunhofer.iosb.ilt.configurable.JsonSchema.SchemaItem;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryColorFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryColorSwing;
import java.awt.Color;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;

/**
 *
 * @author Hylke van der Schaaf
 */
public class EditorColor extends EditorDefault<Color> {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface EdOptsColor {

        /**
         * A value from 0-255.
         *
         * @return The default red value.
         */
        int red() default 0;

        /**
         * A value from 0-255.
         *
         * @return The default green value.
         */
        int green() default 0;

        /**
         * A value from 0-255.
         *
         * @return The default blue value.
         */
        int blue() default 0;

        /**
         * A value from 0-255.
         *
         * @return The default alpha value.
         */
        int alpha() default 255;

        /**
         * @return Flag indicating the alpha value can be edited.
         */
        boolean editAlpha() default true;

        /**
         * A comma separated, case insensitive list of profile names. This field
         * is only editable when one of these profiles is active. The "default"
         * profile is automatically added to the list.
         *
         * @return A comma separated, case insensitive list of profile names.
         */
        String profilesEdit() default "";
    }

    private Color dflt;
    private boolean editAlpla = true;
    private int red;
    private int green;
    private int blue;
    private int alpha = 255;

    public Set<String> profilesEdit = csvToReadOnlySet("");
    private String profile = DEFAULT_PROFILE_NAME;

    private FactoryColorSwing factorySwing;
    private FactoryColorFx factoryFx;

    public EditorColor() {
        this.dflt = Color.BLACK;
        resetToDefault();
    }

    public EditorColor(Color dflt) {
        this.dflt = dflt;
        resetToDefault();
    }

    public EditorColor(Color deflt, boolean editAlpha) {
        this(deflt);
        this.editAlpla = editAlpha;
    }

    public EditorColor(final Color deflt, final boolean editAlpha, final String label, final String description) {
        this(deflt, editAlpha);
        setLabel(label);
        setDescription(description);
    }

    private void resetToDefault() {
        this.red = dflt.getRed();
        this.green = dflt.getGreen();
        this.blue = dflt.getBlue();
        this.alpha = dflt.getAlpha();
    }

    @Override
    public void initFor(Field field) {
        EdOptsColor annotation = field.getAnnotation(EdOptsColor.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Field must have an EdOptsColor annotation to use this editor: " + field.getName());
        }
        editAlpla = annotation.editAlpha();
        red = annotation.red();
        green = annotation.green();
        blue = annotation.blue();
        alpha = annotation.alpha();
        profilesEdit = csvToReadOnlySet(annotation.profilesEdit());
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
        if (config == null) {
            resetToDefault();
        } else if (config.isJsonObject()) {
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
    public SchemaItem getJsonSchema(RootSchema rootSchema) {
        ItemObject myItem = new ItemObject()
                .addProperty("red", false, new ItemInteger().setMinimum(0L).setMaximum(255L).setDeflt(0L).setTitle("Red").setDescription("The red value"))
                .addProperty("green", false, new ItemInteger().setMinimum(0L).setMaximum(255L).setDeflt(0L).setTitle("Green").setDescription("The green value"))
                .addProperty("blue", false, new ItemInteger().setMinimum(0L).setMaximum(255L).setDeflt(0L).setTitle("Blue").setDescription("The blue value"));
        if (editAlpla) {
            myItem.addProperty("alpha", false, new ItemInteger().setMinimum(0L).setMaximum(255L).setDeflt(255L).setTitle("Alpha").setDescription("The alpha value"));
        }
        if (rootSchema == null) {
            return new RootSchema(myItem);
        }
        return myItem;
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
    public Color getDefaultValue() {
        return dflt;
    }

    @Override
    public void setValue(Color value) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        return dflt.getRed() == red
                && dflt.getGreen() == green
                && dflt.getBlue() == blue
                && dflt.getAlpha() == alpha;
    }

}
