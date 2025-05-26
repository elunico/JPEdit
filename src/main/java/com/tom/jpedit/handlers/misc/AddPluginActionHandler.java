package com.tom.jpedit.handlers.misc;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import com.tom.jpedit.util.JPUtil;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

import java.io.File;

public class AddPluginActionHandler extends ActionHandler {
  public AddPluginActionHandler(JPEditWindow jpEditWindow) {
    super(jpEditWindow);
  }

  @Override
  public void handle(ActionEvent event) {
    FileChooser chooser = new FileChooser();
    File jar = chooser.showOpenDialog(owner);
    String mainClassName = JPUtil.mainClassFromJAR(jar);
    if (mainClassName == null) return;
    ApplicationContext.getContext().loadPluginClass(jar, mainClassName);

  }

}
