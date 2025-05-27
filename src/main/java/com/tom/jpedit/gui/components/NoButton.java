package com.tom.jpedit.gui.components;

import com.tom.jpedit.gui.i18n.Strings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * Class representing a No button. Uses the BUTTON_NO text from the Strings class and
 * accepts an action in the constructor. Simplifies the two-step button + handler code into a
 * single step but provides no default action behavior
 */
public class NoButton extends Button {
    public NoButton(EventHandler<ActionEvent> action) {
        super(Strings.Content.BUTTON_NO.text);
        setOnAction(action);
    }
}
