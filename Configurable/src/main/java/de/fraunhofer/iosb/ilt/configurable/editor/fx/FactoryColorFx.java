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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorColor;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryColorFx implements GuiFactoryFx {

    private final EditorColor parentEditor;
    private ColorPicker fxNode;

    public FactoryColorFx(EditorColor parentEditor) {
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
        fxNode = new ColorPicker();
        fxNode.setOnAction((event) -> readComponent());
        fillComponent();
    }

    /**
     * Ensure the component represents the current value.
     */
    public void fillComponent() {
        javafx.scene.paint.Color color = javafx.scene.paint.Color.color(
                parentEditor.getRed() / 255.0,
                parentEditor.getGreen() / 255.0,
                parentEditor.getBlue() / 255.0,
                (255 - parentEditor.getAlpha()) / 255.0);
        fxNode.setValue(color);
    }

    public void readComponent() {
        javafx.scene.paint.Color color = fxNode.getValue();
        parentEditor.setRed((int) (color.getRed() * 255));
        parentEditor.setGreen((int) (color.getGreen() * 255));
        parentEditor.setBlue((int) (color.getBlue() * 255));
        parentEditor.setAlpha((int) ((1 - color.getOpacity()) * 255));
    }

}
