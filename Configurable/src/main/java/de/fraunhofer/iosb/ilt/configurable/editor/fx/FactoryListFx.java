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
import javafx.scene.layout.VBox;

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
	private Button addButton;
	private boolean vertical = true;
	private String text = "Add item";

	public FactoryListFx(EditorList<U, T> parentEditor, boolean vertical) {
		this.parentEditor = parentEditor;
		this.vertical = vertical;
		if (parentEditor.getMinCount() != parentEditor.getMaxCount()) {
			text = "Items:";
		}
	}

	public void setText(String text) {
		this.text = text;
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

		Label addLabel = new Label(text);
		addLabel.setAlignment(Pos.BASELINE_RIGHT);
		controls.getChildren().add(addLabel);
		addButton = new Button("+");
		addButton.setOnAction((event) -> parentEditor.addItem());
		controls.getChildren().add(addButton);

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
		addButton.setVisible(parentEditor.canEdit());
		fxPaneList.getChildren().clear();
		if (parentEditor.getRawValue().isEmpty()) {
			fxPaneList.add(new Label("No items added."), 0, 0);
		}
		int row = 0;
		for (final T item : parentEditor.getRawValue()) {
			Node pane = item.getGuiFactoryFx().getNode();
			GridPane.setConstraints(
					pane,
					vertical ? 0 : row,
					vertical ? row : 0,
					1, 1, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.SOMETIMES);
			if (parentEditor.canEdit()) {
				VBox buttonBox = new VBox();
				Button removeButton = new Button("-");
				removeButton.setOnAction((event) -> parentEditor.removeItem(item));
				buttonBox.getChildren().add(removeButton);

				if (row > 0) {
					final int myRow = row;
					Button upButton = new Button("↑");
					upButton.setOnAction((event) -> parentEditor.upItem(myRow));
					buttonBox.getChildren().add(upButton);
				}
				GridPane.setConstraints(
						buttonBox,
						vertical ? 1 : row,
						vertical ? row : 1,
						1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER);
				fxPaneList.getChildren().addAll(pane, buttonBox);
			}
			row++;
		}
	}

}
