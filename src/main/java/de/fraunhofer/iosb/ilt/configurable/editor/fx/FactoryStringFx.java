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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorPassword;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorString;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryStringFx implements GuiFactoryFx {

	private final EditorString parentEditor;
	private TextInputControl fxNode;
	private boolean passwordControl;

	public FactoryStringFx(EditorString parentEditor) {
		this.parentEditor = parentEditor;
		if (parentEditor instanceof EditorPassword) {
			passwordControl = true;
		}
	}

	@Override
	public Node getNode() {
		if (fxNode == null) {
			createNode();
		}
		return fxNode;
	}

	private void createNode() {
		if (passwordControl) {
			fxNode = new PasswordField();
		} else if (parentEditor.getLines() == 1) {
			fxNode = new TextField();
		} else {
			TextArea text = new TextArea();
			text.setPrefRowCount(parentEditor.getLines());
			fxNode = text;
		}
		fillComponent();
	}

	/**
	 * Ensure the swComponent represents the current value.
	 */
	public void fillComponent() {
		fxNode.setEditable(parentEditor.canEdit());
		fxNode.setText(parentEditor.getRawValue());
	}

	public void readComponent() {
		parentEditor.setRawValue(fxNode.getText());
	}

}
