package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.AbstractConfigurable;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorClass;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author scf
 */
public class Flag extends AbstractConfigurable<Object, Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Flag.class);

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
			editor = EditorClass.class,
			label = "Circle",
			description = "The circle to put on the flag")
	@EditorClass.EdOptsClass(clazz = Circle.class)
	private Circle circle;

	public void wave() {
		LOGGER.info("I'm waving a flag of {} by {}. It has a circle:", width, height);
		circle.paintMe();
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

}
