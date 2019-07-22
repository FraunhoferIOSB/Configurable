package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorColor;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;

/**
 * The abstract class that our shapes extend. It defines the common property
 * 'color' shared amongst all Shapes.
 *
 * @author scf
 */
public abstract class AbstractShape implements Shape {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractShape.class);

    @ConfigurableField(editor = EditorSubclass.class, label = "Shape", description = "The sub-shape to put on this Shape")
    @EditorSubclass.EdOptsSubclass(iface = Shape.class)
    protected Shape shape;

	@ConfigurableField(editor = EditorColor.class,
			label = "Color",
			description = "The colour of the circle",
			optional = true)
	@EditorColor.EdOptsColor(green = 255, editAlpha = false)
	private Color color;

	public Color getColor() {
		return color;
	}


    @Override
    public void paintMe() {
        if (shape != null) {
            LOGGER.info("I have a sub-shape!");
            shape.paintMe();
        }

    }
}
