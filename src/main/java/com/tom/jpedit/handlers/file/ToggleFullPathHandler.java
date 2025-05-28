package com.tom.jpedit.handlers.file;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

public class ToggleFullPathHandler extends ActionHandler {
    public ToggleFullPathHandler(JPEditWindow jpEditWindow) {
        super(jpEditWindow);
    }

    @Override
    public void handle(ActionEvent event) {
        owner.setFullPathInTitle(!owner.isFullPathInTitle());
        ApplicationContext.getContext().getUserPreferences().setFullPathShowing(owner.isFullPathInTitle());
    }
}
