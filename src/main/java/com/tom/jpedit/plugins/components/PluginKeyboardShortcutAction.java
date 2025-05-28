package com.tom.jpedit.plugins.components;

@FunctionalInterface
public interface PluginKeyboardShortcutAction<Event extends javafx.event.Event> {
    void handle(PluginKeyboardShortcut shortcut, Event mouseEvent);
}
