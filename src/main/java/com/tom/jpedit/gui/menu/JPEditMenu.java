package com.tom.jpedit.gui.menu;

import com.tom.jpedit.gui.JPEditWindow;
import javafx.scene.control.Menu;

public abstract class JPEditMenu extends Menu {
  private final JPEditWindow owner;

  public JPEditMenu(JPEditWindow owner, String s) {
    super(s);
    this.owner = owner;
  }
}
