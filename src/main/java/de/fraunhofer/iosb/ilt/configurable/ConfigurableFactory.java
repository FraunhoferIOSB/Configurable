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
	 * @param context The runtime context for the instance.
	 * @param edtCtx The edit context for the instance.
	 * @return an instance of the given class
	 */
	public Object instantiate(String className, JsonElement config, Object context, Object edtCtx);

	/**
	 * Create an instance of the given class.
	 *
	 * @param <T> The class type.
	 * @param clazz The class to instantiate.
	 * @param config The configuration of the class.
	 * @param context The runtime context for the instance.
	 * @param edtCtx The edit context for the instance.
	 * @return an instance of the given class
	 */
	public <T> T instantiate(Class<? extends T> clazz, JsonElement config, Object context, Object edtCtx);
}
