package com.tom.jpedit.gui.menu;

import com.tom.jpedit.Action;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.dialog.HelpDialog;
import com.tom.jpedit.gui.i18n.Strings;
import com.tom.jpedit.handlers.misc.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class JPEditAdvancedMenu extends JPEditMenu {
  public JPEditAdvancedMenu(JPEditWindow owner, String s) {
    super(owner, s);
    final Menu autoSaveControlMenu = new Menu(Strings.Content.ADVANCED_MENU_AUTOSAVE_ITEM.text);
    final RadioMenuItem autoSaveOnItem = new RadioMenuItem(Strings.Content.ADVANCED_MENU_AUTOSAVE_ON_ITEM.text);
    final RadioMenuItem autoSaveOffItem = new RadioMenuItem(Strings.Content.ADVANCED_MENU_AUTOSAVE_OFF_ITEM.text);
    final MenuItem helpItem = new MenuItem(Strings.Content.HELP_ITEM.text);
    final MenuItem addPluginItem = new MenuItem(Strings.Content.ADVANCED_MENU_ADD_PLUGIN.text);
    final MenuItem removePluginItem = new MenuItem(Strings.Content.ADVANCED_MENU_REMOVE_PLUGIN.text);

    autoSaveOnItem.setSelected(true);

    autoSaveOnItem.setOnAction(event -> owner.turnAutoSaveOn());
    autoSaveOffItem.setOnAction(event -> owner.turnAutoSaveOff());

    autoSaveControlMenu.getItems().addAll(autoSaveOnItem, autoSaveOffItem);

    final MenuItem autoSaveTimeItem = new MenuItem(Strings.Content.ADVANCED_MENU_AUTOSAVE_DELAY_ITEM.text);
    autoSaveTimeItem.setOnAction(new ChangeAutoSaveTimeActionHandler(owner));

    addPluginItem.setOnAction(new AddPluginActionHandler(owner));
    removePluginItem.setOnAction(new RemovePluginActionHandler(owner));

    helpItem.setOnAction((event) -> {
      HelpDialog helpDialog = new HelpDialog(owner);
      helpDialog.show();
    });
    JPEditWindow.actionControlMap().put(Action.SHOW_HELP_ACTION, helpItem);

    MenuItem addInternationalStringsItem = new MenuItem(Strings.Content.ADVANCED_MENU_ADD_EDIT_STRINGS.text);
    addInternationalStringsItem.setOnAction(new AddInternationalStringsHandler(owner));

    MenuItem changeLocaleItem = new MenuItem(Strings.Content.ADVANCED_MENU_CHANGE_LOCALE.text);
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
