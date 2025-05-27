package com.tom.jpedit.gui.dialog;

import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.gui.FontUtil;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.i18n.Strings;
import com.tom.jpedit.logging.JPLogger;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InternationalStringEditorWindow extends DependantStage {


    public InternationalStringEditorWindow(JPEditWindow owner, String code) {
        super(owner);

        setTitle(Strings.Content.INTERNATIONAL_STRING_EDITOR_TITLE.text + code);

        var root = new VBox();
        root.setPadding(new Insets(5.0));
        root.setSpacing(10.0);

        var langBox = new VBox();
        langBox.setPadding(new Insets(5.0));
        langBox.setSpacing(10.0);
        populateI18nStrings(Strings.getFileStrings(new Locale(code)), langBox);

        var saveButton = new Button(Strings.Content.BUTTON_SAVE_DISK.text);
        saveButton.setPrefWidth(150.0);
        saveButton.setOnAction((event) -> {
            var p = langBox.getChildrenUnmodifiable().stream().filter((it) -> it instanceof HBox);
            var map = new HashMap<String, String>();
            p.forEach(it -> {
                var h = (HBox) it;
                var k = ((TextField) h.getChildrenUnmodifiable().get(0)).getText();
                var v = ((TextField) h.getChildrenUnmodifiable().get(1)).getText();
                map.put(k, v);
                var filePath = new File("lang/" + code + ".properties");
                writeI18nStrings(filePath, map);
            });

        });
        Label titleLabel = new Label(Strings.Content.INTERNATIONAL_STRING_EDITOR_INFO_LABEL.text + Strings.Content.INTERNATIONAL_STRING_EDITOR_CODE_DELIM_LEFT + code + Strings.Content.INTERNATIONAL_STRING_EDITOR_CODE_DELIM_RIGHT);
        titleLabel.setFont(FontUtil.getTitleLabelFont());
        root.getChildren().add(titleLabel);
        Label messageLabel = new Label(Strings.Content.SAVE_WARNING_MESSAGE.text);
        messageLabel.setFont(FontUtil.getNormalItalicFont());
        root.getChildren().add(messageLabel);
        root.getChildren().add(langBox);
        root.getChildren().add(new Region());
        root.getChildren().add(saveButton);

        setScene(new Scene(new ScrollPane(root)));
        setWidth(1000);
        setHeight(700);
    }

    private static void writeI18nStrings(File filePath, HashMap<String, String> map) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (var entry: map.entrySet()) {
                String line = entry.getKey() + "=" + entry.getValue();
                writer.write(line);
                writer.newLine(); // Add a new line character
            }
            JPLogger.getAppLog().info("Strings written to file: " + filePath.getAbsolutePath());
        } catch (IOException e) {
            JPLogger.getErrLog().severe("An error occurred: " + e.getMessage());
        }
    }

    private void populateI18nStrings(Map<Strings.Content, String> strings, VBox stringBox) {
        for (var entry : strings.entrySet()) {
            var h = new HBox();
            TextField keyField = new TextField(entry.getKey().toString());
            keyField.setPrefWidth(300);
            TextField valueField = new TextField(entry.getValue());
            valueField.setPrefWidth(500);
            h.getChildren().addAll(keyField, valueField);
            h.setSpacing(10.0);
            var removeButton = new Button(Strings.Content.BUTTON_REMOVE.text);
            removeButton.setOnAction((event) -> {
                stringBox.getChildren().remove(h);
            });
            h.getChildren().add(removeButton);
            stringBox.getChildren().add(h);
        }
    }

}
