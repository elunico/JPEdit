package com.tom.jpedit.handlers;

import com.tom.jpedit.gui.JPEditWindow;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class ActionHandler implements EventHandler<ActionEvent> {

  protected final JPEditWindow owner;

  protected ActionHandler(JPEditWindow owner) {
    this.owner = owner;
  }

  @NotNull
  @Contract("_ -> new")
  public static ActionEvent asAction(@NotNull Event event) {
    return new ActionEvent(event.getSource(), event.getTarget());
  }

  @Override
  public abstract void handle(ActionEvent event);
}
