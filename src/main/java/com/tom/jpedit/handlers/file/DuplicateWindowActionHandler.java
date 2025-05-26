package com.tom.jpedit.handlers.file;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

public class DuplicateWindowActionHandler extends ActionHandler {
  public DuplicateWindowActionHandler(JPEditWindow owner) {
    super(owner);
  }

  @Override
  public void handle(ActionEvent event) {
    ApplicationContext.getContext().duplicateWindow(owner);
  }
}
