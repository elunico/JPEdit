package com.tom.jpedit.handlers.misc;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

public class ToggleWrapTextActionHandler extends ActionHandler {
    public ToggleWrapTextActionHandler(JPEditWindow jpEditWindow) {
        super(jpEditWindow);
    }

    @Override
    public void handle(ActionEvent event) {
        owner.getTextArea().setWrapText(!owner.getTextArea().isWrapText());
    }
}
