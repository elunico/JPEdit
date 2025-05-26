package com.tom.jpedit.plugins.components;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

@FunctionalInterface
public interface PluginKeyboardShortcutAction<Event extends javafx.event.Event> {
    void handle(PluginKeyboardShortcut shortcut, Event mouseEvent);
}
