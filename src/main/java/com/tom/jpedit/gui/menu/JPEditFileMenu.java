package com.tom.jpedit.gui.menu;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.i18l.Strings;
import com.tom.jpedit.handlers.file.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class JPEditFileMenu extends JPEditMenu {
  public JPEditFileMenu(JPEditWindow owner, String s) {
    super(owner, s);
    final MenuItem newItem = new MenuItem(Strings.Content.FILE_MENU_ITEM_NEW.text);
    final MenuItem newWindowItem = new MenuItem(Strings.Content.FILE_MENU_ITEM_NEW_WINDOW.text);
    final MenuItem openItem = new MenuItem(Strings.Content.FILE_MENU_ITEM_OPEN.text);
    final Menu openRecentMenu = new JPEditFileRecentItemsMenu(owner, Strings.Content.FILE_MENU_OPEN_RECENT.text);
    final MenuItem saveItem = new MenuItem(Strings.Content.FILE_MENU_ITEM_SAVE.text);
    final MenuItem saveAsItem = new MenuItem(Strings.Content.FILE_MENU_ITEM_SAVE_AS.text);
    final MenuItem duplicateItem = new MenuItem(Strings.Content.FILE_MENU_DUPLICATE_WINDOW.text);
    final MenuItem printItem = new MenuItem(Strings.Content.FILE_MENU_PRINT.text);
    final MenuItem clearRecentMenuItem = new MenuItem(Strings.Content.FILE_MENU_CLEAR_RECENTS.text);
    final MenuItem toggleFullPathItem = new MenuItem(Strings.Content.FILE_MENU_PATH_TOGGLE.text);
    final MenuItem quitItem = new MenuItem(Strings.Content.FILE_MENU_ITEM_CLOSE.text);

    newItem.setOnAction(new NewActionHandler(owner));
    newWindowItem.setOnAction(new NewWindowActionHandler(owner));
    openItem.setOnAction(new OpenActionHandler(owner));
    saveItem.setOnAction(new SaveActionHandler(owner));
    saveAsItem.setOnAction(new SaveAsActionHandler(owner));
    duplicateItem.setOnAction(new DuplicateWindowActionHandler(owner));
    printItem.setOnAction(new PrintActionHandler(owner));
    clearRecentMenuItem.setOnAction(new ClearRecentFilesHandler(owner));
    toggleFullPathItem.setOnAction(new ToggleFullPathHandler(owner));
    quitItem.setOnAction(new CloseWindowActionHandler(owner));

    getItems().addAll(
        newItem,
        newWindowItem,
        openItem,
        openRecentMenu,
        saveItem,
        saveAsItem,
        duplicateItem,
        new SeparatorMenuItem(),
        printItem,
        new SeparatorMenuItem(),
        clearRecentMenuItem,
        new SeparatorMenuItem(),
        toggleFullPathItem,
        new SeparatorMenuItem(),
        quitItem
    );
  }
}
