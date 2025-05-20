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
 * Additional styles.
 */
public class Styles {

    public static final String STYLE_BORDER = ""
            + "    -fx-border-base: gray;"
            + "    -fx-border-shadow: white;"
            + "    -fx-light-border: derive(-fx-border-base, 25%);"
            + "    -fx-border-color: -fx-light-border -fx-border-base -fx-border-base -fx-light-border;"
            + "    -fx-border-insets: 0 1 1 0;"
            + "    -fx-background-color: -fx-border-shadow, -fx-background;"
            + "    -fx-background-insets: 1 0 0 1, 2;"
            + "    -fx-padding: 2;";
}
