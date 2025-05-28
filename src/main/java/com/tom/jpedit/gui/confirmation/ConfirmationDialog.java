package com.tom.jpedit.gui.confirmation;

import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.components.CancelButton;
import com.tom.jpedit.gui.components.NoButton;
import com.tom.jpedit.gui.components.YesButton;
import com.tom.jpedit.logging.JPLogger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class representing a dialog prompt to ask the user to confirm or cancel something
 * This class provides no external behavior; it shows a dialog, pauses the thread until
 * the user answers and returns the choice.
 * <p>
 * Note that the execution of the UI is paused until <code>showPrompt()</code> returns
 */
public class ConfirmationDialog extends DependantStage {
    private final String title;
    private final String head;
    private final String message;

    /**
     * Construct a confirmation dialog with the corresponding message
     *
     * @param owner   the owner to tie this window to
     * @param title   title of the window
     * @param head    main text body
     * @param message the message text of the window
     */
    public ConfirmationDialog(@Nullable JPEditWindow owner, String title, String head, String message) {
        super(owner);
        this.title = title;
        this.head = head;
        this.message = message;
    }

    /**
     * Show the prompt and wait for a response.
     *
     * @return the ConfirmationType based on the button clicked by the user
     */
    public @NotNull ConfirmationType showPrompt() {
        if (isAlreadyShowing()) {
            System.out.println("Already showing");
            return ConfirmationType.CANCEL;
        }
        VBox box = new VBox();
        GridPane buttonPane = new GridPane();
        box.setPadding(new Insets(15.0));
        box.setSpacing(15.0);
        box.setAlignment(Pos.CENTER);

        buttonPane.setPadding(new Insets(5.0));
        buttonPane.setVgap(5.0);
        buttonPane.setHgap(5.0);
        buttonPane.setAlignment(Pos.CENTER);

        Label headLabel = new Label(head);
        headLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize() * 1.2));

        Label messageLabel = new Label(message);

        var ref = new Object() {
            ConfirmationType confirmed = ConfirmationType.CANCEL;
        };

        Button noButton = new NoButton(i -> {
            ref.confirmed = ConfirmationType.NO;
            close();
        });

        Button yesButton = new YesButton(i -> {
            ref.confirmed = ConfirmationType.YES;
            close();
        });

        Button cancelButton = new CancelButton(i -> {
            ref.confirmed = ConfirmationType.CANCEL;
            close();
        });

        buttonPane.add(cancelButton, 0, 0);
        buttonPane.add(noButton, 1, 0);
        buttonPane.add(yesButton, 2, 0);
        box.getChildren().addAll(headLabel, messageLabel, buttonPane);

        Scene scene = new Scene(box);
        setScene(scene);

        setTitle(title);

        showAndWait();
        JPLogger.debug(JPLogger.getAppLog(), "confirm = " + ref.confirmed);
        return ref.confirmed;
    }

    /**
     * Determine if an instance of this window is already showing
     * Helps prevent re-displaying windows like Find and Help
     *
     * @return true if the window is showing or false otherwise
     */
    private boolean isAlreadyShowing() {
        boolean alreadyShowing = false;
        if (owner != null) {
            for (Stage stage : owner.getDependentsUnmodifiable()) {
                if (stage instanceof ConfirmationDialog && stage != this) {
                    alreadyShowing = true;
                    stage.setAlwaysOnTop(true);
                    stage.setAlwaysOnTop(false);
                }
            }
        }
        return alreadyShowing;
    }
}
