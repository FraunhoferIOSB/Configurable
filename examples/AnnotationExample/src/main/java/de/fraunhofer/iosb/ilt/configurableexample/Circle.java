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
public class Circle extends AbstractShape {

	private static final Logger LOGGER = LoggerFactory.getLogger(Circle.class);

	@ConfigurableField(editor = EditorDouble.class,
			label = "X-Coordinate",
			description = "The X-Coordinate of the centre of the circle.")
	@EditorDouble.EdOptsDouble(min = 0, max = 1000, step = 0.1, dflt = 10)
	private double x;

	@ConfigurableField(editor = EditorDouble.class,
			label = "Y-Coordinate",
			description = "The Y-Coordinate of the centre of the circle.")
	@EditorDouble.EdOptsDouble(min = 0, max = 1000, step = 0.1, dflt = 10)
	private double y;

	@ConfigurableField(editor = EditorInt.class,
			label = "Radius",
			description = "The radius of our circle")
	@EditorInt.EdOptsInt(min = 1, max = 100, step = 1, dflt = 10)
	private int r;

	@Override
	public void paintMe() {
		// paint to some device...
		LOGGER.info("I'm a circle at {}, {} with radius {} and color {}!", x, y, r, getColor());
	}

}
