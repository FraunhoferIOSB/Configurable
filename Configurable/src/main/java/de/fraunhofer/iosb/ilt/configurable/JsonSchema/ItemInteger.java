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
public class ItemInteger extends SchemaItemAbstract<ItemInteger> {

	private Long minimum;
	private Long exclusiveMinimum;
	private Long maximum;
	private Long exclusiveMaximum;

	public ItemInteger() {
		super("integer");
	}

	@Override
	public ItemInteger getThis() {
		return this;
	}

	/**
	 * @return the minimum
	 */
	public Long getMinimum() {
		return minimum;
	}

	/**
	 * @param minimum the minimum to set
	 * @return this;
	 */
	public ItemInteger setMinimum(Long minimum) {
		this.minimum = minimum;
		return this;
	}

	/**
	 * @return the exclusiveMinimum
	 */
	public Long getExclusiveMinimum() {
		return exclusiveMinimum;
	}

	/**
	 * @param exclusiveMinimum the exclusiveMinimum to set
	 * @return this;
	 */
	public ItemInteger setExclusiveMinimum(Long exclusiveMinimum) {
		this.exclusiveMinimum = exclusiveMinimum;
		return this;
	}

	/**
	 * @return the maximum
	 */
	public Long getMaximum() {
		return maximum;
	}

	/**
	 * @param maximum the maximum to set
	 * @return this;
	 */
	public ItemInteger setMaximum(Long maximum) {
		this.maximum = maximum;
		return this;
	}

	/**
	 * @return the exclusiveMaximum
	 */
	public Long getExclusiveMaximum() {
		return exclusiveMaximum;
	}

	/**
	 * @param exclusiveMaximum the exclusiveMaximum to set
	 * @return this;
	 */
	public ItemInteger setExclusiveMaximum(Long exclusiveMaximum) {
		this.exclusiveMaximum = exclusiveMaximum;
		return this;
	}

}
