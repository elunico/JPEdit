package com.tom.jpedit.util;

import com.tom.jpedit.logging.JPLogger;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Locale;
import java.util.Properties;

/**
 * Class representing user preferences specific to this Application.
 * Can be queried from {@link com.tom.jpedit.ApplicationContext}
 */
public class UserPreferences extends Properties {

    // Constants for retrieving preferences by key
    public static final String FONT_FAMILY = "font-family";
    public static final String FONT_SIZE = "font-size";
    public static final String FULL_PATH_SHOWING = "is-full-path-showing";
    public static final String FONT_STYLE = "font-style";
    public static final String SYSTEM_FAMILY_NAME = "System";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String DEFAULT_SIZE = "17.0";
    public static final String AUTOSAVE_PERIOD_MILLIS = "autosave-period-millis";
    public static final String PREFERED_LOCALE = "preferred-locale";
    public static final long DEFAULT_AUTOSAVE_PERIOD_MILLIS = Duration.ofMinutes(5).toMillis();

    public Locale getPreferredLocale() {
        return Locale.of(getProperty(PREFERED_LOCALE, Locale.getDefault().getLanguage()));
    }

    public void setPreferredLocale(String locale) {
        setProperty(PREFERED_LOCALE, locale);
        attemptSavePreferences();
    }

    public void setPreferredLocale(@NotNull Locale locale) {
        setPreferredLocale(locale.getLanguage());
    }

    public long getAutosavePeriodMillis() {
        return JPUtil.parseLongOr(getProperty(AUTOSAVE_PERIOD_MILLIS), DEFAULT_AUTOSAVE_PERIOD_MILLIS);
    }

    public void setAutosavePeriodMillis(long millis) {
        setProperty(AUTOSAVE_PERIOD_MILLIS, String.valueOf(millis));
        attemptSavePreferences();
    }

    public Font getPreferredFont() {
        JPLogger.getAppLog().info("Font family " + getProperty(FONT_FAMILY, SYSTEM_FAMILY_NAME));
        return Font.font(
                getProperty(FONT_FAMILY, SYSTEM_FAMILY_NAME),
                JPUtil.parseDoubleOr(getProperty(FONT_SIZE, DEFAULT_SIZE), 17.0)
        );
    }

    public void setPreferredFont(@NotNull Font font) {
        String family = font.getName();
        String size = Double.toString(font.getSize());
        String style = font.getStyle();
        JPLogger.getAppLog().info("Setting user preferred font to " + family + " at " + size + " with style " + style);
        setProperty(FONT_STYLE, style);
        setProperty(FONT_FAMILY, family);
        setProperty(FONT_SIZE, size);
        attemptSavePreferences();
    }

    public boolean isFullPathShowing() {
        return getProperty(FULL_PATH_SHOWING, FALSE).equalsIgnoreCase(TRUE);
    }

    public void setFullPathShowing(boolean showing) {
        JPLogger.getAppLog().info("Setting full path showing to " + showing);
        setProperty(FULL_PATH_SHOWING, Boolean.toString(showing));
        attemptSavePreferences();
    }

    private void attemptSavePreferences() {
        try {
            savePreferences();
        } catch (IOException e) {
            JPLogger.getAppLog().severe("Attempt to save user preferences failed: " + e.getMessage());
        }
    }

    public void savePreferences() throws IOException {
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream("user-prefs.properties"))) {
            store(stream, "");
        } catch (IOException f) {
            JPLogger.getAppLog().severe("Could not save user preferences");
            throw f;
        }
    }
}
