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
import com.google.gson.JsonNull;
import java.awt.BorderLayout;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * An editor that does not edit anything. For cases where you want a class to be
 * configurable, but not actually have an editor.
 *
 * @author Hylke van der Schaaf
 */
public class EditorNull extends EditorDefault<Void> {

	private JComponent swComponent;
	private BorderPane fxNode;

	public EditorNull() {
	}

	public EditorNull(String label, String description) {
		setLabel(label);
		setDescription(description);
	}

	@Override
	public void setConfig(JsonElement config) {
		// Nothing to configure
	}

	@Override
	public JsonElement getConfig() {
		return JsonNull.INSTANCE;
	}

	@Override
	public JComponent getComponent() {
		if (swComponent == null) {
			swComponent = new JPanel(new BorderLayout());
		}
		return swComponent;
	}

	@Override
	public Node getNode() {
		if (fxNode == null) {
			fxNode = new BorderPane();
		}
		return fxNode;
	}

	@Override
	public Void getValue() {
		return null;
	}

	@Override
	public void setValue(Void value) {
	}

}
