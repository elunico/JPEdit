package com.tom.jpedit.handlers.misc;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.dialog.FontPrompt;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

public class FontChangeActionHandler extends ActionHandler {

  public FontChangeActionHandler(JPEditWindow owner) {
    super(owner);
  }

  @Override
  public void handle(ActionEvent event) {
    FontPrompt prompt = new FontPrompt(owner);
    prompt.show();
  }
}
