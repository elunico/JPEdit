package com.tom.jpedit.gui.find;

import com.tom.jpedit.gui.DependableStage;
import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.util.FindCache;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public abstract class FindReplaceBase extends DependantStage {
    protected TextField findTextText;
    protected FindCache cache;
    protected CheckBox caseInsensitiveBox;
    protected DependableStage owner;

    public FindReplaceBase() {
    }

    public TextField getFindTextText() {
        return findTextText;
    }

    public FindCache getCache() {
        return cache;
    }

    public CheckBox getCaseInsensitiveBox() {
        return caseInsensitiveBox;
    }
}
