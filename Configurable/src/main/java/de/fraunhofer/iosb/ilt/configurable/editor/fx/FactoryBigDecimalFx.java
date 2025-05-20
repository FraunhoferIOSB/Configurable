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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorBigDecimal;
import java.math.BigDecimal;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.util.converter.BigDecimalStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryBigDecimalFx implements GuiFactoryFx {

    private static final Logger LOGGER = LoggerFactory.getLogger(FactoryBigDecimalFx.class.getName());

    private final EditorBigDecimal parentEditor;
    private TextInputControl fxNode;

    public FactoryBigDecimalFx(EditorBigDecimal parentEditor) {
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
        BigDecimal rawValue = parentEditor.getRawValue();
        if (rawValue == null) {
            rawValue = BigDecimal.ZERO;
        }
        BigDecimal min = parentEditor.getMin();
        BigDecimal max = parentEditor.getMax();

        if (min != null && min.compareTo(rawValue) > 0) {
            parentEditor.setRawValue(min);
        }
        if (max != null && max.compareTo(rawValue) < 0) {
            parentEditor.setRawValue(min);
        }
        fxNode = new TextField();
        fxNode.setTextFormatter(new TextFormatter(new BigDecimalStringConverter()));
        fillComponent();
    }

    /**
     * Ensure the component represents the current value.
     */
    public void fillComponent() {
        BigDecimal rawValue = parentEditor.getRawValue();
        if (rawValue == null) {
            rawValue = BigDecimal.ZERO;
        }
        fxNode.setText("" + rawValue);
    }

    public void readComponent() {
        if (fxNode != null) {
            try {
                parentEditor.setRawValue(new BigDecimal(fxNode.getText()));
            } catch (NumberFormatException exc) {
                LOGGER.error("Failed to parse text to number: " + fxNode.getText());
            }
        }
    }

}
