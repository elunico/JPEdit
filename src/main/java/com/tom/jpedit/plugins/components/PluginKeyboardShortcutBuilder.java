package com.tom.jpedit.plugins.components;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tom.utils.os.OSUtilsKt;

public class PluginKeyboardShortcutBuilder {
    private final KeyCode keyCode;
    private final PluginKeyboardShortcutAction<KeyEvent> action;
    private boolean shiftDown = false;
    private boolean controlDown = false;
    private boolean altDown = false;
    private boolean metaDown = false;

    public PluginKeyboardShortcutBuilder(KeyCode code, PluginKeyboardShortcutAction<KeyEvent> action) {
        this.action = action;
        this.keyCode = code;
    }

    public PluginKeyboardShortcut build() {
        return new PluginKeyboardShortcut(shiftDown, controlDown, altDown, metaDown, keyCode, action);
    }

    public PluginKeyboardShortcutBuilder withShift() {
        this.shiftDown = true;
        return this;
    }

    public PluginKeyboardShortcutBuilder withControl() {
        this.controlDown = true;
        return this;
    }

    public PluginKeyboardShortcutBuilder withShortcut() {
        if (OSUtilsKt.isMacOS() || OSUtilsKt.isMacOSX()) {
            return withMeta();
        } else {
            return withControl();
        }
    }

    public PluginKeyboardShortcutBuilder withAlt() {
        this.altDown = true;
        return this;
    }

    public PluginKeyboardShortcutBuilder withMeta() {
        this.metaDown = true;
        return this;
    }

}
