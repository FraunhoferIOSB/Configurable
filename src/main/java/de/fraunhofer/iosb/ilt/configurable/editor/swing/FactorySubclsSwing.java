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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactorySubclsSwing implements GuiFactorySwing {

	private final EditorSubclass<?, ?, ?> parentEditor;
	private String displayName = "";
	private String selectLabel = "Available Classes:";
	private JPanel swComponent;
	private JPanel swItemHolder;
	private JComboBox<String> swItems;

	public FactorySubclsSwing(EditorSubclass<?, ?, ?> parentEditor) {
		this.parentEditor = parentEditor;
	}

	@Override
	public JComponent getComponent() {
		if (swComponent == null) {
			createGui();
		}
		return swComponent;
	}

	private void createGui() {
		String[] classes = parentEditor.getClasses();
		displayName = parentEditor.getClassName();
		if (!displayName.isEmpty()) {
			displayName = displayName.substring(parentEditor.getPrefix().length());
		}
		createComponent(classes);
	}

	private void createComponent(String[] classes) {
		JPanel controls = new JPanel(new BorderLayout());
		controls.add(new JLabel(selectLabel), BorderLayout.WEST);
		swItems = new JComboBox<>(classes);
		controls.add(swItems, BorderLayout.CENTER);
		swItems.setSelectedItem(displayName);
		JButton addButton = new JButton("Set");
		addButton.addActionListener((ActionEvent e) -> {
			setItem();
		});
		controls.add(addButton, BorderLayout.EAST);
		swItemHolder = new JPanel(new BorderLayout());
		swComponent = new JPanel(new BorderLayout());
		swComponent.setBorder(new EtchedBorder());
		swComponent.add(controls, BorderLayout.NORTH);
		swComponent.add(swItemHolder, BorderLayout.CENTER);
		fillComponent();
	}

	private void setItem() {
		int idx = swItems.getSelectedIndex();
		if (idx >= 0) {
			String cName = swItems.getModel().getElementAt(idx);
			parentEditor.setClassName(parentEditor.getPrefix() + cName);
		}
	}

	public void fillComponent() {
		String label;
		String className = parentEditor.getClassName();
		String prefix = parentEditor.getPrefix();
		ConfigEditor classEditor = parentEditor.getClassEditor();
		if (className == null || className.isEmpty()) {
			label = "No Class selected.";
		} else {
			displayName = className.substring(prefix.length());
			label = "Selected: " + className.substring(prefix.length());
		}
		swItemHolder.removeAll();
		Dimension dim = new Dimension(5, 5);
		swItemHolder.add(new Box.Filler(dim, dim, dim), BorderLayout.WEST);
		swItemHolder.add(new JLabel(label), BorderLayout.NORTH);
		if (classEditor == null) {
			JLabel noConf = new JLabel("Nothing to be configured.");
			swItemHolder.add(noConf, BorderLayout.CENTER);
		} else {
			swItemHolder.add(classEditor.getGuiFactorySwing().getComponent(), BorderLayout.CENTER);
		}
		swItemHolder.invalidate();
		swComponent.revalidate();
		swComponent.repaint();
	}

	public void setSelectLabel(String selectLabel) {
		this.selectLabel = selectLabel;
	}

}
