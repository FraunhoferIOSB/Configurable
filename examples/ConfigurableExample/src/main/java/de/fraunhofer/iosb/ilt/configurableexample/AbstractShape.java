package de.fraunhofer.iosb.ilt.configurableexample;

import java.awt.Color;

import com.google.gson.JsonElement;

import de.fraunhofer.iosb.ilt.configurable.editor.EditorColor;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;

/**
 * The abstract class that our shapes extend. It defines the common property 'color' shared amongst all Shapes.
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
    public void configure(JsonElement config, Object context, Object edtCtx) {
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
