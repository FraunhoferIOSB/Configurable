# Configurable [![Build Status](https://travis-ci.org/FraunhoferIOSB/Configurable.svg?branch=master)](https://travis-ci.org/FraunhoferIOSB/Configurable)

A library for building classes that have a configuration GUI and can store/load this configuration to/from JSON.

Why Configurable?
1. Creating and Parsing the configuration is done by the class using the configuration.
2. The GUI used to edit the configuration is defined in the class using the configuration.
3. All configuration options are available, and discoverable, through the GUI.
4. The configuration is stored in simple JSON.

## Maven

Configurable is hosted on Bintray. If you add the bintray repository to your pom you are set:
```
    <repositories>
        <repository>
            <id>bintray-fraunhoferiosb-Maven</id>
            <url>https://dl.bintray.com/fraunhoferiosb/Maven</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>de.fraunhofer.iosb.ilt</groupId>
            <artifactId>Configurable</artifactId>
            <version>0.18</version>
        </dependency>
    </dependencies>
```

## Manual

There are two ways to use Configurable: direct, or using annotations. This manual
shows the direct method, since it shows the inner workings of the library much better
than the annotation method. It is also possible to mix the two methods.

In essence, Configurable is an interface. It revolves around classes, or instances of classes,
that can be configured with a JSON object, and that supply the GUI to generate this configuration.

The Configurable interface is very simple. It defines two methods:
```
/**
 * @param <C> The class type that provides context at runtime.
 * @param <D> The class type that provides context while editing.
 */
public interface Configurable<C, D> {

	/**
	 * Configure the instance using the given configuration.
	 *
	 * @param config The configuration to use for this instance.
	 * @param context the object that defines the context at runtime.
	 * @param edtCtx the object that defines the context while editing.
	 * @param configEditor optional {@code ConfigEditor} that may be used to
	 * access and assign configured contents.
	 * @throws ConfigurationException If the configuration can not be loaded.
	 */
	public void configure(JsonElement config, C context, D edtCtx, ConfigEditor<?> configEditor) throws ConfigurationException;

	/**
	 * Returns an editor for this class. Changing the configuration of this
	 * editor <em>may</em> change the configuration of the instance that
	 * generated the editor, but it is not guaranteed to do so.
	 *
	 * @param context the object that defines the context at runtime.
	 * @param edtCtx the object that defines the context while editing.
	 * @return A generic editor for any instance of this class.
	 */
	public ConfigEditor<?> getConfigEditor(C context, D edtCtx);
}
```

The method `Configure` takes a `JsonElement` containing the configuration to load,
while the method `getConfigEditor` returns an editor for editing this configuration.
There are two other parameters to both functions, that are coupled to the two
generics of the interface, but lets ignore those for now.

The interface ConfigEditor is a bit more involved. The important part is:

```
/**
 * @param <T> The type of object returned by getValue.
 */
public interface ConfigEditor<T> {

	/**
	 * Load the given configuration into this editor.
	 *
	 * @param config the configuration to load into this editor.
	 */
	public void setConfig(JsonElement config);

	/**
	 * Get the current (edited) state of the configuration.
	 *
	 * @return The current (edited) configuration.
	 */
	public JsonElement getConfig();

	/**
	 * Get the value configured in the editor.
	 *
	 * @return the value configured in the editor.
	 */
	public T getValue();

	/**
	 * Get a factory that can generate a swing-based gui for this editor.
	 *
	 * @return A factory that can generate a swing-based gui for this editor.
	 */
	public GuiFactorySwing getGuiFactorySwing();

	/**
	 * Get a factory that can generate a JavaFX-based gui for this editor.
	 *
	 * @return A factory that can generate a JavaFX-based gui for this editor.
	 */
	public GuiFactoryFx getGuiFactoryFx();
}
```

There are a few other methods, but they are not important for now.
The most important are `setConfig` and `getConfig`. They are used to save and load
configuration-JSON objects.
The next important methods are the two `getGuiFactory*` methods. They return the
GUI components that you display in your GUI application.
The last important method is `getValue`. The class of the returned Object is set by
the generic type variable `T`. It returns the value that the user set in the editor
using the GUI

### A super-simple example

You can find the source code of this example in the examples folder.

Lets say we have a class that draws a circle, with a certain radius.

```
public class Circle {

    private int r;

    public void paintMe() {
        // paint to some device...
        System.out.println("I'm a circle with radius " + r);
    }
}
```

We want to make this class Configurable, so that we can store circles in JSON, and
so we have a GUI for defining circles. First we make Circle implement Configurable:

```
public class Circle implements Configurable
```

Next we have to implement the method `public ConfigEditor getConfigEditor(Object context, Object edtCtx)`.
We only have one thing to configure, the radius. This radius is an integer, and lets
assume it has to be between 1 and 100. For configuring integer values, there is an
editor called `EditorInt` that displays a spinner control. It takes 4 parameters:
the minimum, maximum and step-size for the spinner, and the default value.

```
    public EditorInt getConfigEditor(Object context, Object edtCtx) {
        return new EditorInt(1, 100, 1, 10);
    }
```

Since we know our `getConfigEditor` method returns an EditorInt, we change the definition
of our method implementation.

Next the Configure method. This involves dealing with JSON... unless we have our
editor do that for us!

```
    public void configure(JsonElement config, Object context, Object edtCtx) {
        EditorInt editor = getConfigEditor(context, edtCtx);
        editor.setConfig(config);
        r = editor.getValue();
    }
```

The editor can parse the json that it generates, and it has a nice getValue() method
for accessing the currently configured value.

That's it. Our circle class is now Configurable. To display our editor in a GUI, we:
1. Create a new Circle
2. Get the editor from this Circle (and keep a reference to it)
3. Optionally load a configuration in this editor
4. Get the Swing Component or JavaFX Node from the editor
5. Add this Component or Node to our gui.

```
    private void addToGui(JPanel parentPanel) {
        Circle circle = new Circle();
        // You will need to keep a reference to this editor, to get the configuration
        // from it later.
        editor = circle.getConfigEditor(null, null);
        panelEditor.add(editor.getGuiFactorySwing().getComponent());
    }
```

To use the configuration:
```
    private void useConfig() {
        // Here we use the editor from above
        JsonElement config = editor.getConfig();
        Circle circle = new Circle();
        circle.configure(config, null, null);
        circle.paintMe();
    }
```

To save a configuration to a JSON string, or load a config from a JSON string.
```
    private void printConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // Here we use the editor from above
        JsonElement config = editor.getConfig();
        String jsonString = gson.toJson(config);
        LOGGER.info("Our configuration is:\n{}", jsonString);
    }

    public void loadConfig(String jsonString) {
        JsonElement config = new JsonParser().parse(jsonString);
        // Here we use the editor from above
        editor.setConfig(config);
    }
```

### Extending our example

Of course, most classes do not just have one parameter to configure, so lets extend
our Circle example to also configure the X and Y coordinates of the centre, and a
Color. Since we can return only one editor, we have to compose an editor from several
separate editors. For that we can use EditorMap.

EditorMap takes one or more child editors and lays them out in a simple grid. Child
editors can be optional. Optional child editors are not displayed by default, but
added to a list for the user to select from.

To be able to use our child editors in the Configure method, we make all the editors
class fields:

```
public class Circle implements Configurable {

    // SLF4J logger.
    private static final Logger LOGGER = LoggerFactory.getLogger(Circle.class);

    private double x;
    private double y;
    private int r;
    private Color color;

    private EditorMap configEditor;
    private EditorInt editorR;
    private EditorDouble editorX;
    private EditorDouble editorY;
    private EditorColor editorColor;

    public void paintMe() {
        // paint to some device...
        LOGGER.info("I'm a circle at {}, {} with radius {} and color {}!", x, y, r, color);
    }

    ...
}
```

Our method getConfigEditor fills those class fields, and returns the main editor
of typeEditorMap. Now we use the constructor with an added label and description
parameter. They are used by the EditorMap as label and tooltip for the child editors.

```
    public EditorMap getConfigEditor(Object context, Object edtCtx) {
        if (configEditor == null) {
            configEditor = new EditorMap();

            editorR = new EditorInt(1, 100, 1, 10, "Radius", "The radius of our circle");
            configEditor.addOption(
                "r",      // The name of the JSON field
                editorR,  // The child editor
                false     // Not Optional
            );

            editorX = new EditorDouble(0, 1000, 0.1, 10, "X-Coordinate", "The X-Coordinate of the centre of the circle.");
            configEditor.addOption("x", editorX, true);

            editorY = new EditorDouble(0, 1000, 0.1, 10, "Y-Coordinate", "The Y-Coordinate of the centre of the circle.");
            configEditor.addOption("y", editorY, true);

            editorColor = new EditorColor(Color.GREEN, false, "Color", "The colour of the circle");
            configEditor.addOption("color", editorColor, true);
        }
        return configEditor;
    }
```

The `addOption` method of the EditorMap is used to register the child editors with
the EditorMap. The first parameter is the name in the JSON configuration that the
child's configuration is stored under. This has to be unique for the EditorMap. The
second parameter is the child editor itself, while the third option is a flag
indicating that the child editor is optional.

Our Configure method now becomes:
```
    public void configure(JsonElement config, Object context, Object edtCtx) {
        getConfigEditor(context, edtCtx);
        configEditor.setConfig(config);
        r = editorR.getValue();
        x = editorX.getValue();
        y = editorY.getValue();
        color = editorColor.getValue();
    }
```

The important part here is that although we greatly extended our Circle class, we did
not have to change anything to the application that used the class. All the changes
to GUI, Loading and saving are confined to the class itself.

### Nesting a Configurable into another Configurable

Sometimes one of the fields of a Configurable class is another Configurable. For
instance, imagine we have a flag of certain dimensions, that has a circle painted
on it:
```
public class Flag implements Configurable<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Flag.class);
    private int width;
    private int height;
    private Circle circle;

    public void wave() {
        LOGGER.info("I'm waving a flag of {} by {}. It has a circle:", width, height);
        circle.paintMe();
    }
}
```

How do we create a ConfigEditor for this that re-uses our Circle ConfigEditor? That
is what the EditorClass is for. This editor injects a ConfigEditor into another
ConfigEditor:

```
    private EditorMap configEditor;
    private EditorInt editorWidth;
    private EditorInt editorHeight;
    private EditorClass<Object, Object, Circle> editorCircle;

    @Override
    public void configure(JsonElement config, Object context, Object edtCtx) {
        getConfigEditor(context, edtCtx);
        configEditor.setConfig(config);
        width = editorWidth.getValue();
        height = editorHeight.getValue();
        circle = editorCircle.getValue();
    }

    @Override
    public ConfigEditor<?> getConfigEditor(Object context, Object edtCtx) {
        if (configEditor == null) {
            configEditor = new EditorMap();

            editorWidth = new EditorInt(1, 100, 1, 10, "Width", "The width of our flag");
            configEditor.addOption("width", editorWidth, false);

            editorHeight = new EditorInt(1, 100, 1, 10, "Height", "The height of our flag");
            configEditor.addOption("height", editorHeight, false);

            editorCircle = new EditorClass(context, edtCtx, Circle.class, "circle", "The circle to put on the flag.");
            configEditor.addOption("circle", editorCircle, false);
        }
        return configEditor;
    }
```


### Choosing an implementation of an Interface

Imagine we not only have our Circle class, but also a Rectangle and Triangle class.
All these classes have a paintMe() method, so we make an interface Shape, that all
three implement. Now we need an editor, that allows the user to pick one of the
three implementations, and then configure it. This editor is the `EditorSubclass`.

Lets change our flag to accept a Shape instead of just a circle:

```
public class FlagShape implements Configurable<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagShape.class);
    private int width;
    private int height;
    private Shape shape;

    private EditorMap configEditor;
    private EditorInt editorWidth;
    private EditorInt editorHeight;
    private EditorSubclass<Object, Object, Shape> editorShape;

    public void wave() {
        LOGGER.info("I'm waving a flag of {} by {}. It has a shape:", width, height);
        shape.paintMe();
    }

    @Override
    public void configure(JsonElement config, Object context, Object edtCtx) {
        getConfigEditor(context, edtCtx);
        configEditor.setConfig(config);
        width = editorWidth.getValue();
        height = editorHeight.getValue();
        shape = editorShape.getValue();
    }

    @Override
    public ConfigEditor<?> getConfigEditor(Object context, Object edtCtx) {
        if (configEditor == null) {
            configEditor = new EditorMap();

            editorWidth = new EditorInt(1, 100, 1, 10, "Width", "The width of our flag");
            configEditor.addOption("width", editorWidth, false);

            editorHeight = new EditorInt(1, 100, 1, 10, "Height", "The height of our flag");
            configEditor.addOption("height", editorHeight, false);

            editorShape = new EditorSubclass<>(context, edtCtx, Shape.class, "Shape", "The shape to put on the flag.");
            configEditor.addOption("shape", editorShape, false);
        }
        return configEditor;
    }
```

### Configuring a list of the same item.

Now we do not just want one shape on our flag, but a list of shapes, as long as
the user wants. No problem, for this we have the EditorList!

Our third flag:
```
public class FlagShapeList implements Configurable<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagShapeList.class);
    private int width;
    private int height;
    private List<Shape> shapes;

    private EditorMap configEditor;
    private EditorInt editorWidth;
    private EditorInt editorHeight;
    private EditorList<Shape, EditorSubclass<Object, Object, Shape>> editorShapes;

    public void wave() {
        LOGGER.info("I'm waving a flag of {} by {}. It has shapes:", width, height);
        for (Shape shape : shapes) {
            shape.paintMe();
        }
    }

    @Override
    public void configure(JsonElement config, Object context, Object edtCtx) {
        getConfigEditor(context, edtCtx);
        configEditor.setConfig(config);
        width = editorWidth.getValue();
        height = editorHeight.getValue();
        shapes = editorShapes.getValue();
    }

    @Override
    public ConfigEditor<?> getConfigEditor(Object context, Object edtCtx) {
        if (configEditor == null) {
            configEditor = new EditorMap();

            editorWidth = new EditorInt(1, 100, 1, 10, "Width", "The width of our flag");
            configEditor.addOption("width", editorWidth, false);

            editorHeight = new EditorInt(1, 100, 1, 10, "Height", "The height of our flag");
            configEditor.addOption("height", editorHeight, false);

            EditorFactory<EditorSubclass<Object, Object, Shape>> factory;
            factory = () -> new EditorSubclass<>(context, edtCtx, Shape.class, "Shape", "A shape to put on the flag.");
            editorShapes = new EditorList<>(factory, "Shapes", "The shapes to put on the flag");
            configEditor.addOption("shape", editorShapes, false);
        }
        return configEditor;
    }
}
```

And the nice thing: If you add a shape class, it automatically appears in your configuration GUI!

### Inheriting options from super

All our shapes, Circle, Triangle and Rectangle, have a Color, but all three define
their own color field and editor. In this case it makes more sense to use an abstract
class `AbstractShape`, that defines the Color field. How do we deal with superclasses
that are Configurable?

First we define our AbstractShape class, just as we normally would, with an important
distinction: `getConfigEditor` specifically returns an `EditorMap`.

```
public abstract class AbstractShape implements Shape {

    private Color color;

    private EditorMap configEditor;
    private EditorColor editorColor;

    public Color getColor() {
        return color;
    }

    @Override
    public void configure(JsonElement config, Object context, Object edtCtx) {
        getConfigEditor(context, edtCtx);
        configEditor.setConfig(config);
        color = editorColor.getValue();
    }

    @Override
    public EditorMap getConfigEditor(Object context, Object edtCtx) {
        if (configEditor == null) {
            configEditor = new EditorMap();

            editorColor = new EditorColor(Color.GREEN, false, "Color", "The colour of the circle");
            configEditor.addOption("color", editorColor, true);
        }
        return configEditor;
    }

}
```

Our subclasses now have two small changes. In `getConfigEditor` they do not create
a new EditorMap themselves, they call `getConfigEditor` on the super class instead:

```
configEditor = super.getConfigEditor(context, edtCtx);
```

As a result, the EditorMap of the child classes already contains the fields that
are defined by the superclass.
Second, in their `configure` method, the child classes do not call `getConfigEditor`
nor `configEditor.setConfig´. Instead they let the superclass do this, by calling
`super.configure`.

```
super.configure(config, context, edtCtx);
```

As a result, adding any configuration options to AbstractShape automatically adds
those options to the child classes, including their GUI.

You can find the source code of this example in the examples folder.
