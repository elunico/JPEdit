package com.tom.jpedit.handlers.file;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

/**
 * @author Thomas Povinelli
 * Created 4/5/19
 * In JPEdit
 */
public class NewWindowActionHandler extends ActionHandler {

    public NewWindowActionHandler(JPEditWindow jpEditWindow) {
        super(jpEditWindow);
    }

    @Override
    public void handle(ActionEvent event) {
        ApplicationContext.getContext().createNewWindow();
    }
}
