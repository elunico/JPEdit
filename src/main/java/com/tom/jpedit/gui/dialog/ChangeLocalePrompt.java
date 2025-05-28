package com.tom.jpedit.gui.dialog;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.DependableStage;
import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.gui.FontUtil;
import com.tom.jpedit.gui.components.OkButton;
import com.tom.jpedit.gui.confirmation.ConfirmationDialog;
import com.tom.jpedit.gui.confirmation.ConfirmationType;
import com.tom.jpedit.gui.i18n.Strings;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Prompts the user to change their Locale using a JavaFX Stage
 */
public class ChangeLocalePrompt extends DependantStage {
    public ChangeLocalePrompt(DependableStage owner) {
        super(owner);

        setTitle(Strings.Content.CHANGE_LOCAL_PROMPT_TITLE.text);
        var title = new Label(Strings.Content.FILE_MENU_ITEM_OPEN.text);
        title.setFont(FontUtil.getTitleLabelFont());

        var finePrint = new Label(Strings.Content.CHANGE_LOCAL_PROMPT_FINE_PRINT.text);
        finePrint.setFont(FontUtil.getNormalItalicFont());

        var warningLabel = new Label(Strings.Content.CHANGE_LOCAL_PROMPT_FINE_RESTART_WARNING.text);

        var choices = new ChoiceBox<String>();
        choices.getItems().addAll(ApplicationContext.getExistingLanguageCodes());
        choices.getSelectionModel().select(Strings.getLocale().getLanguage());

        var okButton = new OkButton(event -> {
            ApplicationContext.getContext()
                              .getUserPreferences()
                              .setPreferredLocale(choices.getSelectionModel().getSelectedItem());
            close();
            var w = new ConfirmationDialog(null, "Quit Program", "Ok to close?", "JPEdit needs to restart to display locale changes.\nDo you want to exit the program now?");
            var resp = w.showPrompt();
            if (resp.equals(ConfirmationType.YES)) {
                ApplicationContext.terminateEarly();
            } else {
                w.close();
            }
        });

        var root = new VBox();

        root.getChildren().addAll(title, finePrint, warningLabel, choices, okButton);
        root.setSpacing(10);
        root.setPadding(new javafx.geometry.Insets(5));

        setScene(new Scene(root));
    }
}
