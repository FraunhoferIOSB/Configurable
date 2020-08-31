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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryDoubleSwing implements GuiFactorySwing {

	private static final Logger LOGGER = LoggerFactory.getLogger(FactoryDoubleSwing.class.getName());

	private final EditorDouble parentEditor;
	private JTextField swComponent;

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
		swComponent = new JTextField();
		swComponent.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				try {
					BigDecimal unused = new BigDecimal(((JTextField) input).getText());
					return true;
				} catch (NumberFormatException exc) {
					LOGGER.trace("Not a decimal.", exc);
					return false;
				}
			}
		;
		});
		swComponent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				readComponent();
			}
		});
		fillComponent();
	}

	private double getRawValue() {
		Double rawValue = parentEditor.getRawValue();
		final double min = parentEditor.getMin();
		final double max = parentEditor.getMax();
		if (rawValue == null) {
			rawValue = 0.0;
			parentEditor.setRawValue(rawValue);
		}
		if (rawValue < min || rawValue > max) {
			rawValue = Math.max(min, Math.min(rawValue, max));
			parentEditor.setRawValue(rawValue);
		}
		return rawValue;
	}

	/**
	 * Ensure the component represents the current value.
	 */
	public void fillComponent() {
		double rawValue = getRawValue();
		swComponent.setText("" + rawValue);
	}

	public void readComponent() {
		if (swComponent != null) {
			try {
				parentEditor.setRawValue(Double.parseDouble(swComponent.getText()));
			} catch (NumberFormatException exc) {
				LOGGER.error("Failed to parse text to number: " + swComponent.getText());
			}
			fillComponent();
		}
	}

}
