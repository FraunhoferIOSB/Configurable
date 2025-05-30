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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryIntSwing implements GuiFactorySwing {

    private final EditorInt parentEditor;
    private SpinnerNumberModel swModel;
    private JSpinner swComponent;
    private int min = 0;

    public FactoryIntSwing(EditorInt parentEditor) {
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
        min = parentEditor.getMin();
        int value = getRawValue();
        swModel = new SpinnerNumberModel(
                value,
                min,
                parentEditor.getMax(),
                parentEditor.getStep());
        swComponent = new JSpinner(swModel);
        fillComponent();
    }

    private int getRawValue() {
        final Integer rawValue = parentEditor.getRawValue();
        if (rawValue == null) {
            return Math.max(0, min);
        } else {
            return rawValue;
        }
    }

    /**
     * Ensure the component represents the current value.
     */
    public void fillComponent() {
        Integer rawValue = getRawValue();
        swComponent.setValue(rawValue);
    }

    public void readComponent() {
        if (swComponent != null) {
            parentEditor.setRawValue(swModel.getNumber().intValue());
        }
    }

}
