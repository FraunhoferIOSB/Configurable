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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorClass;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryClassSwing implements GuiFactorySwing {

	private final EditorClass parentEditor;
	private JPanel component;

	public FactoryClassSwing(EditorClass parentEditor) {
		this.parentEditor = parentEditor;
	}

	@Override
	public JComponent getComponent() {
		if (component == null) {
			createComponent();
		}
		return component;
	}

	private void createComponent() {
		component = new JPanel(new BorderLayout());
		component.setBorder(new EtchedBorder());
		fillComponent();
	}

	public void fillComponent() {
		if (parentEditor.getClassEditor() == null) {
			parentEditor.initClass();
			return; // initClass calls fillComponent again.
		}
		component.removeAll();
		component.add(parentEditor.getClassEditor().getGuiFactorySwing().getComponent(), BorderLayout.CENTER);
		component.revalidate();
		component.repaint();
	}

}
