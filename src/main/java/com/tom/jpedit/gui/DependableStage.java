package com.tom.jpedit.gui;

import javafx.event.Event;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A {@code DependableStage} is a {@link Stage} that can have
 * dependents. A dependent is a {@link Stage} that cannot
 * exist without its owner. If a {@link DependableStage}
 * closes then all of its dependents must also close
 * <p>
 * Circular dependencies are allowed as the methods involved
 * check for this and prevent run-away closing
 */
public abstract class DependableStage extends Stage {
    private final List<Stage> dependents = new LinkedList<>();

    private boolean closing;

    /**
     * Closes the stage but first closes every stage that depends on this stage.
     * <p>
     * When this stage begins closing, {@code closing} is set to true.
     * Every dependent stage is then closed. This is done by firing a
     * {@link WindowEvent#WINDOW_CLOSE_REQUEST} event on all dependent
     * Stages. This method is capable of checking for circular dependencies
     * and will not cause run-away closing to throw {@link StackOverflowError}
     * <p>
     * After all dependents are closed, this stage closes.
     */
    @Override
    public void close() {
        closing = true;
        for (Stage stage : getDependentsUnmodifiable()) {
            if (!(stage instanceof DependableStage) || !((DependableStage) stage).isClosing()) {
                Event.fireEvent(stage, new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        }
        super.close();
    }

    /**
     * Used to determine if a {@code DependableStage} is in the process
     * of closing.
     * <p>
     * This helps to prevent run-away closing if two
     * dependable stages depend on each other
     *
     * @return true if the Stage is in the process of closing itself and all its dependents
     */
    public boolean isClosing() {
        return closing;
    }

    /**
     * Gets a {@link List} of {@link Stage} that depend on this stage.
     * <p>
     * All the Stages in this list will be closed (if they remain open)
     * when this stage closes
     *
     * @return the list of Stages that depend on this Stage
     */
    public List<Stage> getDependentsUnmodifiable() {
        return Collections.unmodifiableList(dependents);
    }

    /**
     * Register a stage as a dependent of this stage.
     * <p>
     * Any stages that are dependent on this stage and not closed will
     * be closed when this stage closes.
     *
     * @param stage the stage that is to be made dependent on this stage
     */
    public void registerDependent(Stage stage) {
        dependents.add(stage);
    }

    /**
     * Removes a stage from depending on this stage.
     * <p>
     * Once a stage is removed from the dependents, it will no longer be closed
     * on the closing of this stage. You can pass any stage to this
     * method, and it will remove the object from the list
     * or do nothing if it is not in the list as it delegates to
     * {@link List#remove(Object)}
     *
     * @param stage the stage to remove from dependents
     */
    public void deregisterDependent(Stage stage) {
        dependents.remove(stage);
    }

}
