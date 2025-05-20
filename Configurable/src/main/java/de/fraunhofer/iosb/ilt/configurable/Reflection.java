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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Utility methods for Reflections.
 *
 * @author Hylke van der Schaaf
 */
public class Reflection {

    private static Reflections reflections;

    public static synchronized Reflections getReflections() {
        if (reflections == null) {
            ConfigurationBuilder config = new ConfigurationBuilder()
                    .addUrls(ClasspathHelper.forJavaClassPath())
                    .setScanners(Scanners.values());
            reflections = new Reflections(config);
        }
        return reflections;
    }

    /**
     * Returns all subtypes of the given class or interface.
     *
     * @param parent The class or interface to get subtypes of.
     * @return The list of classes.
     */
    public static Set<Class<?>> getSubtypesOf(Class parent) {
        return getReflections().getSubTypesOf(parent);
    }

    /**
     * Returns all subtypes of the given class or interface, excluding the given
     * class itself. Optionally filtering out all interfaces.
     *
     * @param parent The class or interface to find subclasses of.
     * @param interfaces Should interfaces and abstract classes be included.
     * @return The list of classes.
     */
    public static List<Class<?>> getSubtypesOf(Class parent, boolean interfaces) {
        return getSubtypesOf(parent, interfaces, false);
    }

    /**
     * Returns all subtypes of the given class or interface, optionally
     * including the given class itself. Optionally filtering out all
     * interfaces.
     *
     * @param parent The class or interface to find subclasses of.
     * @param interfaces Should interfaces and abstract classes be included.
     * @param includeParent Should the given parent class itself be returned.
     * @return The list of classes.
     */
    public static List<Class<?>> getSubtypesOf(Class parent, boolean interfaces, boolean includeParent) {
        Set<Class<?>> types = getReflections().getSubTypesOf(parent);
        if (includeParent && (interfaces || (!parent.isInterface() && !Modifier.isAbstract(parent.getModifiers())))) {
            types.add(parent);
        }
        List<Class<?>> result = new ArrayList<>();
        for (Class subtype : types) {
            if (interfaces || (!subtype.isInterface() && !Modifier.isAbstract(subtype.getModifiers()))) {
                result.add(subtype);
            }
        }
        return result;
    }
}
