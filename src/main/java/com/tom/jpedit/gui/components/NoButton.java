package com.tom.jpedit.gui.components;

import com.tom.jpedit.gui.i18l.Strings;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class NoButton extends Button {
    public NoButton(EventHandler<ActionEvent> action) {
        super(Strings.Content.BUTTON_NO.text);
        setOnAction(action);
    }
}
