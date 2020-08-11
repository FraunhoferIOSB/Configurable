package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorDouble;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An example Configurable class.
 *
 * @author scf
 */
public class Rectangle extends AbstractShape {

	private static final Logger LOGGER = LoggerFactory.getLogger(Rectangle.class);

	@ConfigurableField(editor = EditorDouble.class,
			label = "X-Coordinate",
			description = "The X-Coordinate of the centre of the rectangle.",
			optional = true)
	@EditorDouble.EdOptsDouble(min = 0, max = 1000, step = 0.1, dflt = 10)
	private double x;

	@ConfigurableField(editor = EditorDouble.class,
			label = "Y-Coordinate",
			description = "The Y-Coordinate of the centre of the rectangle.",
			optional = true)
	@EditorDouble.EdOptsDouble(min = 0, max = 1000, step = 0.1, dflt = 10)
	private double y;

	@ConfigurableField(editor = EditorInt.class,
			label = "Width",
			description = "The width of our rectangle")
	@EditorInt.EdOptsInt(min = 1, max = 100, step = 1, dflt = 10)
	private int width;

	@ConfigurableField(editor = EditorInt.class,
			label = "Height",
			description = "The height of our rectangle")
	@EditorInt.EdOptsInt(min = 1, max = 100, step = 1, dflt = 10)
	private int height;

	@Override
	public void paintMe() {
		// paint to some device...
		LOGGER.info("I'm a Rectangle at {}, {} with width {}, height {}, color {}, and pattern {}!", x, y, width, height, getColor(), getPattern());
	}

}
