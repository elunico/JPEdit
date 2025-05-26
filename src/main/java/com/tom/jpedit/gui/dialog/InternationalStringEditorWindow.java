package com.tom.jpedit.gui.dialog;

import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.gui.FontUtil;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.i18l.Strings;
import com.tom.jpedit.logging.JPLogger;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import tom.utils.file.FileUtilsKt;

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

        setTitle("Editing Strings for " + code);

        var root = new VBox();
        root.setPadding(new Insets(5.0));
        root.setSpacing(10.0);

        var langBox = new VBox();
        langBox.setPadding(new Insets(5.0));
        langBox.setSpacing(10.0);
        populateI18lStrings(Strings.getFileStrings(new Locale(code)), langBox);

        var saveButton = new Button("Save to disk");
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
                writeI18lStrings(filePath, map);
            });

        });
        Label titleLabel = new Label("GUI Strings for language  '" + code + "'");
        titleLabel.setFont(FontUtil.getTitleLabelFont());
        root.getChildren().add(titleLabel);
        Label messageLabel = new Label("Changes will not be saved until the button is pressed!");
        messageLabel.setFont(FontUtil.getNormalItalicFont());
        root.getChildren().add(messageLabel);
        root.getChildren().add(langBox);
        root.getChildren().add(new Region());
        root.getChildren().add(saveButton);

        setScene(new Scene(new ScrollPane(root)));
        setWidth(1000);
        setHeight(700);
    }

    private static void writeI18lStrings(File filePath, HashMap<String, String> map) {
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

    private void populateI18lStrings(Map<Strings.Content, String> strings, VBox stringBox) {
        for (var entry : strings.entrySet()) {
            var h = new HBox();
            TextField keyField = new TextField(entry.getKey().toString());
            keyField.setPrefWidth(300);
            TextField valueField = new TextField(entry.getValue());
            valueField.setPrefWidth(500);
            h.getChildren().addAll(keyField, valueField);
            h.setSpacing(10.0);
            var removeButton = new Button("Remove");
            removeButton.setOnAction((event) -> {
                stringBox.getChildren().remove(h);
            });
            h.getChildren().add(removeButton);
            stringBox.getChildren().add(h);
        }
    }

}
