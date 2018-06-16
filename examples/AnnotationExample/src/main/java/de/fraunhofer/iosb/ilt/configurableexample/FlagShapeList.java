package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.AbstractConfigurable;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorList;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author scf
 */
public class FlagShapeList extends AbstractConfigurable<Object, Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FlagShapeList.class);

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
			editor = EditorList.class,
			label = "Shapes",
			description = "The shapes to put on the flag")
	@EditorList.EdOptsList(editor = EditorSubclass.class)
	@EditorSubclass.EdOptsSubclass(iface = Shape.class)
	private List<Shape> shapes = Collections.emptyList();

	public void wave() {
		LOGGER.info("I'm waving a flag of {} by {}. It has shapes:", width, height);
		for (Shape shape : shapes) {
			shape.paintMe();
		}
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

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the shapes
	 */
	public List<Shape> getShapes() {
		return shapes;
	}

	/**
	 * @param shapes the shapes to set
	 */
	public void setShapes(List<Shape> shapes) {
		this.shapes = shapes;
	}

}
