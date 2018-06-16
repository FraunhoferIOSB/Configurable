package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.AbstractConfigurable;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorColor;
import java.awt.Color;

/**
 * The abstract class that our shapes extend. It defines the common property
 * 'color' shared amongst all Shapes.
 *
 * @author scf
 */
public abstract class AbstractShape extends AbstractConfigurable<Void, Void> implements Shape {

	@ConfigurableField(editor = EditorColor.class,
			label = "Color",
			description = "The colour of the circle",
			optional = true)
	@EditorColor.EdOptsColor(green = 255, editAlpha = false)
	private Color color;

	public Color getColor() {
		return color;
	}

}
