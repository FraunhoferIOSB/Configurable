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

import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass.classItem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author Hylke van der Schaaf
 */
public final class FactorySubclsSwing implements GuiFactorySwing {

    private final EditorSubclass<?, ?, ?> parentEditor;
    private classItem item;
    private String selectLabel = "Type:";
    private JPanel swComponent;
    private JPanel swItemHolder;
    private FilteringComboBox<String> swItems;
    private JPanel controls;

    public FactorySubclsSwing(EditorSubclass<?, ?, ?> parentEditor) {
        this.parentEditor = parentEditor;
    }

    @Override
    public JComponent getComponent() {
        if (swComponent == null) {
            createGui();
        }
        return swComponent;
    }

    private void createGui() {
        String jsonName = parentEditor.getJsonName();
        item = parentEditor.findClassItem(jsonName);
        createComponent();
    }

    private void createComponent() {
        Set<String> classes = parentEditor.getClassesByDisplayName().keySet();

        controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controls.add(new JLabel(selectLabel));
        swItems = new FilteringComboBox<>(classes.toArray(new String[classes.size()]));
        controls.add(swItems);
        if (item != null) {
            swItems.setSelectedItem(item.displayName);
        } else {
            swItems.setSelectedItem(null);
        }
        JButton addButton = new JButton("Set");
        addButton.addActionListener((ActionEvent e) -> {
            setItem();
        });
        controls.add(addButton);
        swItemHolder = new JPanel(new BorderLayout());
        swComponent = new JPanel(new BorderLayout());
        swComponent.setBorder(new EtchedBorder());
        swComponent.add(controls, BorderLayout.NORTH);
        swComponent.add(swItemHolder, BorderLayout.CENTER);
        fillComponent();
    }

    private void setItem() {
        int idx = swItems.getSelectedIndex();
        if (idx >= 0) {
            String displayName = swItems.getModel().getElementAt(idx);
            item = parentEditor.getClassesByDisplayName().get(displayName);
            parentEditor.setJsonName(item.jsonName);
        } else if (!parentEditor.isRestrictedClasses()) {
            String className = swItems.getSelectedItem().toString();
            parentEditor.setJsonName(className);
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
            if (item == null) {
                label = "Manual: " + jsonName;
            } else {
                label = "Selected: " + item.displayName;
            }
        }
        swItemHolder.removeAll();
        Dimension dim = new Dimension(5, 5);
        swItemHolder.add(new Box.Filler(dim, dim, dim), BorderLayout.WEST);
        swItemHolder.add(new JLabel(label), BorderLayout.NORTH);
        if (classEditor == null) {
            JLabel noConf = new JLabel("Nothing to be configured.");
            swItemHolder.add(noConf, BorderLayout.CENTER);
        } else {
            swItemHolder.add(classEditor.getGuiFactorySwing().getComponent(), BorderLayout.CENTER);
        }
        swItemHolder.invalidate();
        swComponent.revalidate();
        swComponent.repaint();
    }

    public void setSelectLabel(String selectLabel) {
        this.selectLabel = selectLabel;
    }

}
