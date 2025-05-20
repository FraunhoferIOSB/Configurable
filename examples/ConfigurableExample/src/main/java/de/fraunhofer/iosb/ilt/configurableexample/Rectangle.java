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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorDouble;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An example Configurable class.
 *
 * @author scf
 */
public class Rectangle extends AbstractShape {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rectangle.class);
    private double x;
    private double y;
    private int width;
    private int height;

    private EditorMap configEditor;
    private EditorInt editorWidth;
    private EditorInt editorHeight;
    private EditorDouble editorX;
    private EditorDouble editorY;

    @Override
    public void paintMe() {
        // paint to some device...
        LOGGER.info("I'm a Rectangle at {}, {} with width {}, height {} and color {}!", x, y, width, height, getColor());
    }

    @Override
    public void configure(JsonElement config, Object context, Object edtCtx, ConfigEditor<?> configEditor) {
        super.configure(config, context, edtCtx, configEditor);
        width = editorWidth.getValue();
        height = editorHeight.getValue();
        x = editorX.getValue();
        y = editorY.getValue();
    }

    @Override
    public EditorMap getConfigEditor(Object context, Object edtCtx) {
        if (configEditor == null) {
            configEditor = super.getConfigEditor(context, edtCtx);

            editorWidth = new EditorInt(1, 100, 1, 10, "Width", "The width of our rectangle");
            configEditor.addOption("w", editorWidth, false);

            editorHeight = new EditorInt(1, 100, 1, 10, "Height", "The height of our rectangle");
            configEditor.addOption("h", editorHeight, false);

            editorX = new EditorDouble(0, 1000, 0.1, 10, "X-Coordinate", "The X-Coordinate of the centre of the circle.");
            configEditor.addOption("x", editorX, false);

            editorY = new EditorDouble(0, 1000, 0.1, 10, "Y-Coordinate", "The Y-Coordinate of the centre of the circle.");
            configEditor.addOption("y", editorY, false);
        }
        return configEditor;
    }
}
