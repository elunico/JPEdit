package com.tom.jpedit.gui.dialog;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.DependableStage;
import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.gui.i18n.Strings;
import com.tom.jpedit.util.Version;
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
    private static final String shortcutExplanation = Strings.Content.HELP_MENU_BLURB.text;

    public HelpDialog(DependableStage owner) {
        super(owner);
        Label shortcutsHeader = new Label("Keyboard Shortcuts");
        shortcutsHeader.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault()
                                                                                              .getSize() * 1.3));
        Label shortcuts = new Label(shortcutExplanation);
        Label macNotice = new Label("On a Mac, swap CTRL for CMD for all shortcuts that use it");
        macNotice.setFont(Font.font(Font.getDefault().getFamily(), FontPosture.ITALIC, Font.getDefault()
                                                                                           .getSize() * 1.2));
        shortcuts.setFont(Font.font(Font.getDefault().getFamily(), Font.getDefault().getSize() * 1.25));
        Label author = new Label("JPEdit " + ApplicationContext.VERSION + " " + Strings.Content.COPYRIGHT_TEXT.text);
        author.setFont(Font.font(Font.getDefault().getSize()));
        Button okButton = new Button(Strings.Content.FILE_MENU_ITEM_CLOSE.text);

        VBox root = new VBox(shortcutsHeader, shortcuts, macNotice, okButton, author);
        okButton.setOnAction(event -> close());

        author.setAlignment(Pos.CENTER_RIGHT);

        root.setPadding(new Insets(5));
        root.setSpacing(5);

        setTitle(Strings.Content.HELP_ITEM.text);
        setScene(new Scene(root));
        owner.registerDependent(this);

    }
}


