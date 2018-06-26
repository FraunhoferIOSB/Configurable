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
import de.fraunhofer.iosb.ilt.configurable.Configurable;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hylke van der Schaaf
 */
public class AnnotationHelper {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AnnotationHelper.class.getName());

	private AnnotationHelper() {
		// Can't be instantiated.
	}

	/**
	 * Generate the editor for the given configurable instance. The editor
	 * settings will be taken from annotations on the Class of the instance, and
	 * its super classes.
	 *
	 * @param <C> The class type that provides context at runtime.
	 * @param <D> The class type that provides context while editing.
	 * @param instance The configurable instance to generate an editor for.
	 * @param context The instance that provides context at runtime.
	 * @param edtCtx The instance that provides context while editing.
	 * @return an editor for the given Configurable instance.
	 */
	public static final <C, D> EditorMap<?> GenerateEditorFromAnnotations(Configurable<C, D> instance, C context, D edtCtx) {
		EditorMap<?> map = new EditorMap<>();

		Field[] fields = FieldUtils.getAllFields(instance.getClass());
		for (Field field : fields) {
			if (Modifier.isTransient(field.getModifiers())) {
				LOGGER.debug("Field {} is transient.", field);
				continue;
			}
			ConfigurableField annotation = field.getAnnotation(ConfigurableField.class);
			if (annotation == null) {
				LOGGER.debug("Field {} has no annotations.", field);
			} else {
				Class<? extends ConfigEditor> editorClass = annotation.editor();
				try {
					ConfigEditor fieldEditor = createEditor(editorClass, field, context, edtCtx);

					fieldEditor.setLabel(annotation.label());
					fieldEditor.setDescription(annotation.description());

					String jsonName = jsonNameForField(field, annotation);
					map.addOption(field.getName(), jsonName, fieldEditor, annotation.optional(), 1, annotation.merge());

				} catch (InstantiationException | IllegalAccessException ex) {
					LOGGER.error("could not instantiate give editor: {}", editorClass);
					LOGGER.info("Exception", ex);
				}
			}
		}

		return map;
	}

	public static <E, F, T extends ConfigEditor> T createEditor(Class<T> editorClass, Field field, E context, F edtCtx) throws IllegalAccessException, InstantiationException {
		T fieldEditor = editorClass.newInstance();
		try {
			MethodUtils.invokeMethod(fieldEditor, "setContexts", context, edtCtx);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exc) {
			// editor needs no context.
			LOGGER.trace("", exc);
		}
		fieldEditor.initFor(field);
		return fieldEditor;
	}

	private static String jsonNameForField(Field field, ConfigurableField annotation) {
		if (annotation.jsonField().isEmpty()) {
			return field.getName();
		}
		return annotation.jsonField();
	}
}
