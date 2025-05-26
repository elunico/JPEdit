package com.tom.jpedit.gui.dialog;

import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.gui.JPEditWindow;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Optional;

public class TimeDatePrompt extends DependantStage {
  private static final List<String> formats = List.of(
      "MM-dd-yyyy hh:mm a",
      "MM/dd/yy",
      "HH:mm",
      "hh:mm a",
      "yyyy-MM-dd",
      "yyyy-MM-dd hh:mm a",
      "yyyy-MM-dd HH:mm",
      "EEE MM-dd-yyyy hh:mm a",
      "EEE yyyy-MM-dd hh:mm a",
      "EEE MM/dd/yy hh:mm a",
      "EEE yy/MM/dd hh:mm a",
      "EEE MM-dd-yyyy HH:mm",
      "EEE yyyy-MM-dd HH:mm",
      "EEE MM/dd/yy HH:mm",
      "EEE yy/MM/dd HH:mm"
  );
  private String result;

  public TimeDatePrompt(JPEditWindow owner) {
    ChoiceBox<String> choiceBox = new ChoiceBox<>(FXCollections.observableArrayList(formats));
    Label helpLabel = new Label("Choose your Time & Date format (see below)");
    helpLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize() * 1.2));
    Label infoLabel = new Label(
        "yyyy = Year\nMM = Month number\ndd = Day of month\nEEE = Day of week\na = AM/PM\nH = 24 hour (0-23)\nh = 12 hour (1-12)\nmm = Minutes\nss = Seconds");
    Button goButton = new Button("Insert");
    Button cancelButton = new Button("Cancel");
    GridPane root = new GridPane();
    styleElements(choiceBox, goButton, cancelButton, root);
    root.add(helpLabel, 0, 0, 2, 1);
    root.add(infoLabel, 0, 1, 2, 1);
    root.add(choiceBox, 0, 2, 2, 1);
    root.add(cancelButton, 0, 3, 1, 1);
    root.add(goButton, 1, 3, 1, 1);

    cancelButton.setOnAction(event -> result = null);
    goButton.setOnAction(event -> {
      result = choiceBox.getValue();
      close();
    });

    setScene(new Scene(root));
    owner.registerDependent(this);
  }

  private void styleElements(ChoiceBox<?> box, Button goButton, Button cancelButton, GridPane root) {
    root.setVgap(5);
    root.setHgap(5);
    root.setPadding(new Insets(5));
    double rootEmptySpace = root.getHgap() + root.getPadding().getLeft() + root.getPadding().getRight();
    box.prefWidthProperty().bind(root.widthProperty().subtract(rootEmptySpace));
    goButton.prefWidthProperty()
            .bind(root.widthProperty()
                      .subtract(rootEmptySpace)
                      .divide(2));
    cancelButton.prefWidthProperty()
                .bind(root.widthProperty()
                          .subtract(rootEmptySpace)
                          .divide(2));
    root.setMinWidth(375);
    setTitle("Insert Time & Date");
  }

  public Optional<String> prompt() {
    showAndWait();
    return Optional.ofNullable(result);
  }
}
