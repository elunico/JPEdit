package com.tom.jpedit.gui.dialog;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.gui.FontUtil;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.components.OkButton;
import com.tom.jpedit.gui.i18n.Strings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import tom.javafx.JavaFXUtilsKt;

import java.io.File;

public class InternationalLanguageSelector extends DependantStage {

    private String languageCode = null;

    public String getLanguageCode() {
        return languageCode;
    }

    public InternationalLanguageSelector(JPEditWindow owner) {
        super(owner);

        var titleLabel = new Label(Strings.Content.CHANGE_LOCAL_PROMPT_CHOOSE_LABEL.text);
        titleLabel.setFont(FontUtil.getTitleLabelFont());

        var selectBox = new ChoiceBox<String>();

        selectBox.getItems().addAll(ApplicationContext.getExistingLanguageCodes());
        if (!selectBox.getItems().isEmpty()) {
            selectBox.getSelectionModel().select(0);
        }

        var button = new Button(Strings.Content.INTERNATIONAL_SELECT_ADD_BUTTON.text);
        button.setPrefWidth(150.0);
        button.setOnAction(event -> {
            languageCode = JavaFXUtilsKt.promptForInput(Strings.Content.INTERNATIONAL_SELECT_ADD_TITLE.text, Strings.Content.INTERNATIONAL_SELECT_ADD_HEADER.text, "");
            if (languageCode != null && !languageCode.isEmpty()) {
                languageCode = languageCode.toLowerCase();
                var newFile = new File("lang", languageCode + ".properties");
                try {
                    Strings.createTemplate(newFile);
                    selectBox.getItems().clear();
                    selectBox.getItems().addAll(ApplicationContext.getExistingLanguageCodes());
                    selectBox.getSelectionModel().select(languageCode);
                } catch (Exception e) {
                    JavaFXUtilsKt.popupAlert("Error creating new language file", "Error creating new language file: " + e.getMessage());
                }
            }

        });

        var okButton = new OkButton(event -> {
            languageCode = selectBox.getSelectionModel().getSelectedItem();
            close();
        });
        okButton.setPrefWidth(150.0);

        var root = new VBox(titleLabel, selectBox, button, new Region(), okButton);
        root.setSpacing(10);
        root.setPadding(new Insets(5));

        setScene(new Scene(root));
    }
}
