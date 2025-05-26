package com.tom.jpedit.gui.menu;

import com.tom.jpedit.Action;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.edit.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class JPEditEditMenu extends JPEditMenu {
  public JPEditEditMenu(JPEditWindow owner, String s) {
    super(owner, s);
    final MenuItem cutItem = new MenuItem("Cut");
    final MenuItem copyItem = new MenuItem("Copy");
    final MenuItem pasteItem = new MenuItem("Paste");
    final MenuItem selectAllItem = new MenuItem("Select All");
    cutItem.setOnAction(e -> owner.getTextArea().cut());
    copyItem.setOnAction(e -> owner.getTextArea().copy());
    pasteItem.setOnAction(new PasteActionHandler(owner));
    selectAllItem.setOnAction(new SelectAllActionHandler(owner));

    final MenuItem findItem = new MenuItem("Find");
    final MenuItem replaceItem = new MenuItem("Replace");
    findItem.setOnAction(new FindActionHandler(owner));
    replaceItem.setOnAction(new ReplaceActionHandler(owner));

    final MenuItem timeDateItem = new MenuItem("Insert Time & Date");
    timeDateItem.setOnAction(new TimeDateActionHandler(owner));

    getItems().addAll(
        cutItem,
        copyItem,
        pasteItem,
        selectAllItem,
        new SeparatorMenuItem(),
        findItem,
        replaceItem,
        new SeparatorMenuItem(),
        timeDateItem
    );

    JPEditWindow.actionControlMap().put(Action.FIND_ACTION, findItem);
    JPEditWindow.actionControlMap().put(Action.REPLACE_ACTION, replaceItem);
    JPEditWindow.actionControlMap().put(Action.INSERT_TIME_DATE_ACTION, timeDateItem);

  }
}
