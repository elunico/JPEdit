package com.tom.jpedit.handlers.misc;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.dialog.ChangeLocalePrompt;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

public class ChangeLocaleHandler extends ActionHandler {
    public ChangeLocaleHandler(JPEditWindow owner) {
        super(owner);
    }

    @Override
    public void handle(ActionEvent event) {
        new ChangeLocalePrompt(owner).show();
    }
}
