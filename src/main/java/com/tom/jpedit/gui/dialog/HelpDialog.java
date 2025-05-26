package com.tom.jpedit.gui.dialog;

import com.tom.jpedit.gui.DependableStage;
import com.tom.jpedit.gui.DependantStage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class HelpDialog extends DependantStage {
  private static final String shortcutExplanation =
      "F1 -> display this help dialog\n" +
          "CTRL + N -> create a new file (prompt to save will be shown)\n" +
          "CTRL + S -> save file\n" +
          "CTRL + O -> open file\n" +
          "CTRL + F -> find in file\n" +
          "CTRL + R -> find and replace in file\n" +
          "CTRL + T -> insert time & date\n" +
          "CTRL + W -> close window (prompt to save will be shown)\n" +
          "CTRL + SHIFT + N -> make a new window\n" +
          "CTRL + SHIFT + S -> save file as...\n" +
          "CTRL + SHIFT + F -> choose font\n" +
          "CTRL + C -> copy selected text if any\n" +
          "CTRL + X -> cut selected text if any\n" +
          "CTRL + V -> paste selected text if any";

  public HelpDialog(DependableStage owner) {
    super(owner);
    Label shortcutsHeader = new Label("Keyboard Shortcuts");
    shortcutsHeader.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault()
                                                                                          .getSize() * 1.3));
    Label shortcuts = new Label(shortcutExplanation);
    Label macNotice = new Label("On a Mac, swap CTRL for CMD for all shortcuts that use it");
    macNotice.setFont(Font.font(Font.getDefault().getFamily(), FontPosture.ITALIC, Font.getDefault().getSize() * 1.2));
    shortcuts.setFont(Font.font(Font.getDefault().getFamily(), Font.getDefault().getSize() * 1.25));
    Label author = new Label("JPEdit by Thomas Povinelli (c) 2019");
    author.setFont(Font.font(Font.getDefault().getSize()));
    Button okButton = new Button("Close");

    VBox root = new VBox(shortcutsHeader, shortcuts, macNotice, okButton, author);
    okButton.setOnAction(event -> close());

    author.setAlignment(Pos.CENTER_RIGHT);

    root.setPadding(new Insets(5));
    root.setSpacing(5);

    setTitle("Help");
    setScene(new Scene(root));
    owner.registerDependent(this);

  }
}


