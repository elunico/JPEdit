package com.tom.jpedit.handlers.edit;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.dialog.TimeDatePrompt;
import com.tom.jpedit.handlers.ActionHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public class TimeDateActionHandler extends ActionHandler {
  public TimeDateActionHandler(JPEditWindow owner) {
    super(owner);
  }

  @Override
  public void handle(ActionEvent event) {
    TimeDatePrompt prompt = new TimeDatePrompt(owner);

    Optional<String> format = prompt.prompt();

    if (format.isPresent()) {
      String s = format.get();
      TextArea textArea = owner.getTextArea();
      DateTimeFormatter formatPattern = DateTimeFormatter.ofPattern(s, Locale.getDefault());
      String time = LocalDateTime.now().format(formatPattern);
      textArea.insertText(textArea.getCaretPosition(), time);
    }
  }
}
