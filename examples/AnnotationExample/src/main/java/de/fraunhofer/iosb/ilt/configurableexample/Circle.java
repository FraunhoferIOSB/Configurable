package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.ContentConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.annotations.AnnotationHelper;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableConstructor;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableParameter;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableParameter.ConfigurableParameterType;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorDouble;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;

import com.google.gson.JsonElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An example Configurable class.
 *
 * @author scf
 */
public class Circle extends AbstractShape {

    private static final Logger LOGGER = LoggerFactory.getLogger(Circle.class);

    @ConfigurableField(editor = EditorDouble.class, label = "X-Coordinate", description = "The X-Coordinate of the centre of the circle.")
    @EditorDouble.EdOptsDouble(min = 0, max = 1000, step = 0.1, dflt = 10)
    private double x;

    @ConfigurableField(editor = EditorDouble.class, label = "Y-Coordinate", description = "The Y-Coordinate of the centre of the circle.")
    @EditorDouble.EdOptsDouble(min = 0, max = 1000, step = 0.1, dflt = 10)
    private double y;

    @ConfigurableField(editor = EditorInt.class, label = "Radius", description = "The radius of our circle")
    @EditorInt.EdOptsInt(min = 1, max = 100, step = 1, dflt = 10)
    private int r;


    @ConfigurableConstructor
    public Circle(@ConfigurableParameter(type = ConfigurableParameterType.RUNTIME_CONTEXT) final Void runtimeContext, @ConfigurableParameter(type = ConfigurableParameterType.CLASS_CONFIG) final JsonElement classConfig, @ConfigurableParameter(jsonField = "shape") final Shape nestedShape) {
        shape = nestedShape;
    }


    public static ContentConfigEditor<?> getSingeltonConfigEditor(final Void context, final Void edtCtx) {
        return AnnotationHelper.generateEditorFromAnnotations(Circle.class, context, edtCtx).get();
    }


    @Override
    public ConfigEditor<?> getConfigEditor(final Void context, final Void edtCtx) {
        throw new UnsupportedOperationException("force usage of singelton config editor");
    }


    @Override
    public void paintMe() {
        // paint to some device...
        LOGGER.info("I'm a circle at {}, {} with radius {} and color {}!", x, y, r, getColor());
        super.paintMe();
    }

}
