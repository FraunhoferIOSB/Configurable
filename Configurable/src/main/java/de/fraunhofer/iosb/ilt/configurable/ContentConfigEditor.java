/*
 * Copyright (C) 2024 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
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

    /**
     * Checks if the given option is set. For mandatory options this should
     * always return true.
     *
     * @param name The option to check.
     * @return true if the option is set, false otherwise.
     */
    public boolean isOptionSet(final String name);

    /**
     * Get the value set for the given option.
     *
     * @param name The option to check.
     * @return The value of the given option.
     * @throws ConfigurationException If the given option can not be retrieved
     * because the configuration passed to the editor was incorrect.
     */
    public Object getValue(final String name) throws ConfigurationException;

}
