package de.fraunhofer.iosb.ilt.configurableexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;

import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.Configurable;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;

/**
 *
 * @author scf
 */
public class FlagShape implements Configurable<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagShape.class);
    private int width;
    private int height;
    private Shape shape;

    private EditorMap configEditor;
    private EditorInt editorWidth;
    private EditorInt editorHeight;
    private EditorSubclass<Object, Object, Shape> editorShape;

    public void wave() {
        LOGGER.info("I'm waving a flag of {} by {}. It has a shape:", width, height);
        shape.paintMe();
    }

    @Override
    public void configure(JsonElement config, Object context, Object edtCtx) {
        getConfigEditor(context, edtCtx);
        configEditor.setConfig(config);
        width = editorWidth.getValue();
        height = editorHeight.getValue();
        shape = editorShape.getValue();
    }

    @Override
    public ConfigEditor<?> getConfigEditor(Object context, Object edtCtx) {
        if (configEditor == null) {
            configEditor = new EditorMap();

            editorWidth = new EditorInt(1, 100, 1, 10, "Width", "The width of our flag");
            configEditor.addOption("width", editorWidth, false);

            editorHeight = new EditorInt(1, 100, 1, 10, "Height", "The height of our flag");
            configEditor.addOption("height", editorHeight, false);

            editorShape = new EditorSubclass<>(context, edtCtx, Shape.class, "Shape", "The shape to put on the flag.");
            configEditor.addOption("shape", editorShape, false);
        }
        return configEditor;
    }

}
