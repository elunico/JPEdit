package com.tom.jpedit.gui.components;

import com.tom.jpedit.gui.i18n.Strings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * Class representing a YES button. Uses the BUTTON_YES text from the Strings class and
 * accepts an action in the constructor. Simplifies the two-step button + handler code into a
 * single step but provides no default action behavior
 */
public class YesButton extends Button {
    public YesButton(EventHandler<ActionEvent> action) {
        super(Strings.Content.BUTTON_YES.text);
        setOnAction(action);
    }
}
