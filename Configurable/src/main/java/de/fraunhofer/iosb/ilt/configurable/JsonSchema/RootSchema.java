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

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author scf
 */
@JsonAdapter(RootSchema.RootSchemaSerialiser.class)
public class RootSchema implements SchemaItem {

    @SerializedName("$schema")
    private String schema = "http://json-schema.org/draft/2019-09/schema#";

    private Map<String, SchemaItem> defs;

    private final SchemaItem wrappedItem;

    public RootSchema(SchemaItem wrappedItem) {
        this.wrappedItem = wrappedItem;
    }

    @Override
    public String getType() {
        return wrappedItem.getType();
    }

    public boolean hasDef(String name) {
        return defs != null && defs.containsKey(name);
    }

    public Map<String, SchemaItem> getDefs() {
        if (defs == null) {
            defs = new HashMap<>();
        }
        return defs;
    }

    public void addDef(String name, SchemaItem item) {
        if (defs == null) {
            defs = new HashMap<>();
        }
        defs.put(name, item);
    }

    @Override
    public String getTitle() {
        return wrappedItem.getTitle();
    }

    @Override
    public RootSchema setTitle(String title) {
        wrappedItem.setTitle(title);
        return this;
    }

    @Override
    public String getDescription() {
        return wrappedItem.getDescription();
    }

    @Override
    public RootSchema setDescription(String description) {
        wrappedItem.setDescription(description);
        return this;
    }

    public static class RootSchemaSerialiser implements JsonSerializer<RootSchema> {

        @Override
        public JsonElement serialize(RootSchema t, Type type, JsonSerializationContext jsc) {
            JsonElement item = jsc.serialize(t.wrappedItem);
            JsonElement defs = jsc.serialize(t.defs);
            item.getAsJsonObject().addProperty("$schema", t.getSchema());
            item.getAsJsonObject().add("definitions", defs);
            return item;
        }

    }

    public String getSchema() {
        return schema;
    }

}
