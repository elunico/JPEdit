package com.tom.jpedit.plugins.components;

import com.tom.jpedit.Action;
import com.tom.jpedit.gui.JPEditWindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;


public class PluginMenuItem extends MenuItem implements PluginOwnedComponent, Cloneable {

    private JPEditWindow owner;

    public PluginMenuItem(String itemName) {
        super(itemName);
    }

    public PluginMenuItem(String itemName, EventHandler<ActionEvent> handler) {
        this(itemName);
        setOnAction(handler);
    }

    @Override
    public PluginMenuItem clone() {
        try {
            return (PluginMenuItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public JPEditWindow getOwner() {
        return owner;
    }

    public void setOwner(JPEditWindow owner) {
        this.owner = owner;
    }

}
