package com.tom.jpedit.gui.components;

import com.tom.jpedit.gui.i18n.Strings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * Class representing a Cancel button. Uses the BUTTON_CANCEL text from the Strings class and
 * accepts an action in the constructor. Simplifies the two-step button + handler code into a
 * single step but provides no default action behavior
 */
public class CancelButton extends Button {
    public CancelButton(EventHandler<ActionEvent> action) {
        super(Strings.Content.BUTTON_CANCEL.text);
        setOnAction(action);
    }
}
