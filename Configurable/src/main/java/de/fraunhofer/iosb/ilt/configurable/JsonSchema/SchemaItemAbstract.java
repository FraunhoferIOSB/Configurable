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
package de.fraunhofer.iosb.ilt.configurable.JsonSchema;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author scf
 * @param <T> The exact subType, used for fluent setters.
 */
public abstract class SchemaItemAbstract<T extends SchemaItemAbstract<T>> implements SchemaItem {

    private final String type;

    private String title;

    private String description;

    @SerializedName("default")
    private Object deflt;

    @SerializedName("enum")
    private List<Object> allowedValues;

    private Map<String, Object> options;

    public SchemaItemAbstract(String type) {
        this.type = type;
    }

    public abstract T getThis();

    @Override
    public String getType() {
        return type;
    }

    /**
     * @return the default
     */
    public Object getDeflt() {
        return deflt;
    }

    /**
     * @param deflt the default to set
     * @return this.
     */
    public T setDeflt(Object deflt) {
        this.deflt = deflt;
        return getThis();
    }

    /**
     * @return the title
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     * @return this.
     */
    @Override
    public T setTitle(String title) {
        this.title = title;
        return getThis();
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     * @return this.
     */
    @Override
    public T setDescription(String description) {
        this.description = description;
        return getThis();
    }

    public SchemaItemAbstract<T> addAllowedValue(Object value) {
        if (allowedValues == null) {
            allowedValues = new ArrayList<>();
        }
        allowedValues.add(value);
        return this;
    }

    public SchemaItemAbstract<T> addOption(String name, Object value) {
        if (options == null) {
            options = new HashMap<>();
        }
        options.put(name, value);
        return this;
    }
}
