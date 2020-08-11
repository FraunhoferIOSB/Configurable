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
@Flag.Public
@Flag.Internal
public class Triangle extends AbstractShape {

	private static final Logger LOGGER = LoggerFactory.getLogger(Triangle.class);

	@ConfigurableField(editor = EditorDouble.class,
			label = "X-Coordinate",
			description = "The X-Coordinate of the centre of the triangle.",
			optional = true)
	@EditorDouble.EdOptsDouble(min = 0, max = 1000, step = 0.1, dflt = 10)
	private double x;

	@ConfigurableField(editor = EditorDouble.class,
			label = "Y-Coordinate",
			description = "The Y-Coordinate of the centre of the triangle.",
			optional = true)
	@EditorDouble.EdOptsDouble(min = 0, max = 1000, step = 0.1, dflt = 10)
	private double y;

	@ConfigurableField(editor = EditorInt.class,
			label = "Side",
			description = "The length of a side of our triangle.")
	@EditorInt.EdOptsInt(min = 1, max = 100, step = 1, dflt = 10)
	private int side;

	@Override
	public void paintMe() {
		// paint to some device...
		LOGGER.info("I'm a triangle at {}, {} with side-length of {}, color {}, and pattern {}!", x, y, side, getColor(), getPattern());
	}

}
