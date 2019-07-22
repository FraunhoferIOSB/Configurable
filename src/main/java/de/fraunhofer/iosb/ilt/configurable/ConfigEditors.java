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
package de.fraunhofer.iosb.ilt.configurable;

import de.fraunhofer.iosb.ilt.configurable.annotations.AnnotationHelper;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import static de.fraunhofer.iosb.ilt.configurable.Configurable.CLASS_CONFIG_EDITOR_FACTORY_METHOD_NAME;

/**
 * Configuration editor utilities.
 *
 * @author Fraunhofer IOSB
 */
public final class ConfigEditors {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigEditors.class);

	/**
	 * Static only utility class.
	 */
	private ConfigEditors() {
	}

	public static <R, E> Optional<ConfigEditor<?>> buildEditorFromClass(final Class<?> subclassType, final R runtimeContext, final E editorContext) {
		if (Arrays.stream(subclassType.getMethods())
				.filter(method -> CLASS_CONFIG_EDITOR_FACTORY_METHOD_NAME.equals(method.getName()))
				.findAny()
				.isPresent()) {
			try {
				return Optional.of(
						ConfigEditor.class.cast(
								MethodUtils.invokeStaticMethod(
										subclassType,
										CLASS_CONFIG_EDITOR_FACTORY_METHOD_NAME,
										runtimeContext,
										editorContext)));
			} catch (ClassCastException | ReflectiveOperationException exc) {
				LOGGER.debug("Exception on attempt to build singleton class editor via static factory method.", exc);
			}
		}

		return Optional
				.ofNullable(AnnotationHelper.generateEditorFromAnnotations(subclassType, runtimeContext, editorContext)
				.orElse(null));
	}

}
