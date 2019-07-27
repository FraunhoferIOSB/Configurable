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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass.classItem;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactorySubclsFx implements GuiFactoryFx {

	private final EditorSubclass<?, ?, ?> parentEditor;
	private classItem item;
	private String selectLabel = "Type:";
	private BorderPane fxPaneRoot;
	private BorderPane fxPaneItem;
	private ComboBox<String> fxItems;
	private FlowPane controls;

	public FactorySubclsFx(EditorSubclass<?, ?, ?> parentEditor) {
		this.parentEditor = parentEditor;
	}

	@Override
	public Pane getNode() {
		if (fxPaneRoot == null) {
			createGui();
		}
		return fxPaneRoot;
	}

	private void createGui() {
		String jsonName = parentEditor.getJsonName();
		item = parentEditor.findClassItem(jsonName);
		createPane();
	}

	private void createPane() {
		Set<String> values = parentEditor.getClassesByDisplayName().keySet();

		controls = new FlowPane();
		controls.setAlignment(Pos.TOP_CENTER);
		controls.getChildren().add(new Label(selectLabel));
		fxItems = new FilteringComboBox<>(FXCollections.observableArrayList(values));
		fxItems.setEditable(true);
		if (item != null) {
			fxItems.getSelectionModel().select(item.displayName);
		}
		controls.getChildren().add(fxItems);
		Button addButton = new Button("set");
		addButton.setOnAction((event) -> setItem());
		controls.getChildren().add(addButton);
		fxPaneItem = new BorderPane();
		fxPaneItem.setPadding(new Insets(0, 0, 0, 5));
		fxPaneRoot = new BorderPane();
		fxPaneRoot.setStyle(Styles.STYLE_BORDER);
		fxPaneRoot.setCenter(fxPaneItem);
		fxPaneRoot.setTop(controls);
		fillComponent();
	}

	private void setItem() {
		String selected = fxItems.getSelectionModel().getSelectedItem();
		if (selected != null && !selected.isEmpty()) {
			item = parentEditor.getClassesByDisplayName().get(selected);
			parentEditor.setJsonName(item.jsonName);
		}
	}

	public void fillComponent() {
		controls.setVisible(parentEditor.canEdit());
		String jsonName = parentEditor.getJsonName();
		item = parentEditor.findClassItem(jsonName);

		ConfigEditor classEditor = parentEditor.getClassEditor();
		String label;
		if (jsonName == null || jsonName.isEmpty()) {
			label = "No Class selected.";
		} else {
			label = "Selected: " + item.displayName;
		}
		fxPaneItem.getChildren().clear();
		fxPaneItem.setTop(new Label(label));
		if (classEditor == null) {
			Label noConf = new Label("Nothing to be configured.");
			fxPaneItem.setCenter(noConf);
		} else {
			fxPaneItem.setCenter(classEditor.getGuiFactoryFx().getNode());
		}
	}

	public void setSelectLabel(String selectLabel) {
		this.selectLabel = selectLabel;
	}

}
