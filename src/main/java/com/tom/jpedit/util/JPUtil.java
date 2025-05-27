package com.tom.jpedit.util;

import org.jetbrains.annotations.Nullable;
import tom.javafx.JavaFXUtilsKt;

import java.io.File;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * This class contains many convenience methods for interacting with the application and environment
 */
public class JPUtil {

  /**
   * Returns true if the ENV contains the key and the value of that key in the env is true or TRUE or True
   * @param key
   * @return
   */
  public static boolean environmentIsTrue(String key) {
    return environmentIsTrue(key, false);
  }

  /**
   * Returns true if the ENV contains the key and the value of that key in the env is 'true'
   * @param key
   * @return
   */
  public static boolean environmentIsTrue(String key, boolean caseSensitive) {
    if (!caseSensitive) {
      return System.getenv()
                   .entrySet()
                   .stream()
                   .collect(Collectors.toMap(
                       entry -> entry.getKey().toLowerCase(Locale.ROOT),
                       entry -> entry.getValue().toLowerCase(Locale.ROOT)
                   ))
                   .getOrDefault(key, "false")
                   .equalsIgnoreCase("true");
    } else {
      return System.getenv().getOrDefault(key, "false").equals("true");
    }
  }

  public static double parseDoubleOr(String property, double defaultValue) {
    if (property == null) return defaultValue;
    try {
      return Double.parseDouble(property);
    } catch (NumberFormatException f) {
      return defaultValue;
    }
  }

  public static long parseLongOr(String property, long defaultAutosavePeriodMillis) {
    if (property == null) return defaultAutosavePeriodMillis;
    try {
      return Long.parseLong(property);
    } catch (NumberFormatException f) {
      return defaultAutosavePeriodMillis;
    }
  }

  /**
   * Returns the expected main class name for a plugin jar file. This method can be used
   * by plugin developers to debug their plugins if the main class is not found
   * @param jar the plugin jar file to check
   * @return the expected name of the name class of the plugin jar
   */
  @Nullable
  public static String mainClassFromJAR(File jar) {
    if (jar == null) {
      return null;
    }
    String[] filenameComponents = jar.getName().split("\\.");
    if (filenameComponents.length != 2 || !filenameComponents[1].equals("jar")) {
      JavaFXUtilsKt.popupAlert("Please select a jar in the appropriate format", "Select a jar");
      return null;
    }
    return filenameComponents[0];
  }
}
