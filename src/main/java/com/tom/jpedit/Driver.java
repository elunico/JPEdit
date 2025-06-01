package com.tom.jpedit;

import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.confirmation.ConfirmationDialog;
import com.tom.jpedit.gui.confirmation.ConfirmationType;
import com.tom.jpedit.gui.i18n.Strings;
import com.tom.jpedit.logging.JPLogger;
import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import tom.javafx.JavaFXUtilsKt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * @author Thomas Povinelli
 * Created 4/5/19
 * In JPEdit
 */
public class Driver extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static ConfirmationType promptForRecovery() {
        return new ConfirmationDialog(
                null,
                "Recover test?",
                "Temporary files found",
                "It looks like JPEdit may have crashed, but there is some recoverable text. \nWould you like to recover the text now? \n(Answering no or failing to save the recovered\nfiles will destroy them forever)"
        ).showPrompt();
    }
    private final ApplicationContext context = ApplicationContext.getContext();

    @Override
    public void start(Stage primaryStage) {
        Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(JavaFXUtilsKt::defaultWindowedExceptionHandler);

        var preferredLocale = context.getUserPreferences().getPreferredLocale();
        JPLogger.getAppLog().info("Preferred locale is " + preferredLocale);
        Strings.loadStrings(preferredLocale);

        String tempSaveDir = context.getProperties().getProperty("tempsaves_dir");
        try {
            if (tempSaveDir != null) {
                File file = new File(tempSaveDir);
                File[] contents = file.listFiles();

                if (contents != null && contents.length > 0) {
                    ConfirmationType confirmationType = promptForRecovery();
                    if (confirmationType == ConfirmationType.YES) {
                        recoverAllTempFiles(contents);
                        removeRecoverableFiles(contents);
                        return;
                    }
                    if (confirmationType == ConfirmationType.NO) {
                        removeRecoverableFiles(contents);
                    }
                }
                normalStartUp();
            }
        } catch (Exception e) {
            if (JPLogger.isDebug()) {
                Thread.setDefaultUncaughtExceptionHandler(oldHandler);
            }
            throw e;
        }
    }

    private void recoverAllTempFiles(@NotNull File @NotNull [] contents) {
        for (File tf : contents) {
            try (BufferedReader reader = new BufferedReader(new FileReader(tf))) {
                String recovered = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                JPEditWindow window = new JPEditWindow();
                window.setTitle(tf.getName());
                window.getTextArea().setText(recovered);
                context.registerWindow(window);
                window.show();
            } catch (IOException e) {
                JavaFXUtilsKt.popupAlert(
                        "The temporary saves could not be recovered. No files will be deleted. You may be prompted the next time you start JPEdit",
                        "Could not recover text!"
                );
            }
        }
    }

    private void removeRecoverableFiles(@NotNull File @NotNull [] contents) {
        for (File tf : contents) {
            try {
                JPLogger.getAppLog().info("Attempting to delete " + tf.toPath());
                Files.deleteIfExists(tf.toPath());
            } catch (IOException e) {
                JavaFXUtilsKt.popupAlert("Could not remove temporary files. See logs for details", "Could not remove files");
                JPLogger.getErrLog().warning(() -> "Could not remove temporary file " + tf.getName());
                JPLogger.getErrLog().warning(e::getMessage);
                JPLogger.getErrLog().warning(() -> JavaFXUtilsKt.stackTraceToString(e.getStackTrace()));
                break;
            }
        }
    }

    private void normalStartUp() {
        JPEditWindow window = new JPEditWindow(Strings.Content.WINDOW_TITLE_DEFAULT.text);
        context.registerWindow(window);
        window.show();
        JPLogger.getAppLog().info("main window showing");
    }
}
