package com.tom.jpedit.handlers.edit;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.find.ReplaceDialog;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.IndexRange;
import org.jetbrains.annotations.NotNull;

public class ReplaceItemHandler extends ActionHandler {
  private final ReplaceDialog replaceDialog;

  public ReplaceItemHandler(@NotNull JPEditWindow owner, ReplaceDialog replaceDialog) {
    super(owner);
    this.replaceDialog = replaceDialog;
  }

  @Override
  public void handle(ActionEvent event) {
    IndexRange selection = owner.getTextArea().getSelection();
    String replacement = replaceDialog.getReplaceTextText().getText();
    if (selection != null && selection.getLength() != 0) {
      owner.getTextArea().replaceText(selection, replacement);
    }
    replaceDialog.getNextButton().fire();
  }
}
