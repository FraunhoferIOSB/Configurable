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
package de.fraunhofer.iosb.ilt.configurable;

import com.google.gson.JsonElement;

/**
 * The interface that context classes can implement to indicate they are a
 * factory for classes.
 *
 * @author scf
 */
public interface ConfigurableFactory {

	/**
	 * Create an instance of the given class.
	 *
	 * @param className The name of the class to instantiate.
	 * @param config The configuration of the class.
	 * @param runtimeContext The runtime context for the instance.
	 * @param editContext The edit context for the instance.
	 * @return an instance of the given class
	 * @throws ConfigurationException if there is a problem instantiating the
	 * class.
	 */
	public Object instantiate(String className, JsonElement config, Object runtimeContext, Object editContext) throws ConfigurationException;

	/**
	 * Create an instance of the given class.
	 *
	 * @param <T> The class type.
	 * @param clazz The class to instantiate.
	 * @param config The configuration of the class.
	 * @param runtimeContext The runtime context for the instance.
	 * @param editContext The edit context for the instance.
	 * @return an instance of the given class
	 * @throws ConfigurationException if there is a problem instantiating the
	 * class.
	 */
	public default <T> T instantiate(Class<? extends T> clazz, JsonElement config, Object runtimeContext, Object editContext) throws ConfigurationException {
		return clazz.cast(instantiate(clazz.getName(), config, runtimeContext, editContext));
	}

	/**
	 * Loads the class with the specified <a href="#binary-name">binary name</a>.
	 * This method searches for classes in the same manner as the
	 * {@link #loadClass(String, boolean)} method.
	 *
	 * @param name The <a href="#binary-name">binary name</a> of the class
	 * @return The resulting {@code Class} object
	 * @throws ClassNotFoundException If the class was not found
	 */
	public default Class<?> loadClass(final String name) throws ClassNotFoundException {
		return getClass().getClassLoader().loadClass(name);
	}
}
