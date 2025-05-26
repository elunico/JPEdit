package com.tom.jpedit.handlers.edit;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.find.FindReplaceBase;
import com.tom.jpedit.handlers.ActionHandler;
import com.tom.jpedit.logging.JPLogger;
import javafx.event.ActionEvent;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tom.javafx.JavaFXUtilsKt;

public class FindDialogNextActionHandler extends ActionHandler {
  private final FindReplaceBase findDialog;

  public FindDialogNextActionHandler(JPEditWindow owner, FindReplaceBase findReplaceBase) {
    super(owner);
    this.findDialog = findReplaceBase;
    findReplaceBase.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
      if (event.isShiftDown() && event.isControlDown() && event.isAltDown() && event.getCode() == KeyCode.T) {
        throw new RuntimeException("Debug exception thrown!");
      }
    });
  }

  @Override
  public void handle(ActionEvent event) {
    String query = findDialog.getFindTextText().getText();
    IndexRange range = getRangeOfQuery(query);
    if (range == null) {
      if (!query.isEmpty() && findDialog.getCache().getLastStop() != 0) {
        findDialog.getCache().setLastStop(0);
        handle(event);
        return;
      }
      JavaFXUtilsKt.popupAlert("String '" + query + "' not found!", "Not found!");
      return;
    }
    int start = range.getStart();
    int end = range.getEnd();
    JPLogger.debug(JPLogger.getAppLog(), "Start of find text: " + start);
    owner.getTextArea().selectRange(start, end);
    findDialog.getCache().setLastStop(end);
  }

  public IndexRange getRangeOfQuery(String query) {
    if (query == null || query.isEmpty()) {
      return null;
    }
    String text = owner.getTextArea().getText();
    if (findDialog.getCaseInsensitiveBox().isSelected()) {
      text = text.toLowerCase();
      query = query.toLowerCase();
    }
    int start = text.indexOf(query, findDialog.getCache().getLastStop());
    int end = start + query.length();
    if (start < 0) {
      return null;
    }
    return new IndexRange(start, end);
  }
}
