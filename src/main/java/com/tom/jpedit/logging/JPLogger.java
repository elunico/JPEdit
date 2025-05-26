package com.tom.jpedit.logging;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class JPLogger {

  private static final Logger appLog = Logger.getLogger("com.tom.jpedit.application");
  private static final Logger errLog = Logger.getLogger("com.tom.jpedit.error");
  private static Level debugLevel = Level.INFO;
  private static boolean debug;

  static {
    configLogProperties();
    registerFileHandlers();
  }

  public static boolean isDebug() {
    return debug;
  }

  private static void configLogProperties() {
    final InputStream inputStream;
    try {
      inputStream = new FileInputStream(new File("logging.properties"));
      final Properties loggingProperties = new Properties();
      loggingProperties.load(inputStream);
      debug = Boolean.parseBoolean(loggingProperties.getProperty("DEBUG"));
      debugLevel = Level.parse(loggingProperties.getProperty("debugLevel"));
      Level appLevel = Level.parse(loggingProperties.get("appLevel").toString());
      appLog.setLevel(appLevel);
      debug(appLog, "appLog level set to " + appLevel);
      Level errLevel = Level.parse(loggingProperties.get("errLevel").toString());
      errLog.setLevel(errLevel);
      debug(errLog, "errLog level set to " + errLevel);
    } catch (IOException e) {
      System.err.println("Log properties not found! Using defaults!");
      appLog.info("Setting app log level to ALL");
      appLog.setLevel(Level.ALL);
      appLog.info("Setting err log level to WARNING");
      errLog.setLevel(Level.WARNING);
      errLog.warning("err log level set to WARNING");
      appLog.info("Setting debug to false");
      debug = false;
    }
  }

  public static void debug(Logger logger, String message) {
    if (debug) {
      logger.log(debugLevel, message);
    }
  }

  private static void registerFileHandlers() {
    try {

      final FileHandler appFile = new FileHandler("application.log");
      final FileHandler errFile = new FileHandler("error.log");

      // create a TXT formatter
      final SimpleFormatter txtFormatter1 = new SimpleFormatter();
      appFile.setFormatter(txtFormatter1);
      appLog.addHandler(appFile);

      // create a TXT formatter
      final SimpleFormatter txtFormatter2 = new SimpleFormatter();
      errFile.setFormatter(txtFormatter2);
      errLog.addHandler(errFile);
    } catch (IOException e) {
      System.err.println("Could not create file handlers for logger");
      errLog.severe("could not create log files!");
      errLog.severe(e.toString());
    }
  }

  public static void debug(Logger logger, Level level, Supplier<String> messageSupplier) {
    debug(logger, level, messageSupplier.get());
  }

  private static void debug(Logger logger, Level level, String message) {
    if (debug) {
      logger.log(level, message);
    }
  }

  public static Logger getLogger(String... pkgcmps) {
    return Logger.getLogger(String.join(".", pkgcmps));
  }

  @Contract(pure = true)
  public static Logger getAppLog() {
    return appLog;
  }

  @Contract(pure = true)
  public static Logger getErrLog() {
    return errLog;
  }
}
