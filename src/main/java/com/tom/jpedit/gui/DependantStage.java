package com.tom.jpedit.gui;

import javafx.stage.Stage;

public abstract class DependantStage extends Stage {
  protected DependableStage owner;

  public DependantStage() {
    super();
  }

  public DependantStage(DependableStage owner) {
    super();
    this.owner = owner;
    if (owner != null) {
      owner.registerDependent(this);
    }
  }

  @Override
  public void close() {
    if (owner != null) {
      owner.deregisterDependent(this);
    }
    super.close();
  }
}
