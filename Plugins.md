# Plugins for JPEdit 

### WARNING! Plugins are currently very experimental. There is no sandboxing or security involved and the API will likely change quite a bit or at least many times

#### You can see a sample plugin that is very simple [right here](https://gitlab.com/tpovinelli/jpeditexampleplugin).

## Plugins Now

Plugins can be made for JPEdit currently using the following process.

**Since this is still very experimental, this process will almost certainly change**.

### Steps Needed

*Note: throughout the tutorial, we will refer to a "main class" this class is the class which implements the `JPEditPlugin` interface*

#### Step 1 - Build the JPEdit Jar
The first thing you will need to do is get the JPEdit jar. You can obtain the code from [GitHub](https://github.com/elunico/jpedit)
and build it however you want. The recommended way is with [IntelliJ](https://www.jetbrains.com/idea/) as it is already an IntelliJ project.

#### Important Caveat
JavaFX is not. You will need to go [to this website](https://gluonhq.com/products/javafx/) to download JavaFX for 
Unfortunately, JavaFX is no longer bundled with the JDK and it is a platform specific dependency. Therefore, you will need to 
download JavaFX for your platform and add it as a dependency. I recommend opening all projects (JPEdit or any plugins
you want to use or create) in IntelliJ. You can 
then add the downloaded JavaFX jars as library dependencies in the project structure of your plugin in IntelliJ. 

#### Step 2 - Creating the Plugin Project
The next thing you will need a *new* Java project that depends on JPEdit and JavaFX. The recommended way to create a plugin is to create a new project with [IntelliJ](https://www.jetbrains.com/idea/).
Then you can obtain the latest JavaFX SDK [from this site](https://gluonhq.com/products/javafx/). You will also need to add this as a separate requirement to build your plugin.

You can add both the JavaFX jars and the JPEdit jar as dependencies in the Project Structure section of IntelliJ. (Again see the "important caveats" section above in step 1)

At this point you have the project set up, but in order to use it in JPEdit you will need to build a jar. For this you can use IntelliJ's "artifacts" section. Add a new artifact "from modules with dependencies" in the Project Structure setting of IntelliJ. 

Use the build menu to build the jar artifact (you will need to do this every time the code changes).

**Note: you can name your project anything you like, however you must ensure that your project name, JAR file name, package name, and Main class name are all the same meaning your project name should be a *valid Java Identifier***

#### Step 3 - Creating the Plugin Project Structure

Your plugin project must have a particular structure in order to be readable by JPEdit. 
~~Ever since 2022, JPEdit has been updated to work with the Java Module System introduced in Java 9. As such~~ You will need to ensure correct structure in your project to make your plugins work.

To start, you must create a package named `jpplugin`. In this package you need to create *another* package. This package should be named
based on your plugin. **It is REQUIRED that this second nested package have the _same_ name as your jar and your main class**

For example, if I wanted to create the sample plugin "Example Plugin" I would create the package structure `jpplugin.exampleplugin`.

Your main class **must** go in the root of the package `jpplugin.exampleplugin` or whatever you choose to name the second package. 

#### JPEdit used the Java Module system from 2022-2025. module-info.java is no longer required
~~#### Step 4 - Creating the `module-info.java` File~~

~~Since JPEdit is now compatible with the module system of Java, you must create–in the source root of your project—a `module-info.java`~~
~~file. You **must** export your `jpplugin.exampleplugin` package at least (again replacing `exampleplugin` with the name of your plugin that you created in step 3)~~

~~In addition, your module must `require jpedit` and will most likely have to require many `javafx.*` packages.~~

~~Here is an example `module-info.java`~~

```java
module ExamplePlugin {
  requires jpedit;

  requires javafx.base;
  requires javafx.controls;
  exports jpplugin.exampleplugin;
  opens jpplugin.exampleplugin;
}
```

#### Step 5 - Creating the Main class

The JPEdit Plugins do not have a main class in the traditional sense of `public static void main(String[] args){}` but rather, 
JPEdit allows for exactly 1 class per JAR file to implement the `JPEditPlugin` interface. This class is responsible for the 
core loading, window handling, and exiting code of the plugin. There is not limit to the structure or use of other 
classes within the project, however you must have exactly 1 class which implements `JPEditPlugin` and it must
be in the `jpplugin.exampleplugin` (or equivalently named package) package. 

You must implement the following methods. Note that are some additional methods for convenience that have empty default
implementations. You may choose to override these if you wish.


#### The `void onPluginLoad(List<JPEditWindow> windows) throws Exception` method

This method is called __only once__ when the plugin is loaded.
This method may throw any exception in the event that a plugin cannot load. It will be reported to the user
that the plugin cannot load and the exception will be logged

#### The `void onNewWindow(List<JPEditWindow> existingWindows, JPEditWindow newWindow)` method

This method is called for every plugin every time a new window is created.

#### The `void onWindowClose(List<JPEditWindow> existingWindows, JPEditWindow closingWindow)` method.

This method was actually added to the interface after initial publication and as such is a default method that performs a NOP if not implemented. 
It is optional. However, if you choose to implement it, it is called every time a Window is closed with the remaining windows in a list and the closing window passed separately. 
The `existingWindows` list may be empty. `closingWindow` will not be null.

#### The `void onExit()` method

This method is called **only once**
for every plugin that is loaded when the program is about to quit after all windows have been closed.
There are also some getter and setter methods for properties that can be added or injected.

#### The `PluginProperties pluginProperties()` method

This method is used to retrieve the plugin properties of the class
if they exist. It may return null if there are no properties to implement. 

#### Step 6 - Build the plugin
Now that your project has been set up and the interface implemented, you can build the artifact in IntelliJ that gives you a Jar for your plugin. Remember the JAR file **must have the exact same name as your Main class** though obviously the JAR file has a `.jar` extension and your class will not.

### Loading a JPEdit Plugin
Once you have your plugin built, you can load it. Launch JPEdit and find `add plugin (experimental)` in the `Advanced` menu. Find the JAR you just built of your plugin and load it. If it is successful, the window will disappear and you should see the properties loaded (if any). If it is not successful, a stack trace window will pop-up with the error that occurred. 


### Interacting with JPEdit in a Plugin

You can find a `add Plugin (experimental)` menu option under the `Advanced` menu of JPEdit to add your plugin jar.

You may include additional classes, methods, etc. but it is necessary that you have this class and this 
interface implemented at the very least.

### :rotating_light: Caveats :rotating_light:

#### Lifetimes 
The program instantiates an instance of your class and maintains a reference to it throughout the life
of the program until the last window is destroyed and the program quits. The program will call the
appropriate methods at the appropriate times as described above.

#### Dealing with events
Primarily, the best way to interact with the program is likely by setting EventHandlers on certain objects. 
As mentioned, this functionality will increase and change over time, probably by a lot. 
**NEVER use `setOnX` methods as these may overwrite critical functionality of JPEdit. 
ALWAYS use `addEventHandler(EventType type, EventHandler<?> handler)`**


#### Using `PluginProperties`

If your plugin requires the use of a keyboard shortcut, a menu item, or a toolbar button, you can implement those using the 
classes `PluginToolbarButton`, `PluginMenuItem`, and `PluginKeyboardShortcut`. An instance of these classes will define how those 
things will function for your plugin. To use these classes you must maintain a `PluginProperties` object.
It is necessary that this object be returned by `pluginProperties()`. Note that in order for this to function properly
you must ensure that `PluginMenuItem`, `PluginToolbarButton` and `PluginKeyboardShortcut` objects are unique for every
window. The way this is done is by calling `pluginProperties()` once for every existing window when the plugin loads
and then once on every plugin again when new window is created. The best way to accomplish this is to build
the objects within the `PluginProperties` object within this method itself. Do not cache these items in a field as this
will disable the plugin from being able to work with more than just the most recently created window. 
The `pluginProperties()` method is called after `onLoadPlugin(List<JPEditWindow> windows)`.

##### Important note:
If you provide a pluginProperties, the program will take care of loading
the button, MenuItem, and keyboard shortcuts on every window when the
plugin loads and on every subsequent created window while the plugin is
loaded. You should **not** write the code to add these things into 
the window in **any** of your implemented methods. This will be taken
care of by the program

## Future Goals

My goal is make easy-to-write and easy-to-use plugins. Many changes (and possibly the introduction of a separate API) 
will be needed for this. Things will likely change a lot as currently they are not very flexible. 

The most immediate change I would like to make is remembering which plugins are loaded and reloading them on start, 
being able to choose to stop loading plugins on start up.

***Stay tuned!***