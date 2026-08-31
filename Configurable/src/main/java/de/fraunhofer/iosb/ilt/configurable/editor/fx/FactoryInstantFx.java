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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInstant;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Editor for Swing for time Instant. Bases on simple String parsing.
 */
public final class FactoryInstantFx implements GuiFactoryFx {

    private static final Logger LOGGER = LoggerFactory.getLogger(FactoryInstantFx.class.getName());
    private final EditorInstant parentEditor;
    private TextField fxNode;
    private Background bgNormal;

    public FactoryInstantFx(EditorInstant parentEditor) {
        this.parentEditor = parentEditor;
    }

    @Override
    public Node getNode() {
        if (fxNode == null) {
            createNode();
        }
        return fxNode;
    }

    private void createNode() {
        fxNode = new TextField();
        fxNode.textProperty().addListener(o -> validate());
        fillComponent();
    }

    private void validate() {
        if (bgNormal == null) {
            bgNormal = fxNode.getBackground();
        }
        final String text = fxNode.getText();
        try {
            Instant.parse(text);
            if (bgNormal != null) {
                fxNode.setBackground(bgNormal);
            }
        } catch (DateTimeParseException ex) {
            LOGGER.warn("Failed to parse {}", text);
            fxNode.setBackground(Background.fill(Paint.valueOf("#F55")));
        }
    }

    /**
     * Ensure the swComponent represents the current value.
     */
    public void fillComponent() {
        fxNode.setEditable(parentEditor.canEdit());
        Instant value = parentEditor.getRawValue();
        if (value == null) {
            fxNode.setText("");
            return;
        }
        fxNode.setText(value.toString());
    }

    public void readComponent() {
        parentEditor.setRawValue(Instant.parse(fxNode.getText()));
    }

}
