package com.tom.jpedit;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.file.OpenRecentActionHandler;
import com.tom.jpedit.handlers.misc.AddPluginActionHandler;
import com.tom.jpedit.logging.JPLogger;
import com.tom.jpedit.plugins.JPEditPlugin;
import com.tom.jpedit.plugins.PluginProperties;
import com.tom.jpedit.util.JPUtil;
import com.tom.jpedit.util.LoadedJPPlugin;
import com.tom.jpedit.util.UserPreferences;
import com.tom.jpedit.util.Version;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.MenuItem;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tom.javafx.JavaFXUtilsKt;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

/**
 * Non-instantiable class for managing the context, state, and environment of the application
 * This class provides many useful fields, lists, data, and methods
 * <p>
 * Be sure to read the documentation carefully as the methods in this class can significantly alter the
 * function of the application
 */
public class ApplicationContext {
    /* V E R S I O N */
    public static final Version VERSION = new Version(5, 0, 0, "beta");
    /* V E R S I O N */

    public static final String PLUGIN_LIST_FILE_NAME = "loaded-plugins.txt";

    private static final ApplicationContext context = new ApplicationContext();
    private static boolean terminating = false;

    /**
     * Returns a list of codes which represent the properties files available for langugage conversion
     *
     * @return a list of language codes corresponding to the files in the lang dir
     */
    public static List<String> getExistingLanguageCodes() {
        var langFiles = List.of(Objects.requireNonNullElseGet(new File("lang").listFiles(), () -> new File[0]));
        return langFiles.stream().map(file -> {

            var name = file.getName();
            name = name.substring(0, name.lastIndexOf('.'));
            return name;
        }).toList();
    }

    /**
     * Terminates the application early by simulating window close events for all open windows
     * and performing the necessary cleanup without immediately triggering system exit.
     * It is an early termination because it can be called to stop the application before
     * the normal termination time which occurs when all windows are closed.
     * <p>
     * This method retrieves all currently open application windows, creates and fires
     * window close request events for each of them, and then invokes the teardown process
     * in the application's context to release resources. The explicit invocation of
     * `System.exit(0)` is intentionally avoided, allowing for additional termination
     * handling through hooks or other mechanisms if needed.
     * <p>
     * The method is static and operates at the application level, ensuring a controlled
     * and unified shutdown sequence across all managed components of the application.
     */
    public static void terminateEarly() {
        if (terminating) {
            return;
        }
        terminating = true;
        var size = context.getWindows().size();
        var events = new Event[size];

        List<JPEditWindow> contextWindows = context.getWindows();
        for (int i = 0; i < size; i++) {
            JPEditWindow w = contextWindows.get(i);
            events[i] = new WindowEvent(w, WindowEvent.WINDOW_CLOSE_REQUEST);
        }

        for (Event event : events) {
            Event.fireEvent(event.getTarget(), event);
        }

        context.teardown();
        // System exit should happen naturally to allow hooking into the termination process
        // System.exit(0);
    }

    private static void checkCallerClass(Class<?>[] validCallers, String msg) throws IllegalAccessException {
        checkCallerClass(validCallers, msg, false);
    }

    private static void checkCallerClass(Class<?>[] validCallers, String msg, boolean strict)
            throws IllegalAccessException {
        Class<?> callerClass;
        try {
            callerClass = Objects.requireNonNull(StackWalker.getInstance(RETAIN_CLASS_REFERENCE).getCallerClass());
        } catch (IllegalCallerException | NullPointerException e) {
            if (strict) {
                JPLogger.getAppLog().severe("Could not check caller class. Strict mode prevents all access.");
                throw new IllegalAccessException(msg);
            } else {
                JPLogger.getAppLog().severe("Could not check caller class. All classes are allowed.");
                return;
            }
        }

        JPLogger.debug(JPLogger.getAppLog(), "Check caller class: Called by " + callerClass.getName());
        if (!Arrays.asList(validCallers).contains(callerClass)) {
            throw new IllegalAccessException(msg);
        }
    }

    @com.tom.jpedit.util.Deprecated(value = "Plugins should no longer access ApplicationContext indiscriminately. Transition to using JPPluginAPI", since = "4.0.0", forRemoval = true, forPackage = "jpplugin.*", replaceWith = "JPPluginAPI")
    public static ApplicationContext getContext() {
        return context;
    }

    @NotNull
    public static Collection<? extends MenuItem> emptyRecents() {
        MenuItem item = new MenuItem("NO ITEMS");
        item.setDisable(true);
        return List.of(item);
    }
    private final List<JPEditWindow> windows = new ArrayList<>();
    private final ObservableList<LoadedJPPlugin> loadedPlugins = FXCollections.observableArrayList();
    private final ThreadGroup autoSaveWorkersThreadGroup = new ThreadGroup(
            Thread.currentThread()
                  .getThreadGroup(),
            "Auto Save Workers Thread"
    );
    private final Properties properties = new Properties();
    private final UserPreferences userPreferences = new UserPreferences();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(8);
    private int existingWindows = 0;
    private int windowIdGen = 0;
    private int totalWindowCount = 0;
    private List<File> recentFiles = null;

    /**
     * No instances
     */
    private ApplicationContext() {
        loadProperties();
        loadPreferences();
        loadPlugins();
        executor.setRemoveOnCancelPolicy(true);
    }

    /**
     * Tells when the application is terminating
     *
     * @return true when the application is terminating
     */
    public boolean isTerminating() {
        return terminating;
    }

    private void loadPlugins() {
        File pluginList = new File(PLUGIN_LIST_FILE_NAME);
        if (!pluginList.exists()) {
            try {
                if (!pluginList.createNewFile()) {
                    throw new IOException("Could not create plugin list");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(PLUGIN_LIST_FILE_NAME))) {
            reader.lines().forEach(line -> {
                var jarFile = new File(line.trim());
                var name = JPUtil.mainClassFromJAR(jarFile);
                if (name == null) {
                    JavaFXUtilsKt.popupMessage(
                            "Missing Plugin",
                            "Could not load plugin " + line,
                            "The plugin jar could not be found"
                    );
                    return;
                }
                loadPluginClass(jarFile, name);
            });
        } catch (IOException e) {
            JavaFXUtilsKt.popupMessage(
                    "Missing Plugin",
                    "Could not load plugin",
                    "The plugin jar could not be found\n" + e.getMessage() + "\n" + JavaFXUtilsKt.stackTraceToString(e.getStackTrace())
            );
        }
    }

    /**
     * Called as the first step in the chain to loading a plugin into the application.
     * This should <b>not</b> be called by the plugins nor should it be called by other users.
     * The program will call this method when it is ready to load the plugin
     *
     * @param jar           the plugin jar file to load
     * @param mainClassName the main class of the plugin – the one that implements JPEditPlugin
     * @see JPEditPlugin
     */
    public void loadPluginClass(@NotNull File jar, @NotNull String mainClassName) {
        ensureNotTerminating();
        try {
            checkCallerClass(
                    new Class[]{ ApplicationContext.class, AddPluginActionHandler.class },
                    "Do not call loadPluginClass! The program will call it when you load the plugin in the GUI"
            );
            ClassLoader loader = ApplicationContext.class.getClassLoader();
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{ jar.toURI().toURL() }, loader);
            String qualifiedName = qualifyPluginMainClassName(mainClassName);
            Class<?> aClass = Class.forName(qualifiedName, true, urlClassLoader);
            Class<JPEditPlugin> mainClass = (Class<JPEditPlugin>) aClass;
            JPEditPlugin pluginClass = mainClass.getConstructor().newInstance();
            registerPlugin(jar, pluginClass);
        } catch (MalformedURLException e) {
            JavaFXUtilsKt.popupMessage(
                    "Error loading plugin",
                    "Invalid URL for JAR " + jar.toURI(),
                    JavaFXUtilsKt.stackTraceToString(e.getStackTrace())
            );
            JPLogger.getErrLog().severe("Could not load plugin " + e.getMessage());
        } catch (ClassNotFoundException e) {
            JavaFXUtilsKt.popupMessage(
                    "Error loading plugin",
                    "No such class: '" + mainClassName + "'",
                    JavaFXUtilsKt.stackTraceToString(e.getStackTrace())
            );
            JPLogger.getErrLog().severe("Could not load plugin " + e.getMessage());
        } catch (IllegalAccessException | InvocationTargetException e) {
            JavaFXUtilsKt.popupMessage(
                    "Error loading plugin",
                    "Access or Invocation Target is invalid in class " + mainClassName,
                    JavaFXUtilsKt.stackTraceToString(e.getStackTrace())
            );
            JPLogger.getErrLog().severe("Could not load plugin " + e.getMessage());
        } catch (NoSuchMethodException | InstantiationException e) {
            JavaFXUtilsKt.popupMessage(
                    "Could not instantiate Plugin Instance",
                    "Plugins must implement the JPEditPlugin interface and all of its methods\n" + "And declare a single no-argument constructor",
                    JavaFXUtilsKt.stackTraceToString(e.getStackTrace())
            );
            JPLogger.getErrLog().severe("Could not load plugin " + e.getMessage());
        } catch (ClassCastException e) {
            JavaFXUtilsKt.popupMessage(
                    "Plugin Load Error: Not a Plugin",
                    "Plugins must contain a class with the same name as the jar file (" + mainClassName + ") that they are loaded from" + " which implement the com.tom.jpedit.plugins.JPEditPlugin interface.",
                    JavaFXUtilsKt.stackTraceToString(e.getStackTrace())
            );
            JPLogger.getErrLog().severe("Could not load plugin " + e.getMessage());
        } catch (Exception e) {
            JavaFXUtilsKt.popupMessage(
                    "Error loading plugin",
                    "Unexpected error occurred during load: " + e.getMessage(),
                    JavaFXUtilsKt.stackTraceToString(e.getStackTrace())
            );
            JPLogger.getErrLog().severe("Could not load plugin " + e.getMessage());
        }
    }

    private void ensureNotTerminating() {
        if (terminating) {
            throw new IllegalStateException("Cannot perform this operation while terminating");
        }
    }

    @Contract("_ -> new")
    private @NotNull String qualifyPluginMainClassName(String mainClassName) {
        return String.join(".", "jpplugin", mainClassName.toLowerCase(Locale.ROOT), mainClassName);
    }

    private void registerPlugin(File jar, JPEditPlugin pluginClass) throws IllegalAccessException {
        checkCallerClass(new Class[]{ getClass() }, "Do not call register plugin! Let the program take care of that");
        LoadedJPPlugin loadedJPPlugin = tryLoadPlugin(jar, pluginClass);
        if (loadedJPPlugin == null) {
            return;
        }
        loadPluginProperties(loadedJPPlugin);
    }

    private @Nullable LoadedJPPlugin tryLoadPlugin(File jar, JPEditPlugin pluginClass) {
        try {
            pluginClass.onPluginLoad(windows);
            LoadedJPPlugin loadedJPPlugin = new LoadedJPPlugin(pluginClass, jar);
            loadedPlugins.add(loadedJPPlugin);
            return loadedJPPlugin;
        } catch (Exception e) {
            String exName = e.getClass().getName();
            String msg = e.getMessage();
            String pluginName = pluginClass.getClass().getName();
            String message = "Failed to load plugin " + pluginName + "\n" + exName + ": " + msg;
            JavaFXUtilsKt.popupMessage(
                    "Plugin Error!",
                    message,
                    message + "\n" + JavaFXUtilsKt.stackTraceToString(e.getStackTrace())
            );
            String loggerMsg = "Failed to load plugin " + message + "\n" + JavaFXUtilsKt.stackTraceToString(e.getStackTrace());
            JPLogger.getErrLog().severe(loggerMsg);
            return null;
        }
    }

    private void loadPluginProperties(LoadedJPPlugin loadedJPPlugin) {
        if (windows.isEmpty()) {
            return;
        }

        int idx = 0;
        PluginProperties properties = null;
        while (idx < windows.size()) {
            properties = loadedJPPlugin.pluginProperties();
            if (properties == null) {
                break;
            }
            windows.get(idx++).addPluginProperties(properties, loadedJPPlugin);

        }

        if (idx != 0 && properties == null) {
            unexpectedPropertiesNull();
        }
    }

    private void unexpectedPropertiesNull() {
        NullPointerException npe = new NullPointerException(
                "pluginProperties() returned null for some calls but not for others.");
        JavaFXUtilsKt.popupMessage(
                "Error in PluginProperties",
                "pluginProperties() should not return null after having returned non-null",
                JavaFXUtilsKt.stackTraceToString(npe.getStackTrace())
        );
    }

    private void loadPreferences() {
        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream("user-prefs.properties"))) {
            userPreferences.load(reader);
        } catch (IOException e) {
            System.err.println("Could not read user preferences file. Using defaults");
        }
    }

    private void loadProperties() {
        try {
            if (JPUtil.environmentIsTrue("DEBUG")) {
                properties.load(new InputStreamReader(new FileInputStream("debug.properties")));
            } else {
                properties.load(new InputStreamReader(new FileInputStream("app.properties")));
            }
        } catch (IOException e) {
            System.err.println("Could not read properties. Using defaults");
            properties.put("recent_files_dir", "recentfiles");
            properties.put("recent_files_file", "recentfiles.txt");
            properties.put("log_file_dir", "logs");
            properties.put("log_file_app", "application.log");
            properties.put("log_file_err", "error.log");
            properties.put("tempsaves_dir", "tempsaves");
        }
    }

    /**
     * Gets the thread pool executor for the autosave thread
     *
     * @return the thread pool executor for autosave
     */
    public ScheduledThreadPoolExecutor getExecutor() {
        return executor;
    }

    /**
     * Gets the instance of the user preferences Properties class
     *
     * @return return the user preferences properties
     */
    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    /**
     * Create a duplicate of the JPEditWindow passed into the function
     *
     * @param window the window to be duplicated
     */
    public void duplicateWindow(@NotNull JPEditWindow window) {
        ensureNotTerminating();
        JPEditWindow duplicate = window.duplicate(new JPEditWindow());
        if (duplicate == null) {
            return;
        }
        ApplicationContext.getContext().registerWindow(duplicate);
        duplicate.setTitle(window.getTitle() + " Copy");
        duplicate.show();
    }

    void registerWindow(@NotNull JPEditWindow window) {
        try {
            Class<?>[] callers = { ApplicationContext.class, Driver.class };
            String msg = "Do not call register window! To create a new window, " + "create a NewWindowActionHandler and call void handle()";
            checkCallerClass(callers, msg);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        JPLogger.debug(JPLogger.getAppLog(), "Registering Window " + window.getTitle());
        for (LoadedJPPlugin plugin : loadedPlugins) {
            try {
                plugin.onNewWindow(windows, window);
                PluginProperties properties = plugin.pluginProperties();
                if (properties != null) {
                    window.addPluginProperties(properties, plugin);
                }
            } catch (Exception e) {
                throw new RuntimeException(
                        "The plugin " + plugin.getClass()
                                              .getCanonicalName() + " caused in error during construction of a new window.",
                        e
                );
            }
        }
        windows.add(window);
        existingWindows++;
        totalWindowCount++;
        JPLogger.debug(JPLogger.getAppLog(), "Total Windows Ever now: " + totalWindowCount);
        JPLogger.debug(JPLogger.getAppLog(), "Existing windows: " + existingWindows);
    }

    /**
     * Creates a new editor window in the application. This method initializes a new instance of
     * {@code JPEditWindow}, registers it within the application context, assigns a unique title
     * with a numeric identifier, and displays the window.
     * <p>
     * The method begins by ensuring that the application is not in the process of terminating.
     * If the application is terminating, an {@code IllegalStateException} is thrown. Subsequently,
     * the method creates the new window, registers it in the application context for management,
     * sets its title to "Untitled" followed by the total count of currently opened windows,
     * and finally makes the window visible on the screen.
     * <p>
     * This operation relies on the application context to manage the lifecycle and state of
     * windows, ensuring that proper bookkeeping of the open windows is maintained.
     * <p>
     * Throws:
     * - {@code IllegalStateException} if the application is terminating.
     */
    public void createNewWindow() {
        ensureNotTerminating();
        JPEditWindow newWindow = new JPEditWindow();
        ApplicationContext.getContext().registerWindow(newWindow);
        newWindow.setTitle("Untitled " + ApplicationContext.getContext().getTotalWindowCount());
        newWindow.show();
    }

    /**
     * Gets the total number of windows that have been created since application launch even if they have since closed
     * <p>
     * This method can return a number larger than the number of open windows but never smaller than the number of open windows
     *
     * @return the number of total windows ever opened in this run
     */
    public int getTotalWindowCount() {
        return totalWindowCount;
    }

    public int newWindowId() {
        return ++windowIdGen;
    }

    public void newRecentFile(File f) {
        recentFiles.remove(f);
        recentFiles.add(0, f);
        try {
            saveRecentFiles();
            for (JPEditWindow window : windows) {
                window.populateRecentItems();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveRecentFiles() throws IOException {
        if (recentFiles == null) {
            return;
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(properties.getProperty("recent_files_dir")))) {
            recentFiles.stream().map(File::getAbsolutePath).forEach(writer::println);
        }
    }

    /**
     * Called by the {@link com.tom.jpedit.handlers.file.CloseWindowActionHandler} when a window is closing
     * Should not be invoked directly
     *
     * @param window the window that is closing
     */
    public void unregisterWindow(JPEditWindow window) {
        windows.remove(window);
        existingWindows--;
        JPLogger.debug(JPLogger.getAppLog(), "Closing window and attempting to notify plugins");
        for (LoadedJPPlugin plugin : getLoadedPlugins()) {
            JPLogger.debug(JPLogger.getAppLog(), "Calling onCloseWindow for " + plugin.getClass().getCanonicalName());
            plugin.onWindowClose(windows, window);
        }
        if (existingWindows == 0) {
            teardown();
        }
    }

    /**
     * Called by the program once all windows have closed to finalize shutdown
     * <p>
     * This method should <b>NOT</b> be called by plugins. To terminate the application
     * at an arbitrary point use {@link ApplicationContext#terminateEarly()}
     */
    public void teardown() {
        terminating = true;
        try {
            JPLogger.getAppLog().info("Saving recent files...");
            saveRecentFiles();
            JPLogger.getAppLog().info("Done!");
            JPLogger.getAppLog().info("Saving user preferences...");
            savePreferences();
            JPLogger.getAppLog().info("Done!");
            JPLogger.getAppLog().info("Tearing down plugins...");
            loadedPlugins.forEach(LoadedJPPlugin::onExit);
            JPLogger.getAppLog().info("Done!");
            JPLogger.getAppLog().info("Saving loaded plugins...");
            saveLoadedPlugins();
            JPLogger.getAppLog().info("Done!");
            // force shutdown since all auto-save files are deleted on normal shutdown anyway
            JPLogger.getAppLog().info("Shutting down autosave worker pool");
            executor.shutdownNow();
            JPLogger.getAppLog().info("Done!");
            JPLogger.getAppLog().info("Shutting down.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveLoadedPlugins() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PLUGIN_LIST_FILE_NAME))) {
            for (LoadedJPPlugin plugin : loadedPlugins) {
                var path = plugin.getLoadedJARFile().getAbsolutePath();
                writer.write(path + System.lineSeparator());
            }
        } catch (IOException e) {
            JavaFXUtilsKt.popupMessage(
                    "Error saving Plugins",
                    "Could not save loaded plugin list",
                    "The list of loaded plugins could not be saved\n" + e.getMessage() + "\n" + JavaFXUtilsKt.stackTraceToString(e.getStackTrace())
            );

        }
    }

    private void savePreferences() throws IOException {
        userPreferences.savePreferences();
    }

    /**
     * Provides a list of all the loaded plugins in the program. This list should not be modified
     *
     * @return the list of loaded plugins
     */
    public List<LoadedJPPlugin> getLoadedPlugins() {
        return loadedPlugins;
    }

    public Collection<? extends MenuItem> fetchRecentItems(JPEditWindow window) {
        if (recentFiles() == null) {
            try {
                loadRecentFiles();
            } catch (IOException e) {
                return emptyRecents();
            }
        }
        List<MenuItem> items = recentFiles().stream()
                                            .map(File::getAbsolutePath)
                                            .map(MenuItem::new)
                                            .peek(item -> addRecentItemHandler(window, item))
                                            .collect(Collectors.toList());

        return items.isEmpty() ? ApplicationContext.emptyRecents() : items;
    }

    private void addRecentItemHandler(JPEditWindow window, @NotNull MenuItem item) {
        item.setOnAction(new OpenRecentActionHandler(window, item.getText()));
    }

    public List<File> recentFiles() {
        return recentFiles;
    }

    public void loadRecentFiles() throws IOException {
        if (recentFiles != null) {
            return;
        }
        recentFiles = new ArrayList<>();
        String recentFilesPath = properties.getProperty("recent_files_dir");
        File recentFile = new File(recentFilesPath);
        if (recentFile.exists() && recentFile.canRead()) {
            Files.readAllLines(recentFile.toPath()).stream().map(File::new).forEach(recentFiles::add);
        } else {
            if (!recentFile.createNewFile()) {
                throw new IOException("Could not create new file: " + recentFile.getAbsolutePath());
            }
            recentFiles = new ArrayList<>();
        }
    }

    public void clearRecentFiles() {
        recentFiles.clear();
        windows.forEach(JPEditWindow::clearRecentFiles);
        try {
            saveRecentFiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ThreadGroup getAutoSaveWorkersThreadGroup() {
        return autoSaveWorkersThreadGroup;
    }

    public String getProperty(String property) {
        return getProperties().getProperty(property);
    }

    public Properties getProperties() {
        return properties;
    }

    private List<JPEditWindow> getWindows() {
        return windows;
    }

    public List<JPEditWindow> getWindowsUnmodifiable() {
        return Collections.unmodifiableList(windows);
    }

    public void unregisterPlugin(@NotNull LoadedJPPlugin plugin) {
        plugin.onRemoved(windows);
        loadedPlugins.remove(plugin);
        windows.forEach(window -> window.removePlugin(plugin));
    }
}
