package com.tom.jpedit.plugins;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.plugins.components.PluginKeyboardShortcut;
import com.tom.jpedit.plugins.components.PluginMenuItem;
import com.tom.jpedit.plugins.components.PluginToolbarButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface JPEditPlugin {

  /**
   * This method is called <b>exactly once</b> when the plugin is loaded.
   * It is passed a list of all the {@link JPEditWindow} instances
   * that currently exist. The class that implements this
   * method is reflectively constructed and this method is called,
   * <b>however</b> before this method is called, the properties
   * returned by {@link #pluginProperties()} are loaded.
   * <p>
   * The {@link #pluginProperties()} method can return null on which case
   * nothing happens. Any property can also be null. Once all the
   * properties are dealt with this method is called.
   * <p>
   * It is appropriate to do and expected that all plugin initialization
   * will take place in this method such as opening or reading files
   * or preferences and creating and starting any workers or threads
   * <p>
   * This method can throw any exception if the initialization of a plugin fails
   * A message will be shown to the user indicating your class failed to load
   * and the exception thrown will be written to the error log
   *
   * @param windows the list of currently existing JPEditWindows
   * @throws Exception according to implementation if the plugin fails to load
   * @see PluginProperties
   * @see #pluginProperties()
   * @see JPEditWindow
   */
  void onPluginLoad(@NotNull List<JPEditWindow> windows) throws Exception;

  /**
   * This method is called <b>on every plugin</b> that is loaded one time whenever,
   * and each time a new JPEditWindow is created.
   *
   * @param existingWindows a list of all the JPEditWindows that existed <b>before</b>
   *                        the creation of the new window
   * @param newWindow       the new JPEditWindow instance that was created
   */
  void onNewWindow(@NotNull List<JPEditWindow> existingWindows, @NotNull JPEditWindow newWindow);


  /**
   * This method is called on every JPEditPlug that has been loaded each time
   * when a window is destroyed. It gives the plugin the opportunity to address
   * a particular window before it is destroyed without requiring the entire
   * application to quit. The `windows` list may be empty if it is the last
   * window being destroyed.
   * <p>
   * The <code>closingWindow</code> is not hidden or closed until *after* this method
   * is called on it. It is still visible when this method is called on it
   * <p>
   * Because this method was introduced long after the initial API, it is
   * a default method that by default takes no action. Existing plugins
   * and new ones can take advantage of this method if they'd like.
   *
   * @param windows       currently all the windows that are open and staying open (might be empty)
   * @param closingWindow the window that is being destroyed
   */
  default void onWindowClose(@NotNull List<JPEditWindow> windows, @NotNull JPEditWindow closingWindow) {

  }

  /**
   * This method is called to retrieve an instance of {@link PluginProperties}
   * that describes the properties of this plugin. This method may return null.
   * <p>
   * If you are using the {@link PluginProperties} method then it is important
   * that each JPEditWindow gets a new instance of {@link PluginKeyboardShortcut},
   * {@link PluginMenuItem}, and {@link PluginToolbarButton}. Any sharing between
   * instances will deprive all but one of the windows that are sharing from being
   * able to use the items.
   * <p>
   * The best way to accomplish this uniqueness is to construct each object
   * inside this method so that each time it is called a new PluginMenuItem,
   * PluginToolbarButton, etc. are created. This method is called once for each
   * existing window when the plugin loads and once again on every plugin when
   * a new window is created. Caching PluginProperties object to return every
   * time will not work as multiple windows cannot have the same instances
   * of these objects.
   * <p>
   * If you provide a pluginProperties, the program will take care of loading
   * the button, MenuItem, and keyboard shortcuts on every window when the
   * plugin loads and on every subsequent created window while the plugin is
   * loaded. You should <b>not</b> write the code to add these things into
   * the window in <b>any</b> of your implemented methods. This will be taken
   * care of by the program
   *
   * @return the {@link PluginProperties} object representing the
   * properties of this plugin or null if no properties exist
   * @see PluginProperties
   */
  @Nullable
  PluginProperties pluginProperties();

  /**
   * This method is called on the plugin when the user opts to remove it, but the application is not closing.
   * This is called on the plugin <b>before</b> the buttons, shortcuts, and menu items are removed from the program.
   * <p>
   * after this method completes the objects in {@link #pluginProperties()} are removed from the program.
   * <p>
   * By default this method calls {@link #onExit()} to allow for backwards-compatibility with existing plugins.
   * The onExit behavior is most similar to this method. The default assuption is that whatever the plugin would do on
   * exit of the program is approximately what it should do here, but the method can be implemented to customize this.
   * <p>
   * <b>It should be noted that the program will take of calling unregister methods and removing the various
   * items included in PluginProperties. This method and the plugin itself should not attempt to manipulate the
   * ApplicationContext or individual windows during this method. It is best to save whatever user data is needed and
   * return from the method.</b>
   *
   * @param windows the list of currently open windows
   */
  default void onRemoved(List<JPEditWindow> windows) {
    onExit();
  }

  /**
   * This method is called <b>exactly once</b> on every plugin when JPEditWindow
   * is about to quit and <b>after</b> every existing JPEditWindow has been
   * {@link JPEditWindow#close() closed}
   * <p>
   * This method should do all the necessary tearing down and de-initialization
   * of the plugin such as saving files or settings and ending workers or threads.
   */
  void onExit();

}
