package com.tom.jpedit.handlers.edit;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

public class SelectAllActionHandler extends ActionHandler {
    public SelectAllActionHandler(JPEditWindow jpEditWindow) {
        super(jpEditWindow);
    }

    @Override
    public void handle(ActionEvent event) {
        owner.getTextArea().selectAll();
    }
}
