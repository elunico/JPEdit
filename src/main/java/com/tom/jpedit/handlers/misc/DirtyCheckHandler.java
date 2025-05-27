package com.tom.jpedit.handlers.misc;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.confirmation.ConfirmationDialog;
import com.tom.jpedit.gui.confirmation.ConfirmationType;
import com.tom.jpedit.handlers.ActionHandler;
import com.tom.jpedit.handlers.file.SaveActionHandler;
import com.tom.jpedit.logging.JPLogger;
import javafx.event.ActionEvent;

public class DirtyCheckHandler extends ActionHandler {
  private final Runnable next;

  public DirtyCheckHandler(JPEditWindow owner, Runnable nextSteps) {
    super(owner);
    this.next = nextSteps;
  }

  @Override
  public void handle(ActionEvent event) {
    JPLogger.debug(JPLogger.getAppLog(), "dirty = " + owner.isDirty());
    if (!owner.isDirty()) {
      next.run();
      return;
    }

    ConfirmationDialog dialog = new ConfirmationDialog(
        owner,
        "Save Changes?",
        "File modified",
        "The file has been modified since saving. Do you want to save? Answering 'no' will DESTROY changes."
    );
    ConfirmationType choice = dialog.showPrompt();
    JPLogger.getAppLog().info("Choice was " + choice);

    if (choice == ConfirmationType.CANCEL) {
      event.consume();
      // abort closing
      return;
    }

    if (choice == ConfirmationType.YES) {
      SaveActionHandler handler = new SaveActionHandler(owner);
      handler.handle(event);
    }

    // continue closing the window
    next.run();
  }
}
