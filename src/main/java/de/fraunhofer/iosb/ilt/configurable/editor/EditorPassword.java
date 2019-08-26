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
package de.fraunhofer.iosb.ilt.configurable.editor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * A single-line text editor that hides the content that is typed into it. The
 * content is still added to the JSON as plain text.
 *
 * @author Hylke van der Schaaf
 */
public class EditorPassword extends EditorString {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface EdOptsPassword {

		/**
		 * @return The default value.
		 */
		String dflt() default "";

		/**
		 * A comma separated, case insensitive list of profile names. This field
		 * is only editable when one of these profiles is active. The "default"
		 * profile is automatically added to the list.
		 *
		 * @return A comma separated, case insensitive list of profile names.
		 */
		String profilesEdit() default "";
	}

	public EditorPassword() {
	}

	public EditorPassword(String deflt) {
		this(deflt, "", "");
	}

	public EditorPassword(String deflt, String label, String description) {
		super(deflt, 1, label, description);
	}

	@Override
	public void initFor(Field field) {
		EdOptsPassword annotation = field.getAnnotation(EdOptsPassword.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Field must have an EdOptsPassword annotation to use this editor: " + field.getName());
		}
		setDflt(annotation.dflt());
		setRawValue(annotation.dflt());
		setProfilesEdit(annotation.profilesEdit());
	}

}
