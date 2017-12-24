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
import de.fraunhofer.iosb.ilt.configurable.editor.AbstractEditorMap;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryMapSwing implements GuiFactorySwing {

	private final AbstractEditorMap<?, ?> parentEditor;
	// Swing components
	private JPanel swComponent;
	private JPanel swListHolder;
	private JComboBox<AbstractEditorMap.Item> swNames;
	private MutableComboBoxModel<AbstractEditorMap.Item> swModel;

	public FactoryMapSwing(AbstractEditorMap<?, ?> parentEditor) {
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
		if (!parentEditor.getOptionalOptions().isEmpty()) {
			controls.add(new JLabel("List of items:"), BorderLayout.WEST);
			List<AbstractEditorMap.Item> optionals = new ArrayList<>();
			for (final String optionName : parentEditor.getOptionalOptions()) {
				if (!parentEditor.getRawValue().contains(optionName)) {
					optionals.add(parentEditor.getOptions().get(optionName));
				}
			}
			optionals.sort((final AbstractEditorMap.Item o1, final AbstractEditorMap.Item o2) -> o1.label.compareTo(o2.label));
			swModel = new DefaultComboBoxModel<>(optionals.toArray(new AbstractEditorMap.Item[optionals.size()]));
			swNames = new JComboBox<>(swModel);
			controls.add(swNames, BorderLayout.CENTER);
			final JButton addButton = new JButton("+");
			addButton.addActionListener((event) -> addItem());
			controls.add(addButton, BorderLayout.EAST);
		}
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
		int row = 0;
		int endCol = -1;
		// Iterate over the options so the order is fixed.
		for (Map.Entry<String, ? extends AbstractEditorMap.Item<?>> entry : parentEditor.getOptions().entrySet()) {
			final String key = entry.getKey();
			if (!parentEditor.getRawValue().contains(key)) {
				continue;
			}
			final AbstractEditorMap.Item<?> item = entry.getValue();
			endCol += item.colwidth;
			if (endCol >= parentEditor.getColumns()) {
				endCol = item.colwidth - 1;
				row++;
			}
			final int startCol = endCol - item.colwidth + 1;
			final int width = 3 * item.colwidth - 2;
			final int x0 = startCol * 3;
			final int x1 = x0 + 1;
			final int x2 = x0 + width + 1;
			String label = item.editor.getLabel();
			if (label.isEmpty()) {
				label = key;
			}
			addToGridSw(row, x0, label, x1, item, width, x2, key);
		}
		swListHolder.invalidate();
		swComponent.revalidate();
		swComponent.repaint();
	}

	private void addToGridSw(int row, final int x0, String label, final int x1, final AbstractEditorMap.Item<?> item, final int width, final int x2, final String key) {
		GridBagConstraints gbc;
		gbc = new GridBagConstraints();
		gbc.gridx = x0;
		gbc.gridy = row;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 3, 1, 1);
		final JLabel jLabel = new JLabel(label);
		jLabel.setToolTipText(item.editor.getDescription());
		swListHolder.add(jLabel, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = x1;
		gbc.gridy = row;
		gbc.gridwidth = width;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		swListHolder.add(item.editor.getGuiFactorySwing().getComponent(), gbc);
		if (!parentEditor.getOptionalOptions().isEmpty()) {
			JButton removeButton = new JButton("-");
			removeButton.setEnabled(item.optional);
			removeButton.addActionListener((event) -> parentEditor.removeItem(key));
			gbc = new GridBagConstraints();
			gbc.gridx = x2;
			gbc.gridy = row;
			swListHolder.add(removeButton, gbc);
		}
	}

	private void addItem() {
		int idx = swNames.getSelectedIndex();
		if (idx >= 0) {
			String key = swNames.getModel().getElementAt(idx).getName();
			parentEditor.addItem(key);
		}
	}

	public void addItem(final String key) {
		if (swModel != null) {
			swModel.removeElement(parentEditor.getOptions().get(key));
		}
		fillComponent();
	}

	public void removeItem(final AbstractEditorMap.Item<?> item) {
		if (item.optional) {
			if (swModel != null) {
				swModel.addElement(item);
			}
			fillComponent();
		}
	}

}
