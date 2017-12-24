/*
 * Copyright (C) 2017 Fraunhofer IOSB
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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorDouble;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryDoubleSwing implements GuiFactorySwing {

	private final EditorDouble parentEditor;
	private SpinnerNumberModel swModel;
	private JSpinner swComponent;

	public FactoryDoubleSwing(EditorDouble parentEditor) {
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
		swModel = new SpinnerNumberModel(parentEditor.getRawValue(), parentEditor.getMin(), parentEditor.getMax(), parentEditor.getStep());
		swComponent = new JSpinner(swModel);
		fillComponent();
	}

	/**
	 * Ensure the component represents the current value.
	 */
	public void fillComponent() {
		swComponent.setValue(parentEditor.getRawValue());
	}

	public void readComponent() {
		if (swComponent != null) {
			parentEditor.setRawValue(swModel.getNumber().doubleValue());
		}
	}

}
