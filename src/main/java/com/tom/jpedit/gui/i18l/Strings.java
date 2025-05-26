package com.tom.jpedit.gui.i18l;

import com.tom.jpedit.logging.JPLogger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;

public final class Strings {

    public enum Content {


        MENU_TITLE_ADVANCED,
        WINDOW_TITLE_DEFAULT,
        MENU_TITLE_EDIT,
        FILE_MENU_ITEM_CLEAR_RECENT_FILES,
        FILE_MENU_ITEM_CLOSE,
        FILE_MENU_ITEM_EXIT,
        FILE_MENU_ITEM_NEW,
        FILE_MENU_ITEM_NEW_WINDOW,
        UILABEL_LAST_SAVE_LABEL,
        UILABEL_LAST_SAVE_TIME,

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
        MENU_TITLE_FILE,
        MENU_TITLE_FORMAT,

        ;

        public String text;

        Content() {
            this.text = name().toLowerCase();
        }
    }

    public static Locale locale = Locale.getDefault();

    // no instances
    private Strings() {
    }

    public static void loadStrings(Locale locale) {
        var fileStrings = getFileStrings(locale);
        for (var entry : fileStrings.entrySet()) {
            System.out.println(entry);
            Content content = entry.getKey();
            content.text = entry.getValue();
        }
    }

    public static @NotNull HashMap<Content, String> getFileStrings(@NotNull Locale locale) {
        var strings = new HashMap<Content, String>();
        File fileName = new File("lang/" + locale.getLanguage() + ".properties");
        JPLogger.getAppLog().info("Loading language strings for " + locale.getLanguage() + " from " + fileName.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = reader.readLine()) != null) {
                try {
                    // Skip empty lines and comments
                    if (s.trim().isEmpty() || s.startsWith("#")) {
                        continue;
                    }
                    
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

    public static void createTemplate(@NotNull File path) throws IOException {
        boolean _ = path.createNewFile();
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (Content content : Content.values()) {
                writer.println(content.name() + "=");
            }
        }
    }

}