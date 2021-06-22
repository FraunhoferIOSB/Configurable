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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorColor;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryColorSwing implements GuiFactorySwing {

	private final EditorColor parentEditor;
	private JPanel swComponent;
	private SpinnerNumberModel swModelAlpha;
	private SpinnerNumberModel swModelBlue;
	private SpinnerNumberModel swModelGreen;
	private SpinnerNumberModel swModelRed;
	private boolean filling = false;

	public FactoryColorSwing(EditorColor parentEditor) {
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
		swModelAlpha = new SpinnerNumberModel(0, 0, 255, 1);
		swModelBlue = new SpinnerNumberModel(0, 0, 255, 1);
		swModelGreen = new SpinnerNumberModel(0, 0, 255, 1);
		swModelRed = new SpinnerNumberModel(0, 0, 255, 1);
		swComponent = new JPanel(new GridBagLayout());
		swComponent.add(new JSpinner(swModelRed), new GridBagConstraints());
		swComponent.add(new JSpinner(swModelGreen), new GridBagConstraints());
		swComponent.add(new JSpinner(swModelBlue), new GridBagConstraints());
		if (parentEditor.isEditAlpla()) {
			swComponent.add(new JSpinner(swModelAlpha), new GridBagConstraints());
		}

		JButton button = new JButton("…");
		button.setMargin(new java.awt.Insets(0, 2, 0, 2));
		button.addActionListener((ActionEvent e) -> openPicker());
		swComponent.add(button, new GridBagConstraints());

		fillComponent();
		ChangeListener cl = (ChangeEvent e) -> {
			readComponent();
			swComponent.setBackground(new Color(parentEditor.getRed(), parentEditor.getGreen(), parentEditor.getBlue()));
		};
		swModelRed.addChangeListener(cl);
		swModelGreen.addChangeListener(cl);
		swModelBlue.addChangeListener(cl);
		swModelAlpha.addChangeListener(cl);
	}

	private void openPicker() {
		Color newColor = JColorChooser.showDialog(swComponent, "Choose Color", new Color(parentEditor.getRed(), parentEditor.getGreen(), parentEditor.getBlue(), parentEditor.getAlpha()));
		if (newColor != null) {
			parentEditor.setRed(newColor.getRed());
			parentEditor.setGreen(newColor.getGreen());
			parentEditor.setBlue(newColor.getBlue());
			if (parentEditor.isEditAlpla()) {
				parentEditor.setAlpha(newColor.getAlpha());
			}
		}
		fillComponent();
	}

	/**
	 * Ensure the component represents the current value.
	 */
	public void fillComponent() {
		filling = true;
		swModelRed.setValue(parentEditor.getRed());
		swModelGreen.setValue(parentEditor.getGreen());
		swModelBlue.setValue(parentEditor.getBlue());
		if (parentEditor.isEditAlpla()) {
			swModelAlpha.setValue(parentEditor.getAlpha());
		}
		swComponent.setBackground(new Color(parentEditor.getRed(), parentEditor.getGreen(), parentEditor.getBlue()));
		filling = false;
	}

	public void readComponent() {
		if (filling) {
			return;
		}
		parentEditor.setRed(swModelRed.getNumber().intValue());
		parentEditor.setGreen(swModelGreen.getNumber().intValue());
		parentEditor.setBlue(swModelBlue.getNumber().intValue());
		parentEditor.setAlpha(swModelAlpha.getNumber().intValue());
	}

}
