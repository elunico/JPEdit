package com.tom.jpedit.handlers.file;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import com.tom.jpedit.handlers.misc.DirtyCheckHandler;
import javafx.event.ActionEvent;

/**
 * @author Thomas Povinelli
 * Created 4/5/19
 * In JPEdit
 */
public class NewActionHandler extends ActionHandler {

  public NewActionHandler(JPEditWindow jpEditWindow) {
    super(jpEditWindow);
  }

  @Override
  public void handle(ActionEvent event) {
    new DirtyCheckHandler(owner, owner::newFile).handle(event);
  }
}
