package com.tom.jpedit.gui.dialog;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.components.CancelButton;
import com.tom.jpedit.gui.components.OkButton;
import com.tom.jpedit.gui.i18n.Strings;
import com.tom.jpedit.util.FontStyleParser;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Povinelli
 * Created 4/5/19
 * In JPEdit
 */
public class FontPrompt extends DependantStage {

    private final ChoiceBox<String> fontFamilyBox;
    private final TextField fontSizeField;
    private final ChoiceBox<FontWeight> fontWeightBox;
    private final CheckBox fontItalicBox;
    private final JPEditWindow owner;

    public FontPrompt(@NotNull JPEditWindow owner) {
        this.owner = owner;
        super.setTitle(Strings.Content.FONT_PROMPT_TITLE.text);
        GridPane root = new GridPane();
        Label fontFamilyLabel = new Label(Strings.Content.FONT_PROMPT_FAMILY_LABEL.text);
        Label fontSizeLabel = new Label(Strings.Content.FONT_PROMPT_SIZE_LABEL.text);
        Label fontWeightLabel = new Label(Strings.Content.FONT_PROMPT_WEIGHT_LABEL.text);
        Label fontPostureLabel = new Label(Strings.Content.FONT_PROMPT_POSTURE_LABEL.text);

        fontFamilyBox = new ChoiceBox<>(FXCollections.observableArrayList(Font.getFamilies()));
        fontSizeField = new TextField();
        fontWeightBox = new ChoiceBox<>(FXCollections.observableArrayList(FontWeight.values()));
        fontItalicBox = new CheckBox();

        Font currentFont = owner.getTextArea().getFont();

        FontStyleParser parser = new FontStyleParser(currentFont.getStyle());

        parser.parse();

        fontFamilyBox.setValue(currentFont.getFamily());
        fontSizeField.setText(String.valueOf(currentFont.getSize()));
        fontWeightBox.setValue(parser.getWeight());
        fontItalicBox.setSelected(parser.isItalic());

        root.add(fontFamilyLabel, 0, 0);
        root.add(fontSizeLabel, 0, 1);
        root.add(fontWeightLabel, 0, 2);
        root.add(fontPostureLabel, 0, 3);
        root.add(fontFamilyBox, 1, 0);
        root.add(fontSizeField, 1, 1);
        root.add(fontWeightBox, 1, 2);
        root.add(fontItalicBox, 1, 3);

        Button cancelButton = new CancelButton(e -> close());
        Button okButton = new OkButton(e -> {
            setFont();
            close();
        });

        root.setAlignment(Pos.CENTER);

        root.add(cancelButton, 0, 4);
        root.add(okButton, 1, 4);

        root.setHgap(10);
        root.setVgap(10);

        root.setPadding(new Insets(5));

        Scene scene = new Scene(root);

        setScene(scene);
        sizeToScene();
        owner.registerDependent(this);
    }

    private void setFont() {
        String familyName = fontFamilyBox.getValue();
        double size = Double.parseDouble(fontSizeField.getText());
        FontWeight weight = fontWeightBox.getValue();
        FontPosture posture = fontItalicBox.isSelected() ? FontPosture.ITALIC : FontPosture.REGULAR;
        Font font = Font.font(familyName, weight, posture, size);
        ApplicationContext.getContext().getUserPreferences().setPreferredFont(font);
        owner.getTextArea().setFont(font);
    }

}
