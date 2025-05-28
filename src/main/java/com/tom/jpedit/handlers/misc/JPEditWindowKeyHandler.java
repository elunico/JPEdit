package com.tom.jpedit.handlers.misc;

import com.tom.jpedit.Action;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.dialog.FontPrompt;
import com.tom.jpedit.handlers.ActionHandler;
import com.tom.jpedit.handlers.edit.PasteActionHandler;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import tom.javafx.JavaFXUtilsKt;
import tom.utils.os.OSUtilsKt;

public class JPEditWindowKeyHandler implements EventHandler<KeyEvent> {
    private final JPEditWindow jpEditWindow;

    public JPEditWindowKeyHandler(JPEditWindow jpEditWindow) {
        this.jpEditWindow = jpEditWindow;
    }

    private boolean controlEquivalentModifierDown(KeyEvent event) {
        return OSUtilsKt.isMacOSX() ? event.isMetaDown() : event.isControlDown();
    }

    @Override
    public void handle(@NotNull KeyEvent event) {
        if (event.getCode() == KeyCode.F1) {
            jpEditWindow.fire(Action.SHOW_HELP_ACTION);
        }
        if (JavaFXUtilsKt.isCopyEvent(event)) {
            jpEditWindow.getTextArea().copy();
        } else if (JavaFXUtilsKt.isCutEvent(event)) {
            jpEditWindow.getTextArea().cut();
        } else if (JavaFXUtilsKt.isPasteEvent(event)) {
            new PasteActionHandler(jpEditWindow).handle(ActionHandler.asAction(event));
        }
        if (controlEquivalentModifierDown(event) && event.getCode() == KeyCode.W && !event.isShiftDown() && !event.isAltDown()) {
            System.out.println("Fired cmd w");
            Event.fireEvent(jpEditWindow, new WindowEvent(jpEditWindow, WindowEvent.WINDOW_CLOSE_REQUEST));
            event.consume();
            return;
        }
        if (controlEquivalentModifierDown(event) && !event.isShiftDown()) {
            switch (event.getCode()) {
                case F:
                    jpEditWindow.fire(Action.FIND_ACTION);
                    break;
                case R:
                    jpEditWindow.fire(Action.REPLACE_ACTION);
                    break;
                case N:
                    jpEditWindow.fire(Action.NEW_ACTION);
                    break;
                case O:
                    jpEditWindow.fire(Action.OPEN_ACTION);
                    break;
                case S:
                    jpEditWindow.fire(Action.SAVE_ACTION);
                    break;
                case T:
                    jpEditWindow.fire(Action.INSERT_TIME_DATE_ACTION);
                    break;
                case W:
                    jpEditWindow.close();
                    break;
            }
        } else if (controlEquivalentModifierDown(event) && event.isShiftDown()) {
            if (event.getCode() == KeyCode.N) {
                jpEditWindow.fire(Action.NEW_WINDOW_ACTION);
            } else if (event.getCode() == KeyCode.S) {
                jpEditWindow.fire(Action.SAVE_AS_ACTION);
            } else if (event.getCode() == KeyCode.F) {
                FontPrompt prompt = new FontPrompt(jpEditWindow);
                prompt.show();
            }
        }
    }
}
