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
package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.AbstractConfigurable;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorBoolean;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorClass;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author scf
 */
public class Flag extends AbstractConfigurable<Object, Object> {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface Internal {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface Private {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface Public {}

    private static final Logger LOGGER = LoggerFactory.getLogger(Flag.class);

    @ConfigurableField(
            editor = EditorInt.class,
            label = "Width",
            description = "The width of our flag")
    @EditorInt.EdOptsInt(min = 1, max = 100, step = 1, dflt = 10)
    private int width;

    @ConfigurableField(
            editor = EditorInt.class,
            label = "Height",
            description = "The height of our flag")
    @EditorInt.EdOptsInt(min = 1, max = 100, step = 1, dflt = 10)
    private int height;

    @ConfigurableField(
            editor = EditorClass.class,
            label = "Circle",
            description = "The circle to put on the flag")
    @EditorClass.EdOptsClass(clazz = Circle.class)
    private Circle circle;

    @ConfigurableField(
            editor = EditorBoolean.class,
            label = "Cloth",
            description = "Is this flag made of cloth?")
    @EditorBoolean.EdOptsBool(dflt = true)
    private boolean cloth;

    public void wave() {
        LOGGER.info("I'm waving a flag of {} by {}. It is made of cloth: {}. It has a circle:", width, height, cloth);
        circle.paintMe();
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

}
