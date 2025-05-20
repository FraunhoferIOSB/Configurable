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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation holding options for dealing with this class in the Configurable
 * system.
 *
 * @author Hylke van der Schaaf
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigurableClass {

    /**
     * The value to use in EditorSubClass when specifying this class. The
     * default is the fully qualified class name.
     *
     * @return The value to use in EditorSubClass when specifying this class.
     */
    String jsonName() default "";

    String displayName() default "";

    /**
     * A comma separated, case insensitive list of profile names. Items can only
     * be added or removed from the EditorMap generated for this class, when one
     * of these profiles is active. The "default" profile is automatically added
     * to the list.
     *
     * @return A comma separated, case insensitive list of profile names.
     */
    String profilesEdit() default "";

}
