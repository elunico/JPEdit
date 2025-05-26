package com.tom.jpedit.handlers.misc;

import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.dialog.RemovePluginSelectionStage;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

public class RemovePluginActionHandler extends ActionHandler {

  public RemovePluginActionHandler(JPEditWindow jpEditWindow) {
    super(jpEditWindow);
  }

  @Override
  public void handle(ActionEvent event) {
    DependantStage prompt = new RemovePluginSelectionStage(owner);
    prompt.showAndWait();
  }
}
