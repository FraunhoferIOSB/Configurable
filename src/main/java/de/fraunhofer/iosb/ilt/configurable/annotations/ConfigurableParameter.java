/*
 * Copyright (C) 2019 Fraunhofer IOSB
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
 * Annotation holding options for dealing with this parameter in the
 * Configurable system.
 *
 * @author Fraunhofer IOSB
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ConfigurableParameter {

	enum ConfigurableParameterType {
		RUNTIME_CONTEXT, CLASS_CONFIG, JSON_FIELD
	}

	ConfigurableParameterType type() default ConfigurableParameterType.JSON_FIELD;
	/**
	 * The name of the field in the configuration JSON to use for storing the
	 * value of the annotated field. Defaults to the field name (case
	 * sensitive).
	 *
	 * @return The name of the field in the configuration JSON to use for
	 * storing the value of the annotated field.
	 */
	String jsonField() default "";
}
