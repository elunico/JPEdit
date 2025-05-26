package com.tom.jpedit.handlers.file;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

import java.io.File;

/**
 * @author Thomas Povinelli
 * Created 4/5/19
 * In JPEdit
 */
public class SaveActionHandler extends ActionHandler {

  public SaveActionHandler(JPEditWindow jpEditWindow) {
    super(jpEditWindow);
  }

  @Override
  public void handle(ActionEvent event) {
    SaveAsActionHandler handler = new SaveAsActionHandler(owner);
    if (owner.hasSaveFile()) {
      System.out.println(owner.getSaveFile());
      handler.saveAs(owner.getSaveFile());
    } else {
      File file = handler.getFileFromUser(event);
      if (file == null) {
        return;
      }
      handler.saveAs(file);
    }
  }

}
