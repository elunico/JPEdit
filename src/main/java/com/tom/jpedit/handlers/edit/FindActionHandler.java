package com.tom.jpedit.handlers.edit;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.find.FindDialog;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

public class FindActionHandler extends ActionHandler {
    public FindActionHandler(JPEditWindow jpEditWindow) {
        super(jpEditWindow);
    }

    @Override
    public void handle(ActionEvent event) {
        FindDialog dialog = new FindDialog(owner);
        dialog.show();
    }
}
