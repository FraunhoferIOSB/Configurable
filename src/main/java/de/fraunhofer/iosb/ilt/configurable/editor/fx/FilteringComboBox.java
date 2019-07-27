/*
 * Copyright (C) 2019 Fraunhofer IOSB
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

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A combo box that can filter its elements, using a String.contains on the
 * elements' toString values.
 *
 * @param <E> The type of the elements in the combobox.
 */
public class FilteringComboBox<E> extends ComboBox<E> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilteringComboBox.class.getName());
	/**
	 * The complete list of elements.
	 */
	private final List<E> allElements;

	/**
	 * Create a new FilteringComboBox, using the given list of elements.
	 *
	 * @param elements The elements to let the user select from.
	 */
	public FilteringComboBox(List<E> elements) {
		super(FXCollections.observableArrayList(elements));
		this.allElements = elements;
		setEditable(true);

		final TextField textfield = getEditor();

		/**
		 * Listen for key presses.
		 */
		textfield.setOnKeyReleased((ke) -> {
			switch (ke.getCode()) {
				case DOWN:
				case UP:
				case LEFT:
				case RIGHT:
				case KP_DOWN:
				case KP_UP:
				case KP_LEFT:
				case KP_RIGHT:
				case ENTER:
					return;
			}
			Platform.runLater(() -> {
				filterElements(textfield.getText().toLowerCase());
			});
		});
	}

	/**
	 * Build a list of elements that contain the given search string. Elements
	 * are converted to Strings using their toString method.
	 *
	 * @param enteredText The text to search for.
	 */
	private void filterElements(String enteredText) {
		List<E> entriesFiltered = new ArrayList<>();

		for (E entry : allElements) {
			if (entry.toString().toLowerCase().contains(enteredText)) {
				entriesFiltered.add(entry);
			}
		}

		if (entriesFiltered.size() > 0) {
			ObservableList<E> items = getItems();
			if (items.size() == entriesFiltered.size()) {
				// The list didn't actually change. Do nothing.
				return;
			}
			items.clear();
			items.addAll(entriesFiltered);
			show();
		} else {
			hide();
		}
	}
}
