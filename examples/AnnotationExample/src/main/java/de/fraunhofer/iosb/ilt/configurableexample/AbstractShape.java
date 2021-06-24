package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorColor;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorEnum;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;
import java.awt.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The abstract class that our shapes extend. It defines the common property
 * 'color' shared amongst all Shapes.
 *
 * @author scf
 */
public abstract class AbstractShape implements Shape {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractShape.class);

	public enum Pattern {
		PLAIN,
		STRIPED,
		STARRED
	}

	@ConfigurableField(editor = EditorSubclass.class, optional = true,
			label = "Shape",
			description = "The sub-shape to put on this Shape")
	@EditorSubclass.EdOptsSubclass(iface = Shape.class)
	protected Shape shape;

	@ConfigurableField(editor = EditorColor.class, optional = true,
			label = "Color",
			description = "The colour of the circle")
	@EditorColor.EdOptsColor(green = 255, editAlpha = false)
	private Color color;

	@ConfigurableField(editor = EditorEnum.class,
			label = "Pattern",
			description = "The pattern of the flag")
	@EditorEnum.EdOptsEnum(sourceType = Pattern.class)
	private Pattern pattern;

	public Color getColor() {
		return color;
	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public void paintMe() {
		if (shape != null) {
			LOGGER.info("I have a sub-shape!");
			shape.paintMe();
		}

	}
}
