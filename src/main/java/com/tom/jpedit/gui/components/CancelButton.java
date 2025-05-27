package com.tom.jpedit.gui.components;

import com.tom.jpedit.gui.i18l.Strings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class CancelButton extends Button {
    public CancelButton(EventHandler<ActionEvent> action) {
        super(Strings.Content.BUTTON_CANCEL.text);
        setOnAction(action);
    }
}
