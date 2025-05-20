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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorEnum;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;

/**
 *
 * @author Hylke van der Schaaf
 * @param <T> The type this editor selects.
 */
public final class FactoryEnumFx<T extends Enum<T>> implements GuiFactoryFx {

    private final EditorEnum<T> parentEditor;
    private ChoiceBox<T> fxNode;
    private final EditorEnum<T> outer;

    public FactoryEnumFx(EditorEnum<T> parentEditor, final EditorEnum<T> outer) {
        this.outer = outer;
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
        fxNode = new ChoiceBox<>(FXCollections.observableArrayList(outer.getSourceType().getEnumConstants()));
        fillComponent();
    }

    /**
     * Ensure the component represents the current value.
     */
    public void fillComponent() {
        fxNode.setValue(outer.getRawValue());
    }

    public void readComponent() {
        outer.setRawValue(fxNode.getValue());
    }

}
