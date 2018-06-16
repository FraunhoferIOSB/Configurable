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

import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorList;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author Hylke van der Schaaf
 * @param <T> The type of editors that edit the items in the list.
 * @param <U> The type of items in the list.
 */
public final class FactoryListSwing<U, T extends ConfigEditor<U>> implements GuiFactorySwing {

	private final EditorList<U, T> parentEditor;
	private JPanel swComponent;
	private JPanel swListHolder;

	public FactoryListSwing(EditorList<U, T> parentEditor) {
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
		JPanel controls = new JPanel(new BorderLayout());
		controls.add(new JLabel("Add item", SwingConstants.LEFT), BorderLayout.CENTER);
		JButton addButton = new JButton("+");
		addButton.addActionListener((event) -> parentEditor.addItem());
		controls.add(addButton, BorderLayout.WEST);
		swListHolder = new JPanel(new GridBagLayout());
		swComponent = new JPanel(new BorderLayout());
		swComponent.setBorder(new EtchedBorder());
		swComponent.add(controls, BorderLayout.NORTH);
		swComponent.add(swListHolder, BorderLayout.CENTER);
		fillComponent();
	}

	/**
	 * Ensure the component represents the current value.
	 */
	public void fillComponent() {
		swListHolder.removeAll();
		if (parentEditor.getRawValue().isEmpty()) {
			swListHolder.add(new JLabel("No items added."));
		}
		GridBagConstraints gbc;
		int row = 0;
		Insets insets = new Insets(1, 1, 1, 1);
		for (final T item : parentEditor.getRawValue()) {
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = row;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = insets;
			swListHolder.add(item.getGuiFactorySwing().getComponent(), gbc);
			JButton removeButton = new JButton("-");
			removeButton.addActionListener((event) -> parentEditor.removeItem(item));
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = row;
			gbc.insets = insets;
			swListHolder.add(removeButton, gbc);
			row++;
		}
		swListHolder.invalidate();
		swComponent.revalidate();
	}

}
