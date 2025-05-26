package com.tom.jpedit.gui;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class FontUtil {

    public static Font getTitleLabelFont() {
        return Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize() * 1.5);
    }

    public static Font getNormalItalicFont() {
        return Font.font(Font.getDefault().getFamily(), FontPosture.ITALIC, Font.getDefault().getSize());
    }
}
