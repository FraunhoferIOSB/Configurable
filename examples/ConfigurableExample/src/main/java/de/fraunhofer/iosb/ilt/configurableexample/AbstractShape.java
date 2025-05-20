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
package de.fraunhofer.iosb.ilt.configurableexample;

import com.google.gson.JsonElement;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorColor;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;
import java.awt.Color;

/**
 * The abstract class that our shapes extend. It defines the common property
 * 'color' shared amongst all Shapes.
 *
 * @author scf
 */
public abstract class AbstractShape implements Shape {

    private Color color;

    private EditorMap configEditor;
    private EditorColor editorColor;

    public Color getColor() {
        return color;
    }

    @Override
    public void configure(JsonElement config, Object context, Object edtCtx, ConfigEditor<?> ignoredConfigEditor) {
        getConfigEditor(context, edtCtx);
        configEditor.setConfig(config);
        color = editorColor.getValue();
    }

    @Override
    public EditorMap getConfigEditor(Object context, Object edtCtx) {
        if (configEditor == null) {
            configEditor = new EditorMap();

            editorColor = new EditorColor(Color.GREEN, false, "Color", "The colour of the circle");
            configEditor.addOption("color", editorColor, true);
        }
        return configEditor;
    }

}
