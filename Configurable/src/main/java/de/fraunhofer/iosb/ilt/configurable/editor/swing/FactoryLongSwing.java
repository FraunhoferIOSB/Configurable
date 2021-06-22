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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorLong;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryLongSwing implements GuiFactorySwing {

	private static final Logger LOGGER = LoggerFactory.getLogger(FactoryLongSwing.class.getName());

	private final EditorLong parentEditor;
	private JTextField swComponent;

	public FactoryLongSwing(EditorLong parentEditor) {
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
		swComponent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				readComponent();
			}
		});
		fillComponent();
	}

	private long getRawValue() {
		Long rawValue = parentEditor.getRawValue();
		final long min = parentEditor.getMin();
		final long max = parentEditor.getMax();
		if (rawValue == null) {
			rawValue = 0L;
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
		swComponent.setText("" + getRawValue());
	}

	public void readComponent() {
		if (swComponent != null) {
			try {
				parentEditor.setRawValue(Long.parseLong(swComponent.getText()));
			} catch (NumberFormatException exc) {
				LOGGER.error("Failed to parse text to number: " + swComponent.getText());
			}
			fillComponent();
		}
	}
}
