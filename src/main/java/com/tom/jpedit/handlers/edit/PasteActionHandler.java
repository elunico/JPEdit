package com.tom.jpedit.handlers.edit;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

import java.io.File;
import java.util.stream.Collectors;

public class PasteActionHandler extends ActionHandler {

    public PasteActionHandler(JPEditWindow jpEditWindow) {
        super(jpEditWindow);
    }

    private String getRichestData(Clipboard clipboard) {
        if (clipboard.hasContent(DataFormat.HTML)) {
            return clipboard.getHtml();
        } else if (clipboard.hasContent(DataFormat.URL)) {
            return clipboard.getUrl();
        } else if (clipboard.hasContent(DataFormat.FILES)) {
            return clipboard.getFiles().stream().map(File::getAbsolutePath).collect(Collectors.joining(", "));
        } else if (clipboard.hasContent(DataFormat.RTF)) {
            return clipboard.getRtf();
        } else if (clipboard.hasContent(DataFormat.PLAIN_TEXT)) {
            return clipboard.getString();
        }
        return "";
    }

    @Override
    public void handle(ActionEvent event) {
        owner.getTextArea()
             .insertText(owner.getTextArea().getCaretPosition(), getRichestData(Clipboard.getSystemClipboard()));
    }
}
