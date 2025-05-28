package com.tom.jpedit.util;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNullElse;

/**
 * Used to parse the input of a change font prompt when the user chooses a font
 * <p>
 * Mainly used to pull information from the "style" attribute of {@link Font}
 * objects since it is complicated.
 * <p>
 * This class can take a {@link Font#getStyle()} string and turn it into individual
 * {@link FontWeight} objects and {@link FontPosture} objects.
 * <p>
 * It works by creating a {@code FontStyleParser} instance with the {@link String}
 * style from {@link Font#getStyle()} and then calling {@link FontStyleParser#parse()}
 * Once that is done you can use the {@link #getWeight()} and {@link #isItalic()}
 * methods to determine what the weight and posture is. These start out as
 * the default values so can always be accessed. Be sure to call parse first.
 * If parse fails for some reason (though it never should unless the format
 * of {@link Font#getStyle()} changes) {@link #getWeight()} will not return
 * null
 */
public class FontStyleParser {
    @Nullable
    @Contract(pure = true)
    private static FontWeight parseWeight(String text) {
        try {
            return FontWeight.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Nullable
    @Contract(pure = true)
    private static FontPosture parsePosture(String text) {
        try {
            return FontPosture.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    private final String styles;
    private FontWeight weight;
    private FontPosture posture;

    /**
     * Construct a new {@code FontStyleParser} with the given String.
     * This String should come from {@link Font#getStyle()}
     *
     * @param styles the {@link Font#getStyle()} style of a font
     */
    public FontStyleParser(String styles) {
        this(styles, FontWeight.NORMAL, FontPosture.REGULAR);
    }

    /**
     * Construct a new {@code FontStyleParser} with the given String.
     * This String should come from {@link Font#getStyle()}.
     * This constructor also provides default values for the {@link FontWeight}
     * and {@link FontPosture}. These will be used in the {@link #getWeight()}
     * and {@link #isItalic()} methods before the {@link #parse()} method is
     * called or if the {@link #parse()} method is called and it fails
     *
     * @param styles         the {@link Font#getStyle()} style of a font
     * @param defaultWeight  the default {@link FontWeight} to return
     * @param defaultPosture the default {@link FontPosture} to return
     */
    public FontStyleParser(String styles, FontWeight defaultWeight, FontPosture defaultPosture) {
        this.styles = styles;
        this.weight = defaultWeight;
        this.posture = defaultPosture;
    }

    /**
     * Parses the {@code styles} String given to this class in the constructor
     * and sets the appropriate fields of the class to the proper values given
     * this string.
     *
     * @return true if the parsing was accomplished successfully and false
     * if the parsing could not be accomplished in an expected way
     */
    public boolean parse() {
        String[] components = styles.split("\\s+");

        if (components.length >= 3) {
            return false;
        }

        if (components.length == 2) {
            FontWeight weight = parseWeight(components[0]);
            FontPosture posture = parsePosture(components[1]);

            if (weight != null && posture != null) {
                this.weight = weight;
                this.posture = posture;
                return true;
            }

            return false;
        }

        if (components.length == 1) {
            FontWeight weight = parseWeight(components[0]);
            FontPosture posture = parsePosture(components[0]);
            if (weight == null && posture == null) {
                return false;
            }
            this.posture = requireNonNullElse(posture, this.posture);
            this.weight = requireNonNullElse(weight, this.weight);
            return true;
        }

        return true;
    }

    /**
     * Returns the {@link FontWeight} specified by the {@link Font#getStyle()}
     * String that was given in construction of the {@code FontStyleParser}.
     * <p>
     * This method should be called after calling {@link #parse()}
     *
     * @return the {@link FontWeight} of the style
     */
    @NotNull
    public FontWeight getWeight() {
        return weight;
    }

    /**
     * Returns true if the {@link Font#getStyle()} specifies the font is
     * {@link FontPosture#ITALIC}
     * <p>
     * This method should be called after calling {@link #parse()}
     *
     * @return true if the Font style is italic
     */
    public boolean isItalic() {
        return posture == FontPosture.ITALIC;
    }
}
