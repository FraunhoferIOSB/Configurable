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

/**
 *
 * @author scf
 */
public class ItemArray extends SchemaItemAbstract<ItemArray> {

	private SchemaItem items;
	private int minItems;
	private int maxItems;

	public ItemArray() {
		super("array");
	}

	@Override
	public ItemArray getThis() {
		return this;
	}

	/**
	 * @return the items
	 */
	public SchemaItem getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 * @return this.
	 */
	public ItemArray setItems(SchemaItem items) {
		this.items = items;
		return this;
	}

	/**
	 * @return the minItems
	 */
	public int getMinItems() {
		return minItems;
	}

	/**
	 * @param minItems the minItems to set
	 * @return this.
	 */
	public ItemArray setMinItems(int minItems) {
		this.minItems = minItems;
		return this;
	}

	/**
	 * @return the maxItems
	 */
	public int getMaxItems() {
		return maxItems;
	}

	/**
	 * @param maxItems the maxItems to set
	 * @return this.
	 */
	public ItemArray setMaxItems(int maxItems) {
		this.maxItems = maxItems;
		return this;
	}

}
