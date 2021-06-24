/*
 * Copyright (C) 2021 Fraunhofer IOSB
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

/**
 *
 * @author scf
 */
public class ItemRef extends SchemaItemAbstract<ItemRef> {

	@SerializedName("$ref")
	private final String ref;

	public ItemRef(String ref) {
		this(ref, true);
	}

	public ItemRef(String ref, boolean inDefs) {
		super(null);
		if (inDefs) {
			this.ref = "#/definitions/" + ref;
		} else {
			this.ref = ref;
		}
	}

	@Override
	public ItemRef getThis() {
		return this;
	}

	public String getRef() {
		return ref;
	}

}
