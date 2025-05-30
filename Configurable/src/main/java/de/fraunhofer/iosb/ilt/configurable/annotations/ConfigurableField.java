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
package de.fraunhofer.iosb.ilt.configurable.annotations;

import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Hylke van der Schaaf
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigurableField {

    /**
     * The class to use as editor for the annotated field. The editor should
     * also supply an annotation type to use for defining further parameters.
     *
     * @return The class to use as editor for the annotated field.
     */
    Class<? extends ConfigEditor> editor();

    /**
     * The label to use in the user interface. Defaults to the field name (case
     * sensitive).
     *
     * @return The label to use in the user interface.
     */
    String label() default "";

    /**
     * The description to use in the user interface.
     *
     * @return The description to use in the user interface.
     */
    String description() default "";

    /**
     * The name of the field in the configuration JSON to use for storing the
     * value of the annotated field. Defaults to the field name (case
     * sensitive).
     *
     * @return The name of the field in the configuration JSON to use for
     * storing the value of the annotated field.
     */
    String jsonField() default "";

    boolean optional() default false;

    /**
     * Merge the configuration of the field into the configuration of the Map
     * holding the field. This only works for fields that have a configuration
     * of the JSON type Object.
     *
     * @return Merge the configuration of the field into the configuration of
     * the Map holding the field.
     */
    boolean merge() default false;

    /**
     * A comma separated, case insensitive list of profile names. This field is
     * only included in the output json when one of these profiles is active.
     * The "default" profile is automatically added to the list.
     *
     * @return A comma separated, case insensitive list of profile names.
     */
    String profilesSave() default "";

    /**
     * A comma separated, case insensitive list of profile names. This field is
     * only included in the GUI when one of these profiles is active. The
     * "default" profile is automatically added to the list.
     *
     * @return A comma separated, case insensitive list of profile names.
     */
    String profilesGui() default "";
}
