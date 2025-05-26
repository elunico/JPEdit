package com.tom.jpedit.gui.menu;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.misc.FontChangeActionHandler;
import com.tom.jpedit.handlers.misc.ToggleWrapTextActionHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class JPEditFormatMenu extends JPEditMenu {
  public JPEditFormatMenu(JPEditWindow owner, String s) {
    super(owner, s);
    final MenuItem fontSelectItem = new MenuItem("Choose Font");
    final MenuItem toggleWrapText = new MenuItem("Toggle text wrap");

    fontSelectItem.setOnAction(new FontChangeActionHandler(owner));
    toggleWrapText.setOnAction(new ToggleWrapTextActionHandler(owner));

    getItems().addAll(fontSelectItem, new SeparatorMenuItem(), toggleWrapText);
  }
}
