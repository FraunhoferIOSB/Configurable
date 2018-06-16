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
}
