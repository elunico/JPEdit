package com.tom.jpedit.handlers.edit;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.find.ReplaceDialog;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

public class ReplaceActionHandler extends ActionHandler {
  public ReplaceActionHandler(JPEditWindow jpEditWindow) {
    super(jpEditWindow);
  }

  @Override
  public void handle(ActionEvent event) {
    // TODO: this
    ReplaceDialog dialog = new ReplaceDialog(this.owner);
    dialog.show();
  }
}
