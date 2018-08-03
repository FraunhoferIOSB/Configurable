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

import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.Styles;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 *
 * @author Hylke van der Schaaf
 * @param <T> The type of editors that edit the items in the list.
 * @param <U> The type of items in the list.
 */
public final class FactoryListFx<U, T extends ConfigEditor<U>> implements GuiFactoryFx {

	private final EditorList<U, T> parentEditor;
	private BorderPane fxPaneRoot;
	private GridPane fxPaneList;

	public FactoryListFx(EditorList<U, T> parentEditor) {
		this.parentEditor = parentEditor;
	}

	@Override
	public Pane getNode() {
		if (fxPaneRoot == null) {
			createPane();
		}
		return fxPaneRoot;
	}

	private void createPane() {
		FlowPane controls = new FlowPane();
		controls.setAlignment(Pos.TOP_RIGHT);

		if (parentEditor.getMinCount() != parentEditor.getMaxCount()) {
			Label addLabel = new Label("Add item");
			addLabel.setAlignment(Pos.BASELINE_RIGHT);
			controls.getChildren().add(addLabel);
			Button addButton = new Button("+");
			addButton.setOnAction((event) -> parentEditor.addItem());
			controls.getChildren().add(addButton);
		} else {
			Label addLabel = new Label("Items:");
			addLabel.setAlignment(Pos.BASELINE_RIGHT);
			controls.getChildren().add(addLabel);
		}

		fxPaneList = new GridPane();
		fxPaneRoot = new BorderPane();
		fxPaneRoot.setStyle(Styles.STYLE_BORDER);
		fxPaneRoot.setTop(controls);
		fxPaneRoot.setCenter(fxPaneList);
		fillComponent();
	}

	/**
	 * Ensure the component represents the current value.
	 */
	public void fillComponent() {
		if (fxPaneRoot == null) {
			createPane();
		}
		fxPaneList.getChildren().clear();
		if (parentEditor.getRawValue().isEmpty()) {
			fxPaneList.add(new Label("No items added."), 0, 0);
		}
		int row = 0;
		for (final T item : parentEditor.getRawValue()) {
			Node pane = item.getGuiFactoryFx().getNode();
			GridPane.setConstraints(pane, 0, row, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.SOMETIMES);
			if (parentEditor.getMinCount() != parentEditor.getMaxCount()) {
				Button removeButton = new Button("-");
				removeButton.setOnAction((event) -> parentEditor.removeItem(item));
				GridPane.setConstraints(removeButton, 1, row, 1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER);
				fxPaneList.getChildren().addAll(pane, removeButton);
			}
			row++;
		}
	}

}
