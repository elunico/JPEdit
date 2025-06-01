package com.tom.jpedit.plugins;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.confirmation.ConfirmationDialog;
import com.tom.jpedit.gui.confirmation.ConfirmationType;
import com.tom.jpedit.util.LoadedJPPlugin;
import com.tom.jpedit.util.UserPreferences;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tom.javafx.JavaFXUtilsKt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * This class helps plugins interface with JPEdit. Rather than having unfettered access to all internal APIs
 * this class provides a guaranteed way to interact with the application that will not cause conflicts or issues.
 * <p>
 * For example, there are methods in {@link ApplicationContext} that should not be called by plugins but, the entire
 * ApplicationContext class is exposed due to constraints in visibility.
 * <p>
 * The true remedy for this is Java Modules which will completely limit access to ApplicationContext. However,
 * this class will serve as an intermediary, ironing out the proper public UI while leaving ApplicationContext exposed
 * so as to not break any existing plugins. However in the future Modules will be used and all access will have to be
 * through designated APIs like this class as other classes will be inaccessible
 */
public class JPPluginAPI {
    /**
     * Convenience method that uses {@link #getFileForPlugin(Class, Path)} to obtain a file and then reads the entire contents
     * of the file into a string and returns that string to the caller
     *
     * @param pluginClass your class that implements JPEditPlugin
     * @param path        the path you would like to write to
     * @param charset     charset for file contents. if not needed use {@link Charset#defaultCharset()}
     * @return the contents of the file as a string
     * @throws IOException if the file does not exist or cannot be read or written
     */
    public static @NotNull String getFileContentForPlugin(
            Class<? extends JPEditPlugin> pluginClass,
            @NotNull Path path,
            Charset charset
    ) throws IOException {
        File target = getFileForPlugin(pluginClass, path);
        BufferedReader reader = new BufferedReader(new FileReader(target, charset));
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Requests a file from the Application for use by the plugin. The file returned is read/writeable or else
     * an IOException is thrown. The path provided cannot be absolute, to a directory, or inaccessible or an IOException
     * is thrown.
     * <p>
     * If this method returns successfully with a File object, then that file is guaranteed to exist and be read/writeable
     *
     * @param pluginClass your class that implements JPEditPlugin
     * @param path        the path you would like to write to
     * @return a {@link File} object which represents the file which your plugin has rights to use for whatever purpose it deems fit
     * @throws IOException if the {@param path} parameter is an absolute path or not read/writeable
     */
    public static @NotNull File getFileForPlugin(Class<? extends JPEditPlugin> pluginClass, @NotNull Path path)
            throws IOException {
        if (path.isAbsolute()) {
            throw new IOException("Path " + path + " is not a valid path. It is an absolute path");
        }

        if (path.endsWith(File.separator)) {
            throw new IOException("Path " + path + " is not a valid path. It is a directory");
        }

        Path pluginDir = Paths.get(System.getProperty("user.dir"), pluginClass.getPackageName());
        File pluginDirFile = pluginDir.toFile();
        if (!pluginDirFile.isDirectory() && !pluginDirFile.exists()) {
            if (!pluginDirFile.mkdirs() || (pluginDirFile.exists() && !pluginDirFile.isDirectory())) {
                throw new IOException("Could not create directory " + pluginDir + " for plugin " + pluginClass.getName());
            }
        }
        File file = pluginDir.resolve(path).toFile();
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Could not create requested file " + file + " for plugin " + pluginClass.getName());
        }
        return file;
    }

    /**
     * Convenience method to set preferences for this plugin only to ensure that the plugin is not trampling built-in
     * preferences or the preferences of other plugins. Uses {@link #pluginQualifiedName(Class)} to avoid collisions
     *
     * @param mainClass the main plugin class implementing {@link JPEditPlugin}
     * @param property  the name of the preference to save
     * @param value     the value of the saved preference
     */
    public static void setLocalUserPreference(
            @NotNull Class<? extends JPEditPlugin> mainClass,
            String property,
            String value
    ) {
        getUserPreferences().setProperty(localKeyName(mainClass, property), value);
    }

    /**
     * Get the users saved preferences Object. See {@link UserPreferences}.
     * <p>
     * Generally this method can be used for getting and modifying Application default settings-namely ones that already
     * exist in the program. If you want to set new settings specific to your plugin, it is better to call
     * {@link #setLocalUserPreference(Class, String, String)} since this method sets a preference with the plugin package
     * name prepended to the key to avoid trampling other plugins' or future built-in preference keys
     *
     * @return User's preferences
     */
    public static UserPreferences getUserPreferences() {
        return ApplicationContext.getContext().getUserPreferences();
    }

    @NotNull
    public static String localKeyName(@NotNull Class<? extends JPEditPlugin> mainClass, String property) {
        return String.join(pluginQualifiedName(mainClass), "-", property);
    }

    @Contract(pure = true)
    public static @NotNull String pluginQualifiedName(@NotNull Class<? extends JPEditPlugin> mainClass) {
        return mainClass.getPackageName();
    }

    /**
     * Get an Application-wide property
     *
     * @param property the name of the property to retrieve
     * @return the Property value or null if it doesn't exist
     */
    public static String getProperty(String property) {
        return ApplicationContext.getContext().getProperties().getProperty(property);
    }

    /**
     * Get a property set by or local to your Plugin
     *
     * @param property the name of the property to retrieve
     * @return the Property value or null if it doesn't exist
     */
    public static String getLocalProperty(@NotNull Class<? extends JPEditPlugin> mainClass, String property) {
        return ApplicationContext.getContext()
                                 .getProperties()
                                 .getProperty(localKeyName(mainClass, property));
    }

    /**
     * Set a property  local to your Plugin
     *
     * @param property the name of the property to set
     * @param value    the value of the property
     */
    public static void setLocalProperty(
            @NotNull Class<? extends JPEditPlugin> mainClass,
            String property,
            String value
    ) {
        ApplicationContext.getContext()
                          .getProperties()
                          .setProperty(String.join(pluginQualifiedName(mainClass), "-", property), value);
    }

    /**
     * Show the user a Yes/No/Cancel dialog. The dialog is modal and execution of the program will block until the dialog
     * is closed. The response of the user is returned.
     * <p>
     * Messages should be phrased as a yes or no question consistent with the phrasing of other yes/no/cancel prompts
     * throughout the program.
     * <p>
     * Generally, a {@link ConfirmationType} of Yes indicates perform the prompted action and continue. A Type of No
     * indicates the prompted action should *not* take place but the program should continue. A type of Cancel indicates
     * that the action should not be taken *and* the program should *not* continue and rather maintain the exact same
     * state as it did *before* displaying the prompt
     *
     * @param title   Window title
     * @param prompt  Main text
     * @param message Detailed message
     * @return {@link ConfirmationType} representing the User's choice.
     */
    public static ConfirmationType showConfirmation(String title, String prompt, String message) {
        ConfirmationDialog dialog = new ConfirmationDialog(null, title, prompt, message);
        return dialog.showPrompt();
    }

    /**
     * Show a modal alter with an OK button. Thread is blocked until alert is closed
     *
     * @param title   Window title
     * @param prompt  Main text
     * @param message Detailed message
     */
    public static void showAlert(String title, String prompt, String message) {
        JavaFXUtilsKt.popupMessage(title, prompt, message, true);
    }

    /**
     * Show an alert with an OK button. Thread is not blocked. Program continues executing after the prompt is shown
     * even before the user responds
     *
     * @param title   Window title
     * @param prompt  Main text
     * @param message Detailed message
     */
    public static void showAlertNonModal(String title, String prompt, String message) {
        JavaFXUtilsKt.popupMessage(title, prompt, message, false);
    }

    /**
     * @see #getPluginExecutor(Class)
     */
    private static final Map<Class<? extends JPEditPlugin>, ScheduledExecutorService> EXECUTOR_CACHE = new HashMap<>();

    /**
     * This method provides each plugin with a Single Threaded Executor for running or scheduling tasks
     * <p>
     * This executor service will be forcibly stopped when the application shuts down. All running tasks will be
     *  interrupted, and queued tasks will be discarded. There is no way to stop this. All tasks submitted to the
     * Executor should be able to handle being interrupted or discarded at shutdown
     *
     * @param pluginClass the main class (implementing JPEditPlugin) of your plugin
     * @return the {@link Executor} for your plugin
     * @see Executor
     */
    @NotNull
    public static ScheduledExecutorService getPluginExecutor(Class<? extends JPEditPlugin> pluginClass) {
        return EXECUTOR_CACHE.computeIfAbsent(pluginClass, key ->
                ApplicationContext.getContext()
                                  .getLoadedPlugins()
                                  .stream()
                                  .filter(p -> p.getMainClass().getClass().equals(key))
                                  .findFirst()
                                  .map(LoadedJPPlugin::getPluginExecutor)
                                  .get()
        );
    }
}
