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
import de.fraunhofer.iosb.ilt.configurable.Styles;
import de.fraunhofer.iosb.ilt.configurable.editor.AbstractEditorMap;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactoryMapFx implements GuiFactoryFx {

	private final AbstractEditorMap<?, ?> parentEditor;
	// FX Nodes
	private BorderPane fxPaneRoot;
	private GridPane fxPaneList;
	private ComboBox<AbstractEditorMap.Item> fxBoxNames;

	public FactoryMapFx(AbstractEditorMap<?, ?> parentEditor) {
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
		if (!parentEditor.getOptionalOptions().isEmpty()) {
			controls.getChildren().add(new Label("Available items:"));
			List<AbstractEditorMap.Item> optionals = new ArrayList<>();
			for (final String optionName : parentEditor.getOptionalOptions()) {
				if (!parentEditor.getRawValue().contains(optionName)) {
					optionals.add(parentEditor.getOptions().get(optionName));
				}
			}
			optionals.sort((final AbstractEditorMap.Item o1, final AbstractEditorMap.Item o2) -> o1.label.compareTo(o2.label));
			fxBoxNames = new ComboBox<>(FXCollections.observableArrayList(optionals));
			controls.getChildren().add(fxBoxNames);
			Button addButton = new Button("+");
			addButton.setOnAction((event) -> addItem());
			controls.getChildren().add(addButton);
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
		fxPaneList.getChildren().clear();
		GridBagConstraints gbc;
		int row = 0;
		int endCol = -1;
		// Iterate over the options so the order is fixed.
		for (Map.Entry<String, ? extends AbstractEditorMap.Item<?>> entry : parentEditor.getOptions().entrySet()) {
			final String key = entry.getKey();
			if (!parentEditor.getRawValue().contains(key)) {
				continue;
			}
			final AbstractEditorMap.Item<?> item = entry.getValue();
			endCol += item.colwidth;
			if (endCol >= parentEditor.getColumns()) {
				endCol = item.colwidth - 1;
				row++;
			}
			final int startCol = endCol - item.colwidth + 1;
			final int width = 3 * item.colwidth - 2;
			final int x0 = startCol * 3;
			final int x1 = x0 + 1;
			final int x2 = x0 + width + 1;
			String label = item.editor.getLabel();
			if (label.isEmpty()) {
				label = key;
			}
			addToGridFx(row, x0, label, x1, item, width, x2, key);
		}
	}

	private void addToGridFx(int row, final int x0, String label, final int x1, final AbstractEditorMap.Item<?> item, final int width, final int x2, final String key) {
		Label fxLabel = new Label(label);
		fxLabel.setTooltip(new Tooltip(item.editor.getDescription()));
		GridPane.setConstraints(fxLabel, x0, row, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.NEVER, Priority.NEVER);
		fxPaneList.getChildren().add(fxLabel);
		Node itemPane = item.editor.getGuiFactoryFx().getNode();
		GridPane.setConstraints(itemPane, x1, row, width, 1, HPos.LEFT, VPos.BASELINE, Priority.SOMETIMES, Priority.NEVER);
		fxPaneList.getChildren().add(itemPane);
		if (!parentEditor.getOptionalOptions().isEmpty()) {
			Button removeButton = new Button("-");
			removeButton.setDisable(!item.optional);
			removeButton.setOnAction((event) -> parentEditor.removeItem(key));
			GridPane.setConstraints(removeButton, x2, row, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.NEVER, Priority.NEVER);
			fxPaneList.getChildren().add(removeButton);
		}
	}

	private void addItem() {
		AbstractEditorMap.Item item = fxBoxNames.getSelectionModel().getSelectedItem();
		if (item != null) {
			String key = item.getName();
			parentEditor.addItem(key);
		}
	}

	public void addItem(final String key) {
		if (fxBoxNames != null) {
			fxBoxNames.getItems().remove(parentEditor.getOptions().get(key));
		}
		fillComponent();
	}

	public void removeItem(final AbstractEditorMap.Item<?> item) {
		if (item.optional) {
			if (fxBoxNames != null) {
				fxBoxNames.getItems().add(item);
			}
			fillComponent();
		}
	}

}
