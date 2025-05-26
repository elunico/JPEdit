package com.tom.jpedit.handlers.edit;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.find.ReplaceDialog;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.IndexRange;
import org.jetbrains.annotations.NotNull;

public class ReplaceAllItemHandler extends ActionHandler {
  private final ReplaceDialog replaceDialog;

  public ReplaceAllItemHandler(@NotNull JPEditWindow owner, ReplaceDialog replaceDialog) {
    super(owner);
    this.replaceDialog = replaceDialog;
  }

  @Override
  public void handle(ActionEvent event) {
    // Move cursor to beginning to ensure replacement of ALL instances
    owner.getTextArea().deselect();
    owner.getTextArea().positionCaret(0);
    replaceDialog.clearCache();

    // find from the beginning each instance of the query and replace it
    FindDialogNextActionHandler delegate = new FindDialogNextActionHandler(owner, replaceDialog);
    IndexRange nextReplacement = delegate.getRangeOfQuery(replaceDialog.getFindTextText().getText());
    while (nextReplacement != null) {
      owner.getTextArea().replaceText(nextReplacement, replaceDialog.getReplaceTextText().getText());
      nextReplacement = delegate.getRangeOfQuery(replaceDialog.getFindTextText().getText());
    }
  }
}
