package com.tom.jpedit.gui.dialog;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.DependableStage;
import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.gui.FontUtil;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ChangeLocalePrompt extends DependantStage {
    public ChangeLocalePrompt(DependableStage owner) {
        super(owner);
        setTitle("Change Locale");
        var title = new Label("Choose Your Locale");
        title.setFont(FontUtil.getTitleLabelFont());

        var finePrint = new Label("If your locale does not appear, it is not currently supported. But you can add the strings for it if you would like.");
        finePrint.setFont(FontUtil.getNormalItalicFont());

        var warningLabel = new Label("YOU MUST RESTART TO SEE CHANGES");

        var choices = new ChoiceBox<String>();
        choices.getItems().addAll(InternationalLanguageSelector.getExistingLanguageCodes());
        choices.getSelectionModel().select(0);

        var okButton = new Button("OK");
        okButton.setOnAction(event -> {
            ApplicationContext.getContext().getUserPreferences().setPreferredLocale(choices.getSelectionModel().getSelectedItem());
        });

        var root = new VBox();

        root.getChildren().addAll(title, finePrint, warningLabel, choices, okButton);
        root.setSpacing(10);
        root.setPadding(new javafx.geometry.Insets(5));

        setScene(new Scene(root));



    }
}
