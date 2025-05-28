package com.tom.jpedit.listeners;

import com.tom.jpedit.gui.JPEditWindow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;

public class TextAreaTextChangeListener implements ChangeListener<String> {
    public static final int MAX_COMPARE_LENGTH = 1000;
    private final JPEditWindow jpEditWindow;

    public TextAreaTextChangeListener(JPEditWindow jpEditWindow) {
        this.jpEditWindow = jpEditWindow;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, @NotNull String oldValue, String newValue) {
        if (oldValue.length() != newValue.length()) {
            jpEditWindow.wasDirtied();
            return;
        }

        if (oldValue.length() > MAX_COMPARE_LENGTH) {
            jpEditWindow.wasDirtied();
            return;
        }

        if (!oldValue.equals(newValue)) {
            jpEditWindow.wasDirtied();
        }
    }
}
