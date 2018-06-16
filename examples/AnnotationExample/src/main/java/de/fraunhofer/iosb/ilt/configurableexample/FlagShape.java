package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.AbstractConfigurable;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author scf
 */
public class FlagShape extends AbstractConfigurable<Object, Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FlagShape.class);

	@ConfigurableField(
			editor = EditorInt.class,
			label = "Width",
			description = "The width of our flag")
	@EditorInt.EdOptsInt(min = 1, max = 100, step = 1, dflt = 10)
	private int width;

	@ConfigurableField(
			editor = EditorInt.class,
			label = "Height",
			description = "The height of our flag")
	@EditorInt.EdOptsInt(min = 1, max = 100, step = 1, dflt = 10)
	private int height;

	@ConfigurableField(
			editor = EditorSubclass.class,
			label = "Shape",
			description = "The shape to put on the flag")
	@EditorSubclass.EdOptsSubclass(iface = Shape.class)
	private Shape shape;

	public void wave() {
		if (shape == null) {
			LOGGER.info("I'm waving a flag of {} by {}. It is plain.", width, height);
		} else {
			LOGGER.info("I'm waving a flag of {} by {}. It has a shape:", width, height);
			shape.paintMe();
		}
	}

}
