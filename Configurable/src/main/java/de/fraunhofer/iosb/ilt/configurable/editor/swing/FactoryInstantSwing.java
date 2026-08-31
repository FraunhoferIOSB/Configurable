/*
 * Copyright (C) 2024 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
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

import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInstant;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Editor for Swing for time Instant. Bases on simple String parsing, since
 * Swing has no time picker.
 */
public final class FactoryInstantSwing implements GuiFactorySwing {

    private static final Logger LOGGER = LoggerFactory.getLogger(FactoryInstantSwing.class.getName());
    private final EditorInstant parentEditor;
    private JTextComponent swText;
    private JComponent swComponent;
    private Color bgNormal;

    public FactoryInstantSwing(EditorInstant parentEditor) {
        this.parentEditor = parentEditor;
    }

    @Override
    public JComponent getComponent() {
        if (swComponent == null) {
            createComponent();
        }
        return swComponent;
    }

    private void createComponent() {
        swText = new JTextField();
        swText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validate();
            }
        });
        bgNormal = swText.getBackground();
        swComponent = swText;
        fillComponent();
    }

    private void validate() {
        final String text = swText.getText();
        try {
            Instant.parse(text);
            swText.setBackground(bgNormal);
        } catch (DateTimeParseException ex) {
            LOGGER.debug("Failed to parse {}", text);
            swText.setBackground(Color.red);
        }
    }

    /**
     * Ensure the swComponent represents the current value.
     */
    public void fillComponent() {
        swText.setEditable(parentEditor.canEdit());
        swText.setText(parentEditor.getRawValue().toString());
    }

    public void readComponent() {
        final String text = swText.getText();
        try {
            parentEditor.setRawValue(Instant.parse(text));
        } catch (DateTimeParseException ex) {
            LOGGER.warn("Failed to parse {}", text);
        }
    }

}
