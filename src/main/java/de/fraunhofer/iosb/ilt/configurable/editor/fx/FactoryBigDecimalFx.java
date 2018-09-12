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
package de.fraunhofer.iosb.ilt.configurable.editor.fx;

import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorBigDecimal;
import java.math.BigDecimal;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryBigDecimalFx implements GuiFactoryFx {

	private static final Logger LOGGER = LoggerFactory.getLogger(FactoryBigDecimalFx.class.getName());

	private final EditorBigDecimal parentEditor;
	private TextInputControl fxNode;

	public FactoryBigDecimalFx(EditorBigDecimal parentEditor) {
		this.parentEditor = parentEditor;
	}

	@Override
	public Node getNode() {
		if (fxNode == null) {
			createComponent();
		}
		return fxNode;
	}

	private void createComponent() {
		BigDecimal rawValue = parentEditor.getRawValue();
		BigDecimal min = parentEditor.getMin();
		BigDecimal max = parentEditor.getMax();

		if (min != null && min.compareTo(rawValue) > 0) {
			parentEditor.setRawValue(min);
		}
		if (max != null && max.compareTo(rawValue) < 0) {
			parentEditor.setRawValue(min);
		}
		fxNode = new TextField();
		fillComponent();
	}

	/**
	 * Ensure the component represents the current value.
	 */
	public void fillComponent() {
		fxNode.setText("" + parentEditor.getRawValue());
	}

	public void readComponent() {
		if (fxNode != null) {
			try {
				parentEditor.setRawValue(new BigDecimal(fxNode.getText()));
			} catch (NumberFormatException exc) {
				LOGGER.error("Failed to parse text to number: " + fxNode.getText());
			}
		}
	}

}
