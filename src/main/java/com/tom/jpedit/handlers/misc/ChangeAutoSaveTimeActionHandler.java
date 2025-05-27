package com.tom.jpedit.handlers.misc;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import com.tom.jpedit.logging.JPLogger;
import javafx.event.ActionEvent;
import tom.javafx.JavaFXUtilsKt;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static tom.javafx.JavaFXUtilsKt.popupAlert;

public class ChangeAutoSaveTimeActionHandler extends ActionHandler {
  public ChangeAutoSaveTimeActionHandler(JPEditWindow jpEditWindow) {
    super(jpEditWindow);
  }

  @Override
  public void handle(ActionEvent event) {
    boolean invalid = true;
    while (invalid) {
      Optional<String> timeIn = JavaFXUtilsKt.promptForInputOptional(
          "Enter Seconds",
          "Enter new autosave interval",
          "Enter the number of SECONDS to wait in between autosaves.\nThis will apply to all windows present and future"
      );
      if (timeIn.isEmpty()) {
        break;
      }
      try {
        int newTime = Integer.parseInt(timeIn.get());
        if (newTime < 1 || newTime > 3600) {
          throw new NumberFormatException("Invalid Range");
        }
        ApplicationContext.getContext().getUserPreferences().setAutosavePeriodMillis(newTime * 1000);
        ApplicationContext.getContext()
                          .getWindowsUnmodifiable()
                          .forEach(window -> window.getAutoSaveWorker().scheduleTask(newTime, TimeUnit.SECONDS));
        invalid = false;
        JPLogger.getAppLog()
                .info("Autosave delay changed to: " + ApplicationContext.getContext()
                                                                        .getUserPreferences()
                                                                        .getAutosavePeriodMillis() + "ms");
      } catch (NumberFormatException e) {
        popupAlert(
            "Time " + timeIn.get() + " is not valid. Make it an integer between 1 and 3600",
            "Invalid Time",
            true
        );
      }
    }
  }
}
