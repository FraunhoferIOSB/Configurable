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

import java.math.BigDecimal;

/**
 *
 * @author scf
 */
public class ItemNumber extends SchemaItemAbstract<ItemNumber> {

    private BigDecimal minimum;
    private BigDecimal exclusiveMinimum;
    private BigDecimal maximum;
    private BigDecimal exclusiveMaximum;

    public ItemNumber() {
        super("number");
    }

    @Override
    public ItemNumber getThis() {
        return this;
    }

    /**
     * @return the minimum
     */
    public BigDecimal getMinimum() {
        return minimum;
    }

    /**
     * @param minimum the minimum to set
     * @return this;
     */
    public ItemNumber setMinimum(BigDecimal minimum) {
        this.minimum = minimum;
        return this;
    }

    /**
     * @return the exclusiveMinimum
     */
    public BigDecimal getExclusiveMinimum() {
        return exclusiveMinimum;
    }

    /**
     * @param exclusiveMinimum the exclusiveMinimum to set
     */
    public void setExclusiveMinimum(BigDecimal exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    /**
     * @return the maximum
     */
    public BigDecimal getMaximum() {
        return maximum;
    }

    /**
     * @param maximum the maximum to set
     * @return this.
     */
    public ItemNumber setMaximum(BigDecimal maximum) {
        this.maximum = maximum;
        return this;
    }

    /**
     * @return the exclusiveMaximum
     */
    public BigDecimal getExclusiveMaximum() {
        return exclusiveMaximum;
    }

    /**
     * @param exclusiveMaximum the exclusiveMaximum to set
     */
    public void setExclusiveMaximum(BigDecimal exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

}
