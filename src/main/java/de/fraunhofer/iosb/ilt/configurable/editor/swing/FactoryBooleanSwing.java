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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorBoolean;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryBooleanSwing implements GuiFactorySwing {

	private final EditorBoolean parentEditor;
	private JCheckBox swComponent;

	public FactoryBooleanSwing(EditorBoolean parentEditor) {
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
		swComponent = new JCheckBox();
		fillComponent();
	}

	/**
	 * Ensure the component represents the current value.
	 */
	public void fillComponent() {
		swComponent.setSelected(parentEditor.getValue());
	}

	public boolean isSelected() {
		return swComponent.isSelected();
	}

}
