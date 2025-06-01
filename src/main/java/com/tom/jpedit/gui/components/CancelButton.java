package com.tom.jpedit.gui.components;

import com.tom.jpedit.gui.i18n.Strings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Class representing a Cancel button. Uses the BUTTON_CANCEL text from the Strings class and
 * accepts an action in the constructor. Simplifies the two-step button + handler code into a
 * single step.
 *
 * If a {@link Stage} is given to the constructor, the window will be closed when the button is clicked
 */
public class CancelButton extends Button {
    private Stage window;

    public CancelButton(EventHandler<ActionEvent> action) {
        super(Strings.Content.BUTTON_CANCEL.text);
        setOnAction(action);
    }

    public CancelButton(Stage window, EventHandler<ActionEvent> action) {
        this(action);
        this.window = window;
    }

    public CancelButton(Stage window) {
        this(window, event -> {

        });
    }

    public void fire() {
        if (window != null) {
            window.close();
        }
        super.fire();
    }
}
