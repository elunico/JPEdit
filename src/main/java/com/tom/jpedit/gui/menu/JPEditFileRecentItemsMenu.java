package com.tom.jpedit.gui.menu;

import com.tom.jpedit.gui.JPEditWindow;

import static com.tom.jpedit.ApplicationContext.getContext;

public class JPEditFileRecentItemsMenu extends JPEditMenu {
    public JPEditFileRecentItemsMenu(JPEditWindow owner, String s) {
        super(owner, s);
        getItems().clear();
        getItems().addAll(getContext().fetchRecentItems(owner));
    }
}
