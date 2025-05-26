package com.tom.jpedit.gui.menu;

import com.tom.jpedit.Action;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.dialog.HelpDialog;
import com.tom.jpedit.handlers.misc.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class JPEditAdvancedMenu extends JPEditMenu {
  public JPEditAdvancedMenu(JPEditWindow owner, String s) {
    super(owner, s);
    final Menu autoSaveControlMenu = new Menu("Autosave");
    final RadioMenuItem autoSaveOnItem = new RadioMenuItem("Turn autosave on");
    final RadioMenuItem autoSaveOffItem = new RadioMenuItem("Turn autosave off");
    final MenuItem helpItem = new MenuItem("Help");
    final MenuItem addPluginItem = new MenuItem("Add Plugin (experimental)");
    final MenuItem removePluginItem = new MenuItem("Remove Plugin (experimental)");

    autoSaveOnItem.setSelected(true);

    autoSaveOnItem.setOnAction(event -> owner.turnAutoSaveOn());
    autoSaveOffItem.setOnAction(event -> owner.turnAutoSaveOff());

    autoSaveControlMenu.getItems().addAll(autoSaveOnItem, autoSaveOffItem);

    final MenuItem autoSaveTimeItem = new MenuItem("Change autosave delay");
    autoSaveTimeItem.setOnAction(new ChangeAutoSaveTimeActionHandler(owner));

    addPluginItem.setOnAction(new AddPluginActionHandler(owner));
    removePluginItem.setOnAction(new RemovePluginActionHandler(owner));

    helpItem.setOnAction((event) -> {
      HelpDialog helpDialog = new HelpDialog(owner);
      helpDialog.show();
    });
    JPEditWindow.actionControlMap().put(Action.SHOW_HELP_ACTION, helpItem);

    MenuItem addInternationalStringsItem = new MenuItem("Add International Strings");
    addInternationalStringsItem.setOnAction(new AddInternationalStringsHandler(owner));

    MenuItem changeLocaleItem = new MenuItem("Change Locale");
    changeLocaleItem.setOnAction(new ChangeLocaleHandler(owner));

    getItems().addAll(
        autoSaveControlMenu,
        autoSaveTimeItem,
        new SeparatorMenuItem(),
        addPluginItem,
        removePluginItem,
        new SeparatorMenuItem(),
        changeLocaleItem,
        addInternationalStringsItem,
        new SeparatorMenuItem(),
        helpItem
    );
  }
}
