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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorClass;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryClassFx implements GuiFactoryFx {

    private final EditorClass parentEditor;
    private BorderPane fxPaneRoot;

    public FactoryClassFx(EditorClass parentEditor) {
        this.parentEditor = parentEditor;
    }

    @Override
    public Node getNode() {
        if (fxPaneRoot == null) {
            createPane();
        }
        return fxPaneRoot;
    }

    private void createPane() {
        fxPaneRoot = new BorderPane();
        fillComponent();
    }

    public void fillComponent() {
        if (parentEditor.getClassEditor() == null) {
            parentEditor.initClass();
            return; // initClass calls fillComponent again.
        }
        fxPaneRoot.getChildren().clear();
        fxPaneRoot.setCenter(parentEditor.getClassEditor().getGuiFactoryFx().getNode());
    }

}
