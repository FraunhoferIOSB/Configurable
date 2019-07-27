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
package de.fraunhofer.iosb.ilt.configurable.editor.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * A combo box that can filter its elements, using a String.contains on the
 * elements' toString values.
 *
 * @param <E> The type of the elements in the combobox.
 */
public class FilteringComboBox<E> extends JComboBox<E> {

	/**
	 * The complete list of elements.
	 */
	private final E[] allElements;

	/**
	 * Create a new FilteringComboBox, using the given list of elements.
	 *
	 * @param elements The elements to let the user select from.
	 */
	public FilteringComboBox(E[] elements) {
		super(elements);
		this.allElements = elements;
		setEditable(true);

		final JTextField textfield = (JTextField) getEditor().getEditorComponent();

		/**
		 * Listen for key presses.
		 */
		textfield.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {
				SwingUtilities.invokeLater(() -> {
					filterElements(textfield.getText().toLowerCase());
				});
			}
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
			setModel(new DefaultComboBoxModel(entriesFiltered.toArray()));
			setSelectedItem(enteredText);
			showPopup();
		} else {
			hidePopup();
		}
	}
}
