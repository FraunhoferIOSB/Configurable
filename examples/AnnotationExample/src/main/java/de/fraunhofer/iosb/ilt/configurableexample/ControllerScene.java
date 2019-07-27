/*
 * Copyright (C) 2017 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.configurableexample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.ConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FXML Controller class
 *
 * @author hylke
 */
public class ControllerScene implements Initializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerScene.class.getName());

	@FXML
	private BorderPane paneEditor;

	@FXML
	private TextArea textAreaOutput;

	private ConfigEditor editor;

	@FXML
	private void actionLoad(ActionEvent event) {
		loadConfig();
	}

	@FXML
	private void actionPrint(ActionEvent event) {
		printConfig();
	}

	@FXML
	private void actionWave(ActionEvent event) {
		useConfig();
	}

	private void addEditorToGui() {
		FlagShapeList flag = new FlagShapeList();
		editor = flag.getConfigEditor(null, null);
		paneEditor.setCenter(editor.getGuiFactoryFx().getNode());
	}

	private void useConfig() {
		try {
			JsonElement config = editor.getConfig();
			FlagShapeList flag = new FlagShapeList();
			flag.configure(config, null, null, null);
			flag.wave();
		} catch (ConfigurationException ex) {
			LOGGER.error("Could not configure the flag!", ex);
		}
	}

	private void printConfig() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement config = editor.getConfig();
		String jsonString = gson.toJson(config);
		textAreaOutput.setText(jsonString);
		LOGGER.info("Our configuration is:\n{}", jsonString);
	}

	public void loadConfig() {
		loadConfig(textAreaOutput.getText());
	}

	public void loadConfig(String jsonString) {
		JsonElement config = new JsonParser().parse(jsonString);
		editor.setConfig(config);
	}

	/**
	 * Initializes the controller class.
	 *
	 * @param url unused
	 * @param rb unused
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		addEditorToGui();
	}

}
