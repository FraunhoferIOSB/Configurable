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

/**
 * Interface defining configuration editors suited to set configured content.
 *
 * @author Fraunhofer IOSB
 * @param <T> The type of object returned by getValue.
 */
public interface ContentConfigEditor<T> extends ConfigEditor<T> {

	/**
	 * For any configurable content, tries to set the configured value on the
	 * target object.
	 *
	 * @param target The target instance to use.
	 * @throws ConfigurationException if any of the values could not be loaded.
	 */
	public void setContentsOn(final Object target) throws ConfigurationException;

	public Object getValue(final String name) throws ConfigurationException;

}
