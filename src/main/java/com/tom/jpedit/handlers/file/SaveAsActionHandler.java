package com.tom.jpedit.handlers.file;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import tom.javafx.JavaFXUtilsKt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Thomas Povinelli
 * Created 4/5/19
 * In JPEdit
 */
public class SaveAsActionHandler extends ActionHandler {


    public SaveAsActionHandler(JPEditWindow owner) {
        super(owner);
    }

    public File getFileFromUser(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        return chooser.showSaveDialog(owner);
    }

    @Override
    public void handle(ActionEvent event) {
        File saveFile = getFileFromUser(event);
        if (saveFile == null) {
            JavaFXUtilsKt.popupAlert("No file chosen. File NOT saved", "NOT SAVED");
            return;
        }
        saveAs(saveFile);
    }

    boolean saveAs(@NotNull File saveFile) {
        String text = owner.getTextArea().getText();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            writer.write(text);
            owner.setSaveFile(saveFile);
            owner.updateTitleForSave();
            owner.saveUpdated();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
