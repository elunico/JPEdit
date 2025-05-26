# JPEdit

This is a project that I began in 2019 to revisit Java after spending much time with Kotlin. I enjoy Kotlin a lot, but 
at the time that I started this project, I hadn't written Java in a while. As such, everything written directly in this 
project is done in Java. There are dependencies (namely TomUtils and TomUtilsJFX) that are not exclusively Java, but 
all the content directly in the project is. 

This editor is modeled on a simple, basic text editor, such as Notepad on Windows. In addition to the text editing, 
I wanted to try out the implementation of a plugin system for this editor. You can read more about that later.

This project started in Java 8 and has migrated through many versions of Java until its current state in Java 23. At one point,
I did start using Java Modules with this project but I had a ***lot*** of issues dealing with IntelliJ and the modules and 
dependencies. I abandoned the module system in Java shortly thereafter. To build this project, you do need to 
change the `modulePath` to include `javafx.controls` or the program will crash at runtime. 

## Plugins

JPEdit Supports plugins. Since this is really a hobby project that I wrote for the purpose of using Java, there is no official release, 
marketplace, or anything like that. You can download the source and build the project if you want to use JPEdit or develop any plugins. 
There is no central distribution method for plugins and they are formatted as normal JAR files. 

### Building Plugins
To build a plugin, I recommend using IntelliJ. You will need to build the JPEdit source and use it as a 
dependency. Currently, you have to depend on all the code in JPEdit. You can build the classes or build a JAR artifact 
using IntelliJ and depend on that. Once you have the JPEdit JAR, you need to create a new class that implements
`com.tom.jpedit.plugins.JPEditPlugin`. You will have access to much of JPEdit including the convenience class `JPPluginAPI`
as well as many other classes. Of particular note are the `JPEditWindow` class which you will interact with a lot, and the `ApplicationContext` class. 

If you want to know more about Plugins, read [this document](Plugins.md)