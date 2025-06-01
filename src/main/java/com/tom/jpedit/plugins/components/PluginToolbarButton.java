package com.tom.jpedit.plugins.components;

import com.tom.jpedit.gui.JPEditWindow;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.event.EventHandler;

import javafx.event.ActionEvent;

public class PluginToolbarButton extends Button implements PluginOwnedComponent, Cloneable {
    private JPEditWindow owner;

    public PluginToolbarButton() {
        this("", null);
    }

    public PluginToolbarButton(String text, Node graphic) {
        super(text, graphic);
    }

    public PluginToolbarButton(String title) {
        this(title, null);
    }

    public PluginToolbarButton(String title, Node graphic, EventHandler<ActionEvent> action) {
        this(title, graphic);
        setOnAction(action);
    }

    @Override
    public PluginToolbarButton clone() {
        try {
            return (PluginToolbarButton) super.clone();
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
