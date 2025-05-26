package com.tom.jpedit.handlers.file;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

public class ClearRecentFilesHandler extends ActionHandler {
  public ClearRecentFilesHandler(JPEditWindow jpEditWindow) {
    super(jpEditWindow);
  }

  @Override
  public void handle(ActionEvent event) {
    ApplicationContext.getContext().clearRecentFiles();
  }
}
