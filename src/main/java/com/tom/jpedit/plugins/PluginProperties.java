package com.tom.jpedit.plugins;

import com.tom.jpedit.plugins.components.PluginKeyboardShortcut;
import com.tom.jpedit.plugins.components.PluginMenuItem;
import com.tom.jpedit.plugins.components.PluginToolbarButton;

public class PluginProperties {
  private PluginMenuItem menuItem;
  private PluginKeyboardShortcut keyboardShortcut;
  private PluginToolbarButton toolbarButton;

  public PluginProperties() {
  }

  public PluginProperties(PluginMenuItem menuItem) {
    this.menuItem = menuItem;
  }

  public PluginProperties(PluginMenuItem menuItem, PluginKeyboardShortcut keyboardShortcut) {
    this.menuItem = menuItem;
    this.keyboardShortcut = keyboardShortcut;
  }

  public PluginProperties(
      PluginMenuItem menuItem,
      PluginKeyboardShortcut keyboardShortcut,
      PluginToolbarButton toolbarButton
  ) {
    this.menuItem = menuItem;
    this.keyboardShortcut = keyboardShortcut;
    this.toolbarButton = toolbarButton;
  }

  public PluginMenuItem getMenuItem() {
    return menuItem;
  }

  public PluginKeyboardShortcut getKeyboardShortcut() {
    return keyboardShortcut;
  }

  public PluginToolbarButton getToolbarButton() {
    return toolbarButton;
  }
}
