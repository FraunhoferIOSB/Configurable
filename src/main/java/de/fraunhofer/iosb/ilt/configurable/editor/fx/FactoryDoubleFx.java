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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorDouble;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryDoubleFx implements GuiFactoryFx {

	private final EditorDouble parentEditor;
	private Spinner<Double> fxNode;

	public FactoryDoubleFx(EditorDouble parentEditor) {
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
		SpinnerValueFactory.DoubleSpinnerValueFactory factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(parentEditor.getMin(), parentEditor.getMax(), parentEditor.getValue(), parentEditor.getStep());
		fxNode = new Spinner<>(factory);
		fxNode.setEditable(true);
		// hook in a formatter with the same properties as the factory
		TextFormatter formatter = new TextFormatter(factory.getConverter(), factory.getValue());
		fxNode.getEditor().setTextFormatter(formatter);
		// bidi-bind the values
		factory.valueProperty().bindBidirectional(formatter.valueProperty());
		fillComponent();
	}

	/**
	 * Ensure the component represents the current value.
	 */
	public void fillComponent() {
		fxNode.getValueFactory().setValue(parentEditor.getRawValue());
	}

	public void readComponent() {
		if (fxNode != null) {
			parentEditor.setRawValue(fxNode.getValue());
		}
	}

}
