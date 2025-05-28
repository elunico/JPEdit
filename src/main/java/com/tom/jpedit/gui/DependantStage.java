package com.tom.jpedit.gui;

import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents a stage which depends on other stages
 * These DependantStages will close when their owner is closed
 * They can be prevented from opening multiple times
 */
public abstract class DependantStage extends Stage {
    protected DependableStage owner;

    public DependantStage() {
        super();
    }

    public DependantStage(@Nullable DependableStage owner) {
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
