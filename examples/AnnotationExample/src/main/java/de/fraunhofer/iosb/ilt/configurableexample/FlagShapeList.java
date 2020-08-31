package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.AbstractConfigurable;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorBigDecimal;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorBoolean;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorDouble;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorList;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorLong;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorString;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;
import java.math.BigDecimal;
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
			editor = EditorInt.class, optional = true,
			label = "Height",
			description = "The height of our flag")
	@EditorInt.EdOptsInt(min = 1, max = 100, step = 1, dflt = 10)
	private Integer height;

	@ConfigurableField(
			editor = EditorList.class,
			label = "Shapes",
			description = "The shapes to put on the flag")
	@EditorList.EdOptsList(editor = EditorSubclass.class)
	@EditorSubclass.EdOptsSubclass(
			iface = Shape.class,
			allowList = {Flag.Public.class, Flag.Internal.class},
			denyList = {Flag.Private.class},
			restrictedClasses = true
	)
	private List<Shape> shapes = Collections.emptyList();

	@ConfigurableField(
			editor = EditorBoolean.class,
			label = "Cloth",
			description = "Is this flag made of cloth?")
	@EditorBoolean.EdOptsBool(dflt = true)
	private boolean cloth;

	@ConfigurableField(editor = EditorString.class, optional = true,
			label = "textNonNull", description = "A text with non-null default.")
	@EditorString.EdOptsString(dflt = "My Default Value")
	private String textNonNull;

	@ConfigurableField(editor = EditorString.class, optional = true,
			label = "textNull", description = "A text with null default")
	@EditorString.EdOptsString(dfltIsNull = true)
	private String textNull;

	@ConfigurableField(editor = EditorString.class, optional = true,
			label = "textNullValue", description = "A text with null default and an initialised value")
	@EditorString.EdOptsString(dfltIsNull = true)
	private String textNullValue = "Initialised Value";

	@ConfigurableField(
			editor = EditorBoolean.class, optional = true,
			label = "booleanNonNull", description = "A boolean that can not be null")
	@EditorBoolean.EdOptsBool()
	private boolean boolNonNull;
	@ConfigurableField(
			editor = EditorBoolean.class, optional = true,
			label = "booleanNull", description = "A Boolean that can be null")
	@EditorBoolean.EdOptsBool(dfltIsNull = true)
	private Boolean boolNull;

	@ConfigurableField(
			editor = EditorInt.class, optional = true,
			label = "intNonNull",
			description = "An int that can not be null")
	@EditorInt.EdOptsInt()
	private int intNonNull;

	@ConfigurableField(
			editor = EditorInt.class, optional = true,
			label = "intNull",
			description = "An Integer that can be null")
	@EditorInt.EdOptsInt(dfltIsNull = true)
	private Integer intNull;

	@ConfigurableField(
			editor = EditorLong.class, optional = true,
			label = "longNonNull",
			description = "A long that can not be null")
	@EditorLong.EdOptsLong()
	private long longNonNull;

	@ConfigurableField(
			editor = EditorLong.class, optional = true,
			label = "LongNull",
			description = "An Long that can be null")
	@EditorLong.EdOptsLong(dfltIsNull = true)
	private Long longNull;

	@ConfigurableField(
			editor = EditorDouble.class, optional = true,
			label = "doubleNonNull",
			description = "A double that can not be null")
	@EditorDouble.EdOptsDouble()
	private double doubleNonNull;

	@ConfigurableField(
			editor = EditorDouble.class, optional = true,
			label = "DoubleNull",
			description = "A Double that can be null")
	@EditorDouble.EdOptsDouble(dfltIsNull = true)
	private Double doubleNull;

	@ConfigurableField(
			editor = EditorBigDecimal.class, optional = true,
			label = "bdNonNull",
			description = "A BigDecimal that can not be null")
	@EditorBigDecimal.EdOptsBigDecimal()
	private BigDecimal bdNonNull;

	@ConfigurableField(
			editor = EditorBigDecimal.class, optional = true,
			label = "bdNull",
			description = "A BigDecimal that can be null")
	@EditorBigDecimal.EdOptsBigDecimal(dfltIsNull = true)
	private BigDecimal bdNull;

	public void wave() {
		LOGGER.info("I'm waving a flag of {} by {}. It is made of cloth: {}. It has shapes:", width, height, cloth);
		for (Shape shape : shapes) {
			shape.paintMe();
		}
		LOGGER.info("textNonNull: {}", textNonNull);
		LOGGER.info("textNull: {}", textNull);
		LOGGER.info("textNullValue: {}", textNullValue);
		LOGGER.info("bdNonNull: {}", bdNonNull);
		LOGGER.info("bdNull: {}", bdNull);
		LOGGER.info("boolNonNull: {}", boolNonNull);
		LOGGER.info("boolNull: {}", boolNull);
		LOGGER.info("doubleNonNull: {}", doubleNonNull);
		LOGGER.info("doubleNull: {}", doubleNull);
		LOGGER.info("intNonNull: {}", intNonNull);
		LOGGER.info("intNull: {}", intNull);
		LOGGER.info("longNonNull: {}", longNonNull);
		LOGGER.info("longNull: {}", longNull);
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
