# Release Version 0.15

**Updates**
* Reworked the instantiation system to allow  for a static getConfigEditor method
  called getClassConfigEditor. This avoids unnecessary instantiation of the
  Configurable class.
* Added the option to configure the constructor of the Configurable class, so that
  the run-time context, the configuration or fields can be passed to the constructor. 
* Added the config editor as parameter to the configure() method, so that when a
  config editor is already present, the class does not have to create a new one
  just to configure itself.


# Release Version 0.14

**Updates**
* Introduced a ConfigurableFactory interface that can instantiate classes.
  If any of the contexts implement this interface, they will be used to
  create instances.
* If configuration of a class fails because a class can not be instantiated,
  the error is no longer silently ignored, but an exception is thrown.


# Release Version 0.13

**Updates**
* Updated version of Reflections library.


# Release Version 0.12

**Updates**
* Fixed build for Java 9+


# Release Version 0.11

**Updates**
* Added support for nested Lists.
* Added min and max item count options for Lists.
* Added "profiles" and annotations to configure them.
* Changed EditorDouble to text field, from spinner.
* Added editor for BigDecimal.
* Added input verification to Double and BigDecimal editors.
* Fixed Swing colour picker.


# Release Version 0.10

**Updates**
* Improved layout: Fixed adding empty FlowPane to EditorMap with no optionals.
* Use ChoiceBox instead of Spinner for FactoryEnumFx.


# Release Version 0.9

**Updates**
* EditorSubClass now also offers the configured superclass as an option, instead of only its subclasses.
* Editors can now be configured using annotations. See AnnotationExample for examples of how to use the annotations.


# Release Version 0.8

**Updates**
* Added some helper methods to EditorSubClass.


# Release Version 0.7

**Updates**
* Removed abstract classes from the implementation-selection list of EditorSubclass.


# Release Version 0.6

**Updates**
* The first properly released version.
