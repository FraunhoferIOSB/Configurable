package de.fraunhofer.iosb.ilt.configurableexample;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;

import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.Configurable;
import de.fraunhofer.iosb.ilt.configurable.ConfigurationException;
import de.fraunhofer.iosb.ilt.configurable.EditorFactory;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorList;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;

/**
 *
 * @author scf
 */
public class FlagShapeList implements Configurable<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagShapeList.class);
    private int width;
    private int height;
    private List<Shape> shapes;

    private EditorMap configEditor;
    private EditorInt editorWidth;
    private EditorInt editorHeight;
    private EditorList<Shape, EditorSubclass<Object, Object, Shape>> editorShapes;

    public void wave() {
        LOGGER.info("I'm waving a flag of {} by {}. It has shapes:", width, height);
        for (Shape shape : shapes) {
            shape.paintMe();
        }
    }

    @Override
    public void configure(JsonElement config, Object context, Object edtCtx) throws ConfigurationException {
        getConfigEditor(context, edtCtx);
        configEditor.setConfig(config);
        width = editorWidth.getValue();
        height = editorHeight.getValue();
        shapes = editorShapes.getValue();
    }

    @Override
    public ConfigEditor<?> getConfigEditor(Object context, Object edtCtx) {
        if (configEditor == null) {
            configEditor = new EditorMap();

            editorWidth = new EditorInt(1, 100, 1, 10, "Width", "The width of our flag");
            configEditor.addOption("width", editorWidth, false);

            editorHeight = new EditorInt(1, 100, 1, 10, "Height", "The height of our flag");
            configEditor.addOption("height", editorHeight, false);

            EditorFactory<EditorSubclass<Object, Object, Shape>> factory;
            factory = () -> new EditorSubclass<>(context, edtCtx, Shape.class, "Shape", "A shape to put on the flag.");
            editorShapes = new EditorList<>(factory, "Shapes", "The shapes to put on the flag");
            configEditor.addOption("shape", editorShapes, false);
        }
        return configEditor;
    }

}
