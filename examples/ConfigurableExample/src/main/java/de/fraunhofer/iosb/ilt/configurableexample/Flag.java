package de.fraunhofer.iosb.ilt.configurableexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;

import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.Configurable;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorClass;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;

/**
 *
 * @author scf
 */
public class Flag implements Configurable<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Flag.class);
    private int width;
    private int height;
    private Circle circle;

    private EditorMap configEditor;
    private EditorInt editorWidth;
    private EditorInt editorHeight;
    private EditorClass<Object, Object, Circle> editorCircle;

    public void wave() {
        LOGGER.info("I'm waving a flag of {} by {}. It has a circle:", width, height);
        circle.paintMe();
    }

    @Override
    public void configure(JsonElement config, Object context, Object edtCtx) {
        getConfigEditor(context, edtCtx);
        configEditor.setConfig(config);
        width = editorWidth.getValue();
        height = editorHeight.getValue();
        circle = editorCircle.getValue();
    }

    @Override
    public ConfigEditor<?> getConfigEditor(Object context, Object edtCtx) {
        if (configEditor == null) {
            configEditor = new EditorMap();

            editorWidth = new EditorInt(1, 100, 1, 10, "Width", "The width of our flag");
            configEditor.addOption("width", editorWidth, false);

            editorHeight = new EditorInt(1, 100, 1, 10, "Height", "The height of our flag");
            configEditor.addOption("height", editorHeight, false);

            editorCircle = new EditorClass(context, edtCtx, Circle.class, "circle", "The circle to put on the flag.");
            configEditor.addOption("color", editorCircle, false);
        }
        return configEditor;
    }

}
