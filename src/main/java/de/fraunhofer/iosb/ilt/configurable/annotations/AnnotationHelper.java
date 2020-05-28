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

import com.google.gson.JsonElement;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditors;
import de.fraunhofer.iosb.ilt.configurable.Configurable;
import de.fraunhofer.iosb.ilt.configurable.ConfigurationException;
import de.fraunhofer.iosb.ilt.configurable.ContentConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.Utils;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.LoggerFactory;

/**
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
	public static final <C, D> Optional<EditorMap<?>> generateEditorFromAnnotations(Configurable<C, D> instance, C context, D edtCtx) {
		return generateEditorFromAnnotations(instance.getClass(), context, edtCtx);
	}

	/**
	 * Generate the editor for the given configurable instance. The editor
	 * settings will be taken from annotations on the configurable Class, and
	 * its super classes.
	 *
	 * @param <C> The class type that provides context at runtime.
	 * @param <D> The class type that provides context while editing.
	 * @param configurableClass The configurable class to generate an editor
	 * for.
	 * @param context The instance that provides context at runtime.
	 * @param edtCtx The instance that provides context while editing.
	 * @return an editor for the given Configurable class, or an empty optional
	 * if no Configurable annotations exist.
	 */
	public static final <C, D> Optional<EditorMap<?>> generateEditorFromAnnotations(final Class<?> configurableClass, final C context, final D edtCtx) {
		final EditorMap<?> map = new EditorMap<>();
		boolean annotated = false;

		Class<?> type = configurableClass;
		do {
			ConfigurableClass classAnnotation = type.getAnnotation(ConfigurableClass.class);
			if (classAnnotation != null) {
				annotated = true;
				if (!classAnnotation.profilesEdit().isEmpty()) {
					map.setProfilesEdit(classAnnotation.profilesEdit());
				}
				break;
			} else {
				for (final Class<?> iface : type.getInterfaces()) {
					classAnnotation = iface.getAnnotation(ConfigurableClass.class);
					if (classAnnotation != null) {
						annotated = true;
						if (!classAnnotation.profilesEdit().isEmpty()) {
							map.setProfilesEdit(classAnnotation.profilesEdit());
						}
						break;
					}
				}
				if (annotated) {
					break;
				}
			}
			type = type.getSuperclass();
		} while (type != null);

		final Field[] fields = FieldUtils.getAllFields(configurableClass);
		for (final Field field : fields) {
			final ConfigurableField annotation = field.getAnnotation(ConfigurableField.class);
			if (annotation == null) {
				LOGGER.debug("Field {} has no annotations.", field);
			} else {
				annotated = true;
				final Class<? extends ConfigEditor> editorClass = annotation.editor();
				try {
					final ConfigEditor fieldEditor = createEditor(editorClass, field, context, edtCtx);

					fieldEditor.setLabel(annotation.label());
					fieldEditor.setDescription(annotation.description());

					final String jsonName = jsonNameForField(field, annotation);
					map.addOption(field.getName(), jsonName, fieldEditor, annotation);

				} catch (final ReflectiveOperationException ex) {
					LOGGER.error("could not instantiate give editor: {}", editorClass);
					LOGGER.info("Exception", ex);
				}
			}
		}

		if (!annotated) {
			annotated = hasConfigurableConstructorAnnotation(configurableClass);
		}

		return annotated ? Optional.of(map) : Optional.empty();
	}

	public static <E, F, T extends ConfigEditor> T createEditor(
			final Class<T> editorForClass,
			final Field field,
			final E context,
			final F edtCtx) throws ReflectiveOperationException {
		final T fieldEditor = editorForClass.getDeclaredConstructor().newInstance();
		try {
			MethodUtils.invokeMethod(fieldEditor, "setContexts", context, edtCtx);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exc) {
			// editor needs no context.
			LOGGER.trace("", exc);
		}
		fieldEditor.initFor(field);
		return fieldEditor;
	}

	public static <E, F, T extends ConfigEditor> T createEditor(
			final Class<T> editorForClass,
			final Field field,
			final E context,
			final F edtCtx,
			final String key) throws IllegalAccessException, ReflectiveOperationException {
		final T fieldEditor = editorForClass.getDeclaredConstructor().newInstance();
		try {
			MethodUtils.invokeMethod(fieldEditor, "setContexts", context, edtCtx);
		} catch (ReflectiveOperationException exc) {
			// editor needs no context.
			LOGGER.trace("", exc);
		}
		fieldEditor.initFor(field, key);
		return fieldEditor;
	}

	private static String jsonNameForField(final Field field, final ConfigurableField annotation) {
		if (annotation.jsonField().isEmpty()) {
			return field.getName();
		}
		return annotation.jsonField();
	}

	public static Set<String> csvToReadOnlySet(String csv) {
		Set<String> set = new HashSet<>();
		set.add(ConfigEditor.DEFAULT_PROFILE_NAME);
		String[] split = csv.split(",");
		for (String item : split) {
			String lcItem = item.trim().toLowerCase();
			if (!lcItem.isEmpty()) {
				set.add(lcItem);
			}
		}
		return Collections.unmodifiableSet(set);
	}

	public static boolean hasConfigurableConstructorAnnotation(final Class<?> configurableClass) {
		return getConfigurableConstructor(configurableClass).isPresent();
	}

	public static Optional<Constructor<?>> getConfigurableConstructor(final Class<?> configurableClass) {
		return Arrays.stream(configurableClass.getConstructors())
				.filter(ctor -> Objects.nonNull(ctor.getAnnotation(ConfigurableConstructor.class)))
				.findFirst();
	}

	public static <T, R, E> T instantiateFrom(
			final Constructor<?> configurableConstructor,
			final JsonElement classConfig,
			final R runtimeContext,
			final E editorContext) throws ReflectiveOperationException, IllegalArgumentException, ConfigurationException {

		// we'd expect to deal with a ContentConfigEditor here
		final ContentConfigEditor<?> editor = (ContentConfigEditor<?>) ConfigEditors
				.buildEditorFromClass(configurableConstructor.getDeclaringClass(), runtimeContext, editorContext)
				.get();
		editor.setConfig(classConfig);

		final T instance = instantiateFrom(configurableConstructor, classConfig, editor, runtimeContext);
		((Configurable) instance).configure(classConfig, runtimeContext, editorContext, editor);

		return instance;
	}

	private static <T, R, E> T instantiateFrom(
			final Constructor<?> configurableConstructor,
			final JsonElement classConfig,
			final ContentConfigEditor<?> editor,
			final R runtimeContext)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		final Object[] initargs = buildConstructorInitargs(configurableConstructor, classConfig, editor, runtimeContext);
		return (T) configurableConstructor.newInstance(initargs);
	}

	private static <R> Object[] buildConstructorInitargs(final Constructor<?> configurableConstructor, final JsonElement classConfig, final ContentConfigEditor<?> editor, final R runtimeContext) {
		return Arrays.stream(configurableConstructor.getParameters())
				.map(param -> buildConstructorInitarg(param, classConfig, editor, runtimeContext))
				.toArray();
	}

	private static <R> Object buildConstructorInitarg(final Parameter parameter, final JsonElement classConfig, final ContentConfigEditor<?> editor, final R runtimeContext) {
		try {
			final ConfigurableParameter annotation = parameter.getAnnotation(ConfigurableParameter.class);
			if (annotation == null) {
				return null;
			}

			switch (annotation.type()) {
				case RUNTIME_CONTEXT:
					return runtimeContext;
				case CLASS_CONFIG:
					return classConfig;
				case JSON_FIELD: {
					final String jsonField = annotation.jsonField();
					if (Utils.isNullOrEmpty(jsonField)) {
						return null; // usually this is an error
					}
					return editor.getValue(jsonField);
				}
				default:
					return null;
			}
		} catch (final ConfigurationException exc) {
			throw new IllegalArgumentException(exc);
		}
	}

	public static boolean hasConfigurableConstructorParameter(final Object instance, final String jsonField) {
		if (instance == null) {
			return false;
		}

		final Optional<Constructor<?>> configurableConstructor = getConfigurableConstructor(instance.getClass());
		if (!configurableConstructor.isPresent()) {
			return false;
		}

		return Arrays.stream(configurableConstructor.get().getParameters())
				.map(parameter -> parameter.getAnnotation(ConfigurableParameter.class))
				.filter(Objects::nonNull)
				.anyMatch(config -> config.jsonField().equals(jsonField));
	}
}
