package com.tom.jpedit.gui.confirmation;

import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.logging.JPLogger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfirmationDialog extends DependantStage {
  private final String title;
  private final String head;
  private final String message;
  private final JPEditWindow owner;

  public ConfirmationDialog(@Nullable JPEditWindow owner, String title, String head, String message) {
    this.owner = owner;
    this.title = title;
    this.head = head;
    this.message = message;
    if (owner != null) {
      owner.registerDependent(this);
    }
  }

  public @NotNull ConfirmationType showPrompt() {
    if (isAlreadyShowing()) {
      return ConfirmationType.CANCEL;
    }
    Stage e = new Stage();
    VBox box = new VBox();
    GridPane buttonPane = new GridPane();
    box.setPadding(new Insets(15.0));
    box.setSpacing(15.0);
    box.setAlignment(Pos.CENTER);

    buttonPane.setPadding(new Insets(5.0));
    buttonPane.setVgap(5.0);
    buttonPane.setHgap(5.0);
    buttonPane.setAlignment(Pos.CENTER);

    Label headLabel = new Label(head);
    headLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize() * 1.2));

    Label messageLabel = new Label(message);

    var ref = new Object() {
      ConfirmationType confirmed = ConfirmationType.CANCEL;
    };

    Button noButton = new Button("No");
    noButton.setOnAction(i -> {
      ref.confirmed = ConfirmationType.NO;
      e.close();
    });

    Button yesButton = new Button("Yes");
    yesButton.setOnAction(i -> {
      ref.confirmed = ConfirmationType.YES;
      e.close();
    });

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(i -> {
      ref.confirmed = ConfirmationType.CANCEL;
      e.close();
    });

    buttonPane.add(cancelButton, 0, 0);
    buttonPane.add(noButton, 1, 0);
    buttonPane.add(yesButton, 2, 0);
    box.getChildren().addAll(headLabel, messageLabel, buttonPane);

    Scene scene = new Scene(box);
    e.setScene(scene);

    e.setTitle(title);

    e.showAndWait();
    JPLogger.debug(JPLogger.getAppLog(), "confirm = " + ref.confirmed);
    return ref.confirmed;
  }

  private boolean isAlreadyShowing() {
    boolean alreadyShowing = false;
    if (owner != null) {
      for (Stage stage : owner.getDependentsUnmodifiable()) {
        if (stage instanceof ConfirmationDialog && stage != this) {
          alreadyShowing = true;
          stage.setAlwaysOnTop(true);
          stage.setAlwaysOnTop(false);
        }
      }
    }
    return alreadyShowing;
  }
}
