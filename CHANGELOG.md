# Release Version 0.33

**Updates**
* Improved reliability of ordering of items in generated JSON.


# Release Version 0.32

**Updates**
* Switched back to the original Reflections library.
* Made JavaFX dependency `provided`, so JavaFX projects should include it themselves.


# Release Version 0.31

**Updates**
* Added JSON Schema generation. The schema can be used to generate an editor using
  for instance [JSON Editor](https://github.com/json-editor/json-editor).
* Updated dependencies.


# Release Version 0.30

**Updates**
* Changed distribution to oss.sonatype.org / maven central.
* Updated dependencies.


# Release Version 0.29

**Updates**
* Fixed Boolean values not being transferred to the GUI.


# Release Version 0.28

**Updates**
* All (simple) editors can now have a NULL default value, by setting the
  dfltIsNull annotation. Editors not configured using annotations could already
  accept a null dflt.


# Release Version 0.27

**Updates**
* Fixed allow/deny list annotations not having empty default values.


# Release Version 0.26

**Updates**
* Added allowList and denyList of annotations to EditorSubClass, to filter the
  list of classes presented to the user.
* Allow an empty default value for EditorEnum, mapped to null.
* Added an option to EditorSubClass to allow a user to type in any class name,
  regardless of the allow/deny lists. In this case the allow/deny list will only
  be used to filter the suggested class names.


# Release Version 0.25

**Updates**
* Fixed some editors not being reset when setting a null configuration.


# Release Version 0.24

**Updates**
* Changed default setting of EditorSubclass.shortenClassNames to false.
* Fixed Java 8 compatibility.


# Release Version 0.23

**Updates**
* Fixed EditorClass not working correctly with annotations.
* Changed search for ConfigurableClass annotation to include interfaces. 


# Release Version 0.22

**Updates**
* Fixed EditorColor in FX.
* Updated depencencies.


# Release Version 0.21

**Updates**
* Added an editor for passwords. It hides the typed text in the GUI, but still puts
  the password in the JSON as plain text.


# Release Version 0.20

**Updates**
* Fixed running with java -jar jarfile.jar


# Release Version 0.19

**Updates**
* Fixed behaviour in Windows look and feel of Swing.
* Improved behaviour when selecting text with keys.


# Release Version 0.18

**Updates**
* Changed the ComboBox of EditorSubClass to a type that can filter.
* Added option to EditorSubClass to not shorten class names.


# Release Version 0.17

**Updates**
* Fixed Swing EditorColor misbehaving.
* Added isOptionSet method to ContentConfigEditor interface.


# Release Version 0.16

**Updates**
 * Fixed NoSuchElementException for old-style configurables.


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
