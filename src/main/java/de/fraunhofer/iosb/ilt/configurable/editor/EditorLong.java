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
package de.fraunhofer.iosb.ilt.configurable.editor;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class EditorLong extends EditorDefault<Object, Object, Long> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EditorLong.class);
	private final long min;
	private final long max;
	private final long deflt;
	private long value;
	/**
	 * Flag indicating we are in JavaFX mode.
	 */
	private Boolean fx;
	// Swing components
	private JTextField swComponent;
	// JavaFX Nodes
	private TextInputControl fxNode;

	public EditorLong(long min, long max, long deflt, String label, String description) {
		this.deflt = deflt;
		this.value = deflt;
		this.min = min;
		this.max = max;
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void setConfig(JsonElement config, Object context, Object edtCtx) {
		if (config != null && config.isJsonPrimitive() && config.getAsJsonPrimitive().isNumber()) {
			value = config.getAsInt();
		} else {
			value = deflt;
		}
		fillComponent();
	}

	@Override
	public JsonElement getConfig() {
		return new JsonPrimitive(getValue());
	}

	private void setFx(boolean fxMode) {
		if (fx != null && fx != fxMode) {
			throw new IllegalStateException("Can not switch between swing and FX mode.");
		}
		fx = fxMode;
	}

	@Override
	public JComponent getComponent() {
		setFx(false);
		if (swComponent == null) {
			createComponent();
		}
		return swComponent;
	}

	@Override
	public Node getNode() {
		setFx(true);
		if (fxNode == null) {
			createComponent();
		}
		return fxNode;
	}

	private void createComponent() {
		if (value < min || value > max) {
			LOGGER.error("min < value < max is false: {} < {} < {}.", min, value, max);
			value = Math.max(min, Math.min(value, max));
		}

		if (fx) {
			fxNode = new TextField();
		} else {
			swComponent = new JTextField();
		}
		fillComponent();
	}

	/**
	 * Ensure the component represents the current value.
	 */
	private void fillComponent() {
		if (fx == null) {
			return;
		}
		if (fx) {
			fxNode.setText("" + value);
		} else {
			swComponent.setText("" + value);
		}
	}

	@Override
	public Long getValue() {
		if (swComponent != null) {
			try {
				value = Long.parseLong(swComponent.getText());
			} catch (NumberFormatException exc) {
				LOGGER.error("Failed to parse text to number: " + swComponent.getText());
			}
		}
		if (fxNode != null) {
			try {
				value = Long.parseLong(fxNode.getText());
			} catch (NumberFormatException exc) {
				LOGGER.error("Failed to parse text to number: " + swComponent.getText());
			}
		}
		if (value > max) {
			value = max;
		}
		if (value < min) {
			value = min;
		}
		return value;
	}

	@Override
	public void setValue(Long value) {
		this.value = value;
		fillComponent();
	}

}
