package com.tom.jpedit.handlers.misc;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.dialog.InternationalLanguageSelector;
import com.tom.jpedit.gui.dialog.InternationalStringEditorWindow;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;

public class AddInternationalStringsHandler extends ActionHandler {
    public AddInternationalStringsHandler(JPEditWindow owner) {
        super(owner);
    }

    @Override
    public void handle(ActionEvent event) {
        var loc = new InternationalLanguageSelector(owner);
        loc.showAndWait();
        var code = loc.getLanguageCode();
        if (code == null) return;
        new InternationalStringEditorWindow(owner, code).show();
    }
}
