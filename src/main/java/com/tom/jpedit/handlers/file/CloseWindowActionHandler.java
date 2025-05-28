package com.tom.jpedit.handlers.file;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.handlers.ActionHandler;
import com.tom.jpedit.handlers.misc.DirtyCheckHandler;
import com.tom.jpedit.logging.JPLogger;
import javafx.event.ActionEvent;
import tom.utils.annotations.Blocking;
import tom.utils.annotations.TimeoutPolicy;

import java.util.concurrent.ExecutionException;

public class CloseWindowActionHandler extends ActionHandler {
    public CloseWindowActionHandler(JPEditWindow owner) {
        super(owner);
    }

    @Override
    @Blocking(TimeoutPolicy.NO_TIMEOUT)
    public void handle(ActionEvent event) {
        DirtyCheckHandler handler = new DirtyCheckHandler(owner, () -> {
            try {
                owner.terminateAutoSaveWorker();
            } catch (InterruptedException e) {
                JPLogger.getErrLog().severe("Interrupted while waiting for autosave to stop");
            } catch (ExecutionException e) {
                JPLogger.getErrLog().severe("Execution exception while waiting for autosave thread termination");
            }
            // stop auto-save first because if this is the last window, teardown destroyes the pool
            ApplicationContext.getContext().unregisterWindow(owner);
            owner.close();
        });
        handler.handle(event);
    }
}
