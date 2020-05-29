package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.AbstractConfigurable;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorBoolean;
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

	@ConfigurableField(
			editor = EditorBoolean.class,
			label = "Cloth",
			description = "Is this flag made of cloth?")
	@EditorBoolean.EdOptsBool(dflt = true)
	private boolean cloth;

	public void wave() {
		LOGGER.info("I'm waving a flag of {} by {}. It is made of cloth: {}. It has a circle:", width, height, cloth);
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
