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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorString;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryStringSwing implements GuiFactorySwing {

	private final EditorString parentEditor;
	private JTextComponent swText;
	private JComponent swComponent;

	public FactoryStringSwing(EditorString parentEditor) {
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
		if (parentEditor.getLines() == 1) {
			swText = new JTextField();
			swComponent = swText;
		} else {
			JTextArea textArea = new JTextArea();
			swText = textArea;
			textArea.setRows(parentEditor.getLines());
			textArea.setLineWrap(true);
			JScrollPane jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(textArea);
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(jScrollPane, BorderLayout.CENTER);
			swComponent = panel;
		}
		fillComponent();
	}

	/**
	 * Ensure the swComponent represents the current value.
	 */
	public void fillComponent() {
		swText.setText(parentEditor.getRawValue());
	}

	public void readComponent() {
		parentEditor.setRawValue(swText.getText());
	}

}
