package com.tom.jpedit.handlers.file;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

import java.io.File;


public class OpenRecentActionHandler extends ActionHandler {
  private final String fileName;

  public OpenRecentActionHandler(JPEditWindow owner, String text) {
    super(owner);
    this.fileName = text;
  }

  @Override
  public void handle(ActionEvent event) {
    OpenActionHandler handler = new OpenActionHandler(owner);
    handler.openFile(new File(fileName));
  }
}
