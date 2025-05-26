package com.tom.jpedit.plugins.components;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.logging.JPLogger;
import com.tom.jpedit.plugins.JPEditPlugin;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class PluginKeyboardShortcut implements PluginOwnedComponent, Cloneable {
  private final boolean shiftDown;
  private final boolean controlDown;
  private final boolean altDown;
  private final boolean metaDown;
  private final KeyCode keyCode;
  private JPEditWindow owner;

  private final PluginKeyboardShortcutAction<KeyEvent> handler;

  public PluginKeyboardShortcut(
      boolean shiftDown,
      boolean controlDown,
      boolean altDown,
      boolean metaDown,
      KeyCode keyCode,
      PluginKeyboardShortcutAction<KeyEvent> handler
  ) {
    this.shiftDown = shiftDown;
    this.controlDown = controlDown;
    this.altDown = altDown;
    this.metaDown = metaDown;
    this.keyCode = keyCode;
    this.handler = handler;
  }

  @Override
  public PluginKeyboardShortcut clone() {
    try {
      return (PluginKeyboardShortcut) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean matchesEvent(KeyEvent event) {
      return event.getCode() == keyCode && event.isShiftDown() == shiftDown && event.isControlDown() == controlDown && event.isAltDown() == altDown && event.isMetaDown() == metaDown;
  }

  public EventHandler<KeyEvent> asHandler() {
    return event -> {
      if (matchesEvent(event)) {
        handler.handle(this, event);
      }
    };
  }


  public boolean isShiftDown() {
    return shiftDown;
  }

  public boolean isControlDown() {
    return controlDown;
  }

  public boolean isMetaDown() {
    return metaDown;
  }

  public KeyCode getKeyCode() {
    return keyCode;
  }

  public JPEditWindow getOwner() {
    return owner;
  }

  public void setOwner(JPEditWindow owner) {
    this.owner = owner;
  }

  public boolean isAltDown() {
    return altDown;
  }
}
