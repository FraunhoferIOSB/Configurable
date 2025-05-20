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
package de.fraunhofer.iosb.ilt.configurable.editor.fx;

import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorLong;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryLongFx implements GuiFactoryFx {

    private static final Logger LOGGER = LoggerFactory.getLogger(FactoryLongFx.class.getName());

    private final EditorLong parentEditor;
    private TextInputControl fxNode;

    public FactoryLongFx(EditorLong parentEditor) {
        this.parentEditor = parentEditor;
    }

    @Override
    public Node getNode() {
        if (fxNode == null) {
            createComponent();
        }
        return fxNode;
    }

    private void createComponent() {
        fxNode = new TextField();
        fillComponent();
    }

    private long getRawValue() {
        Long rawValue = parentEditor.getRawValue();
        final long min = parentEditor.getMin();
        final long max = parentEditor.getMax();
        if (rawValue == null) {
            rawValue = 0L;
            parentEditor.setRawValue(rawValue);
        }
        if (rawValue < min || rawValue > max) {
            rawValue = Math.max(min, Math.min(rawValue, max));
            parentEditor.setRawValue(rawValue);
        }
        return rawValue;
    }

    /**
     * Ensure the component represents the current value.
     */
    public void fillComponent() {
        long rawValue = getRawValue();
        fxNode.setText("" + rawValue);
    }

    public void readComponent() {
        if (fxNode != null) {
            try {
                parentEditor.setRawValue(Long.parseLong(fxNode.getText()));
            } catch (NumberFormatException exc) {
                LOGGER.error("Failed to parse text to number: " + fxNode.getText());
            }
        }
    }
}
