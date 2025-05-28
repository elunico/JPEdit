package com.tom.jpedit.plugins;

import com.tom.jpedit.plugins.components.PluginKeyboardShortcut;
import com.tom.jpedit.plugins.components.PluginMenuItem;
import com.tom.jpedit.plugins.components.PluginToolbarButton;
import org.jetbrains.annotations.Nullable;

/**
 * Class representing the user-interactable properties of a JPEdit Plugin
 * See {@link JPEditPlugin#pluginProperties()} for more information
 */
public class PluginProperties {
    private PluginMenuItem menuItem;
    private PluginKeyboardShortcut keyboardShortcut;
    private PluginToolbarButton toolbarButton;

    public PluginProperties() {
    }

    public PluginProperties(@Nullable PluginMenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public PluginProperties(@Nullable PluginMenuItem menuItem, @Nullable PluginKeyboardShortcut keyboardShortcut) {
        this.menuItem = menuItem;
        this.keyboardShortcut = keyboardShortcut;
    }

    public PluginProperties(
            @Nullable PluginMenuItem menuItem,
            @Nullable PluginKeyboardShortcut keyboardShortcut,
            @Nullable PluginToolbarButton toolbarButton
    ) {
        this.menuItem = menuItem;
        this.keyboardShortcut = keyboardShortcut;
        this.toolbarButton = toolbarButton;
    }

    public @Nullable PluginMenuItem getMenuItem() {
        return menuItem;
    }

    public @Nullable PluginKeyboardShortcut getKeyboardShortcut() {
        return keyboardShortcut;
    }

    public @Nullable PluginToolbarButton getToolbarButton() {
        return toolbarButton;
    }
}
