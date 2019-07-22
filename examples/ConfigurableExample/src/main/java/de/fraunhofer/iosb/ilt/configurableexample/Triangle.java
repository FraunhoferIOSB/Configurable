package de.fraunhofer.iosb.ilt.configurableexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;

import de.fraunhofer.iosb.ilt.configurable.editor.EditorDouble;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;

/**
 * An example Configurable class.
 *
 * @author scf
 */
public class Triangle extends AbstractShape {

    private static final Logger LOGGER = LoggerFactory.getLogger(Triangle.class);
    private double x;
    private double y;
    private int side;

    private EditorMap configEditor;
    private EditorInt editorSide;
    private EditorDouble editorX;
    private EditorDouble editorY;

    @Override
    public void paintMe() {
        // paint to some device...
        LOGGER.info("I'm a triangle at {}, {} with side-length of {} and color {}!", x, y, side, getColor());
    }

    @Override
    public void configure(JsonElement config, Object context, Object edtCtx, ConfigEditor<?> configEditor) {
        super.configure(config, context, edtCtx, configEditor);
        side = editorSide.getValue();
        x = editorX.getValue();
        y = editorY.getValue();
    }

    @Override
    public EditorMap getConfigEditor(Object context, Object edtCtx) {
        if (configEditor == null) {
            configEditor = super.getConfigEditor(context, edtCtx);

            editorSide = new EditorInt(1, 100, 1, 10, "Side", "The length of a side of our triangle.");
            configEditor.addOption("side", editorSide, false);

            editorX = new EditorDouble(0, 1000, 0.1, 10, "X-Coordinate", "The X-Coordinate of the centre of the circle.");
            configEditor.addOption("x", editorX, true);

            editorY = new EditorDouble(0, 1000, 0.1, 10, "Y-Coordinate", "The Y-Coordinate of the centre of the circle.");
            configEditor.addOption("y", editorY, true);
        }
        return configEditor;
    }
}
