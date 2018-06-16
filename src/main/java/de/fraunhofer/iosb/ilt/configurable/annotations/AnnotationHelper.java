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

	public static final <E, F> EditorMap<?> GenerateEditorFromAnnotations(Configurable<E, F> instance, E context, F edtCtx) {
		EditorMap<?> map = new EditorMap<>();

		Field[] fields = FieldUtils.getAllFields(instance.getClass());
		for (Field field : fields) {
			ConfigurableField annotation = field.getAnnotation(ConfigurableField.class);
			if (annotation == null) {
				LOGGER.debug("Field {} has no annotations.", field);
			} else {
				Class<? extends ConfigEditor> editorClass = annotation.editor();
				try {
					ConfigEditor fieldEditor = createEditor(editorClass, field, context, edtCtx);

					fieldEditor.setLabel(annotation.label());
					fieldEditor.setDescription(annotation.description());

					String name = nameForField(field, annotation);
					map.addOption(name, fieldEditor, annotation.optional());

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

	private static String nameForField(Field field, ConfigurableField annotation) {
		if (annotation.jsonField().isEmpty()) {
			return field.getName();
		}
		return annotation.jsonField();
	}
}
