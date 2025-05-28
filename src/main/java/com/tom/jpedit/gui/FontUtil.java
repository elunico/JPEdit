package com.tom.jpedit.gui;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class FontUtil {
    /**
     * Returns a javafx font used for title labels
     *
     * @return Font object for titles
     */
    public static Font getTitleLabelFont() {
        return Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize() * 1.5);
    }

    /**
     * Returns a javafx font used for italics
     *
     * @return an italic but otherwise plain font
     */
    public static Font getNormalItalicFont() {
        return Font.font(Font.getDefault().getFamily(), FontPosture.ITALIC, Font.getDefault().getSize());
    }
}
