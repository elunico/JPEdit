package com.tom.jpedit.util;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.plugins.JPEditPlugin;
import com.tom.jpedit.plugins.PluginProperties;
import com.tom.jpedit.plugins.components.PluginKeyboardShortcut;
import com.tom.jpedit.plugins.components.PluginMenuItem;
import com.tom.jpedit.plugins.components.PluginToolbarButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is an internal representation of a JPEditPlugin after it has been loaded by the program
 * This class is specific to the function of the program and should not be interacted with by a particular
 * plugin's implementation. For information on implementing a plugin see {@link JPEditPlugin}
 */
public class LoadedJPPlugin {
    private final JPEditPlugin mainClass;
    private final File loadedJARFile;
    private final Map<JPEditWindow, PluginToolbarButton> button = new HashMap<>();
    private final Map<JPEditWindow, PluginMenuItem> item = new HashMap<>();
    private final Map<JPEditWindow, PluginKeyboardShortcut> shortcut = new HashMap<>();
    private boolean isLoaded;

    public LoadedJPPlugin(JPEditPlugin mainClass, File loadedJARFile) {
        this(mainClass, loadedJARFile, true);
    }

    public LoadedJPPlugin(JPEditPlugin mainClass, File loadedJARFile, boolean isLoaded) {
        this.mainClass = mainClass;
        this.loadedJARFile = loadedJARFile;
        this.isLoaded = isLoaded;
    }

    public Map<JPEditWindow, PluginToolbarButton> getButton() {
        return button;
    }

    public Map<JPEditWindow, PluginMenuItem> getItem() {
        return item;
    }

    public Map<JPEditWindow, PluginKeyboardShortcut> getShortcut() {
        return shortcut;
    }

    public JPEditPlugin getMainClass() {
        return mainClass;
    }

    public File getLoadedJARFile() {
        return loadedJARFile;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    private String getName() {
        return mainClass.getClass().getSimpleName();
    }


    public void onPluginLoad(@NotNull List<JPEditWindow> windows) throws Exception {
        mainClass.onPluginLoad(windows);
    }

    public void onNewWindow(@NotNull List<JPEditWindow> existingWindows, @NotNull JPEditWindow newWindow) {
        mainClass.onNewWindow(existingWindows, newWindow);
    }

    public void onWindowClose(@NotNull List<JPEditWindow> windows, @NotNull JPEditWindow closingWindow) {
        mainClass.onWindowClose(windows, closingWindow);
    }

    public void onExit() {
        mainClass.onExit();
    }

    public @Nullable PluginProperties pluginProperties() {
        return mainClass.pluginProperties();
    }

    public void onRemoved(List<JPEditWindow> windows) {
        mainClass.onRemoved(windows);
    }

    public String getPluginQualifiedName() {
        return mainClass.getClass().getPackageName();
    }
}
