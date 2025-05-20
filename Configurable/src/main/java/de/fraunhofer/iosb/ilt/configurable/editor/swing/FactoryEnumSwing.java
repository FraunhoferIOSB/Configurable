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
package de.fraunhofer.iosb.ilt.configurable.editor.swing;

import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorEnum;
import java.awt.BorderLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author Hylke van der Schaaf
 * @param <T> The type this editor selects.
 */
public final class FactoryEnumSwing<T extends Enum<T>> implements GuiFactorySwing {

    private final EditorEnum<T> parentEditor;
    private JComboBox<T> swComboBox;
    private ComboBoxModel<T> swModel;
    private JPanel swComponent;
    private final EditorEnum<T> outer;

    public FactoryEnumSwing(EditorEnum<T> parentEditor, final EditorEnum<T> outer) {
        this.outer = outer;
        this.parentEditor = parentEditor;
    }

    @Override
    public JComponent getComponent() {
        if (swComponent == null) {
            createComponent();
        }
        return swComponent;
    }

    private void createComponent() {
        swModel = new DefaultComboBoxModel<>(outer.getSourceType().getEnumConstants());
        swComboBox = new JComboBox<>(swModel);
        swComponent = new JPanel(new BorderLayout());
        swComponent.add(swComboBox, BorderLayout.CENTER);
        swComponent.add(parentEditor.getHelpButton(), BorderLayout.WEST);
        fillComponent();
    }

    /**
     * Ensure the component represents the current value.
     */
    public void fillComponent() {
        swComboBox.setSelectedItem(outer.getRawValue());
    }

    public void readComponent() {
        int index = swComboBox.getSelectedIndex();
        outer.setRawValue(swModel.getElementAt(index));
    }

}
