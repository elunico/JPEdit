package com.tom.jpedit.gui.i18n;

import com.tom.jpedit.logging.JPLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * Non-instantiable class for interacting with Internationalization (I18n). The class is used to load strings from a
 * .properties file containing the strings for a language (found in the lang folder)
 */
public final class Strings {

    /**
     * Contains the constants which represent the keys in the .properties file.
     * The enum constants have a field called text which is the string content found in the .properties file.
     * These constants can be used directly in the code with the text field in the UI
     */
    public enum Content {

        HELP_ITEM,

        // BUTTONS
        BUTTON_NO,
        BUTTON_YES,
        BUTTON_CANCEL,
        BUTTON_OK,
        BUTTON_REMOVE,
        BUTTON_SAVE_DISK,

        // UI LABELS AND TEXT
        UILABEL_LAST_SAVE_LABEL,
        UILABEL_LAST_SAVE_TIME,

        CHANGE_LOCAL_PROMPT_TITLE,
        CHANGE_LOCAL_PROMPT_CHOOSE_LABEL,
        CHANGE_LOCAL_PROMPT_FINE_PRINT,
        CHANGE_LOCAL_PROMPT_FINE_RESTART_WARNING,

        INTERNATIONAL_SELECT_ADD_BUTTON,
        INTERNATIONAL_SELECT_ADD_TITLE,
        INTERNATIONAL_SELECT_ADD_HEADER,
        INTERNATIONAL_STRING_EDITOR_TITLE,

        INTERNATIONAL_STRING_EDITOR_INFO_LABEL,
        INTERNATIONAL_STRING_EDITOR_CODE_DELIM_LEFT,
        INTERNATIONAL_STRING_EDITOR_CODE_DELIM_RIGHT,

        SAVE_WARNING_MESSAGE,

        FONT_PROMPT_TITLE,
        FONT_PROMPT_FAMILY_LABEL,
        FONT_PROMPT_SIZE_LABEL,
        FONT_PROMPT_WEIGHT_LABEL,
        FONT_PROMPT_POSTURE_LABEL,

        FIND_DIALOG_FIND_TITLE,
        FIND_DIALOG_REPLACE_TITLE,
        FIND_DIALOG_FIND_LABEL,
        FIND_DIALOG_REPLACE_LABEL,
        FIND_DIALOG_IGNORE_CASE,
        FIND_DIALOG_FIND_NEXT_BUTTON,
        FIND_DIALOG_FIND_PREVIOUS_BUTTON,
        FIND_DIALOG_REPLACE_BUTTON,
        FIND_DIALOG_REPLACE_ALL_BUTTON,

        WINDOW_TITLE_DEFAULT,

        MENU_TITLE_ADVANCED,
        MENU_TITLE_EDIT,
        MENU_TITLE_FILE,
        MENU_TITLE_FORMAT,

        AUTOSAVE_WORKER_FAILED_DIR_INACC_STRING,
        AUTOSAVE_WORKER_FAILED_DIR_INACC_TITLE,

        FILE_MENU_OPEN_RECENT,
        FILE_MENU_DUPLICATE_WINDOW,
        FILE_MENU_PRINT,
        FILE_MENU_CLEAR_RECENTS,
        FILE_MENU_PATH_TOGGLE,
        FILE_MENU_ITEM_OPEN,
        FILE_MENU_ITEM_RECENT_FILES,
        FILE_MENU_ITEM_SAVE,
        FILE_MENU_ITEM_SAVE_AS,
        FILE_MENU_ITEM_CLEAR_RECENT_FILES,
        FILE_MENU_ITEM_CLOSE,
        FILE_MENU_ITEM_EXIT,
        FILE_MENU_ITEM_NEW,
        FILE_MENU_ITEM_NEW_WINDOW,

        ADVANCED_MENU_AUTOSAVE_ITEM,
        ADVANCED_MENU_AUTOSAVE_ON_ITEM,
        ADVANCED_MENU_AUTOSAVE_OFF_ITEM,
        ADVANCED_MENU_AUTOSAVE_DELAY_ITEM,
        ADVANCED_MENU_ADD_PLUGIN,
        ADVANCED_MENU_REMOVE_PLUGIN,
        ADVANCED_MENU_CHANGE_LOCALE,
        ADVANCED_MENU_ADD_EDIT_STRINGS,

        COPYRIGHT_TEXT,

        HELP_MENU_BLURB;

        public String text;

        Content() {
            this.text = name().toLowerCase();
        }
    }

    public static Locale locale = Locale.getDefault();

    /**
     * Returns the preferred locale of the user. It is the same as {@link Locale#getDefault()} unless the user has
     * chosen a specific preferred locale in the settings menu.
     *
     * @return the user's preferred locale, or the default locale if none has been set.
     */
    public static Locale getLocale() {
        return locale;
    }

    /**
     * Load the strings for a given locale from the lang folder. The string values are set as the text field of the
     * enum constant whose name matches the key in the .properties file.
     *
     * @param locale the locale file to load strings from. If null, the default locale is used.
     */
    public static void loadStrings(@Nullable Locale locale) {
        Strings.locale = Objects.requireNonNullElse(locale, Locale.getDefault());
        var fileStrings = getFileStrings(Strings.locale);
        for (var entry : fileStrings.entrySet()) {
            Content content = entry.getKey();
            content.text = entry.getValue();
        }
    }

    /**
     * Returns a HashMap containing the strings for a given locale. The key is the enum constant whose name matches the
     * key in the .properties file, and the value is the string content found in the .properties file.
     *
     * @param locale the locale file to load strings from
     * @return a HashMap containing the strings for a given locale
     */
    public static @NotNull HashMap<Content, String> getFileStrings(@NotNull Locale locale) {
        var strings = new HashMap<Content, String>();
        File fileName = new File("lang/" + locale.getLanguage() + ".properties");
        JPLogger.getAppLog()
                .info("Loading language strings for " + locale.getLanguage() + " from " + fileName.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = reader.readLine()) != null) {
                s = s.trim();
                if (s.isEmpty() || s.startsWith("#")) {
                    continue;
                }
                if (s.endsWith("\\")) {
                    StringBuilder sb = new StringBuilder();
                    do {
                        sb.append(s, 0,  s.length() - 1);
                        sb.append("\n");
                        s = reader.readLine();
                    } while (s != null && s.endsWith("\\"));
                    s = sb.toString();
                }
                try {
                    var split = s.split("=", 2); // limit to 2 parts to handle = in values
                    if (split.length >= 1) {
                        String key = split[0].trim();
                        String value = split.length > 1 ? split[1] : "";
                        try {
                            Content enumKey = Content.valueOf(key);
                            strings.put(enumKey, value);
                        } catch (IllegalArgumentException e) {
                            // Log invalid enum key but continue processing
                            JPLogger.getAppLog().warning("Invalid language key found: " + key);
                        }
                    }
                } catch (Exception e) {
                    JPLogger.getAppLog().warning("Error processing line: " + s + "\n" + e.getMessage());
                }
            }
        } catch (IOException e) {
            JPLogger.getAppLog().warning("Could not read language strings file for " + locale.getLanguage() +
                    "!\nUsing English strings.\nError: " + e.getMessage());
        }
        return strings;
    }

    /**
     * Create a blank .properties file for a given locale. The file is created at the path given.
     *
     * @param path the path to create the file at
     * @throws IOException if the file cannot be created for any reason
     */
    public static void createTemplate(@NotNull File path) throws IOException {
        boolean _ = path.createNewFile();
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (Content content : Content.values()) {
                writer.println(content.name() + "=");
            }
        }
    }

    // no instances
    private Strings() {
    }

}