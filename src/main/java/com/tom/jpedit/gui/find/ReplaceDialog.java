package com.tom.jpedit.gui.find;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.edit.FindDialogNextActionHandler;
import com.tom.jpedit.handlers.edit.FindDialogPreviousActionHandler;
import com.tom.jpedit.handlers.edit.ReplaceAllItemHandler;
import com.tom.jpedit.handlers.edit.ReplaceItemHandler;
import com.tom.jpedit.util.FindCache;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

public class ReplaceDialog extends FindReplaceBase {
  private final TextField replaceTextText;
  private final Button nextButton;

  public ReplaceDialog(@NotNull JPEditWindow owner) {
    super.owner = owner;
    super.setTitle("Replace");
    GridPane root = new GridPane();
    root.setHgap(5);
    root.setVgap(5);
    root.setPadding(new Insets(5));
    Label findTextLabel = new Label("Find: ");
    Label replaceTextLabel = new Label("Replace: ");
    findTextText = new TextField();
    replaceTextText = new TextField();
    caseInsensitiveBox = new CheckBox("Ignore case?");
    GridPane.setColumnSpan(caseInsensitiveBox, 2);
    GridPane.setColumnSpan(findTextText, 2);
    GridPane.setColumnSpan(replaceTextText, 2);
    nextButton = new Button("Find Next");
    Button replaceButton = new Button("Replace");
    Button replaceAllButton = new Button("Replace All");
    Button previousButton = new Button("Find Previous");

    replaceButton.setPrefWidth(125);
    replaceAllButton.setPrefWidth(125);
    nextButton.setPrefWidth(125);
    previousButton.setPrefWidth(125);

    nextButton.setOnAction(new FindDialogNextActionHandler(owner, this));
    previousButton.setOnAction(new FindDialogPreviousActionHandler(owner, this));

    replaceButton.setOnAction(new ReplaceItemHandler(owner, this));
    replaceAllButton.setOnAction(new ReplaceAllItemHandler(owner, this));

    root.add(findTextLabel, 0, 0);
    root.add(findTextText, 1, 0);
    root.add(replaceTextLabel, 0, 1);
    root.add(replaceTextText, 1, 1);
    root.add(caseInsensitiveBox, 1, 2);
    root.add(nextButton, 1, 3);
    root.add(previousButton, 2, 3);
    root.add(replaceButton, 1, 4);
    root.add(replaceAllButton, 1, 5);

    setScene(new Scene(root));
    cache = new FindCache(0);
    owner.registerDependent(this);

  }

  public FindCache getCache() {
    return cache;
  }

  public TextField getFindTextText() {
    return findTextText;
  }

  public CheckBox getCaseInsensitiveBox() {
    return caseInsensitiveBox;
  }

  public TextField getReplaceTextText() {
    return replaceTextText;
  }

  public Button getNextButton() {
    return nextButton;
  }

  public void clearCache() {
    super.getCache().setLastStop(0);
  }
}
