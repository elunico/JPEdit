package com.tom.jpedit.handlers.file;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import com.tom.jpedit.handlers.misc.DirtyCheckHandler;
import com.tom.jpedit.logging.JPLogger;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import tom.utils.file.FileUtilsKt;
import tom.javafx.JavaFXUtilsKt;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @author Thomas Povinelli
 * Created 4/5/19
 * In JPEdit
 */
public class OpenActionHandler extends ActionHandler {


  public OpenActionHandler(JPEditWindow owner) {
    super(owner);
  }

  @Override
  public void handle(ActionEvent event) {
    DirtyCheckHandler handler = new DirtyCheckHandler(owner, () -> {
      FileChooser fc = new FileChooser();
      fc.setInitialDirectory(new File(System.getProperty("user.dir")));
      File f = fc.showOpenDialog(owner);
      if (f == null) {
        return;
      }
      openFile(f);
    });

    handler.handle(event);
  }

  public void openFile(File f) {
    try {
      String s = FileUtilsKt.getText(f);
      owner.setSaveFile(f);
      owner.getTextArea().setText(s);
      owner.saveUpdated();
      ApplicationContext.getContext().newRecentFile(f);
      JPLogger.debug(JPLogger.getAppLog(), Level.FINE, () -> "Opened file of length " + owner.getTextArea().getText()
                                                                                             .length() + " chars.");
    } catch (IOException e) {
      JavaFXUtilsKt.popupAlert("The file " + f.getAbsolutePath() + " was not found!", "File Not Found!");
      JPLogger.debug(
          JPLogger.getErrLog(),
          Level.WARNING,
          () -> "File to open not found: " + f.getAbsolutePath() + "\n" + JavaFXUtilsKt
              .stackTraceToString(e.getStackTrace())
      );
    }

  }
}
