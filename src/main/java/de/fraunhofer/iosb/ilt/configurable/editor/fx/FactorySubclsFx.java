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
	private String displayName = "";
	private String selectLabel = "Available Classes:";
	private BorderPane fxPaneRoot;
	private BorderPane fxPaneItem;
	private ComboBox<String> fxItems;

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
		String[] classes = parentEditor.getClasses();
		displayName = parentEditor.getClassName();
		if (!displayName.isEmpty()) {
			displayName = displayName.substring(parentEditor.getPrefix().length());
		}
		createPane(classes);
	}

	private void createPane(String[] classes) {
		FlowPane controls = new FlowPane();
		controls.setAlignment(Pos.TOP_RIGHT);
		controls.getChildren().add(new Label(selectLabel));
		fxItems = new ComboBox<>(FXCollections.observableArrayList(classes));
		fxItems.getSelectionModel().select(displayName);
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
		String cName = fxItems.getSelectionModel().getSelectedItem();
		if (cName != null && !cName.isEmpty()) {
			String prefix = parentEditor.getPrefix();
			parentEditor.setClassName(prefix + cName);
			displayName = cName;
		}
	}

	public void fillComponent() {
		String label;
		String className = parentEditor.getClassName();
		String prefix = parentEditor.getPrefix();
		ConfigEditor classEditor = parentEditor.getClassEditor();
		if (className == null || className.isEmpty()) {
			label = "No Class selected.";
		} else {
			label = "Selected: " + className.substring(prefix.length());
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
